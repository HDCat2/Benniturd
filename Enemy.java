
/*
Enemy.java
Henning Jiang, Matthew Mckewan, Bennett Grenier
This class implements some methods for enemies
*/
public class Enemy {
	//The enemy class is never directly used, however MeleeEnemy, RangedEnemy, and BossEnemy all inherit from this class
	protected int x, y, hp, maxHp, hitnum, lastHit, range, stunned;
	protected double spd;
	protected String type, name;
	protected Projectile stored;

	public boolean dmg(int dmg) { //take damage
		hp -= dmg;
		if (hp <= 0) {
			Music.playFX("Game FX/enemy_dies.wav");
			return false;
		}

		return true;
	}

	public boolean exists(int px, int py) { //despawn enemy if the distance between the enemy and the player is high enough
		if(Algs.dist(x, y, px, py) > 3000) {
			return false;
		}
		return true;
	}

	public void hit(int tick) {
		hitnum++;
		lastHit = tick;
	}

	public int getX() {return x;} //getters
	public int getY() {return y;}
	public int getRange() {return range;}
	public int gethithum() {return hitnum;}
	public int getLastHit() {return lastHit;}
	public int getStunned() {return stunned;}
	public String getType() {return type;}
	public String getName() {return name;}

	public double percHp() { //method to help with drawing hp bars
		return (double)hp / (double)maxHp;
	}

	public void addStun(int duration){ //handle stuns
		stunned += duration;
	}

	public void move (int px, int py, int[][] tiles) {} //these are expounded upon in the classes that inherit from Enemy
	public boolean canHit(int px, int py) {return false;}
	
}

