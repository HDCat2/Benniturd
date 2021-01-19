import java.util.*;

/*
BossEnemy.java
Henning Jiang, Matthew Mckewan, Bennett Grenier
This program implements the final boss
*/

public class BossEnemy extends Enemy{
	private int headHP, chestHP, armHP, orbHP; //the boss has 4 distinct parts, each with their own hp bar
	private Pair<Integer, Integer> headPos, chestPos, armPos, orbPos;
	private static int HEAD_THICK = 196, CHEST_THICK = 256, ARM_THICK = 128, ORB_THICK = 128;
	private int phase = 0;
	public BossEnemy(){
		chestPos = new Pair(64000,64000);
		headPos = new Pair(64000, 63774);
		armPos = new Pair(63700, 64000);
		orbPos = new Pair(64300, 64000);
		headHP = chestHP = armHP = orbHP = 1000;
	}

	private ArrayList<Projectile> fight(Player play){
		//the method that handles the boss's projectiles
		//returns an arraylist of projectiles to fire
		int px = play.getX(), py = play.getY();
		ArrayList<Projectile> k = new ArrayList<>();
		phase += 1;
		phase %= 1200;
		if(phase/300 == 0){
			if(headHP <= 0){
				phase += 300;
				return k;
			}
			if(phase % 100 == 0){
				k.add(new Projectile(headPos.f, headPos.s, headPos.f, headPos.s, Math.PI/2 - Math.atan2(px - headPos.f, py - headPos.s),6400,20,400,"boss"));
				return k;
			}
		}
		if(phase/300 == 1){
			if(chestHP <= 0){
				phase += 300;
				return k;
			}
			if(phase % 50 == 0){
				k.add(new Projectile(chestPos.f, chestPos.s, chestPos.f, chestPos.s, Math.PI/2 - Math.atan2(px - chestPos.f, py - chestPos.s),6400,50,100,"boss"));
				k.add(new Projectile(chestPos.f, chestPos.s, chestPos.f, chestPos.s, Math.PI/2 - Math.atan2(px - chestPos.f, py - chestPos.s) + Math.PI/8,6400,50,100,"boss"));
				k.add(new Projectile(chestPos.f, chestPos.s, chestPos.f, chestPos.s, Math.PI/2 - Math.atan2(px - chestPos.f, py - chestPos.s) + 2*Math.PI/8,6400,50,100,"boss"));
				k.add(new Projectile(chestPos.f, chestPos.s, chestPos.f, chestPos.s, Math.PI/2 - Math.atan2(px - chestPos.f, py - chestPos.s) - Math.PI/8,6400,50,100,"boss"));
				k.add(new Projectile(chestPos.f, chestPos.s, chestPos.f, chestPos.s, Math.PI/2 - Math.atan2(px - chestPos.f, py - chestPos.s) - 2*Math.PI/8,6400,50,100,"boss"));
				return k;
			}
		}
		if(phase/300 == 2){
			if(armHP <= 0){
				phase += 300;
				return k;
			}
			if(phase % 100 == 0){
				for(int i = 0; i < 16; i++){
					k.add(new Projectile(chestPos.f + (int)(6400*Math.cos(i*Math.PI/8)),
						  chestPos.s + (int)(6400*Math.sin(i*Math.PI/8)),
						  chestPos.f + (int)(6400*Math.cos(i*Math.PI/8)),
						  chestPos.s + (int)(6400*Math.sin(i*Math.PI/8)),
						  -(i*Math.PI/8),
						  6400,
						  50,
						  100,
						  "boss"));
				}
				return k;
			}
		}
		if(phase/300 == 3){
			if(orbHP <= 0){
				phase += 300;
				return k;
			}
			if(phase % 10 == 0){
				k.add(new Projectile(headPos.f, headPos.s, headPos.f, headPos.s, Math.PI/2 - Math.atan2(px - headPos.f, py - headPos.s),12800,20,400,"boss"));
				return k;
			}
		}
		return k;
	}

	public boolean isDead(){ //returns true if all boss parts are dead, false otherwise
		return !(chestHP <= 0 && armHP <= 0 && headHP <= 0 && orbHP <= 0);
	}
	public boolean takeDmg(Projectile proj){ //takes damage from a projectile, returns false if the projectile connects and true otheriwse
		if(proj.getX() > headPos.f - HEAD_THICK/2 && proj.getX() < headPos.f - HEAD_THICK/2
		 && proj.getY() > headPos.s - HEAD_THICK/2 && proj.getY() < headPos.s - HEAD_THICK/2 && headHP > 0){
			headHP -= proj.getDmg();
			return false;
		}
		if(proj.getX() > chestPos.f - CHEST_THICK/2 && proj.getX() < chestPos.f - CHEST_THICK/2
		 && proj.getY() > chestPos.s - CHEST_THICK/2 && proj.getY() < chestPos.s - CHEST_THICK/2 && chestHP > 0){
			chestHP -= proj.getDmg();
			return false;
		}
		if(proj.getX() > orbPos.f - ORB_THICK/2 && proj.getX() < orbPos.f - ORB_THICK/2
		 && proj.getY() > orbPos.s - ORB_THICK/2 && proj.getY() < orbPos.s - ORB_THICK/2 && orbHP > 0){
			orbHP -= proj.getDmg();
			return false;
		}
		if(proj.getX() > armPos.f - ARM_THICK/2 && proj.getX() < armPos.f - ARM_THICK/2
		 && proj.getY() > armPos.s - ARM_THICK/2 && proj.getY() < armPos.s - ARM_THICK/2 && armHP > 0){
			armHP -= proj.getDmg();
			return false;
		}
		return true;
	}
	public int getOrbHP(){return OrbHP;}
}