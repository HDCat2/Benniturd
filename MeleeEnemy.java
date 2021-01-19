
/*
MeleeEnemy.java
Henning Jiang, Matthew Mckewan, Bennett Grenier
This implements the melee archetype of enemies
*/

public class MeleeEnemy extends Enemy {
	//Melee Enemies all attempt to run at the player and stab him
	public MeleeEnemy(int x, int y, int hp, int range, double spd, String name) {
		this.x = x;
		this.y = y;
		this.hp = hp;
		this.maxHp = hp;
		this.spd = spd;
		this.range = range;
		this.type = "melee";
		this.name = name;
		hitnum = 0;
		lastHit = 0;
	}

	public void move(int px, int py, int[][] tiles) { //handling movement on terrain
		stunned = Math.max(0,stunned-1);
		if (Algs.dist(x, y, px, py) > Player.THICK / 2) {
			double ang = Math.atan2( y - py, x - px);
			int potX = -(int)(Math.cos(ang) * spd);
			int potY = -(int)(Math.sin(ang) * spd);
			Pair<Integer, Integer> moving = Algs.collideSetPiece(this, potX, potY, tiles);
			//Pair<Integer, Integer> moving = new Pair(potX, potY);

			x += moving.f;
			y += moving.s;
		}
	}

	public boolean canHit(int px, int py) { //handling if the melee enemy should stop to hit the player
		if(stunned > 0) return false;
		return Algs.dist(x, y, px, py)  < Player.THICK; 
	}
	/*
	public int doHit() {

	}
	*/
}