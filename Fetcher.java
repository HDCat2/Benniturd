import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;

public class Fetcher {
	private static final String[] tilenames = new String[]{"grass_whole", 
														   "grass_whole", 
														   "desert_whole", 
														   "desert_whole", 
														   "tundra_whole", 
														   "tundra_whole", 
														   "water_whole", 
														   "grass_path", 
														   "mountPic", 
														   "portal_block",
														   "portal_transport", 
														   "sky_block", 
														   "sky_transport", 
														   "desert_path", 
														   "tundra_path", 
														   "water_path", 
														   "", 
														   "", 
														   "", 
														   "", 
														   "cloud", 
														   "stormcloud", 
														   "sky", 
														   "sky_transport", 
														   "", 
														   "", 
														   "", 
														   "", 
														   "",
														   "", 
														   "u_ground", 
														   "u_lava", 
														   "u_rock", 
														   "portal_transport", 
														   "u_ground2", 
														   "u_ground3", 
														   "s_altarBrick", 
														   "s_altar", 
														   "u_altarBrick", 
														   "u_altar", 
														   "o_altarBrick",
														   "o_altar",
														   "endPortal",
														   "endPortal_c",
														   "endPortal_o",
														   "endPortalBlock",
														   "end_void",
														   "end_around",
														   "end_diamond"};
	private static final String[] obsnames = new String[] {"", "grass_structure", "", "desert_structure", "", "tundra_structure"};

	public static HashMap<String, EnemyStat> getEnemies() {
		HashMap<String, EnemyStat> out = new HashMap<String, EnemyStat> ();
		try{
			Scanner inFile = new Scanner (new BufferedReader(new FileReader("enemyTypes.txt") ));
			int numE = Integer.parseInt(inFile.nextLine());

			for(int i = 0; i < numE; i++){
				
				String s = inFile.nextLine();
				EnemyStat putting = new EnemyStat(s);
				out.put(putting.name, putting);
			}
			inFile.close();

        } catch(IOException ex) {
        	System.out.println("bad and ez");
        }
        return out;
	}

	public static HashMap<String, Image[][]> getEnemySprites(HashMap<String, EnemyStat> stats) {

		HashMap<String, Image[][]> out = new HashMap<String, Image[][]>();

		for (String name : stats.keySet()){
			Image[] walks = new Image[stats.get(name).walkF];
			Image[] acs = new Image[stats.get(name).actionF];
			for (int i = 0; i < stats.get(name).walkF; i++) {
				walks[i] = new ImageIcon("pics/enemy/" + name + "/" + name + "_walk_" + i + ".png").getImage().getScaledInstance(stats.get(name).size, stats.get(name).size, Image.SCALE_DEFAULT);
			}
			for (int j = 0; j < stats.get(name).actionF; j++) {
				acs[j] = new ImageIcon("pics/enemy/" + name + "/" + name + "_swing_" + j + ".png").getImage().getScaledInstance(stats.get(name).size, stats.get(name).size, Image.SCALE_DEFAULT);
			}
			out.put(name, new Image[][]{walks, acs});
		}


		return out;
	}

	public static Image[] getTiles() {
		//System.out.println(tilenames.length);
		Image[] out = new Image[49];

		for(int i = 0; i < 49; i++) {
			Image im = new ImageIcon("pics/tiles/" + tilenames[i] + ".png").getImage().getScaledInstance(128, 128, Image.SCALE_DEFAULT);
			out[i] = im;
		}

		return out;
	}

	public static BufferedImage[] getObs() {
		BufferedImage[] out = new BufferedImage[6];

		for(int i = 0; i < 6; i++) {
			BufferedImage img = null;
			
			try { img = ImageIO.read(new File("pics/tiles/" + obsnames[i] + ".png")); }
			catch (IOException e) {}
			if (img != null) {
				img = scaleBuffered(img, 4);
			}
			out[i] = img;
		}

		return out;
	}
	
	public static HashMap<String, BufferedImage> getEnemyImages(HashMap<String, EnemyStat> stats) {
		HashMap<String, BufferedImage> out = new HashMap<String, BufferedImage>();
		for (String k : stats.keySet() ) {
			if (stats.get(k).atkType.equals("ranged")) {
				String file = "pics/enemyProj/projectile_" + stats.get(k).pFile + ".png";

				BufferedImage i = null;
				try {i = ImageIO.read(new File(file));}
				catch (IOException e ) {}
				i = scaleBufferedToSize(i, stats.get(k).pSize);
				out.put(k, i);
			}
			
		}
		return out;
	}

	public static BufferedImage scaleBuffered(BufferedImage before, double s) {
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage((int)(w * s), (int)(h * s), BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(s,s);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);

		return after;
    }

    public static BufferedImage scaleBufferedToSize(BufferedImage before, double width) {
    	return scaleBuffered(before, width / before.getWidth());
    }
}