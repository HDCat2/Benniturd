
/*
RangedEnemy.java
Henning Jiang, Matthew Mckewan, Bennett Grenier
This program implements the ranged archetype of enemies
*/
public class RangedEnemy extends Enemy {
	//Ranged enemies attempt to walk into range, at which they then fire projectiles at the player
	public RangedEnemy(int x, int y, int hp, int range, double spd, String name) {
		this.x = x;
		this.y = y;
		this.hp = hp;
		this.maxHp = hp;
		this.spd = spd;
		this.range = range;
		this.type = "ranged";
		this.name = name;
		hitnum = 0;
		lastHit = 0;
	}

	public void move(int px, int py, int[][] tiles) { //enemy movement
		stunned = Math.max(0,stunned-1);
		//System.out.println(Algs.dist(px, py, x, y) + " " + (range + 10));
		if (Algs.dist(px, py, x, y) >= range) {
			double ang = Math.atan2( y - py, x - px);
			int potX = -(int)(Math.cos(ang) * spd);
			int potY = -(int)(Math.sin(ang) * spd);
			Pair<Integer, Integer> moving = Algs.collideSetPiece(this, potX, potY, tiles);
			//x += moving.f;
			//y += moving.s;
			x += moving.f;
			y += moving.s;
		}
	}

	public boolean canHit(int px, int py) { //check if enemy should shoot at the player
		if(stunned > 0) return false;
		return Algs.dist(px, py, x, y) <= range + 10;
	}
/*
	public int doHit() {

	}
*/
}