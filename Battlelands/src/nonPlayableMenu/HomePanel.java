package nonPlayableMenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import main.KeyHandler;
import main.Main;
import main.MouseHandler;

public class HomePanel extends JPanel implements Runnable{
	GradientButton buttonPlay,buttonMutliplayer,buttonLeave;
	TextInput textInput;
	Thread homeThread;
	MouseHandler mouseHand = new MouseHandler(this);
	KeyHandler keyHand = new KeyHandler();
	BufferedImage backgroundImage;
	
	public HomePanel() {
		this.setPreferredSize(new Dimension(1280, 720));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addMouseListener(mouseHand);
		this.addKeyListener(keyHand);
		this.setFocusable(true);
		
		buttonPlay = new GradientButton("Play");
		buttonMutliplayer = new GradientButton("Multiplayer");
		buttonLeave = new GradientButton("Leave");
		textInput= new TextInput(keyHand);
		
		startGameThread();
		
		try {
			backgroundImage = ImageIO.read(Files.newInputStream(Paths.get("./res/menuImage/background.png")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startGameThread() {
		homeThread = new Thread(this,"homeThread");
		homeThread.start();
	}
	
	@Override
	public void run() {
		
		double drawnInterval = 1000000000/120;
		double nextDrawTime = System.nanoTime() + drawnInterval;
		
		while(homeThread !=null) {
			
			update();
			repaint();
			try {
				double remainingTime = nextDrawTime - System.nanoTime();
				remainingTime = remainingTime/1000000;
				if(remainingTime>0) {
					Thread.sleep((long) remainingTime);
				}
				nextDrawTime += drawnInterval;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void update(){
		buttonPlay.updateButtonStade(mouseHand.getMouseX(), mouseHand.getMouseY());
		buttonMutliplayer.updateButtonStade(mouseHand.getMouseX(), mouseHand.getMouseY());
		buttonLeave.updateButtonStade(mouseHand.getMouseX(), mouseHand.getMouseY());
		textInput.updateButtonStade(mouseHand.getMouseX(), mouseHand.getMouseY());
		
		if(mouseHand.button1Pressed) {
			if(buttonPlay.mouseIn) {
				Main.playClicked();
				homeThread = null;
			}
			
			if(buttonMutliplayer.mouseIn){
				Main.MutliplayerClicked(textInput.value);
				homeThread = null;
			}
		}
		
		if(mouseHand.button1Pressed) {
			textInput.recalculateOffset = true;
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		g2.drawImage(backgroundImage,0,0, Main.window.getSize().width, Main.window.getSize().height, null);
		
		buttonPlay.updateButtonGraphics(0.15f, 0.25f, Main.window.getSize(), 0.25f,0.065f);
		buttonPlay.paintButton(g2);
		
		buttonMutliplayer.updateButtonGraphics(0.15f, 0.40f, Main.window.getSize(), 0.25f,0.065f);
		buttonMutliplayer.paintButton(g2);
		
		buttonLeave.updateButtonGraphics(0.15f, 0.55f, Main.window.getSize(), 0.25f,0.065f);
		buttonLeave.paintButton(g2);
		
		textInput.updateButtonGraphics(0.15f, 0.46f, Main.window.getSize(), 0.25f, 0.030f);
		textInput.paintButton(g2);
		
		g2.dispose();

	}
}
