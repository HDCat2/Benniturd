import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Random;


public class Main extends JFrame implements ActionListener,KeyListener{
	Timer myTimer;
	
	int myTick = 100;

	boolean playing = false;

	CardLayout cLayout = new CardLayout();
	GamePanel game;
	JPanel cards, dead, title, victory, choose;
	ArrayList<JButton> bList;

    public Main() {
		super("Benneturd");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200,860);

		myTimer = new Timer(10, this);	 // trigger every 10 ms
		myTimer.start();

		bList = new ArrayList<JButton>();

		game = new GamePanel();

		dead = new JPanel();
		dead.setLayout(null);

		addButton("Menu", dead, bList, 25, 740, 250, 60, Color.WHITE, 30, "Helvetica", Color.BLACK, this);
		addLabel("YOU DIED", dead, 240, 400, 800, 300, 150, "Helvetica", Color.RED);
		addImage("pics/death.jpg", dead, 350, 0, 500, 860);
		addImage("pics/black.png", dead, 0, 0, 1200, 860);

		title = new JPanel();
		title.setLayout(null);

		addImage("pics/play.png", title, 500, 600, 150, 150);
		addButton("", title, bList, 430, 600, 300, 125, Color.BLACK, 30, "Helvetica", new Color(187, 191, 58), this);
		addImage("pics/logo.png", title, 0, 20, 1200, 200);
		addImage("pics/titleIm.gif", title, 0, 0, 1200, 860);

		victory = new JPanel();
		victory.setLayout(null);

		addButton("Menu", victory, bList, 25, 740, 250, 60, Color.BLACK, 30, "Helvetica", new Color(187, 191, 58), this);
		addImage("pics/victory.png", victory, 100, 50, 1000, 500);
		addImage("pics/crown.png", victory, 390, 400, 384, 384);
		addImage("pics/victoryPic.jpg", victory, 0, 0, 1200, 860);

		choose = new JPanel();
		choose.setLayout(null);

		addButton("Warrior", choose, bList, 100, 100, 450, 280, Color.BLACK, 30, "Helvetica", new Color(187, 191, 58), this);
		addButton("Bowman", choose, bList, 650, 100, 450, 280, Color.BLACK, 30, "Helvetica", new Color(187, 191, 58), this);
		addButton("Mage", choose, bList, 100, 480, 450, 280, Color.BLACK, 30, "Helvetica", new Color(187, 191, 58), this);
		addButton("Automaton", choose, bList, 650, 480, 450, 280, Color.BLACK, 30, "Helvetica", new Color(187, 191, 58), this);
		addImage("pics/titleIm.gif", choose, 0, 0, 1200, 860);

		cards = new JPanel(cLayout);
		cards.add(game, "game");
		cards.add(dead, "dead");
		cards.add(title, "title");
		cards.add(victory, "victory");
		cards.add(choose, "choose");

		cLayout.show(cards,"title");
		Music.playMusic("songs/title.wav");

		add(cards);
		addKeyListener(this);
		setResizable(false);
		setVisible(true);
    }

	public void actionPerformed(ActionEvent evt){
		Object source = evt.getSource();

		if(game != null){
			if(source == myTimer){
				++myTick;
			}
			game.refresh(myTick);
			game.repaint();
		}
		if (bList.size() >= 7) {
			if(source == bList.get(0)){ //if the 'menu' button is pressed after death
			Music.playMusic("songs/title.wav");
		    cLayout.show(cards,"Title");
			myTimer.start();
			}
			if(source == bList.get(1)){ //if the 'play' button is pressed
			    cLayout.show(cards,"choose");
			}
			if(source == bList.get(2)){ //if the 'menu' button is pressed after victory
				Music.playMusic("songs/title.wav");
			    cLayout.show(cards,"Title");
				myTimer.start();		
			}

			if(source == bList.get(3)) { //the next 4 if's are for choosing your warrior
				cLayout.show(cards,"game");
				game.reset("knight");
				myTimer.start();
			    game.requestFocus();
			    game.requestFocusInWindow();
			    playing = true;
			}

			if(source == bList.get(4)) {
				cLayout.show(cards,"game");
				game.reset("archer");
				myTimer.start();
			    game.requestFocus();
			    game.requestFocusInWindow();
			    playing = true;

			}

			if(source == bList.get(5)) {
				cLayout.show(cards,"game");
				game.reset("mage");
				myTimer.start();
			    game.requestFocus();
			    game.requestFocusInWindow();
			    playing = true;
			    
			}

			if(source == bList.get(6)) {
				cLayout.show(cards,"game");
				game.reset("automaton");
				myTimer.start();
			    game.requestFocus();
			    game.requestFocusInWindow();
			    playing = true;
			}
		}

		if (playing && game.dead()) {
			cLayout.show(cards, "dead");
			playing = false;
		}

		if (playing && game.victory()) {
			cLayout.show(cards, "victory");
			playing = false;
		}
		
	}


	public static void addImage(String fname, Container container, int x, int y, int w, int h) { //adds an image
    	ImageIcon icon = new ImageIcon(fname);
		JLabel label = new JLabel(icon);
		label.setSize(w,h);
		label.setLocation(x,y);
		container.add(label);
    }

    public static void addJImage(String fname, Container container, int x, int y, int w, int h) { //adds an image
		ImagePanel label = new ImagePanel(fname, w, h);
		label.setLocation(x,y);
		container.add(label);
    }

    private static void addLabel(String text, Container container, int x, int y, int w, int h, int fontSize, String typeFace, Color textCol) { //adds a label
        JLabel label = new JLabel(text);
        label.setFont(new Font(typeFace, Font.PLAIN, fontSize));
        label.setForeground(textCol);
        label.setSize(w,h);
		label.setLocation(x, y);

        container.add(label);
    }

    private static void addButton(String text, Container container, ArrayList<JButton> blist, int x, int y, int w, int h, Color butCol,
    								 int fontSize, String typeFace, Color textCol, Main m) { //adds buttons
        JButton button = new JButton(text);
        button.setFont(new Font(typeFace, Font.PLAIN, fontSize));
        button.setForeground(textCol);
        button.setBackground(butCol);
        button.addActionListener(m);

		button.setSize(w, h);
		button.setLocation(x, y);

        container.add(button);
        blist.add(button);
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
    	game.setKey(e.getKeyCode(),true);
    }

    public void keyReleased(KeyEvent e) {
    	game.setKey(e.getKeyCode(),false);
    }

    public static void main(String[] arguments) {
		Main frame = new Main();
    }
}

class GamePanel extends JPanel implements MouseListener, KeyListener{
	private static final int lockx = 450;
	private static final int locky = 475;

	private boolean []keys;
	private Image pic;
	private BufferedImage spic, mountPic;
	private Player player;
	private int potX, potY;
	private double ang;
	private ArrayList<int[]> visibleTiles;
	private int globaltick;
	private boolean aboveWater;
	private ArrayList<int[][]> worlds;

	private SpriteMask sm;
	private boolean inMotion;
	private boolean sKey = false, uKey = false, oKey = false;

	private int px, py;
	private int mx, my;
	private Queue<Projectile> projList;	
	private Image projpic;

	private Queue<Enemy> enemyList;
	private HashMap<String, EnemyStat> enemyTypes;
	private HashMap<String, Image[][]> enemyFrames;
	private Queue<Projectile> enemyProj;
	private HashMap<String, BufferedImage> enemyProjPics;

	private int[][] tiles;
	private Image[] tilesI;
	private BufferedImage[] obsI;
	private BufferedImage colMap;
	private Color[] cols;
	private String lastBiome;

	private BufferedImage invPic;
	private ArrayList<Pair<Pair<Integer, Integer>, Pair<Item, Item>>> bagList;
	private Image bagPic = new ImageIcon("pics/bag.png").getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT);

	public boolean victory = false, dead = false;


	public GamePanel(){
		addMouseListener(this);
		addKeyListener(this);
		
		keys = new boolean[KeyEvent.KEY_LAST + 1];
		try {spic = ImageIO.read(new File("pics/tiles/grass-grass-path.png"));
			 mountPic = ImageIO.read(new File("pics/tiles/mountpic.png"));} 
		catch(IOException e) {}
		spic = scaleBuffered(spic,2);
		mountPic = scaleBuffered(mountPic,4);

		player = new Player("automaton");
		potX = 0;
		potY = 0;
		ang = 0.0;
		setSize(1200, 860);

		sm = new SpriteMask();

		player.initStats();

		inMotion = false;

		projList = new LinkedList<Projectile>();

		worlds = new ArrayList<>();
		worlds.add(Algs.genTilesO());
		worlds.add(Algs.genTilesS());
		worlds.add(Algs.genTilesU());
		worlds.add(Algs.genTilesB());

		lastBiome = "";
		tiles = worlds.get(0);
		tilesI = Fetcher.getTiles();
		obsI = Fetcher.getObs();
		cols = new Color[] {Color.GREEN, //1
							Color.BLACK, 
							Color.YELLOW, 
							Color.BLACK, 
							new Color(0, 255, 255),
							Color.BLACK, 
							Color.BLUE, 
							new Color(243,253,105),
							new Color(100, 100, 100), 
							Color.RED, //10
							Color.RED,
							new Color(0,255,255), 
							new Color(0,255,255),
							new Color(90,90,10), 
							Color.WHITE, 
							new Color(200,50,200),
							null,
							null,
							null,
							null,//20
							Color.WHITE,
							new Color(128,128,128),
							new Color(0,255,255),
							Color.BLUE,
							null,
							null,
							null,
							null,
							null,
							new Color(165,50,50), //30
							Color.RED,
							new Color(255,128,0),
							Color.BLACK,
							Color.BLUE,
							Color.BLACK,
							Color.YELLOW,
							Color.YELLOW,
							Color.YELLOW,
							Color.BLACK,
							Color.YELLOW, //40
							Color.BLACK,
							new Color(0,100,0),
							new Color(148,43,226),
							Color.BLACK,
							new Color(255,0,255),
							new Color(153,50,204),
							Color.BLACK,
							new Color(75,0,130),
							new Color(0,0,64)};


		colMap = getMap(tiles);
		

		enemyList = new LinkedList<Enemy>();
		enemyTypes = Fetcher.getEnemies();
		enemyFrames = Fetcher.getEnemySprites(enemyTypes);
		enemyProj = new LinkedList<Projectile>(); 
		enemyProjPics = Fetcher.getEnemyImages(enemyTypes);

		EnemyStat e = enemyTypes.get("scorpion");
		enemyList.add(new RangedEnemy(200, 200, e.hp, e.range, e.spd, "scorpion"));

		invPic = getInvPic();
		bagList = new ArrayList<Pair<Pair<Integer, Integer>, Pair<Item, Item>>> ();

	}

	public void reset(String inClass) {
		addMouseListener(this);
		addKeyListener(this);
		
		keys = new boolean[KeyEvent.KEY_LAST + 1];
		try {spic = ImageIO.read(new File("pics/tiles/grass-grass-path.png"));
			 mountPic = ImageIO.read(new File("pics/tiles/mountpic.png"));} 
		catch(IOException e) {}
		spic = scaleBuffered(spic,2);
		mountPic = scaleBuffered(mountPic,4);

		player = new Player(inClass);
		potX = 0;
		potY = 0;
		ang = 0.0;
		setSize(1200, 860);

		sm = new SpriteMask();

		player.initStats();

		inMotion = false;

		projList = new LinkedList<Projectile>();

		worlds = new ArrayList<>();
		worlds.add(Algs.genTilesO());
		worlds.add(Algs.genTilesS());
		worlds.add(Algs.genTilesU());
		worlds.add(Algs.genTilesB());

		lastBiome = "";
		tiles = worlds.get(0);
		tilesI = Fetcher.getTiles();
		obsI = Fetcher.getObs();
		cols = new Color[] {Color.GREEN, //1
							Color.BLACK, 
							Color.YELLOW, 
							Color.BLACK, 
							new Color(0, 255, 255),
							Color.BLACK, 
							Color.BLUE, 
							new Color(243,253,105),
							new Color(100, 100, 100), 
							Color.RED, //10
							Color.RED,
							new Color(0,255,255), 
							new Color(0,255,255),
							new Color(90,90,10), 
							Color.WHITE, 
							new Color(200,50,200),
							null,
							null,
							null,
							null,//20
							Color.WHITE,
							new Color(128,128,128),
							new Color(0,255,255),
							Color.BLUE,
							null,
							null,
							null,
							null,
							null,
							new Color(165,50,50), //30
							Color.RED,
							new Color(255,128,0),
							Color.BLACK,
							Color.BLUE,
							Color.BLACK,
							Color.YELLOW,
							Color.YELLOW,
							Color.YELLOW,
							Color.BLACK,
							Color.YELLOW, //40
							Color.BLACK,
							new Color(0,100,0),
							new Color(148,43,226),
							Color.BLACK,
							new Color(255,0,255),
							new Color(153,50,204),
							Color.BLACK,
							new Color(75,0,130),
							new Color(0,0,64)};


		colMap = getMap(tiles);
		

		enemyList = new LinkedList<Enemy>();
		enemyTypes = Fetcher.getEnemies();
		enemyFrames = Fetcher.getEnemySprites(enemyTypes);
		enemyProj = new LinkedList<Projectile>(); 
		enemyProjPics = Fetcher.getEnemyImages(enemyTypes);

		EnemyStat e = enemyTypes.get("scorpion");
		enemyList.add(new RangedEnemy(200, 200, e.hp, e.range, e.spd, "scorpion"));

		invPic = getInvPic();
		bagList = new ArrayList<Pair<Pair<Integer, Integer>, Pair<Item, Item>>> ();

	}


    public void setKey(int k, boolean v) {
    	keys[k] = v;
    }

    public boolean victory() {return victory;}
    public boolean dead() {return dead;}

	public void refresh(int tick){
		aboveWater = true;
		inMotion = false;
		globaltick = tick;
		potX = 0;
		potY = 0;

		if(keys[KeyEvent.VK_Q] ){
			ang += Math.PI/30;
		}
		if(keys[KeyEvent.VK_E] ){
			ang -= Math.PI/30;
		}
		if(keys[KeyEvent.VK_X] ){
			ang = 0;
		}
		if(keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W]){
			potY -=player.getSpd();
			inMotion = true;
		}
		if(keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S]){
			potY += player.getSpd();
			inMotion = true;
		}
		if(keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D]){
			potX += player.getSpd();
			inMotion = true;
		}
		if(keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A]){
			potX -= player.getSpd();
			inMotion = true;
		}
		if(keys[KeyEvent.VK_SPACE]){
			if(globaltick - player.getLastUsed() > 200){
				handleAbility();
				Music.playFX("Game FX/use_ability.wav");
			}
		}
		if (calcTile(player.getX(), player.getY()) == 7) {
			aboveWater = false;
		}
		if (!aboveWater) {
			potX /= 2;
			potY /= 2;
		}
		Pair<Integer, Integer> tryMove = player.calcMove(potX, potY, ang);
		potX = tryMove.f;
		potY = tryMove.s;
		Pair<Integer, Integer> movement = Algs.collideSetPiece(player, potX, potY, tiles);
    	player.move(movement.f, movement.s);
	}

    public void paintComponent(Graphics g){
    	px = player.getX();
    	py = player.getY();
    	if (globaltick % 10 == 0) {player.regen();}
    	
		if (calcTile(px, py) == 13) {
			player.move(0,384);
			tiles = worlds.get(1);
			Music.playFX("Game FX/enter_portal.wav");
			enemyList = new LinkedList<Enemy>();
			bagList = new ArrayList<Pair<Pair<Integer, Integer>, Pair<Item, Item>>> ();
			colMap = getMap(tiles);	
		}

    	else if (calcTile(px,py) == 24 || calcTile(px,py) == 34) {
    		player.move(0,384);
    		tiles = worlds.get(0);
    		Music.playFX("Game FX/enter_portal.wav");
    		enemyList = new LinkedList<Enemy>();
			bagList = new ArrayList<Pair<Pair<Integer, Integer>, Pair<Item, Item>>> ();
    		colMap = getMap(tiles);
    	}

    	else if (calcTile(px,py) == 11){
    		player.move(0,384);
    		tiles = worlds.get(2);
    		Music.playFX("Game FX/enter_portal.wav");
    		enemyList = new LinkedList<Enemy>();
			bagList = new ArrayList<Pair<Pair<Integer, Integer>, Pair<Item, Item>>> ();
    		colMap = getMap(tiles);
    	}

    	else if (calcTile(px,py) == 43){
    		player.move(-63200,-63200);
    		tiles = worlds.get(3);
    		Music.playFX("Game FX/enter_portal.wav");
    		enemyList = new LinkedList<Enemy>();
			bagList = new ArrayList<Pair<Pair<Integer, Integer>, Pair<Item, Item>>> ();
    		colMap = getMap(tiles);
    	}

    	if(calcTile(px,py) == 38 && !sKey){
    		sKey = true;
    		Music.playFX("Game FX/key_collected.wav");
    	}
    	if(calcTile(px,py) == 40 && !uKey){
    		uKey = true;
    		Music.playFX("Game FX/key_collected.wav");
    	}
    	if(calcTile(px,py) == 42 && !oKey){
    		oKey = true;
    		Music.playFX("Game FX/key_collected.wav");
    	}

    	visibleTiles = Algs.calcTiles(player.getX(), player.getY(), ang);

		drawBack(g, px, py);

		int walkFrame = 0;
		int walkcoeff = 3;
		if (inMotion) {
	    	walkFrame = globaltick % (4 * walkcoeff);
	    	if (walkFrame / walkcoeff == 3) {
	    		walkFrame = 2;
	    	} else if (walkFrame / walkcoeff == 2) {
	    		walkFrame = 0;
	    	} else {
	    		walkFrame = walkFrame / walkcoeff;
	    	}
	    	if (walkFrame == 0 * walkcoeff || walkFrame == 2 * walkcoeff) {
	    		if (aboveWater) {
	    			Music.playFX("Game FX/step.wav");
	    		} else {
	    			Music.playFX("Game FX/water_step.wav");
	    		}
	    	}
	    }

	    int atkFrame = 0;
	    int atkcoeff = 1;
	    if (globaltick - player.getLastHit() <= 3 * atkcoeff) {
	    	atkFrame = (globaltick - player.getLastHit()) % (3 * atkcoeff) / atkcoeff + 1;
	    	if (atkFrame == 3) {
	    		atkFrame = 1;
	    	}
	    }

		pic = Toolkit.getDefaultToolkit().createImage(sm.coat(player.getInv(), player.getPClass(), walkFrame, atkFrame, true, aboveWater, "").getScaledInstance(80, 80, Image.SCALE_DEFAULT).getSource());
		g.drawImage(pic, lockx - player.THICK/2, locky - player.THICK/2, this);


		doProj(g, px, py);
		doEnemies(g, px, py);
		doEnemyProj(g, px, py);

		drawUI(g, px, py);

		handleSpawns(px, py);
	
		testForPlay(px, py);
    }

    public void drawBack (Graphics g, int px, int py) {

    	int[] bounds = visibleTiles.get(visibleTiles.size() - 1);
    	visibleTiles.remove(visibleTiles.size() - 1);

    	int minX = bounds[0];
    	int maxX = bounds[1];
    	int minY = bounds[2];
    	int maxY = bounds[3];

    	BufferedImage bimage = new BufferedImage((maxX - minX + 1) * 128, (maxY - minY + 1) * 128, BufferedImage.TYPE_BYTE_INDEXED);
    	Graphics2D g2d = bimage.createGraphics();    

    	for (int[] pos : visibleTiles) {
    		
    		if (pos[0] < 0 || pos[1] < 0 || pos[0] > 999 || pos[1] > 999) {
    			g2d.setColor(Color.BLUE);
    			g2d.drawImage(mountPic, (pos[0] - minX) * 128, (pos[1] - minY) * 128, this);
    		} else {
 
    			Image toPut = tilesI[tiles[pos[0]][pos[1]] - 1];
    			g2d.drawImage(toPut, (pos[0] - minX) * 128, (pos[1] - minY) * 128, this);
    			
    		}
    	}

		Graphics2D g2D = (Graphics2D)g;
		AffineTransform saveXform = g2D.getTransform();
		
		AffineTransform at = new AffineTransform();;
		at.rotate(ang, lockx, locky);
		g2D.transform(at);
		g2D.drawImage(bimage,minX * 128 - px + lockx, minY * 128 - py + locky,this);
		
		g2D.setTransform(saveXform);

		for (int[] pos : visibleTiles) {
			if (!(pos[0] < 0 || pos[1] < 0 || pos[0] > 999 || pos[1] > 999)) {
				if (tiles[pos[0]][pos[1]] == 2 || tiles[pos[0]][pos[1]] == 4 || tiles[pos[0]][pos[1]] == 6) {
					BufferedImage obsPut = obsI[tiles[pos[0]][pos[1]] - 1];
					Pair<Integer, Integer> putting = placeImage(obsPut, pos[0] * 128 + 64, pos[1] * 128 + 64, px, py, 128);
					g.drawImage(obsPut, putting.f, putting.s, null);
	    		}
	    	}
		}
    }

    public void doProj(Graphics g, int px, int py) {
    	BufferedImage baseImage = scaleBuffered(sm.coat(player.getInv(), player.getPClass(), 0, 0, false, true, ""), 2);
    	for (int i = 0; i < projList.size(); i++) {
    		boolean addb = true;
    		Projectile p = projList.poll();
    		if (p.move()) {
    			double dis = Math.hypot(p.getX() - px, p.getY() - py);
    			double pAng = Math.PI/2 - Math.atan2(p.getX() - px, p.getY() - py) + ang;

    			BufferedImage pImage = rotateBuffered(baseImage, p.getTraj() + Math.PI/2 + ang, 32, 32);

    			int putx = (int)(Math.cos(pAng) * dis);
    			int puty = (int)(Math.sin(pAng) * dis);;
    			g.drawImage(pImage, lockx + putx - 32, locky + puty - 32, null);

    			//RIGHT HERE
    			addb = Algs.collideEnemies(p, enemyList, enemyTypes, player, bagList);
    			
    			if (addb) {
    				projList.add(p);
    			}
    		}

    	}
    }

    public void doEnemies(Graphics g, int px, int py) {

    	Enemy e;
    	for (int i = 0; i < enemyList.size(); i++) {
    		e = enemyList.poll();
    		boolean hitting;
    		int frame;
    		
    		e.move(px, py, tiles);

    		double dis = Math.hypot(e.getX() - px, e.getY() - py);
    		double pAng = Math.PI/2 - Math.atan2(e.getX() - px, e.getY() - py) + ang;

    		int putx = (int)(Math.cos(pAng) * dis);
    		int puty = (int)(Math.sin(pAng) * dis);

    		EnemyStat stat = enemyTypes.get(e.getName());

    		if (e.canHit(px, py)) {

    			frame = calcFrame(stat, false);
    			g.drawImage(enemyFrames.get(e.getName())[1][frame], lockx + putx - stat.size/2, locky + puty - stat.size/2, null);
    			if (globaltick - e.getLastHit() >= stat.atkspd) {
    				createEnemyProj(e, stat, px, py);

    			}
    		} else {
    			frame = calcFrame(stat, true);
    			g.drawImage(enemyFrames.get(e.getName())[0][frame], lockx + putx - stat.size/2, locky + puty - stat.size/2, null);
    		}

    		g.setColor(Color.RED);
    		g.fillRect(putx + lockx - stat.size / 2, puty + locky - stat.size * 3 / 4, stat.size, stat.size / 8);
    		g.setColor(Color.GREEN);
    		g.fillRect(putx + lockx - stat.size / 2, puty + locky - stat.size * 3 / 4, (int)(stat.size * e.percHp()), stat.size / 8);

    		enemyList.add(e);
    	}

    }
    
    public void doEnemyProj(Graphics g, int px, int py) {
    	for (int i = 0; i < enemyProj.size(); i++) {
    		Projectile p = enemyProj.poll();
    		boolean addb = true; 
    		if (p.move()) {

    			BufferedImage pImage = enemyProjPics.get(p.getSource());
    			Pair<Integer, Integer> putting = placeImage(pImage, p.getX(), p.getY(), px, py, enemyTypes.get(p.getSource()).pSize);
    			int putx = putting.f;
    			int puty = putting.s;
    			
    			drawRotatedBuffered(g, pImage, p.getTraj() + ang, putx, puty);

    			Pair<Boolean, Boolean> result = Algs.collidePlayer(p, player);
    			addb = result.s;
    			dead = result.f;

    			if (addb) {
    				enemyProj.add(p);
    			}
    		}
    	}
    }

    public int calcFrame(EnemyStat stat, boolean walk) {
    	if (walk) {
    		int walkproc = globaltick % ((stat.walkF - 1) * 2 * stat.walkC);
	    	int walkframe = walkproc < (stat.walkF - 1) * stat.walkC ? walkproc / stat.walkC : (stat.walkF - 1) * 2 - walkproc / stat.walkC;
	    	return walkframe;
    	} else {
    		int actproc = globaltick % ((stat.actionF - 1) * 2 * stat.actionC);
	    	int actframe = actproc < (stat.actionF - 1) * stat.actionC ? actproc / stat.actionC : (stat.actionF - 1) * 2 - actproc / stat.actionC;
	    	return actframe;
    	}
    	
    }

    public void createEnemyProj(Enemy e, EnemyStat stat, int px, int py) {
    	if (e.getType().equals("ranged")) {
    		Music.playFX("Game FX/ranged_enemy_fires.wav");
    		Projectile p = new Projectile(e.getX(), e.getY(),e.getX(), e.getY(), Math.PI/2 - Math.atan2(px - e.getX(), py - e.getY()), stat.pRange, stat.pSpd, stat.dmg, e.getName());
    		e.hit(globaltick);
    		enemyProj.add(p);
    	} else if (e.getType().equals("melee")) {
    		player.dmg(enemyTypes.get(e.getName()).dmg);
    		e.hit(globaltick);
    	}
    }

    public void drawUI(Graphics g, int px, int py) {

    	g.setColor(new Color(60, 60, 60));
		g.fillRect(900, 0, 300, 860);
		g.fillRect(0, 650, 1200, 210);
		//rect length 700
		g.setColor(Color.BLACK);
		g.fillRect(25 ,650 + 30, 700 , 40);
		g.fillRect(25 ,650 + 30 + 70, 700 , 40);

		g.setColor(new Color(220, 0, 0));
		g.fillRect(25 ,650 + 30, (int)(player.getHp() / player.getMaxHp() * 700) , 40);
		g.setColor(new Color(0, 187, 255));
		g.fillRect(25 ,650 + 30 + 70, (int)(player.getMp() / player.getMaxMp() * 700) , 40);
		g.setColor(Color.WHITE);
		drawText(g, (int)player.getHp() + " / " + (int)player.getMaxHp(), 30, 320, 680);
		g.setColor(Color.WHITE);
		drawText(g, (int)player.getMp() + " / " + (int)player.getMaxMp(), 30, 320, 747);
		
		BufferedImage miniMap = new BufferedImage(250, 250, BufferedImage.TYPE_BYTE_INDEXED);
		Graphics2D minig = miniMap.createGraphics();
		minig.drawImage(colMap, 125 - px / 64, 125 - py / 64, null);
		g.drawImage(miniMap, 900 + 20, 25, null);

		BufferedImage pointer = null;
		try {pointer = ImageIO.read(new File("pics/pointer.png"));}
		catch (IOException e) {}
		drawRotatedBuffered(g, pointer, - Math.PI / 2 - ang, 900 + 25 + 125 - 8, 25 + 125 - 8);

		g.setColor(Color.BLACK);
		g.fillRect(745, 690, 200, 20);
		g.setColor(new Color(180, 0 ,180));
		g.fillRect(745, 690,  (int)((double)player.getXp() / ((double)player.getLvl() * 100) * 200), 20);

		g.setColor(Color.BLACK);
		g.fillRect(745, 760, 200, 20);
		g.setColor(new Color(0, 0, 255));
		g.fillRect(745, 760,  Math.min(200, globaltick - player.getLastUsed()), 20);

		g.drawImage(invPic, 945, 300, null);


    	for (int i = 0; i < 8; i++) {
    		if (new Rect(945 + 10 + (i % 2) * 90, 300 + 10 + ((i / 2) % 4) * 90, 80, 80).collidepoint(new Point(mx ,my))) {
    			g.drawImage(getTip(player.getInv()[i]), mx - 100, my - 50, null);
    			
    		}
    	}

    	doBags(g, px, py);


    }

    public BufferedImage getMap(int[][] tiles) {
    	BufferedImage out = new BufferedImage(1000, 1000, BufferedImage.TYPE_BYTE_INDEXED);
    	Graphics2D g = out.createGraphics();

    	for (int i = 0; i < 1000; i ++) {
    		for (int j = 0; j < 1000; j++) {
    			g.setColor(cols[tiles[i][j] - 1]);
    			g.fillRect(i, j, 1, 1);
    		}
    	}
    	out = scaleBuffered(out, 2);

    	return out;
    }

    public void doBags(Graphics g, int px, int py) {

    	for (int i = 0; i < bagList.size(); i++) {
    		Pair<Pair<Integer, Integer>, Pair<Item, Item>> bag = bagList.get(i);
    		Pair<Integer, Integer> putting = placeImage(bag.f.f, bag.f.s, px, py, 64);
    		g.drawImage(bagPic, putting.f - 32, putting.s - 32, null);
    		if (new Rect(bag.f.f - 32, bag.f.s - 32, 64, 64).collidepoint(new Point(mx, my))) {
    			g.setColor(Color.WHITE);
    			g.fillRect(945, 700, 190, 100);
    		}
    	}
    	System.out.println(bagList.size());
    }

    public BufferedImage getInvPic() {
    	String[] pieces = new String[] {"primary", "secondary", "helm", "pauldrons", "chest", "gauntlets", "leggings", "boots"};
    	
    	BufferedImage inv = new BufferedImage(190, 370, BufferedImage.TYPE_BYTE_INDEXED);
    	Graphics2D g = inv.createGraphics();
    	g.setColor(new Color(190, 190, 190));
    	g.fillRect(0, 0, 190, 370);
    	g.setColor(Color.WHITE);
    	g.fillRect(5, 0, 185, 365);

    	g.setColor(new Color(60, 60, 60));
    	for (int i = 0; i < 8; i++) {
    		Image pic = Toolkit.getDefaultToolkit().createImage(sm.coat(player.getInv(), player.getPClass(), 0, 2, true, true, pieces[i]).getScaledInstance(64, 64, Image.SCALE_DEFAULT).getSource());
    		g.fillRect(10 + (i % 2) * 90, 10 + ((i / 2) % 4) * 90, 80, 80 );
    		g.drawImage(pic, 10 + (i % 2) * 90 + 8, 10 + ((i / 2) % 4) * 90 + 8, null);
    	}


    	return inv;
    }

    public BufferedImage getTip(Item i) {

    	BufferedImage out = new BufferedImage(100, 50, BufferedImage.TYPE_BYTE_INDEXED);
    	Graphics2D g2d = out.createGraphics();

    	g2d.setColor(new Color(50, 50, 50));
    	g2d.fillRect(0, 0, 100, 50);
    	g2d.setColor(Color.WHITE);
    	drawText(g2d, i.getName(), 10, 2, 2);
    	drawText(g2d, i.getStatNames().f + ": " + i.getStat1(), 10, 2, 15); 
    	if (!i.getStatNames().s.equals("")) {drawText(g2d, i.getStatNames().s + ": " + i.getStat2(), 10, 2, 28); }

    	return out;
    }

    public void testForPlay(int px, int py) {
    	String[] tileBiomes = new String[] {"savannah", "", "desert", "", "tundra", "", "" , "", "", "", 
    										"", "", "savannah", "desert", "tundra", "", "", "" , "", "",
    									    "sky", "sky", "sky", "sky", "sky", "sky", "", "", "", "",
    									    "underworld", "underworld", "underworld", "underworld", "underworld", "underworld", "", "", "" , "",
    									    "", "", "", "", "", "" , "", "", ""};

    	String biome = tileBiomes[calcTile(px, py) - 1];
    	if (isForest(px, py)) { biome = "forest"; }
		if (biome.equals("")) { return; }

		if (!biome.equals(lastBiome)) {
			Music.stopMusic();
			Music.playMusic("songs/" + biome + ".wav");
			lastBiome = biome;
		}
    }
    
    public void handleSpawns(int px, int py) {
    	int tries = 20;
    	int tileRad = 20;
    	if (globaltick % 150 == 0) {
    		for (int i = 0; i < tries; i++) {
    			spawnEnemy(20, px, py);
    		}
    	}
    }

    public void spawnEnemy(int rad, int px, int py) {
    	String[] tileBiomes = new String[] {"savannahh", "", "desert", "", "tundra", "", "" , "", "", "", "", "", "savannah", "desert", "tundra", ""};
    	boolean success = false;
    	int putX = 0; 
    	int putY = 0;
    	String biome = "";
    	int tries = 0;
    	while (!success) {
    		Random r = new Random();
    		double sAng = r.nextDouble() * Math.PI * 2;
    		putX  = px + (int)(Math.cos(sAng) * 20 * 128);
    		putY  = py + (int)(Math.sin(sAng) * 20 * 128);
			int tile = calcTile(putX, putY);
			if (tile == -1) {
				continue;
			}
			biome = tileBiomes[tile - 1];
			if (!(biome.equals(""))) {
				if (isForest(putX, putY)) {
					biome = "forest";
				}
				success = true;
			}
			tries++;
			if (tries > 100) {
				return;
			}
    	}
    	ArrayList<String> suitable = new ArrayList<String>();
    	for (String key : enemyTypes.keySet() ){
			if (enemyTypes.get(key).region.equals(biome)) {
				suitable.add(key);
			}
		}
		if (suitable.size() > 0) {
			Random r = new Random();
			String spawning = suitable.get(r.nextInt(suitable.size()));
			EnemyStat stat = enemyTypes.get(spawning);

			if (r.nextDouble() < (double)stat.chance / 100) {
				if (stat.atkType.equals("ranged")) {
					enemyList.add(new RangedEnemy(putX, putY, stat.hp, stat.range, stat.spd, spawning));
				} else if (stat.atkType.equals("melee")) {
					enemyList.add(new MeleeEnemy(putX, putY, stat.hp, stat.range, stat.spd, spawning));
				}
				
			}
		}
    }

    public boolean isForest(int x, int y) {
    	int tx = x / 128;
    	int ty = y / 128;
    	int trees = 0;
    	for (int i = 0; i < 7; i++) {
    		for (int j = 0; j < 7; j++) {
    			if (tx + i - 3 >= 0 && ty + j - 3 >= 0) {
	    			if (tiles[tx + i - 3][ty + j - 3] == 2) {
	    				trees++;
	    			}
    			}
    		}
    	}
    	if ((double)(trees) / 49 >= 0.13) {
    		return true;
    	}
    	return false;

    }
	
    public Pair<Integer, Integer> placeImage(BufferedImage i, int x, int y, int px, int py, int size) {
    	double dis = Math.hypot(x - px, y - py);
    	double pAng = Math.PI/2 - Math.atan2(x - px, y - py) + ang;

    	int putx = lockx + (int)(Math.cos(pAng) * dis) - size / 2;
    	int puty = locky + (int)(Math.sin(pAng) * dis) - size / 2;

    	return new Pair(putx, puty);
    }

    public Pair<Integer, Integer> placeImage(int x, int y, int px, int py, int size) {
    	double dis = Math.hypot(x - px, y - py);
    	double pAng = Math.PI/2 - Math.atan2(x - px, y - py) + ang;

    	int putx = lockx + (int)(Math.cos(pAng) * dis) - size / 2;
    	int puty = locky + (int)(Math.sin(pAng) * dis) - size / 2;

    	return new Pair(putx, puty);
    }

    public int calcTile(int x, int y) {
    	if (x >= 0 && y >= 0) {
    		return tiles[x / 128][y / 128];
    	}
    	return -1;
    	
    }

    public BufferedImage scaleBuffered(BufferedImage before, double s) {
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage((int)(w * s), (int)(h * s), BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(s,s);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);

		return after;
    }

    public BufferedImage scaleBufferedToSize(BufferedImage before, int width) {
    	return scaleBuffered(before, width / before.getWidth());
    }

    public BufferedImage rotateBuffered(BufferedImage before, double a, int xrot, int yrot) {	
    	
    	AffineTransform tx = AffineTransform.getRotateInstance(a, xrot, yrot);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
	    BufferedImage after = op.filter(before, null);
	    

		return after;
    }

    public void drawRotatedBuffered(Graphics g, BufferedImage bimage, double angle, int x, int y) {

    	Graphics2D g2D = (Graphics2D)g;
    	AffineTransform saveXform = g2D.getTransform();
		
		AffineTransform at = new AffineTransform();
		int w = bimage.getWidth();
		int h = bimage.getHeight();
		at.rotate(angle + Math.PI/2, x + w / 2, y + h / 2);
		g2D.transform(at);
		g2D.drawImage(bimage, x, y, null);
		
		g2D.setTransform(saveXform);
    }
   	public void drawText(Graphics g, String content, int size, int x, int y) {
	    Font font = new Font("Serif", Font.PLAIN, size);
		g.setFont(font);
		FontMetrics fontMetrics = g.getFontMetrics();
		g.drawString(content, x, y + fontMetrics.getAscent());
	}

    public void addProj() {
    	projList.add(new Projectile(px, py, px, py, Math.PI/2 -  Math.atan2(mx - lockx, my - locky) - ang, 300 + player.getInv()[0].getStat2(), 50, player.getInv()[0].getStat1(), "player"));
    	Music.playFX("Game FX/player_firing.wav");
    }

    public void handleAbility(){
    	if(!player.mana(globaltick)) return;
    	if(player.getPClass() == "knight"){
    		for(Enemy i : enemyList){
    			if((i.getX() - player.getX())*(i.getX() - player.getX()) + (i.getY() - player.getY())*(i.getY() - player.getY()) < 1280*1280) i.addStun(player.getLvl() * 80);
    		}
    	}
    	if(player.getPClass() == "mage"){
    		int pp = enemyList.size();
    		for(int q = 0; q < pp; q++){
    			boolean addb = true;
    			Enemy i = enemyList.remove();
    			if((i.getX() - player.getX())*(i.getX() - player.getX()) + (i.getY() - player.getY())*(i.getY() - player.getY()) < 1280*1280){
    				addb = i.dmg(player.getLvl()/2 + 1);
    				player.regen();
    			}
    			if(addb) enemyList.add(i);
    		}
    	}
    	if(player.getPClass() == "archer"){
    		for(int i = 0; i < 16; i++){
    			projList.add(new Projectile(px,py,px,py,i * Math.PI/8,1280, 20, player.getLvl()*5, "player"));
    		}
    	}
    	if(player.getPClass() == "automaton"){
    		int pp = enemyList.size();
    		for(int q = 0; q < pp; q++){
    			boolean addb = true;
    			Enemy i = enemyList.remove();
    			if((i.getX() - player.getX())*(i.getX() - player.getX()) + (i.getY() - player.getY())*(i.getY() - player.getY()) < 1280*1280){
    				addb = i.dmg(player.getLvl() + 3);
    			}
    			if(addb) enemyList.add(i);
    		}
    	}
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e){
    	mx = e.getX();
		my = e.getY();
    	if (globaltick - player.getLastHit() > player.getAtkSpeed()) {
    		addProj();
    		player.swing(globaltick);
    	}
	}

	public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
    	setKey(e.getKeyCode(),true);
    }

    public void keyReleased(KeyEvent e) {
    	setKey(e.getKeyCode(),false);
    }

}