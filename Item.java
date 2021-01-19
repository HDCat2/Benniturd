import java.awt.Color;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

/*
Item.java
Henning Jiang, Matthew Mckewan, Bennett Grenier
This implements items for use in other classes
*/

public class Item {
	//the Item class draws from Prefixes.txt, Materials.text, Weapons.txt, and Suffixes.txt
	//Since there are so many varieties of items, most of the code is simply pulling stuff out of files

	//ITEM MECHANICS:
	//All items do something distinct: e.g. primaries do flat damage, leggings do a fixed amount of damage reduction, etc.
	//All items have 4 qualities: a type, a name, a colour, and a rating
	//The name will always be of the form <Prefix> <Material> <Weapon> of <Suffix>
	//e.g. Shoddy Iron Sword of Might
	//The colour is solely determined by material
	//The type is solely determined by the weapon name
	//The rating is determined by the prefix, material, and suffix (all 3 aforementioned qualities have a rating themselves)
	//the rating goes as thus: (prefix + material) * suffix
	//note that sometimes there may not be a prefix or a suffix, at which they are omitted from the rating calculation

	private String type, name = "";
	private Color col;
	private int stat1, stat2, rating = 1;

	private HashMap<String, Pair<String, String>> statNames= new HashMap<>(); //most of these data structures are for storing text

	private HashMap<String, Integer> aq = new HashMap<>();
	private HashMap<String, Integer> rates = new HashMap<>();
	private HashMap<String, Color> colors = new HashMap<>();
	private HashMap<String, ArrayList<String>> vars = new HashMap<>();
	private HashMap<String, Pair<Integer, Integer>> max = new HashMap<String, Pair<Integer, Integer>>();
	private ArrayList<String> prefix = new ArrayList<>(),
							  suffix = new ArrayList<>(), 
							  variant = new ArrayList<>(), 
							  material = new ArrayList<>(), 
							  chest = new ArrayList<>(), 
							  gauntlets = new ArrayList<>(), 
							  leggings = new ArrayList<>(), 
							  boot = new ArrayList<>(), 
							  headpiece = new ArrayList<>(), 
							  sword = new ArrayList<>(), 
							  pauldron = new ArrayList<>(), 
							  bow = new ArrayList<>(), 
							  staff = new ArrayList<>(), 
							  battery = new ArrayList<>(), 
							  sheath = new ArrayList<>(), 
							  crystal = new ArrayList<>(), 
							  arrow = new ArrayList<>(), 
							  gear = new ArrayList<>();

	private String[] knight = new String[]{"sword","sheath","chest","gauntlets","boots","helm","pauldrons","leggings"};
	private String[] archer = new String[]{"bow","arrow","chest","gauntlets","boots","helm","pauldrons","leggings"};
	private String[] mage = new String[]{"staff","crystal","chest","gauntlets","boots","helm","pauldrons","leggings"};
	private String[] automaton = new String[]{"battery","gear","chest","gauntlets","boots","helm","pauldrons","leggings"};

	public Item(String[] inp) {
		type = inp[0];
		col = new Color(Integer.parseInt(inp[1]), Integer.parseInt(inp[2]), Integer.parseInt(inp[3]));

		name = "";
		if (!(inp[4].equals(""))) { name += inp[4] + " ";}
		if (!(inp[5].equals(""))) { name += inp[5] + " ";}
		name += inp[6];
		if (!(inp[7].equals(""))) { name += inp[7] + " ";}

		stat1 = Integer.parseInt(inp[8]);
		stat2 = Integer.parseInt(inp[9]);
	}

	public Item(String typ, String nam, int ratin, Color co){
		type = typ;
		name = nam;
		rating = ratin;
		col = co;

		initStats();
	}

	public Item(double mod, Player play){
		//mod is a modifier to the chance of getting a good item
		//it is an integer between 1-100 (it is easier to cast it as a double)
		Random rand = new Random();
		Scanner sc;
		File prefs = new File("Prefixes.txt"), suffs = new File("Suffixes.txt"), weaps = new File("Weapons.txt"), mats = new File("Materials.txt");
		
		//the next couple of lines are all pulling from the text files into the class

		try{sc = new Scanner(prefs);}
		catch(FileNotFoundException e){sc = new Scanner(System.in);}

		int qw = sc.nextInt();

		for(int i = 0; i < qw; i++){
			String a = sc.next();
			int b = sc.nextInt();
			rates.put(a,b);
			prefix.add(a);
		}

		sc.close();

		try{sc = new Scanner(suffs);}
		catch(FileNotFoundException e){sc = new Scanner(System.in);}

		qw = sc.nextInt();

		for(int i = 0; i < qw; i++){
			String a = sc.next();
			int b = sc.nextInt();
			rates.put(a,b);
			suffix.add(a);
		}

		sc.close();
		
		try{sc = new Scanner(mats);}
		catch(FileNotFoundException e){sc = new Scanner(System.in);}

		qw = sc.nextInt();

		for(int i = 0; i < qw; i++){
			String a = sc.next();
			int b = sc.nextInt(),r = sc.nextInt(), g = sc.nextInt(), s = sc.nextInt();
			rates.put(a,b);
			colors.put(a, new Color(r,g,s));
			material.add(a);
		}

		sc.close();

		try{sc = new Scanner(weaps);}
		catch(FileNotFoundException e){sc = new Scanner(System.in);}

		for(int i = 0; i < 5; i++) sword.add(sc.next());
		for(int i = 0; i < 5; i++) bow.add(sc.next());
		for(int i = 0; i < 5; i++) staff.add(sc.next());
		for(int i = 0; i < 5; i++) battery.add(sc.next());
		for(int i = 0; i < 5; i++) sheath.add(sc.next());
		for(int i = 0; i < 5; i++) arrow.add(sc.next());
		for(int i = 0; i < 5; i++) crystal.add(sc.next());
		for(int i = 0; i < 5; i++) gear.add(sc.next());
		for(int i = 0; i < 5; i++) chest.add(sc.next());
		for(int i = 0; i < 5; i++) gauntlets.add(sc.next());
		for(int i = 0; i < 5; i++) leggings.add(sc.next());
		for(int i = 0; i < 5; i++) boot.add(sc.next());
		for(int i = 0; i < 5; i++) headpiece.add(sc.next());
		for(int i = 0; i < 5; i++) pauldron.add(sc.next());

		sc.close();

		vars.put("sword",sword);
		vars.put("bow",bow);
		vars.put("staff",staff);
		vars.put("battery",battery);
		vars.put("sheath",sheath);
		vars.put("arrow",arrow);
		vars.put("crystal",crystal);
		vars.put("gear",gear);
		vars.put("chest",chest);
		vars.put("gauntlets",gauntlets);
		vars.put("leggings",leggings);
		vars.put("boots",boot);
		vars.put("helm",headpiece);
		vars.put("pauldrons",pauldron);

		//now we produce the item

		if(play.getPClass() == "knight") type = knight[rand.nextInt(8)]; //not all classes can wield the same items, so we randomly find a type based on that
		if(play.getPClass() == "archer") type = archer[rand.nextInt(8)];
		if(play.getPClass() == "mage") type = mage[rand.nextInt(8)];
		if(play.getPClass() == "automaton") type = automaton[rand.nextInt(8)];

		if(rand.nextInt(2) == 1){ //50% chance of a prefix being created
			int maxpos = Math.max(0, (int)(mod / 100 * 53) - 10);
			String p = prefix.get(rand.nextInt(10) + maxpos);
			name += p;
			name += " ";
			rating += rates.get(p);
		}

		int mp = Math.max(0, (int)(mod/100 * 18) -5); //material generated here
		String g = material.get(rand.nextInt(5) + mp);
		name += g;
		rating += rates.get(g);

		name += " "; //weapon generated here based on type
		name += vars.get(type).get(rand.nextInt(5));

		if(rand.nextInt(100) < mod){ //suffix here
			String o = suffix.get(rand.nextInt(10));
			name += " of ";
			name += o;
			rating *= rates.get(o);
		}

		initStats();
	}

	public void initStats() {
		max.put("sword", new Pair(500, 0));
		max.put("bow", new Pair(300, 0));
		max.put("staff", new Pair(250, 0));
		max.put("battery", new Pair(150, 0));
		max.put("sheath", new Pair(7, 0));
		max.put("arrow", new Pair(6, 250));
		max.put("crystal", new Pair(6, 350));
		max.put("gear", new Pair(6, 150));
		max.put("chest", new Pair(1000, 0));
		max.put("gauntlets", new Pair(80, 0));
		max.put("leggings", new Pair(20, 0));
		max.put("boots", new Pair(30, 0));
		max.put("helm", new Pair(500, 0));
		max.put("pauldrons", new Pair(200, 0));

		stat1 = (int) (((double)rating / 220 ) * max.get(type).f);
		if (stat1 == 0 && (type.equals("sword") || type.equals("battery") || type.equals("bow") || type.equals("crystal"))) {
			stat1 = 1;
		}
		stat2 = (int) (((double)rating / 220 ) * max.get(type).s);
	}

	public Color getCol() { return col; } //getters
	public int getStat1() { return stat1; } 
	public int getStat2() { return stat2; }
	public int getRating() { return rating; }
	public String getType() { return type; }
	public String getName() { return name; }

	public Pair<String, String> getStatNames() {
		statNames.put("sword", new Pair("Damage", ""));
		statNames.put("bow", new Pair("Damage", ""));
		statNames.put("crystal", new Pair("Damage", ""));
		statNames.put("battery", new Pair("Damage", ""));

		statNames.put("sheath", new Pair("Attack Speed", ""));
		statNames.put("gear", new Pair("Attack Speed", "Range"));
		statNames.put("arrow", new Pair("Attack Speed", "Range"));
		statNames.put("staff", new Pair("Attack Speed", "Range"));

		statNames.put("helm", new Pair("Mana", ""));
		statNames.put("chest", new Pair("Health", ""));
		statNames.put("gauntlets", new Pair("% Armour", ""));
		statNames.put("boots", new Pair("Speed", ""));
		statNames.put("pauldrons", new Pair("Regeneration", ""));
		statNames.put("leggings", new Pair("Armour", ""));

		return statNames.get(type);

	}
} 