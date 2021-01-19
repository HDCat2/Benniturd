import java.util.*;
import java.awt.*;
import java.awt.geom.*;

/*
Algs.java
Henning Jiang, Matthew Mckewan, Bennett Grenier
This file implements some needed methods to make the game work
*/

public class Algs{ //some algorithms

    //here is a slightly basic 1D perlin noise generator
    //the goal of the perlin noise generator is to generate a random but smooth curvy line to draw roads and rivers in the world generation
    //the perlin noise generator uses a PRNG (pseudo random number generator) and cosine interpolation to do this
    //PRNG is short for Pseudo-Random Number Generator; it is the same as a normal RNG except if the same input is passed in, it will always return the same output
    //Interpolation is the insertion of values between successive points generated from our PRNG, such that the resultant curve looks kinda smooth
    //Cosine interpolation is the usage of a cosine function to generate values from our interpolation method

    private static double MP = 21378232, AP = 5344559, BP = Math.random()*MP; //MP, AP, and BP are our values for the PRNG
    
    private static final int RIVER_THICK = 7;
    private static final int PATH_THICK = 3;

    private static ArrayList<Pair<Integer, Integer>> underPortals, overPortals;
    private static Pair<Integer, Integer> lightKey, darkKey;

    private static int world = 0; //0 = overworld, 1 = sky, 2 = underworld
    private static boolean portalOpened = false;

    private static double pRand(){ //Our PRNG
        BP = (AP * BP + 1) % MP;   //In order for this to work, AP-1 must be divisible by MP's prime factors
        return (BP) / MP;          //Otherwise this PRNG will eventually constantly generate the same value on successive tries
    }

    private static double pInterpolate(double a,double b,double x){ //Interpolation takes in 2 points (a and b) and a double between 0 and 1
        double cx = x*Math.PI;                                      //signifying what kind of value you want
        double f = (1-Math.cos(cx)) * 0.5;
        return a * (1-f) + b * f;
    }

    private static ArrayList<Integer> genPerlin(double amp){ //the perlin noise generator
        double x = 0, y = 0, wl = 10, a = pRand(), b = pRand();
        //x and y are coordinates of starting point (x,y), amp is the max value of y, wl determines how many points are interpolated between each point
        //a and b are the points that the interpolation method is currently generating values in between
        ArrayList<Integer> path = new ArrayList<Integer>();
        //suppose perlin(x) is the value generated when x is passed into this function
        //then path will contain [0, perlin(1), perlin(2), perlin(3)... perlin(999)] in that order
        while(x < 1000){
            path.add((int) (Math.floor(y)));
            if(x % wl == 0){
                a = b;
                b = pRand();
                y = a * amp;
            }
            else{
                y = pInterpolate(a,b,(x%wl)/wl) * amp;
            }
            x += 1;
        }
        return path;
    }

    public static ArrayList<int[]> calcTiles(int x, int y, double ang){
        //This method returns an arraylist of the tiles that the screen sees
        //This is to reduce lag, as drawing any more than the necessary tiles is a little slow
        //900x by 650y, character is 475 down from top of center
        //tile size is 128 x 128
        //assuming up is 0 deg and goes counterclockwise
        ArrayList<int[]> pieces = new ArrayList<int[]>();
        Polygon bigRect = new Polygon();
        ang *= -1;
        //cx and cy represent where the center of the screen is on the terrain itself
        int cx = (int) (c(ang - Math.PI/2) * 200) + x, cy = (int) (s(ang - Math.PI/2) * 200) + y;
        double rAng = 0.8884797719201485;
        int c1x = cx + (int) (c(ang - Math.PI/2 + rAng) * 630), c1y = cy + (int) (s(ang - Math.PI/2 + rAng) * 630);
        int c2x = cx + (int) (c(ang - Math.PI/2 - rAng + Math.PI) * 630), c2y = cy + (int) (s(ang - Math.PI/2 - rAng + Math.PI) * 630);
        int c3x = cx + (int) (c(ang - Math.PI/2 - rAng) * 630), c3y = cy + (int) (s(ang - Math.PI/2 - rAng) * 630);
        int c4x = cx + (int) (c(ang - Math.PI/2 + rAng - Math.PI) * 630), c4y = cy + (int) (s(ang - Math.PI/2 + rAng - Math.PI) * 630);

        bigRect.addPoint(c1x,c1y);
        bigRect.addPoint(c3x,c3y);
        bigRect.addPoint(c4x,c4y);
        bigRect.addPoint(c2x,c2y);
        Area bigArea = new Area(bigRect);

        //bigRect is the rectangle that represents the screen itself

        int minX = cx/128, maxX = cx/128, minY = cy/128, maxY = cy/128;
        for(int i = cx/128 - 10; i < cx/128 + 10; i++){ //look at every tile within 10 tiles of (cx,cy)
                                                        //make an Area object that represents said tile
                                                        //if the intersection of the screen and the tile is empty, then the tile is not on the screen
                                                        //otherwise, add the tile to the list
            for(int q = cy/128 - 10; q < cy/128 + 10; q++){
                Polygon poly = new Polygon();
                poly.addPoint(i*128,q*128);
                poly.addPoint(i*128 + 128, q * 128);
                poly.addPoint(i*128 + 128, q * 128 + 128);
                poly.addPoint(i*128, q * 128 + 128);
                Area smallArea = new Area(poly);
                smallArea.intersect(bigArea);
                if(!smallArea.isEmpty()){
                    minX = Math.min(minX,i);
                    maxX = Math.max(maxX,i);
                    minY = Math.min(minY,q);
                    maxY = Math.max(maxY,q);
                    pieces.add(new int[]{i,q});
                }
            }
        }
        pieces.add(new int[]{minX,maxX,minY,maxY});
        return pieces;
    }

    //Here are the world generation methods
    //there are 4: overworld, underworld, the sky, and the boss arena
    //the underworld and the sky are connected to the overworld by portals
    //the boss arena is connected to the overworld by a sealed portal that unlocks when all 3 keys are collected

    public static int[][] genTilesO(){
        //This method, like all tile generation methods, returns a 1000x1000 array of integers
        //Each integer represents a tile; precisely which number represents what can be found in Fetcher.java
        underPortals = new ArrayList<Pair<Integer, Integer>>();
        overPortals = new ArrayList<Pair<Integer, Integer>>();
        //order of placement is grass, then desert/tundra, then lakes/rivers, then roads, then portals, then mountains, then altar, then portal
        //order of placement matters because placed tiles will overwrite any tiles that come before
        int[][] k = new int[1000][1000];
        for(int i = 0; i < 1000; i++){ //initially all tiles are cast as grass
            for(int q = 0; q < 1000; q++){
                k[i][q] = 1;
            }
        }

        Random rand = new Random();

        LinkedList<Pair<Integer, Pair<Integer, Integer>>> Q = new LinkedList<>();

        for(int i = 0; i < 20; i++){ //here is the desert generation
            Q.push(new Pair(80, new Pair(rand.nextInt(1000),rand.nextInt(1000))));
        }
        //System.out.println(1);
        while(!Q.isEmpty()){ //the desert generation is essentially jury rigged from a basic BFS
            Pair<Integer, Pair<Integer,Integer>> cTile = Q.remove(); //the first integer of the 3 dictates how likely it is that the sand tile will be placed
            if(cTile.s.f > 999 || cTile.s.f < 0 || cTile.s.s < 0 || cTile.s.s > 999) continue; //the 2nd and 3rd are coordinates
            if(k[cTile.s.f][cTile.s.s] == 3) continue;
            if(rand.nextInt(11) > cTile.f) continue;

            k[cTile.s.f][cTile.s.s] = 3;

            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f + 1, cTile.s.s))); //if placing the tile is successful, attempt to place some other tiles
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f - 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s + 1)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s - 1)));
            //System.out.println(Q.size() + " " + cTile.f);
        }

        for(int i = 0; i < 30; i++){ //tundra generation, same as desert generation but the origin tiles are deliberatly placed away from deserts
            Pair t = new Pair<>(rand.nextInt(1000),rand.nextInt(1000));
            boolean des = false;
            for(int q = ((int)t.f - 50); q < ((int)t.f + 50); q++){
                for(int w = ((int)t.s - 50); w < ((int)t.s + 50); w++){
                    if(q < 0 || w < 0 || q > 999 || w > 999) continue;
                    if(k[q][w] == 3){
                        des = true;
                        break;
                    }
                }
            }
            if(des == true) continue;
            Q.push(new Pair(80, t));
        }
        
        while(!Q.isEmpty()){
            Pair<Integer, Pair<Integer,Integer>> cTile = Q.remove();
            if(cTile.s.f > 999 || cTile.s.f < 0 || cTile.s.s < 0 || cTile.s.s > 999) continue;
            if(k[cTile.s.f][cTile.s.s] == 5) continue;
            if(rand.nextInt(11) > cTile.f) continue;

            k[cTile.s.f][cTile.s.s] = 5;

            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f + 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f - 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s + 1)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s - 1)));
        }

        ArrayList<Pair<Integer,Integer>> rivers = new ArrayList<Pair<Integer,Integer>>();

        for(int i = 0; i < 10; i++){ //generating lakes, there will be 10 of them
            Pair<Integer, Integer> p = new Pair(rand.nextInt(1000), rand.nextInt(1000));
            for(int q = p.f - 100; q <= p.f + 100; q++){
                for(int w = p.s - 100; w <= p.s + 100; w++){
                    int rq = rand.nextInt(5) + 50;
                    if((q-p.f)*(q-p.f) + (w-p.s)*(w-p.s) < rq*rq && q >= 0 && q < 1000 && w >= 0 && w < 1000){
                        k[q][w] = 7;
                    }
                }
            }
            rivers.add(p);

        }
        
        ArrayList<Pair<Integer, Integer>> dirs = new ArrayList<Pair<Integer,Integer>>();
        dirs.add(new Pair(0,1));
        dirs.add(new Pair(1,0));
        dirs.add(new Pair(0,-1));
        dirs.add(new Pair(-1,0));
        dirs.add(new Pair(1,1));
        dirs.add(new Pair(1,-1));
        dirs.add(new Pair(-1,1));
        dirs.add(new Pair(-1,-1));


        for(Pair<Integer, Integer> i : rivers){ //rivers go here
            ArrayList<Integer> river = genPerlin(30);
            Pair<Integer, Integer> dir = dirs.get(rand.nextInt(8));
            for(int q = 0; q < 1000; q++){
                Pair<Integer,Integer> np = new Pair<>(i.f + dir.f * q, i.s + dir.s * q);
                if(dir.f == 0){
                    np.f += river.get(q) - 5;
                }
                else{
                    np.s += river.get(q) - 5;
                }
                for(int w = np.f - RIVER_THICK/2; w < np.f + RIVER_THICK/2; w++){
                    for(int e = np.s - RIVER_THICK/2; e < np.s + RIVER_THICK/2; e++){
                        if(w >= 0 && w <= 999 && e >= 0 && e <= 999) k[w][e] = 7;
                    }
                }
            }
        }
        ArrayList<Integer> road = genPerlin(20); //the road
        for(int i = 0; i < 1000; i++){
            Pair<Integer, Integer> rTile = new Pair<>(i, i + road.get(i));
            if(rTile.f < 0) rTile.f = 0;
            if(rTile.f > 999) rTile.f = 999;
            if(rTile.s < 0) rTile.s = 0;
            if(rTile.s > 999) rTile.s = 999;
            for(int q = rTile.f - 2; q <= rTile.f + 2; q++){
                for(int w = rTile.s - 2; w <= rTile.s + 2; w++){
                    if(q < 0 || q > 999 || w < 0 || w > 999) continue;
                    if(k[q][w] == 1) k[q][w] = 8;
                    if(k[q][w] == 3) k[q][w] = 14;
                    if(k[q][w] == 5) k[q][w] = 15;
                    if(k[q][w] == 7) k[q][w] = 16;
                }
            }
        }

        while(underPortals.size() < 25){ //over and underportal generation
                                         //portals will be generated such that no portal is within 25 tiles of another
                                         //these become important for the generations of other maps so we store them in an arraylist
            Pair<Integer, Integer> underPortLoc = new Pair<>(rand.nextInt(950) + 25, rand.nextInt(950) + 25);
            boolean valid = true;
            if(k[underPortLoc.f][underPortLoc.s] == 7 || k[underPortLoc.f][underPortLoc.s] == 8 || k[underPortLoc.f][underPortLoc.s] == 14 || k[underPortLoc.f][underPortLoc.s] == 15 || k[underPortLoc.f][underPortLoc.s] == 16) continue;
            for(Pair<Integer, Integer> i : underPortals){
                if((underPortLoc.f - i.f) * (underPortLoc.f - i.f) + (underPortLoc.s - i.s) * (underPortLoc.s - i.s) < 25*25){
                    valid = false;
                    break;
                }
            }
            if(!valid) continue;
            underPortals.add(underPortLoc);
        }

        while(overPortals.size() < 25){
            Pair<Integer, Integer> overPortLoc = new Pair<>(rand.nextInt(950) + 25, rand.nextInt(950) + 25);
            if(k[overPortLoc.f][overPortLoc.s] == 7 || k[overPortLoc.f][overPortLoc.s] == 8 || k[overPortLoc.f][overPortLoc.s] == 14 || k[overPortLoc.f][overPortLoc.s] == 15 || k[overPortLoc.f][overPortLoc.s] == 16) continue;
            boolean valid = true;
            for(Pair<Integer, Integer> i : underPortals){
                if((overPortLoc.f - i.f) * (overPortLoc.f - i.f) + (overPortLoc.s - i.s) * (overPortLoc.s - i.s) < 25*25){
                    valid = false;
                    break;
                }
            }
            for(Pair<Integer, Integer> i : overPortals){
                if((overPortLoc.f - i.f) * (overPortLoc.f - i.f) + (overPortLoc.s - i.s) * (overPortLoc.s - i.s) < 25*25){
                    valid = false;
                    break;
                }
            }
            if(!valid) continue;
            overPortals.add(overPortLoc);
        }

        for(int i = 0; i < 25; i++){ //generating a basic formation for portals
            Pair<Integer, Integer> b1 = overPortals.get(i), b2 = underPortals.get(i);
            k[b1.f][b1.s] = 13;
            k[b1.f-2][b1.s-1] = 12;
            k[b1.f-2][b1.s+1] = 12;
            k[b1.f+2][b1.s-1] = 12;
            k[b1.f+2][b1.s+1] = 12;
            k[b1.f-1][b1.s-2] = 12;
            k[b1.f-1][b1.s+2] = 12;
            k[b1.f+1][b1.s-2] = 12;
            k[b1.f+1][b1.s+2] = 12;

            k[b2.f][b2.s] = 11;
            k[b2.f-2][b2.s-1] = 10;
            k[b2.f-2][b2.s+1] = 10;
            k[b2.f+2][b2.s-1] = 10;
            k[b2.f+2][b2.s+1] = 10;
            k[b2.f-1][b2.s-2] = 10;
            k[b2.f-1][b2.s+2] = 10;
            k[b2.f+1][b2.s-2] = 10;
            k[b2.f+1][b2.s+2] = 10;
        }

        for(int i = 0; i < 1000; i++){ //generating random trees/cactuses/tumbleweed
            for(int q = 0; q < 1000; q++){
                if(rand.nextInt(40) > 38){
                    if(k[i][q] == 1) k[i][q] = 2;
                    if(k[i][q] == 3) k[i][q] = 4;
                    if(k[i][q] == 5) k[i][q] = 6;
                }
            }
        }

        for(int qwe = 0; qwe < 10; qwe++){ //forest generation, forests are massive circles with higher tree density than other places
            Pair<Integer, Integer> forest;
            while(true){
                forest = new Pair<>(rand.nextInt(800) + 100, rand.nextInt(800) + 100);
                if(k[forest.f][forest.s] == 1 || k[forest.f][forest.s] == 2) break;
            }
            for(int i = forest.f - 75; i < forest.f + 75; i++){
                for(int q = forest.s - 75; q < forest.s + 75; q++){
                    if((i-forest.f)*(i-forest.f) + (q-forest.s)*(q-forest.s) < 75*75 && rand.nextInt(5) > 3 && k[i][q] == 1) k[i][q] = 2;
                }
            }
        }

        
        for(int i = 0; i < 1000; i++){ //mountain generation, mountains will ring the entire map
            Q.add(new Pair<>(2, new Pair<>(0,i)));
            Q.add(new Pair<>(2, new Pair<>(i,0)));
            Q.add(new Pair<>(2, new Pair<>(999,i)));
            Q.add(new Pair<>(2, new Pair<>(i,999)));
        }
        
        while(!Q.isEmpty()){
            Pair<Integer, Pair<Integer,Integer>> cTile = Q.remove();
            if(cTile.s.f > 999 || cTile.s.f < 0 || cTile.s.s < 0 || cTile.s.s > 999) continue;
            if(k[cTile.s.f][cTile.s.s] == 9) continue;
            if(rand.nextInt(4) > cTile.f) continue;

            k[cTile.s.f][cTile.s.s] = 9;

            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f + 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f - 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s + 1)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s - 1)));
        }

        for(int i = 0; i < 10; i++){
            for(int q = 0; q < 10; q++){
                if(k[i][q] == 2) k[i][q] = 1; //clearing out a spawning area for the player, so he doesn't spawn in a tree or something
                if(k[i][q] == 4) k[i][q] = 3;
                if(k[i][q] == 6) k[i][q] = 5;
            }
        }
        
        Pair<Integer, Integer> bp;

        while(true){ //the overworld altar where the key is collected goes here
            Pair<Integer, Integer> ct = new Pair(500 + rand.nextInt(450), 500 + rand.nextInt(450));
            if(Math.abs(ct.f - ct.s) < 100) continue;
            boolean flag = false;
            for(Pair<Integer, Integer> i : overPortals){
                if(dist(ct.f,ct.s,i.f,i.s) < 25) flag = true;
            }
            for(Pair<Integer, Integer> i : underPortals){
                if(dist(ct.f,ct.s,i.f,i.s) < 25) flag = true;
            }
            if(flag == false){
                for(int i = ct.f - 3; i <= ct.f + 3; i++){
                    for(int q = ct.s - 3; q <= ct.s + 3; q++){
                        k[i][q] = 41;
                    }
                }
                bp = new Pair(ct.f,ct.s);
                break;
            }
        }
        ArrayList<Pair<Integer, Integer>> pcq = new ArrayList<>();
        if(bp.f > bp.s){ //adding a straight road that splits off of the central road to highlight the portal location
            int mid = 0;
            while(mid < Math.min(bp.f, 1000 - bp.s)){
                if(k[bp.f - mid][bp.s + mid] == 8 || k[bp.f - mid][bp.s + mid] == 14 || k[bp.f - mid][bp.s + mid] == 15 || k[bp.f - mid][bp.s + mid] == 16) break;
                for(int i = bder(bp.f-mid-1); i <= bder(bp.f-mid+1); i++){
                    for(int q = bder(bp.s+mid-1); q <= bder(bp.s+mid+1); q++){
                        pcq.add(new Pair(i,q));
                    }
                }
                mid += 1;
            }
        }
        else{
            int mid = 0;
            while(mid < Math.min(1000-bp.f, bp.s)){
                if(k[bp.f + mid][bp.s - mid] == 8 || k[bp.f + mid][bp.s - mid] == 14 || k[bp.f + mid][bp.s - mid] == 15 || k[bp.f + mid][bp.s - mid] == 16) break;
                for(int i = bder(bp.f+mid-1); i <= bder(bp.f+mid+1); i++){
                    for(int q = bder(bp.s-mid-1); q <= bder(bp.s-mid+1); q++){
                        pcq.add(new Pair(i,q));
                    }
                }
                mid += 1;
            }
        }

        for(Pair<Integer, Integer> i : pcq){
            k[i.f][i.s] = 8;
        }

        for(int i = bp.f - 3; i <= bp.f + 3; i++){
            for(int q = bp.s - 3; q <= bp.s + 3; q++){
                k[i][q] = 41;
            }
        }

        k[bp.f][bp.s] = 42;

        for(int i = 990; i < 1000; i++){ //the portal to the boss arena goes here
            for(int q = 990; q < 1000; q++){
                k[i][q] = 44;
            }
        }

        overPortals.add(new Pair(982,12));
        underPortals.add(new Pair(982,12));

        return k;
    }
    
    public static int[][] genTilesS () { //this is the method that generates the sky map
                                         //the sky map is essentially portals linked by paths of clouds
                                         //a key of light spawns on an altar on the top right corner of the map
                                         //the generation is very similar to the overworld generation
        int[][] k = new int[1000][1000];
        Random rand = new Random();

        LinkedList<Pair<Integer, Pair<Integer,Integer>>> Q = new LinkedList<>();

        for(int i = 0; i < 1000; i++){ //initially all tiles are cast as clear sky
            for(int q = 0; q < 1000; q++){
                k[i][q] = 23;
            }
        }

        for(int i = 0; i < 26; i++){ //generating circles of clouds around the portal spawn locations
            int radius = rand.nextInt(20) + 20;
            Pair<Integer, Integer> op = overPortals.get(i);
            for(int q = op.f - 45; q <= op.f + 45; q++){
                for(int w = op.s - 45; w <= op.s + 45; w++){
                    if((op.f-q)*(op.f-q) + (op.s-w)*(op.s-w) < (radius + rand.nextInt(5)) * (radius + rand.nextInt(5))){
                        k[bder(q)][bder(w)] = 21;
                    }
                }
            }
        }
        
        int[] con = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};

        while(true){ //constructing a random spanning tree and placing paths of clouds between portals based on that

            boolean flag = false;
            for(int i = 0; i < 26; i++){
                if(con[i] != con[0]) flag = true;
            }
            if(!flag) break;
            int a = rand.nextInt(26), b = rand.nextInt(26);
            if(a == b) continue;
            if(con[a] == con[b]) continue;

            int p = con[a];
            for(int i = 0; i < 26; i++){
                if(con[i] == p) con[i] = con[b];
            }

            ArrayList<Integer> path = genPerlin(20);
            Pair<Integer, Integer> ap = overPortals.get(a), bp = overPortals.get(b);
            int n1 = Math.max(Math.abs(ap.f - bp.f), Math.abs(ap.s - bp.s));
            double xdiff = (double)(bp.f-ap.f)/(double)n1, ydiff = (double)(bp.s-ap.s)/(double)n1;
            //System.out.println(xdiff + " " + ydiff + " " + n1 + " " + ap.f + " " + ap.s + " " + bp.f + " " + bp.s);
            for(int i = 0; i < n1; i++){
                int xc = ap.f + (int) (xdiff * i);
                int yc = ap.s + (int) (ydiff * i);
                Q.add(new Pair(7, new Pair(Math.max(0, Math.min(999, xc)),Math.max(0, Math.min(999, yc + path.get(i) - 10)))));
            }
        }

        while(!Q.isEmpty()){
            Pair<Integer, Pair<Integer,Integer>> cTile = Q.remove();
            if(cTile.s.f > 999 || cTile.s.f < 0 || cTile.s.s < 0 || cTile.s.s > 999) continue;
            if(k[cTile.s.f][cTile.s.s] == 21) continue;
            if(rand.nextInt(3) > cTile.f) continue;

            k[cTile.s.f][cTile.s.s] = 21;

            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f + 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f - 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s + 1)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s - 1)));
        }

        for(int i = 0; i < 90; i++){ //constructing random stormclouds
            Q.push(new Pair(20, new Pair(rand.nextInt(1000),rand.nextInt(1000))));
        }

        while(!Q.isEmpty()){ 
            Pair<Integer, Pair<Integer,Integer>> cTile = Q.remove();
            if(cTile.s.f > 999 || cTile.s.f < 0 || cTile.s.s < 0 || cTile.s.s > 999) continue; 
            if(k[cTile.s.f][cTile.s.s] == 22) continue;
            if(rand.nextInt(11) > cTile.f) continue;

            k[cTile.s.f][cTile.s.s] = 22;

            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f + 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f - 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s + 1)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s - 1)));
        }

        for(int i = 0; i < 26; i++){ //placing the portals themselves
            k[overPortals.get(i).f][overPortals.get(i).s] = 24;
        }

        for(int i = 980; i < 985; i++){ //generating the altar in the sky, collect your key from here
            for(int q = 10; q < 15; q++){
                k[i][q] = 37;
            }
        }

        k[982][12] = 38;

        return k;
    }

    public static int[][] genTilesU () { //the generation of the underworld works the exact same as the generation for the sky
                                         //except the tile types are switched out, and lava pools
                                         //are more numerous but smaller than their thundercloud counterparts
        int[][] k = new int[1000][1000];
        Random rand = new Random();

        LinkedList<Pair<Integer, Pair<Integer,Integer>>> Q = new LinkedList<>();

        for(int i = 0; i < 1000; i++){ //initially all tiles are cast as impassable rocks
            for(int q = 0; q < 1000; q++){
                k[i][q] = 33;
            }
        }

        for(int i = 0; i < 26; i++){ //generating circles of walkable cave tiles around the portal locations
            int radius = rand.nextInt(20) + 20;
            Pair<Integer, Integer> op = underPortals.get(i);
            for(int q = op.f - 45; q <= op.f + 45; q++){
                for(int w = op.s - 45; w <= op.s + 45; w++){
                    int rFac = rand.nextInt(5);
                    if((op.f-q)*(op.f-q) + (op.s-w)*(op.s-w) < (radius + rFac) * (radius + rFac)){
                        k[Math.max(0, Math.min(999, q))][Math.max(0, Math.min(999, w))] = 31;
                    }
                }
            }
        }
        
        int[] con = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};

        while(true){ //constructing another random spanning tree connecting all the portals and laying down paths based on that

            boolean flag = false;
            for(int i = 0; i < 26; i++){
                if(con[i] != con[0]) flag = true;
            }
            if(!flag) break;
            int a = rand.nextInt(26), b = rand.nextInt(26);
            if(a == b) continue;
            if(con[a] == con[b]) continue;

            int p = con[a];
            for(int i = 0; i < 26; i++){
                if(con[i] == p) con[i] = con[b];
            }

            ArrayList<Integer> path = genPerlin(50);
            Pair<Integer, Integer> ap = underPortals.get(a), bp = underPortals.get(b);
            int n1 = Math.max(Math.abs(ap.f - bp.f), Math.abs(ap.s - bp.s));
            double xdiff = (double)(bp.f-ap.f)/(double)n1, ydiff = (double)(bp.s-ap.s)/(double)n1;
            //System.out.println(xdiff + " " + ydiff + " " + n1 + " " + ap.f + " " + ap.s + " " + bp.f + " " + bp.s);
            for(int i = 0; i < n1; i++){
                int xc = ap.f + (int) (xdiff * i);
                int yc = ap.s + (int) (ydiff * i);
                Q.add(new Pair(7, new Pair(Math.max(0, Math.min(999, xc)),Math.max(0, Math.min(999, yc + path.get(i) - 10)))));
            }
        }

        while(!Q.isEmpty()){
            Pair<Integer, Pair<Integer,Integer>> cTile = Q.remove();
            if(cTile.s.f > 999 || cTile.s.f < 0 || cTile.s.s < 0 || cTile.s.s > 999) continue;
            if(k[cTile.s.f][cTile.s.s] == 31) continue;
            if(rand.nextInt(3) > cTile.f) continue;

            k[cTile.s.f][cTile.s.s] = 31;

            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f + 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f - 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s + 1)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s - 1)));
        }

        for(int i = 0; i < 1000; i++){ //there are 1000 lava pools, but they are much smaller
            Q.push(new Pair(5, new Pair(rand.nextInt(1000),rand.nextInt(1000))));
        }

        while(!Q.isEmpty()){ 
            Pair<Integer, Pair<Integer,Integer>> cTile = Q.remove();
            if(cTile.s.f > 999 || cTile.s.f < 0 || cTile.s.s < 0 || cTile.s.s > 999) continue; 
            if(k[cTile.s.f][cTile.s.s] != 31) continue;
            if(rand.nextInt(3) > cTile.f) continue;

            k[cTile.s.f][cTile.s.s] = 32;

            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f + 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f - 1, cTile.s.s)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s + 1)));
            Q.add(new Pair<>(cTile.f - 1, new Pair<>(cTile.s.f, cTile.s.s - 1)));
        }

        for(int i = 0; i < 25; i++){ //finally placing the portals themselves
            k[underPortals.get(i).f][underPortals.get(i).s] = 34;
        }

        for(int i = 0; i < 1000; i++){
            for(int q = 0; q < 1000; q++){
                if(rand.nextInt(5) > 3 && k[i][q] == 31) k[i][q] = 35;
                if(rand.nextInt(5) > 3 && k[i][q] == 31) k[i][q] = 36;
            }
        }

        for(int i = 980; i < 985; i++){ //generating the altar in the underworld, collect your key from here
            for(int q = 10; q < 15; q++){
                k[i][q] = 39;
            }
        }

        k[982][12] = 40;

        return k;
    }
    
    public static int[][] genTilesB(){ //generation of the boss arena
        int[][] k = new int[1000][1000];
        for(int i = 0; i < 1000; i++){ //initially all tiles are cast as diamond
            for(int q = 0; q < 1000; q++){
                k[i][q] = 49;
            }
        }
        for(int i = 400; i <= 600; i++){ //a 100 tile radius circle of purple tiles is carved out, this is the arena
            for(int q = 400; q <= 600; q++){
                if((500 - i)*(500-i) + (500-q)*(500-q) < 100*100) k[i][q] = 48;
            }
        }

        for(int i = 0; i < 16; i++){ //16 pillars ring the arena
            int x = 500 + (int)(70*c(i * Math.PI/8));
            int y = 500 + (int)(70*s(i * Math.PI/8));
            //System.out.println(x + " " + y);
            k[x+3][y] = k[x+3][y-1] = k[x+3][y+1] = k[x-3][y] = k[x-3][y-1] = k[x-3][y+1] = 46;
            k[x][y+3] = k[x-1][y+3] = k[x+1][y+3] = k[x][y-3] = k[x+1][y-3] = k[x-1][y-3] = 46;
            k[x+2][y+2] = k[x-2][y+2] = k[x+2][y-2] = k[x-2][y-2] = 46;
        }

        for(int i = 495; i <= 505; i++){ //a small circle of black signifies the spawning area of the boss
            for(int q = 495; q <= 505; q++){
                if((500-i)*(500-i) + (500-q)*(500-q) < 5*5) k[i][q] = 47;
            }
        }
        return k;
    }

    public static int[][] openPortal(int[][] ti){
        portalOpened = true;
        for(int i = 990; i < 1000; i++){
            for(int q = 990; q < 1000; q++){
                ti[i][q] = 45;
            }
        }
        ti[994][995] = ti[994][994] = ti[995][995] = ti[995][994] = 43;
        return ti;
    }

    public static boolean collideEnemies(Projectile proj, Queue<Enemy> eList, HashMap<String, EnemyStat> eTypes, Player player, Queue<Pair<Pair<Integer, Integer>, Pair<Item, Item>>> bagList){
        //the method that tests if a player's projectile hits an enemy
        //it iterates through every enemy and checks if the projectile hits them
        //returns false if the projectile hits something, and true otherwise
        int eSize = eList.size();
        for(int i = 0; i < eSize; i++){
            Enemy cEn = eList.remove();
            EnemyStat cEnS = eTypes.get(cEn.getName());
            int cEnSize = cEnS.size/2;
            //all projectiles are points, and enemies have square hitboxes
            //it becomes relatively trivial to check for collisions on any given enemy and projectile
            if(proj.getX() <= cEn.getX() + cEnSize && proj.getX() >= cEn.getX() - cEnSize && proj.getY() <= cEn.getY() + cEnSize
                && proj.getY() >= cEn.getY() - cEnSize){
                if(cEn.dmg(proj.getDmg())){ //checking if the enemy dies
                    eList.add(cEn);
                } else {
                    Random r = new Random();
                    if (r.nextInt(1) == 1) {
                        if (r.nextInt(1) == 1) {
                            bagList.add(new Pair(new Pair(cEn.getX(), cEn.getY()), new Pair(new Item(eTypes.get(cEn.getName()).dropMod, player), new Item(eTypes.get(cEn.getName()).dropMod, player))));
                        } else {
                            bagList.add(new Pair(new Pair(cEn.getX(), cEn.getY()), new Pair(new Item(eTypes.get(cEn.getName()).dropMod, player), null)));

                        }
                    } 
                }
                return false;
            }
            else{
                eList.add(cEn);
            }
        }
        return true;
    }

    public static boolean collidePlayer(Projectile proj, Player play){
        //collidePlayer checks for projectiles that collider with the player
        //it works in much the same way as the above method, except there is only 1 player instead of potentially many enemies
        if(proj.getX() <= Player.THICK/2 + play.getX() && proj.getX() >= play.getX() - Player.THICK/2 &&
            proj.getY() <= play.getY() + Player.THICK/2 && proj.getY() >= play.getY() - Player.THICK/2){
            play.dmg(proj.getDmg());
            return false;
        }
        return true;
    }
    
    public static Pair<Integer, Integer> collideSetPiece(int px, int py, int potx, int poty, int[][]tiles){
        //tests for entity collision with setpieces (trees, cactuses, etc.)
        //hitboxes against hitboxes are very wierd to do for collisions, though they were doable in naviturd
        //however, when one hitbox can rotate at any time it creates many complications (what happens when you're 1 pixel away from a setpiece and you rotate?)
        //so we set the entity hitbox in this case to be a point centered on the entity
        //the entity position passed in is (px, py)
        //the amount the entity moves can be represented as (potx,poty), as the total movement can be split into x and y components
        //tiles of course is the tile map
        //the method returns the amount in the x and y directions you can go as a pair
        int pxtile = px/128, pytile = py/128;

        ArrayList<Pair<Integer, Integer>> spieces = new ArrayList<>();

        for(int i = bder(pxtile-2); i <= bder(pxtile+2); i++){ //find all tiles that the entity can possibly collide with
            for(int q = bder(pytile-2); q <= bder(pytile+2); q++){ //we are reasonably sure that the entity does not go so fast as to jump 2 tiles between ticks
                if(tiles[i][q] == 2 || tiles[i][q] == 4 || tiles[i][q] == 6 || tiles[i][q] == 9 || tiles[i][q] == 10 || tiles[i][q] == 12 || tiles[i][q] == 23 || tiles[i][q] == 33){
                    if(pxtile == i && pytile == q) return new Pair(potx,poty);
                    spieces.add(new Pair(i,q));
                }
            }
        }

        double sx = px, sy = py, pox = potx, poy = poty;
        double iters = -1;
        if(spieces.isEmpty()) return new Pair(potx,poty); //if there is no setpiece nearby then the entity can simply move the full distance
        int fl = 0;
        for(int i = 0; i < 200; i++){ //at this point we attempt to edge the entity towards the end location in increments of 1/200 of the total distance
            double xco = sx + pox * i/200, yco = sy + poy * i/200;
            double oxco = sx + pox *(i-1)/22, oyco = sy + poy * (i-1)/200;
            boolean flag = false;
            for(Pair<Integer, Integer> q : spieces){ //check if the new position after edging the entity is stuck in a setpiece
                if(128*q.f <= xco && xco <= 128*q.f+128 && 128*q.s <= yco && yco <= 128*q.s+128){
                    sx += pox * (bder(i-1))/200;
                    sy += poy * (bder(i-1))/200;
                    flag = true;
                    break;
                }
            }
            if(flag) break;
            iters += 1;
        }

        if(iters >= 199){ //if the past loop ran its full course then there was no obstacle and nothing happened
            return new Pair(potx,poty);
        }
        
        //if the method is still going here then the entity got stuck in a block before completing its full transit
        //it can either still go some distance in the x direction, y direction, or neither
        //it will never be both because otherwise the entity would've been able to move further in the target direction

        for(int i = (int)iters; i < 200; i++){ //attempting to edge the entity in the y direction
            double yco = sy + poy/200;
            boolean flag = false;
            for(Pair<Integer, Integer> cTile : spieces){
                if(cTile.s*128 <= yco && yco <= cTile.s*128 + 128 && cTile.f*128 <= sx && sx <= cTile.f*128 + 128){
                    flag = true;
                    break;
                }
            }
            if(flag) break;
            sy += poy/200;
        }
        for(int i = (int)iters; i < 200; i++){ //attempting to edge the entity in the x direction
            double xco = sx + pox/200;
            boolean flag = false;
            for(Pair<Integer, Integer> cTile : spieces){
                if(cTile.f*128 <= xco && xco <= cTile.f*128 + 128 && cTile.s*128 <= sy && sy <= cTile.s*128 + 128){
                    flag = true;
                    break;
                }
            }
            if(flag) break;
            sx += pox/200;
        }
        sx -= px;
        sy -= py;
        return new Pair((int)sx,(int)sy); //return the final result
    }
    
    public static Pair<Integer, Integer> collideSetPiece(Player play, int potx, int poty, int[][]tiles){
        //this overloaded method calls the above method on the player
        if(potx == 0 && poty == 0) return new Pair(0,0);
        int px = play.getX(), py = play.getY();
        return collideSetPiece(px, py, potx, poty, tiles);
    }
    
    public static Pair<Integer, Integer> collideSetPiece(Enemy enem, int potx, int poty, int[][]tiles){
        //this overloaded method calls the original method on the enemy
        if(potx == 0 && poty == 0) return new Pair(0,0);
        return collideSetPiece(enem.getX(), enem.getY(), potx, poty, tiles);
    }

    public static Pair<Integer, Integer> collideSetPiece(MeleeEnemy enem, int potx, int poty, int[][]tiles){
        //this overloaded method calls the original method on a melee enemy
        if(potx == 0 && poty == 0) return new Pair(0,0);
        return collideSetPiece(enem.getX(), enem.getY(), potx, poty, tiles);
    }

    public static Pair<Integer, Integer> collideSetPiece(RangedEnemy enem, int potx, int poty, int[][]tiles){
        //this overloaded method calls the original method on a ranged enemy
        if(potx == 0 && poty == 0) return new Pair(0,0);
        return collideSetPiece(enem.getX(), enem.getY(), potx, poty, tiles);
    }



    public static double c (double ang) {return Math.cos(ang);} //easy cos
    public static double s (double ang) {return Math.sin(ang);} //easy sin
    public static int bder (int num) {return Math.max(0, Math.min(999, num));} //forcibly moves a number between 0 and 999, useful because the indices of the map itself go from 0 to 999
    public static int neg (double num){ //returns 1 or-1 based on negativity of the number
        if(num >= 0) return 1;
        return -1;
    }
    public static int dist(int x, int y, int a, int b) {return (int)(Math.pow(Math.pow((x - a), 2) + Math.pow((y - b), 2), 0.5));}   
    public static int getWorld() {return world;}
}