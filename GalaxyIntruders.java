import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;

/*

Galaxy Intruders - not at all related to Taito's 1978 classic: Space Invaders
Henning Jiang, Matthew Mckewan

This implements space invaders with a couple of twists

*/

public class GalaxyIntruders extends JFrame implements ActionListener{
	JPanel cards;
	CardLayout cLayout = new CardLayout();

	javax.swing.Timer myTimer;
	GamePanel game; //adding several different cards to the game - each represents a different screen
	JPanel titlePageR, titlePageB, dead, victory, instr;

	private int myTick; 
	private boolean playing;

	private ArrayList<JButton>buttonList;

	private String col;

    public GalaxyIntruders() { //constructor over here

		super("Galaxy Intruders");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200,850);

		col = "R";
		playing = false;

		buttonList = new ArrayList<JButton>(); //initializing the different screens

		game = new GamePanel(); //gamepanel is the class that contains the actual game

		titlePageR = new JPanel();
		titlePageR.setLayout(null);
		
		addButton("Play", titlePageR, buttonList, 400, 460, 400, 100, Color.BLACK, 40, "Cooper Black", Color.WHITE, this); //adding buttons and text to all the non game cards
		addButton("Instructions", titlePageR, buttonList, 400, 570, 400, 100, Color.BLACK, 40, "Cooper Black", Color.WHITE, this);
		addButton("Quit", titlePageR, buttonList, 400, 680, 400, 100, Color.BLACK, 40, "Cooper Black", Color.WHITE, this);
		addImage("pics/gir.png", titlePageR, 0, -50, 1200, 300);
		addImage("pics/titleBack.jpg", titlePageR, 0, 0, 1200, 850);

		titlePageB = new JPanel();
		titlePageB.setLayout(null);
		
		addButton("Play", titlePageB, buttonList, 400, 460, 400, 100, Color.BLACK, 40, "Cooper Black", Color.WHITE, this);
		addButton("Instructions", titlePageB, buttonList, 400, 570, 400, 100, Color.BLACK, 40, "Cooper Black", Color.WHITE, this);
		addButton("Quit", titlePageB, buttonList, 400, 680, 400, 100, Color.BLACK, 40, "Cooper Black", Color.WHITE, this);
		addImage("pics/gib.png", titlePageB, 0, -50, 1200, 300);
		addImage("pics/titleBack.jpg", titlePageB, 0, 0, 1200, 850);

		instr = new JPanel();
		instr.setLayout(null);

		addLabel("<html>Use A/D or Left Arrow/Right Arrow to move<br>Spacebar to fire<br>Defeat all the enemies to win the game!<br><br>Each enemy is unique<br>The large invader can take three hits to kill instead of one<br>The medium invader rushes the player and detonates when it dies<br>The small invader fires a cone of 5 bullets when it dies<br><br>Your shields shelter you from enemy fire<br>But be careful, they won't last forever<br><br>The game has three stages, each conaining a boss<br>Ultracruiser - showers player with bullets and summons defensive line<br>Crystaline Mothership - summons interceptors that spray bullets<br>Broodlord Leviathan - summons giant enemies that fly down and attempt to destroy the player<br><br>GLHF!</html>", instr, 200, 0, 1000, 800, 20, "Cooper Black", Color.WHITE);
		addButton("To Main Screen", instr, buttonList, 500, 700, 200, 50, Color.BLACK, 20, "Cooper Black", Color.WHITE, this);
		addImage("pics/titleBack.jpg", instr, 0, 0, 1200, 850);

		dead = new JPanel();
		dead.setLayout(null);

		addLabel("YOU FAILED HUMANITY", dead, 200, 30, 1000, 100, 60, "Cooper Black", Color.RED);
		addButton("To Main Screen", dead, buttonList, 500, 700, 200, 50, Color.BLACK, 20, "Cooper Black", Color.WHITE, this);
		addImage("pics/deathBack.jpg", dead, 0, 0, 1200, 940);
		addImage("pics/black.png", dead, 0, 0, 1200, 850);

		victory = new JPanel();
		victory.setLayout(null);

		addLabel("VICTORY", victory, 464, 30, 1000, 100, 60, "Cooper Black", Color.GREEN);
		addButton("To Main Screen", victory, buttonList, 500, 700, 200, 50, Color.BLACK, 20, "Cooper Black", Color.WHITE, this);
		addImage("pics/medal.png", victory, 10, -50, 1200, 940);
		addImage("pics/black.png", victory, 0, 0, 1200, 850);


		myTimer = new javax.swing.Timer(10, this);	 // trigger every 10 ms
		myTimer.start();

		cards = new JPanel(cLayout);
		cards.add(titlePageR, "titleR");
		cards.add(titlePageB, "titleB");
		cards.add(instr, "instr");
		cards.add(game, "game");
		cards.add(dead, "dead");
		cards.add(victory, "victory");

		startBGMusic();
				
		add(cards);
		setResizable(false);
		setVisible(true);
    }

	public void actionPerformed(ActionEvent evt){ //testing for actions
		Object source = evt.getSource();
		
		if(game != null){
			game.refresh(myTick);
			game.repaint();
			if(source == myTimer){
				++myTick;
			}
			if (game.getLives() <= 0 && playing) { //if you lose all your lives and the game is lost
				cLayout.show(cards,"dead");
				addLabel("SCORE: " + game.getScore(), dead, 464,130,1000,100,60,"Cooper Black", Color.RED);
				playing = false;
			}
		}
		if (myTick % 36 == 0 && !playing) {
			if (col.equals("R")) {
				col = "B";
				cLayout.show(cards,"titleB");
			} else if (col.equals("B")){
				col = "R";
				cLayout.show(cards,"titleR");
			}
		}

		if(source == buttonList.get(0)){ //if the 'play' button is pressed
		    cLayout.show(cards,"game");
		    playing = true;
		    game.reset();
			myTimer.start();
		    game.requestFocus();
		    col = "N";
		}
		if(source == buttonList.get(1)){ //if the 'instructions' button is pressed 
		    cLayout.show(cards, "instr");
		    col = "N";
		}
		if(source == buttonList.get(2)){ //if the game is quit
		    System.exit(0);
		}
		if(source == buttonList.get(3)){ //if the 'play' button is pressed
		    cLayout.show(cards,"game");
		    playing = true;
		    game.reset();
			myTimer.start();
		    game.requestFocus();
		    col = "N";
		}
		if(source == buttonList.get(4)){ //if the 'instructions' button is pressed 
		    cLayout.show(cards, "instr");
		    col = "N";
		}
		if(source == buttonList.get(5)){ //if the game is quit
		    System.exit(0);
		}
		if(source == buttonList.get(6)){ //if the 'menu' button is pressed from the instructions screen 
		    cLayout.first(cards);
		    col = "R";
		}
		if(source == buttonList.get(7)){ //if the 'menu' button is pressed from the victory screen
		    cLayout.first(cards);
		    game.setLives(3);
		    col = "R";
		}
		if(game.victory() && playing){ //if all the enemies are killed and the game is won
			addLabel("SCORE: " + game.getScore(), victory, 464,130,1000,100,60,"Cooper Black", Color.GREEN);
		    cLayout.show(cards, "victory");
		    playing = false;
		}
		if(source == buttonList.get(8)){ //if the 'menu' button is pressed form the loss screen
		    cLayout.first(cards);
		    col = "R";
		}
	}

    public static void main(String[] args) { //main method over here, just starts the game
		GalaxyIntruders frame = new GalaxyIntruders();
    }

    private static void addLabel(String text, Container container, int x, int y, int w, int h, int fontSize, String typeFace, Color textCol) { //adds a label
        JLabel label = new JLabel(text);
        label.setFont(new Font(typeFace, Font.PLAIN, fontSize));
        label.setForeground(textCol);
        label.setSize(w,h);
		label.setLocation(x, y);

        container.add(label);
    }

    public static void addImage(String fname, Container container, int x, int y, int w, int h) { //adds an image
    	ImageIcon icon = new ImageIcon(fname);
		JLabel label = new JLabel(icon);
		label.setSize(w,h);
		label.setLocation(x,y);
		container.add(label);
    }

    private static void addButton(String text, Container container, ArrayList<JButton> blist, int x, int y, int w, int h, Color butCol,
    								 int fontSize, String typeFace, Color textCol, GalaxyIntruders gi) { //adds buttons
        JButton button = new JButton(text);
        button.setFont(new Font(typeFace, Font.PLAIN, fontSize));
        button.setForeground(textCol);
        button.setBackground(butCol);
        button.addActionListener(gi);

		button.setSize(w, h);
		button.setLocation(x, y);

        container.add(button);
        blist.add(button);
    }

    public void startBGMusic() { //the music player
        try {

	        URL url = GalaxyIntruders.class.getClassLoader().getResource("sound/titleMusic.wav");
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);

	        Clip clip = AudioSystem.getClip();

	        clip.open(audioIn);
	        //clip.setMicrosecondPosition(11300000);
	        clip.loop(clip.LOOP_CONTINUOUSLY);
	    } catch (UnsupportedAudioFileException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (LineUnavailableException e) {
	        e.printStackTrace();
	    }
    }
        
}  

class GamePanel extends JPanel implements KeyListener{ //our GamePanel, which handles the playing of the game
	private boolean[] keys; //declaring some variables
	private Image back, ship, lifeShip;
	private Player player;
	private Queue<Projectile> projList; //this queue handles the bullets fired by the player
	private int[][][] shieldList; //stores every state of the 4 shields that absorb bullets
	private Image[][][] shieldPieces; //each shield is made up of 10 pieces, and each piece changes sprite as it takes damage

	private Enemy[][] enemyList; //list of enemies
	private Point leader; //the leader is the top left enemy
	private boolean right; //determines which way the enemies move
	private int enemyTheme, score; //there are 3 different themes of enemies loosely based on the SC2 races
	private Queue<Projectile> enemyProj; //projectiles fired by enemies are shoved into a queue that gets processed every tick
	private Queue<Bomber> bombers; //bombers are enemies that rocket down and explode when they die
	private Image bombPic; //image used for bomber based on enemyTheme  
	private boolean canFire; //determines if the player is able to fire on that particular tick
	private int lastFired; //determines when the player last fired so canFire can be determined

	private int importTick; //this borrows the timer from the GalaxyIntruders class
	private boolean victory; //stores if the player has won or not
	private Enemy boss; //the boss
	private Image bossPic; //the image corresponding to the current boss
	private int bossx; //the x-coordinate of the boss (the y-coordinate is fixed)
	private Image crossPic; //the picture of the crosshair on the boss
	
	private int paintTick = 0;
	private int dif;

	public GamePanel(){ //constructor puts all variables in their initial positions
		addKeyListener(this);

		projList = new LinkedList<Projectile>();
		shieldPieces = new Image[3][4][4];;

		enemyList = new Enemy[5][11];
		leader = new Point(100,100);
		right = true;
		enemyTheme = 0; 
		score = 0;
		enemyProj = new LinkedList<Projectile>();
		bombers = new LinkedList<Bomber>();

		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("pics/back.jpg").getImage();
		ship = new ImageIcon("pics/ship.png").getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT);;
		lifeShip = new ImageIcon("pics/ship.png").getImage();
	    player = new Player();

	    canFire = true;
		lastFired = 0;

		importTick = 0;

	    int startv = 4;
	    shieldList = new int[][][]  { {{startv, startv, startv, startv},
									   {startv, startv, startv, startv},
									   {startv, 0, 0, startv}},

									  {{startv, startv, startv, startv},
									   {startv, startv, startv, startv},
									   {startv, 0, 0, startv}},

									  {{startv, startv, startv, startv},
									   {startv, startv, startv, startv},
									   {startv, 0, 0, startv}},

									  {{startv, startv, startv, startv},
									   {startv, startv, startv, startv},
									   {startv, 0, 0, startv}} };	

		loadShields();

	    fillEnemyList();
		setSize(1200,850);
	}

	public void reset() { //essentially the same thing as the constructor, resets all variables for a new game
						  //for some reason simply making a new gamepanel did not work
		addKeyListener(this);

		projList = new LinkedList<Projectile>();
		shieldPieces = new Image[3][4][4];;

		enemyList = new Enemy[5][11];
		leader = new Point(100,100);
		right = true;
		enemyTheme = 0; //FASTTRACK STAGES
		score = 0;
		enemyProj = new LinkedList<Projectile>();
		bombers = new LinkedList<Bomber>();

		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("pics/back.jpg").getImage();
		ship = new ImageIcon("pics/ship.png").getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT);;
		lifeShip = new ImageIcon("pics/ship.png").getImage();
	    player = new Player();

	    canFire = true;
		lastFired = 0;

		importTick = 0;
		victory = false;
		boss = null;
		bossx = 440;
		crossPic = new ImageIcon("pics/crosshair.png").getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT);

	    int startv = 4;
	    shieldList = new int[][][]  { {{startv, startv, startv, startv},
									   {startv, startv, startv, startv},

									   {startv, 0, 0, startv}},

									  {{startv, startv, startv, startv},
									   {startv, startv, startv, startv},
									   {startv, 0, 0, startv}},

									  {{startv, startv, startv, startv},
									   {startv, startv, startv, startv},
									   {startv, 0, 0, startv}},

									  {{startv, startv, startv, startv},
									   {startv, startv, startv, startv},
									   {startv, 0, 0, startv}} };	

		loadShields();

	    fillEnemyList();
		setSize(1200,850);
	}

    public void refresh(int myTick){ //moves along player actions per tick
    	importTick = myTick;
    	dif = importTick - paintTick;

    	if((importTick - lastFired) % 40 == 0) {
    		canFire = true;
    	}
		if(keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D]){

			player.move(3);
		}
		if(keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A]){
			player.move(-3);
		}
		if(keys[KeyEvent.VK_SPACE]) {
			if (canFire){
				projList.add(player.fire());
				lastFired = myTick;
				canFire = false;
			}	
		}
			
	}

	public int getLives() { //getter and setter for lives
		return player.getLives();
	}
	public void setLives(int l) {
		player.setLives(l);
	}
	public boolean victory() {
		return victory;
	}
	public int getScore() {
		return score;
	}

	public void scoreBoard(Graphics g){ //draws scoreboard on the top left corner
		g.setColor(Color.white);
		g.setFont(new Font("Helvetica",Font.BOLD,32));
		g.drawString("SCORE: " + score, 5,30);
	}

	public void lifeCounter(Graphics g) { //draws the lives on the top right corner
		g.setColor(Color.white);
		g.setFont(new Font("Helvetica",Font.BOLD,32));
		g.drawString("LIVES:", 940, 30);
		if (player.getLives() <= 3) {
			for (int i = 0; i < player.getLives(); i++) {
	    		g.drawImage(lifeShip, 1050 + 40 * i, 0, this);
	    	}
    	} else {
			g.drawString(player.getLives() + "", 1050, 30);	
    	}
	}

    public void paintComponent(Graphics g){ //draws all the entities and calls all the movements in the game

    	if (importTick - paintTick == dif) {
	    	paintTick += 1;
	    	g.drawImage(back,0,0,this);
	    	g.drawImage(ship, player.getX() - player.THICK/2, player.getY()  - player.THICK/2, this);
	    	g.setColor(Color.white);

	    	
	    	if (boss == null) {
	    		drawShields(g); 
	    	}
	    	if (leader.getY() + botRow() * 80 >= 650) {
	    		player.setLives(0);
	    	}

	    	if (boss == null && countInvaders() == 0) {
	    		boss = new Enemy(0, 0, 0, 25);
	    		enemyProj = new LinkedList<Projectile>();
				bombers = new LinkedList<Bomber>();
				bossPic = new ImageIcon("pics/boss_" + enemyTheme + ".png").getImage().getScaledInstance(320, 320, Image.SCALE_DEFAULT);
				if (enemyTheme == 2) {
					bombPic = new ImageIcon("pics/medium_beast.png").getImage().getScaledInstance(96, 96, Image.SCALE_DEFAULT);
				}
	    	} else if (boss != null) { //drawing the bosses that will appear
	    		runBoss(g);
	    	}
	    	scoreBoard(g);
	    	lifeCounter(g);
	    	moveEntities(g);
	    	drawEnemies(g);


		}
	}	
	public void moveEntities(Graphics g) { //moves the entities
		Projectile p;
    	Enemy e;
    	int result;

    	int[][] leaders = {{86, 600}, {386, 600}, {686,600}, {986,600}};

		int[][][] rectList =  {{{0, 2, 8, 6}, {8, 0, 8, 7}},
							   {{0, 8, 8, 6}, {8, 7, 8, 6}}, 
							   {{0, 13, 16, 5},{0, 0, 0, 0}} };


    	if (importTick % (countInvaders() * 2 + 2) == 0 && boss == null) { //changes direction when the enemies have reached a side of the screen
    		right = moveLeader(); 
    	}
		for (int i = 0; i < projList.size(); i++) { //checks if any of the enemies get hit by the entities
    		p = projList.poll();
    		if (p.move()) {
    			g.fillRect(p.getX() - 2, p.getY() - 2, 4, 4);
    			boolean doAdd = true;

    			for (int r = 0; r < enemyList.length; r++){ //loop through every guy in the enemy list and check for colliss
	 				for(int c =0; c < enemyList[r].length; c++){
    					e = enemyList[r][c];
    					if (e != null) {
	    					result = e.testForHit(p, (int)(leader.getX()) + c * 80 + 8, (int)(leader.getY()) + r * 80 + 8);
	    					if ((result == 1 || result ==2) && doAdd) {
	    						doAdd = false;
	    						if(result == 2) { //add score points as needed, add death effects as needed
	    							if(e.getDflag() == 1) {
	    								score += 30;
	    								doBurstEnemy((int)(leader.getX()) + c * 80 + 8 + 32, (int)(leader.getY()) + r * 80 + 8 + 32); //handling burst death effect
	    							}
	    							if(e.getDflag() == 0) {
	    								score += 20;
	    								bombers.add(new Bomber((int)(leader.getX()) + c * 80 + 8, (int)(leader.getY()) + r * 80 + 8, 64)); //handling bomber death effect
	    							}
	    							else score += 10;
	    							enemyList[r][c] = null; //delete enemy if it dies
	    						}
	    					} 
    					}
    				}
    				
    			}
    			if (doAdd && boss == null) { //if the bullet has not hit anything, it checks for shield collisions
	    			for (int j = 0; j < 4; j++) {
						for (int r = 0; r < 3; r++) {
							for (int c = 0; c < 4; c++) {
								if (doAdd && shieldList[j][r][c] >= 0) {
										int rectx, recty, rectw, recth;
										int scale = 4;
									if (c >=2) {
										rectx = leaders[j][0] - rectList[r][3 - c][0] * scale - rectList[r][3 - c][2] * scale + 128;
										recty = leaders[j][1] + rectList[r][3 - c][1] * scale;
										rectw = rectList[r][3 - c][2] * scale;
										recth = rectList[r][3 - c][3] * scale;

									} else {
										rectx = leaders[j][0] + rectList[r][c][0] * scale;
										recty = leaders[j][1] + rectList[r][c][1] * scale ;
										rectw = rectList[r][c][2] * scale;
										recth = rectList[r][c][3] * scale;
									}
									if (new Rect(rectx, recty, rectw, recth).collidepoint(new Point(p.getX(), p.getY()) )) {
									  	shieldList[j][r][c] -= 1;
									  	doAdd = false;
									} 
								}	
							}
						}
					}
				}


				if (boss != null && new Rect(bossx + 123, 390, 75, 75).collidepoint(new Point(p.getX(), p.getY()))) {
					doAdd = false;
					if (!boss.takeHit()) {
						boss = null;
						enemyTheme += 1;
						if (enemyTheme == 3) {
							victory = true;
						} else {
						leader = new Point(100, 100);
						fillEnemyList();
						enemyProj = new LinkedList<Projectile>();
						projList = new LinkedList<Projectile>();
						}

					}
				}

    			if (doAdd) { //if the bullet still has not hit anything, put it back into the queue
    				projList.add(p);
    			}	
    		}	
    	}

    	doEnemyProj(g); //handle the enemy projectiles and the bombers
    	moveBombers(g);
	}
	
	public void doEnemyProj(Graphics g) { //enemy projectiles are bullets fired by enemies if you can't infer that	
		int[][] leaders = {{86, 600}, {386, 600}, {686,600}, {986,600}}; //top left corner of each shield

		int[][][] rectList =  {{{0, 2, 8, 6}, {8, 0, 8, 7}}, //different pieces in each shield 
							   {{0, 8, 8, 6}, {8, 7, 8, 6}}, 
							   {{0, 13, 16, 5},{0, 0, 0, 0}} };

		for (int r = 0; r < 5; r++) {
			for (int c = 0; c < 11; c++) {
				if (Math.random() < 0.001 && enemyList[r][c] != null) {
					if (r == 4) { //handling enemy projectiles being fired, with a 1/1000 chance of a given enemy firing per tick
						enemyProj.add(new Projectile((int)(leader.getX() + c * 80 + 8 + 32), (int)(leader.getY() + r * 80 + 8 + 32), 0, Math.PI/2));
					} else if (enemyList[r + 1][c] == null) { //Math.PI/2 means each projectile fires straight down, which they do
						enemyProj.add(new Projectile((int)(leader.getX() + c * 80 + 8 + 32), (int)(leader.getY() + r * 80 + 8 + 32), 0, Math.PI/2));

					}
				}
			}
		} 
		Projectile p;

		for (int i = 0; i < enemyProj.size(); i++) { //looping through each enemy bullet
			p = enemyProj.poll();
			g.fillRect(p.getX() - 2, p.getY() - 2, 4, 4);
			boolean doAdd = true; //a variable indicating if the projectile still exists after the tick has passed over
			if (p.move()){
				int result = player.testForHit(p);
				if (result == 0) { //checking for shield collisions once again
					if (boss == null) {
						for (int j = 0; j < 4; j++) {
							for (int r = 0; r < 3; r++) {
								for (int c = 0; c < 4; c++) {
									if (doAdd && shieldList[j][r][c] >= 0) {
											int rectx, recty, rectw, recth;
											int scale = 4;
										if (c >=2) {
											rectx = leaders[j][0] - rectList[r][3 - c][0] * scale - rectList[r][3 - c][2] * scale + 128;
											recty = leaders[j][1] + rectList[r][3 - c][1] * scale + 32;
											rectw = rectList[r][3 - c][2] * scale;
											recth = rectList[r][3 - c][3] * scale;

										} else {
											rectx = leaders[j][0] + rectList[r][c][0] * scale;
											recty = leaders[j][1] + rectList[r][c][1] * scale + 32;
											rectw = rectList[r][c][2] * scale;
											recth = rectList[r][c][3] * scale;
										}
										if (new Rect(rectx, recty, rectw, recth).collidepoint(new Point(p.getX(), p.getY()) ) ) {
										  	shieldList[j][r][c] -= 1;
										  	doAdd = false;
										} 

										for (int k = 0; k < bombers.size(); k++) { //loops through the bombers and sees if there are any shield collisions
											Bomber b = bombers.poll();
											if (new Rect(rectx, recty, rectw, recth).colliderect(new Rect(b.getX(), b.getY(), 64, 64))) {
												bomberImpact(b.getX() + 32, b.getY() + 32); //bomberImpact checks the collisions
											} else {
												bombers.add(b);
											}
										}
									}
								}	
							}
						}
					}				
				} else {
					doAdd = false; 
				}
				if (doAdd) {
					enemyProj.add(p);
				}
			}
		}
	}
	public void moveBombers(Graphics g) { //move along the bombers
		//the habit of the bombers is to project a circle around them that causes damage to players and shields within its radius
		int rad = 100;

		for (int i = 0; i < bombers.size(); i++) { //looping through each bomber in the queue
			Bomber b = bombers.poll();
			if (b.move()) {
				g.drawImage(bombPic, b.getX(), b.getY(), this);
				g.drawOval(b.getX() - (rad - b.getSize()/2), b.getY() - (rad - b.getSize()/2), rad * 2, rad * 2);
				bombers.add(b);
			} else {
				bomberImpact(b.getX() + 32, b.getY() + 32);
			}	
		}	
	}

	public void bomberImpact(int h, int k) { //checking for a bomber's collision with anything
		int[][] leaders = {{86, 600}, {386, 600}, {686,600}, {986,600}}; //again, looking at the shields

		int[][][] rectList =  {{{0, 2, 8, 6}, {8, 0, 8, 7}},
							   {{0, 8, 8, 6}, {8, 7, 8, 6}}, 
							   {{0, 13, 16, 5},{0, 0, 0, 0}} };
		int rad = 100;

		for (int j = 0; j < 4; j++) { //again, loop through all the shields
			for (int r = 0; r < 3; r++) {
				for (int c = 0; c < 4; c++) {
					if (shieldList[j][r][c] >= 0) {
							int rectx, recty, rectw, recth;
							int scale = 4;
						if (c >=2) {
							rectx = leaders[j][0] - rectList[r][3 - c][0] * scale - rectList[r][3 - c][2] * scale + 128;
							recty = leaders[j][1] + rectList[r][3 - c][1] * scale + 32;
							rectw = rectList[r][3 - c][2] * scale;
							recth = rectList[r][3 - c][3] * scale;

						} else {
							rectx = leaders[j][0] + rectList[r][c][0] * scale;
							recty = leaders[j][1] + rectList[r][c][1] * scale + 32;
							rectw = rectList[r][c][2] * scale;
							recth = rectList[r][c][3] * scale;
						}
						if (new Rect(rectx, recty, rectw, recth).collidecircle(h, k, rad) )  { //looking for collisions with the circle
						  	shieldList[j][r][c] -= 1;
						} 
					}	
				}
			}
		}
		if (new Rect(player.getX() - player.THICK/2, player.getY()  - player.THICK/2, player.THICK, player.THICK).collidecircle(h, k, rad) )  {
		  	player.subLives(); //checks for collisions with the player
		} 	
	}

	public void fillEnemyList() { //initialize the enemy list
		int[][]deaths = {{0, 0, 1, 2, 2},
						 {0, 1, 1, 1, 2},
						 {0, 0, 0, 1, 2}};

		for (int r = 0; r < 5; r++) {
			for (int c = 0; c < 11; c++) {
				enemyList[r][c] = new Enemy(0, deaths[enemyTheme][r], enemyTheme);

			}
		}
		bombPic = enemyList[0][0].getPic();
	}

	public boolean moveLeader() { //moves the leader of the enemies along
		if (right) {
			if (leader.getX() + (rightRow() + 1) * 80 >= 1200 ) {
				leader.translate(0,20); 
				return false;
			} else{ leader.translate(8,0); }
		} else {
			if (leader.getX() + leftRow() * 80 <= 0 ) {
				leader.translate(0,20); 
				return true;
			} else{ leader.translate(-8,0); }
		}

		return right;
	}

	public int rightRow() { //checks for the position of the rightmost enemy
		int rightest = 0;
		Enemy[] e;
		for (int a = 0; a < 5; a++) {
			int rightmost = 0;
			e = enemyList[a];
			for (int b = 0; b < 11; b++) {
				if (e[b] != null) {
					rightmost = b;
				}
			}	
			rightest = Math.max(rightmost, rightest);	
		}
		return rightest;
	}

	public int leftRow() { //checks for the position of the leftmost enemy
		int leftest = 0;

		Enemy[] e;
		for (int a = 0; a < 5; a++) {
			e = enemyList[a];
			int leftmost = 0;
			for (int b = 0; b < 11; b++) {
				if (e[b] != null) {
					leftmost = b;
					break;
				}
			}	
			leftest = Math.min(leftmost, leftest);	
		}
		return leftest;
	}

	public int countInvaders() { //count the remaining enemies
		int total = 0;
		for (Enemy[] erow : enemyList) {
			for (Enemy e : erow) {
				if (e != null) { total++;}
			}
		}
		return total;
	}

	public int botRow(){ //checks for the position of the bottom row of enemies
						 //if the enemies reach the bottom of the screen, you lose
		int bottom = 0;

		Enemy[] e;
		for (int a = 0; a < 5; a++) {
			e = enemyList[a];
			int bottomcol = 0;
			for (int b = 0; b < 11; b++) {
				if (e[b] != null) {
					bottomcol = a;
				}
			}	
			bottom = Math.max(bottom, bottomcol);	
		}
		return bottom;
	}


	public void doBurstEnemy(int x, int y) { //when an enemy of this type dies, it shoots a shotgun of 5 bullets that go out in angle
		enemyProj.add(new Projectile(x, y, 0, 6 * Math.PI/16));
		enemyProj.add(new Projectile(x, y, 0, 7 * Math.PI/16));
		enemyProj.add(new Projectile(x, y, 0, 5 * Math.PI/16));
		enemyProj.add(new Projectile(x, y, 0, 9 * Math.PI/16));
		enemyProj.add(new Projectile(x, y, 0, 10 * Math.PI/16));
	}

	public void drawEnemies(Graphics g) { //draw the enemies
		
		for (int r = 0; r < 5; r++) {
			for (int c = 0; c < 11; c++) {
				if (enemyList[r][c] != null) {
					g.drawImage(enemyList[r][c].getPic(), (int)(leader.getX() + c * 80 + 8), (int)(leader.getY() + r * 80 + 8), this);
				}			
			}
		}
	}

	public void drawShield(int sx, int sy, int snum, Graphics g) { //draws the individual shields based on their health

		int scale = 4;

		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 4; c++) {
				
				if ( !(r == 2 && c == 1) && !(r == 2 && c == 2) && shieldList[snum][r][c] > 0) {
					
					if (c <= 1) {
						g.drawImage(shieldPieces[r][c][4 - shieldList[snum][r][c]], sx, sy, this);
					}
					else {
						int x = sx;
						g.drawImage(shieldPieces[r][c][4 - shieldList[snum][r][c]], x, sy, this);
						
					}

				}
			}
		}
	}

	public void drawShields(Graphics g) { //calls drawShield on all 4 shields
		int[][] leaders = {{86, 600}, {386, 600}, {686,600}, {986,600}};

		for (int i = 0; i < 4; i++) {
			drawShield(leaders[i][0], leaders[i][1], i, g);
		}
	}

	public void loadShields() { //loads the images used to draw the shields

		int[][] core = {{1, 0, 0, 1},
						{3, 2, 2, 3},
						{4, 0, 0, 4}};
		int scale;

		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 4; c++) {
				for (int p = 0; p < 4; p++) {

					String str = "pics/shield_" + core[r][c] + "_" + p;
					if (c >= 2) {str += "_f";}
					str += ".png";
					Image  image = new ImageIcon(str).getImage().getScaledInstance(128, 128, Image.SCALE_DEFAULT);

					shieldPieces[r][c][p] = image;
					
				}
			}
		}
	}
	public void runBoss(Graphics g) {
		g.drawImage(bossPic, bossx, 90, this);
		g.drawImage(crossPic, bossx + 123, 390, this);
		g.setColor(Color.RED);
		g.fillRect(50, 35, (int)(boss.getHp() * (1100 / 25)), 50);
		g.setColor(Color.WHITE);
		g.drawRect(50, 35, 1100, 50);
		if (right) {
			bossx += 1;
			if (bossx >= 880) {
				right = false;
			}
		} else {
			bossx -= 1;
			if (bossx <= 0) {
				right = true;
			}
		}
		if (enemyTheme == 0) { //handling the first boss
			leader = new Point(100, 230);
			if (importTick % 1000 == 0) {
				for (int i = 0; i < 11; i++) {
					enemyList[4][i] = new Enemy(0, 2, 0); 
				}
			}
			if (importTick % 150 == 0) { //the first boss shoots a load of bullets every 100 ticks
				double trajectory = Math.atan2(250 - player.getY(),bossx + 160 - player.getX()) + Math.PI;
				enemyProj.add(new Projectile(bossx + 160, 250, 0, trajectory)); //the central 'turret' fires a shotgun of 3 bullets directly at the player
				enemyProj.add(new Projectile(bossx + 160, 250, 0, trajectory + Math.PI/8));
				enemyProj.add(new Projectile(bossx + 160, 250, 0, trajectory - Math.PI/8));


				enemyProj.add(new Projectile(bossx + 40, 325, 0, Math.PI/2)); //the 4 side turrets fire straight down
				enemyProj.add(new Projectile(bossx + 80, 360, 0, Math.PI/2));
				enemyProj.add(new Projectile(bossx + 240, 360, 0, Math.PI/2));
				enemyProj.add(new Projectile(bossx + 280, 325, 0, Math.PI/2));
			}
		} else if(enemyTheme == 1) {
			if (importTick % 400 == 0 && countInvaders() < 55) {
				while (true) {
					int r = (int)(Math.random() * 5);
					int c = (int)(Math.random() * 11);
					if (enemyList[r][c] == null) {
						enemyList[r][c] = new Enemy(0, 1, 1);
						break;

					}
				}
			}
			if (importTick % 200 == 0) {

    			for (int r = 0; r < 5; r++) {
    				for (int c = 0; c < 11; c++) {
    					if (enemyList[r][c] != null) {
    						doBurstEnemy((int)(leader.getX()) + c * 80 + 8 + 32, (int)(leader.getY()) + r * 80 + 8 + 32);
    					}
    				}
    			}
			}
		} else {
			leader = new Point(100, 0);
			if (importTick % 160 == 0) {
				ArrayList<Integer> used = new ArrayList<Integer>();
				for (int i = 0; i < 3; i++){
					
					while (true) {
						int r = 0;
						int c = (int)(Math.random() * 11);
						if (!(used.contains(c))) {
							bombers.add(new Bomber((int)(leader.getX()) + c * 80 + 8 + 32, (int)(leader.getY()) + r * 80 + 8 + 32, 96));
							used.add(c);
							break;

						}
					}
				}
			}
		}
	}

	public void keyTyped(KeyEvent e) {} //key events here
    public void keyPressed(KeyEvent e) {
    	setKey(e.getKeyCode(),true);
    }
    public void keyReleased(KeyEvent e) {
    	setKey(e.getKeyCode(),false);
    }
    public void setKey(int k, boolean v) {
    	keys[k] = v;
    }

   
}

