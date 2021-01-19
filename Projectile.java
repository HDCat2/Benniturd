
/*
Projectile.java
Henning Jiang, Matthew Mckewan, Bennett Grenier
This program implements projectiles
*/

public class Projectile {
	//projectiles
	private int sx, sy ,r, v, dmg;
	private double traj, x, y;
	private String source;

	public Projectile(int xin, int yin, int startx, int starty, double trajectory, int range, int vel, int dmg, String s) {
		x = xin;
		y = yin;
		sx = startx;
		sy = starty;
		traj = trajectory;
		r = range;
		v = vel;
		this.dmg = dmg;
		source = s;

	}

	public int getX() {return (int)x;}
	public int getY() {return (int)y;}
	public int getDmg() {return dmg;}
	public double getTraj() {return traj;}
	public String getSource() {return source;}

	public boolean move() {
		x += Math.cos(traj) * v;
		y += Math.sin(traj) * v;

		if (Math.hypot(x - sx, y - sy) >= r) {
			return false;
		}
		if (x < 0 || y < 0) {
			return false;
		}
		return true;
	}
}
