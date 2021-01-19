import java.util.*;
import java.awt.*;

/*
Player.java
Henning Jiang, Matthew Mckewan, Bennett Grenier
This program implements the player.
*/

public class Player{

	private int x, y, maxHp, maxMp, lvl, lastHit, lastUsed, xp; //some qualities of the player
	private Item primary, secondary, helm, pauld, chest, wrist, leg, boots; //equipment the player has
	private String pClass; //player class
	private double hp, mp; //player hp/mp
	private boolean buffed;

	private Item[] Inv = new Item[8]; //player equipment rendered into an inventory

	public static final int THICK = 80; //player size
	private static final HashMap<String, double[]> hpScale = new HashMap<String, double[]>();
	private static final HashMap<String, double[]> mpScale = new HashMap<String, double[]>();

	public Player(String pClass){ //constructor
		lastHit = 0; //lastHit is the last time the player has fired; this is used for doing firerate of weapons
		lastUsed = -2000; //lastUsed is the last time the player has used their ability; this is used for enforcing ability cooldowns
		x = 500;
		y = 500;
		xp = 0;

		this.pClass = pClass;
		lvl = 1;	

		hpScale.put("knight", new double[]{1.5, 20, 400}); //knight emphasizes tankiness and can stun enemies
		hpScale.put("automaton", new double[]{1.2, 15, 350}); //automaton uses large amounts of area damage
		hpScale.put("archer", new double[]{1.1, 13, 275}); //archer is long range, high damage
		hpScale.put("mage", new double[]{1.0, 10, 250}); //mage is long range, lots of healing
		
		mpScale.put("mage", new double[]{1.7, 40, 500});
		mpScale.put("automaton", new double[]{1.5, 20, 400});
		mpScale.put("archer", new double[]{1.3, 23, 375});
		mpScale.put("knight", new double[]{1.1, 15, 300});


		if(pClass == "knight"){ //basic starter custom items
			primary = new Item("sword","Wooden Sword",1,new Color(165,50,50));
			secondary = new Item("sheath","Wooden Sheath",1,new Color(165,42,42));
		}
		if(pClass == "automaton"){
			primary = new Item("battery","Wooden Battery",1,new Color(165,50,50));
			secondary = new Item("gear","Wooden Gear",1,new Color(165,42,42));
		}
		if(pClass == "archer"){
			primary = new Item("bow","Wooden Bow",1,new Color(165,50,50));
			secondary = new Item("arrow","Wooden Arrow",1,new Color(165,42,42));
		}
		if(pClass == "mage"){
			primary = new Item("staff","Wooden Staff",1,new Color(165,50,50));
			secondary = new Item("crystal","Wooden Crystal",1,new Color(165,42,42));
		}

		chest = new Item("chest","Wooden Chestpiece",1,new Color(165,60,60));
		helm = new Item("helm","Wooden Helm",1,new Color(165,32,32));
		pauld = new Item("pauldrons","Wooden Pauldron",1,new Color(145,42,42));
		leg = new Item("leggings","Wooden Leggings",1,new Color(175,42,42));
		boots = new Item("boots","Wooden Boots",1,new Color(98,21,21));
		wrist = new Item("gauntlets","Wooden Gauntlets",1,new Color(145,42,42));

	}

	public void initStats() {
		hp = getMaxHp();
		mp = getMaxMp();
	}

	public int getX() {return x;} //getters
	public int getY() {return y;}
	public String getPClass() {return pClass;}

	public double getMaxHp() {return (double)(scaleStat(lvl, hpScale) + chest.getStat1());}
	public double getMaxMp() {return (double)(scaleStat(lvl, mpScale) + helm.getStat1());}
	public double getHp() {return hp;}
	public double getMp() {return mp;}
	public int getLvl() {return lvl;}
	public int getXp() {return xp;}
	public int getAtkSpeed() {return 9 - secondary.getStat2();}
	public int getSpd() {return 200 + boots.getStat1();}

	public int getLastHit() {return lastHit;}
	public int getLastUsed() {return lastUsed;}

	public void buff() {buffed = true;}
	public void debuff() {buffed = false;}

	public Item[] getInv() {
		return new Item[] {primary, secondary, helm, pauld, chest, wrist, leg, boots};
	}

	public void regen() { //method for controlling player regeneration of HP and MP
		hp = Math.min(getMaxHp(), hp + (int)((double)getMaxHp() * 0.005 * (1 + (double)pauld.getStat1() / 100)));
		mp = Math.min(getMaxMp(), mp + (int)((double)getMaxMp() * 0.005 * (1 + (double)pauld.getStat1() / 100)));
	}

	public void setInv(Item[] inv) { //setter for inventory
		primary = inv[0];
	    secondary = inv[1];
		helm = inv[2];
		pauld = inv[3];
		chest = inv[4];
		wrist = inv[5];
		leg = inv[6];
		boots = inv[7];
	}
	public void lvlUp() { //level up
		lvl++;
		hp += scaleStat(lvl, hpScale) - scaleStat(lvl - 1, hpScale);
		mp += scaleStat(lvl, mpScale) - scaleStat(lvl - 1, mpScale);
		xp = 0;
	}

	public void xpUp(int x) { //add xp
		xp += x;
	}

	public void move(int dx, int dy){  //move player
		x += dx;
		y += dy;

		x = Math.max(Math.min(x, 999 * 128), THICK / 2);
		y = Math.max(Math.min(y, 999 * 128), THICK / 2);
	}

	public Pair<Integer, Integer> calcMove(int dx, int dy, double ang) { //calculate the x and y components of movement
		int ox = 0;
		int oy = 0; 

		ox += dx * Math.cos(ang);
		oy -= dx * Math.sin(ang);
		
		ox += dy * Math.sin(ang);
		oy += dy * Math.cos(ang);

		return new Pair(ox, oy);
	}

	public int scaleStat(int level, HashMap<String, double[]> scales) {
		double[] scalings = scales.get(pClass);
		return (int)(level * level * scalings[0] + level * scalings[1] + scalings[2]);
	}


	public boolean dmg(int dmg) { //take damage
		hp -= (int)((double)Math.max(dmg - leg.getStat1(), 0) * (double)(100 - wrist.getStat1()) / 100.0);
		Music.playFX("Game FX/player_getting_hit.wav");
		if (hp <= 0) {
			Music.stopMusic();
			Music.playFX("songs/death.wav");
			return true;
		}
		return false;
	}

	public boolean mana(int tick){ //use ability
		if(mp <= 100) return false;
		mp-= 100;
		lastUsed = tick;
		return true;
	}

	public void swing(int tick) { //use weapon
		lastHit = tick;
	}
}