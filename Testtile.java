import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;

public class Testtile extends JFrame implements ActionListener{
	Timer myTimer;
	Panel game;
	static int [][] tiles,tiles1;

    public Testtile() {
		super("test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 1000);

		myTimer = new Timer(10, this);	 // trigger every 10 ms
		myTimer.start();

		game = new Panel();
		add(game);
		setResizable(false);
		setVisible(true);
    }

	public void actionPerformed(ActionEvent evt){
		Object source = evt.getSource();

		if(game != null){
			game.refresh(tiles1);
			game.repaint();
		}
	}

    public static void main(String[] arguments) {
    	Algs alg = new Algs();
		try {
			tiles1 = alg.genTilesO();
			tiles1 = alg.genTilesB();
			double[] perc = new double[16];
			BufferedWriter writer = new BufferedWriter(new FileWriter("tiles.txt"));
			for (int i = 0; i < 1000; i++) {
				for (int j = 0; j < 1000; j++) {
					//writer.write(tiles[i][j] + " ");
				}
				writer.write("\n");
			}
			writer.close();
			for (int a = 0; a < 16; a++) {
				System.out.println((a + 1) + ": " + (perc[a] / 10000) + "%");
			}
		} catch (IOException e) {
			System.out.println("Bad and ez");
		}
		Testtile frame = new Testtile();
	}  
}

class Panel extends JPanel{
	int[][] tiles;

	Color[] cols = new Color[] {Color.GREEN, //1
							Color.BLACK, 
							Color.YELLOW, 
							Color.BLACK, 
							new Color(0, 255, 255),
							Color.BLACK, 
							Color.BLUE, 
							new Color(243,253,105),
							new Color(100, 100, 100), 
							Color.RED, //10
							Color.RED,
							new Color(0,255,255), 
							new Color(0,255,255),
							new Color(90,90,10), 
							Color.WHITE, 
							new Color(200,50,200),
							null,
							null,
							null,
							null,//20
							Color.WHITE,
							new Color(128,128,128),
							new Color(0,255,255),
							Color.BLUE,
							null,
							null,
							null,
							null,
							null,
							new Color(165,50,50), //30
							Color.RED,
							new Color(255,128,0),
							Color.BLACK,
							Color.BLUE,
							Color.BLACK,
							Color.YELLOW,
							Color.YELLOW,
							Color.YELLOW,
							Color.BLACK,
							Color.YELLOW, //40
							Color.BLACK,
							new Color(0,100,0),
							new Color(148,43,226),
							Color.BLACK,
							new Color(255,0,255),
							new Color(153,50,204),
							Color.BLACK,
							new Color(75,0,130),
							new Color(0,0,64)};

	public Panel(){
		setSize(1000, 1000);
	}
							
	public void refresh(int[][]t){
		tiles = t;
	}

    public void paintComponent(Graphics g){

    	for (int i = 0; i < 1000; i ++) {
    		for (int j = 0; j < 1000; j++) {
    			g.setColor(cols[tiles[i][j] - 1]);
    			g.fillRect(i, j, 1, 1);
    		}
    	}
    }

    



}




