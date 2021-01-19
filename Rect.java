import java.util.*;
import java.awt.*;

public class Rect { //our rectangle class, created in a facsimile of pygame's rects
    
    //in our game, all hitboxes are rendered as rectangles

    private int x,y,w,h; //variables in the same form as pygame
    
    public Rect(int xin, int yin, int win, int hin) { //constructor
        x = xin;
        y = yin;
        w = win;
        h = hin;
    }

    public double dist(int x, int y, int a, int b) { //distance between 2 points
        return Math.pow(Math.pow((x - a), 2) + Math.pow((y - b), 2), 0.5);
    }
    
    public boolean collidepoint(Point p) { //checks if a point is within the rectangle
        double ox = p.getX(), oy = p.getY();
  
        if (ox >= x && ox <= x + w && oy >= y && oy <= y + h) return true;
        
        return false;
    }

    public boolean colliderect(Rect rect) { //checks if a rectangle collides with another rectangle
        if (collidepoint(new Point(rect.x, rect.y)) ||
            collidepoint(new Point(rect.x, rect.y + rect.h)) ||
            collidepoint(new Point(rect.x + rect.w, rect.y)) ||
            collidepoint(new Point(rect.x + rect.w, rect.y + rect.h))) {
                return true;    
        }
        return false;
    }

    public boolean collidecircle(int a, int b, int r) { //checks if a rectangle collides with a circle
        int fx, fy;
        if (a >= x + w) { fx = x + w; } 
        else if (a <= x) {fx = x;}
        else {fx = a;}

        if (b >= y + h) { fy = y + h; } 
        else if (b <= y) {fy = y;}
        else {fy = b;}

        if (dist(a, b, fx, fy) <= r) {
            return true;
        }

        return false;
    }
    
}