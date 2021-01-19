import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;
import javafx.embed.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*; 
import java.io.*; 
import javax.imageio.*; 

/*
SpriteMask.java
Henning Jiang, Matthew Mckewan, Bennett Grenier
This program is used to help with drawing the player sprite
*/

public class SpriteMask { //sprite mask controls how the player sprite is colored

	private static HashMap<String, int[][]> scanCol = new HashMap<String, int[][]>();
	//private static HashMap<String, String[]> classItems = new HashMap<String, String>();
	private static final String[] itemTypes = {"primary", "secondary", "helm", "pauldrons", "chest", "gauntlets", "leggings", "boots"};
	//there is a 'base' player sprite that looks kinda like a rainbow warrior
	//each piece of equipment as a color, and the shape of the piece of equipment is determined by looking at color pixel by pixel

	public SpriteMask(){
		scanCol.put("primary", new int[][]{{255,0,0}, {138,0,0}});
		scanCol.put("secondary", new int[][]{{64,0,106}, {51,0,84}});
		scanCol.put("helm", new int[][]{{98,0,255}, {59,2,149}});
		scanCol.put("chest", new int[][]{{0,255,68}, {3,136,38}});
		scanCol.put("gauntlets", new int[][]{{0,206,255}, {0,113,140}});
		scanCol.put("boots", new int[][]{{255,105,0}, {140,58,0}});
		scanCol.put("leggings", new int[][]{{255,195,0}, {187,143,1}});
		scanCol.put("pauldrons", new int[][]{{255,0,221}, {121,0,105}});

	}

	public static BufferedImage coat(Item[] inv, String pClass, int walkA, int atkA, boolean isPlayer, boolean aboveWater, String item) {
		String fName;
		if (isPlayer) {
			fName = "pics/player/" + pClass + "_temp_" + walkA + "_" + atkA + ".png";
		} else {
			fName = "pics/proj/proj_" + pClass + ".png";
		}

		BufferedImage pic = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

		Image ipic = new ImageIcon(fName).getImage();

		Graphics2D g = pic.createGraphics();
		g.setColor(new Color(255,0,0,0));
		g.fillRect(0,0,32,32);
		g.drawImage(ipic, 0, 0, null);

		Color col; 
		int rd, gr, bl, a;
		boolean placed;
    	
    	for (int r = 0; r < 32; r++){
    		for (int c = 0; c < 32; c++){
			    col = new Color(pic.getRGB(r, c));
			    rd = 0;
			    gr = 0;
			    bl = 0;
			    a = 0;
			    placed = false;
				for (String itemType : itemTypes){
					if (item.equals("") || itemType.equals(item) ) {
						if (colCheck(scanCol.get(itemType)[0], new int[]{col.getRed(), col.getGreen(), col.getBlue()})){
							rd = inv[Arrays.asList(itemTypes).indexOf(itemType)].getCol().getRed();
							gr = inv[Arrays.asList(itemTypes).indexOf(itemType)].getCol().getGreen();
							bl = inv[Arrays.asList(itemTypes).indexOf(itemType)].getCol().getBlue();
							a = 255;
							
						} else if (colCheck(scanCol.get(itemType)[1], new int[]{col.getRed(), col.getGreen(), col.getBlue()})){

							rd = inv[Arrays.asList(itemTypes).indexOf(itemType)].getCol().getRed()/2;
							gr = inv[Arrays.asList(itemTypes).indexOf(itemType)].getCol().getGreen()/2;
							bl = inv[Arrays.asList(itemTypes).indexOf(itemType)].getCol().getBlue()/2;
							a = 255;			
						} else if (item.equals("primary") && !(col.getRed() == 0 && col.getGreen() == 0 &&col.getBlue() ==0)){
							rd = 60;
							gr = 60;
							bl = 60;
							a = 255;
						}

					} else if (!(col.getRed() == 0 && col.getGreen() == 0 &&col.getBlue() ==0)){
						rd = 60;
						gr = 60;
						bl = 60;
						a = 255;
					}
					g.setColor(new Color(rd, gr, bl, a));
					g.fillRect(r, c, 1, 1);
					if (item.equals(itemType)) {
						break;
					}
						
				
					
				}	
    		}
    	}
    	if (!aboveWater) {
    		BufferedImage truePic = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
    		Graphics2D g2d = truePic.createGraphics();
    		g2d.setColor(new Color(255,0,0,0));
			g2d.fillRect(0,0,32,32);
    		g2d.drawImage(pic, 0, 16, null);
    		return truePic;
    	}
		return pic;

	}

	public static boolean colCheck(int[] col1, int[] col2) {
		//System.out.println(Arrays.toString(col1) + "::" + Arrays.toString(col2));
		if (col1[0] == col2[0] && col1[1] == col2[1] && col1[2] == col2[2]) {
			return true;
		} 
		return false;
	}

	public static HashMap<String, int[][]> get() {
		return scanCol;
	}

	public static void main(String[] args) {
		SpriteMask scan = new SpriteMask();
		
	}
}