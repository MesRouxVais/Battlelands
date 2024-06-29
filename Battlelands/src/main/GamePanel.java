package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import entity.CanReceive;
import entity.Entity;
import entity.PlayerServerTank;
import entity.RemotePlayerTank;
import tpcCom.ServerUDP;

public class GamePanel extends JPanel implements Runnable{
	
	// SCREEN SETTINGS------------------------------------
	final int screenOriginalWidth = 1280; //16/9
	final int screenOriginalHeight = 720;
	final int FPS = 60;
	
	KeyHandler keyHand = new KeyHandler();
	MouseHandler mouseHand = new MouseHandler(this);
	Thread gameThread;
	PlayerServerTank player; //create player
	
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public ArrayList<CanReceive> canReceive = new ArrayList<CanReceive>();
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenOriginalWidth, screenOriginalHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyHand);
		this.addMouseListener(mouseHand);
		
		this.setFocusable(true);
		
		ServerUDP.startServer(8585,this);;
	}
	
	public void createsRemotePlayer() {
		RemotePlayerTank remoteTank = new RemotePlayerTank(800,500);
		remoteTank.id = 1;
		entities.add(remoteTank);
		canReceive.add(remoteTank);
		
	}
	
	public void startGameThread() {
		player = new PlayerServerTank(500, 500,keyHand, mouseHand); //create player
		player.id = 0;
		entities.add(player);
		this.requestFocus();
		gameThread = new Thread(this,"gameThread");
		gameThread.start();
	}

	@Override
	public void run() {
		
		double drawnInterval = 1000000000/FPS;
		double nextDrawTime = System.nanoTime() + drawnInterval;
		
		while(gameThread !=null) {
			
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
	
	public void update() {
		
		for (Entity entity : entities) {
			entity.update();
		}
	}
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		for (Entity entity : entities) {
			entity.draw(g2);
		}
		
		g2.dispose();
	}
}
