public class swingEnemy extends Enemy {

	//public swingEnemy(int x, int y, int hp, int range, double spd) {
	public swingEnemy(String type, ) {
		this.x = x;
		this.y = y;
		this.hp = hp;
		this.spd = spd;
		this.range = range;
		hitnum = 0;
	}

	public void move(int px, int py) {
		double ang = Math.atan2(x - px, y - py);
		x += (int)(Math.cos(ang) * spd);
		y += (int)(Math.sin(ang) * spd);
	}

	public void attack(int px, int py) {
		
	}
}