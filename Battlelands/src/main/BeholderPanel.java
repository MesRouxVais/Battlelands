package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import entity.CanReceive;
import entity.ClientTank;
import entity.Entity;
import entity.PlayerClientTank;
import tpcCom.BeholderUDP;

public class BeholderPanel extends JPanel implements Runnable{
	
	final int screenOriginalWidth = 1280; //16/9
	final int screenOriginalHeight = 720;
	final int FPS = 120;
	
	KeyHandler keyHand = new KeyHandler();
	MouseHandler mouseHand = new MouseHandler(this);
	Thread beholderThread;
	
	PlayerClientTank player; //beholderTank player
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public ArrayList<CanReceive> canReceive = new ArrayList<CanReceive>();
	
	public BeholderPanel() {
		this.setPreferredSize(new Dimension(screenOriginalWidth, screenOriginalHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyHand);
		this.addMouseListener(mouseHand);
		
		this.setFocusable(true);
		
		BeholderUDP beholderUDP = new BeholderUDP(8585, "localhost",this);
	}
	
	public void startBeholderThread() {
		player = new PlayerClientTank(800,500,keyHand,mouseHand);
		player.id = 1;
		entities.add(player);
		canReceive.add(player);
		ClientTank serverPlayerTank = new ClientTank(500,500);
		serverPlayerTank.id = 0;
		entities.add(serverPlayerTank);
		
		this.requestFocus();
		beholderThread = new Thread(this,"beholderThread");
		beholderThread.start();
	}

	@Override
	public void run() {
		double drawnInterval = 1000000000/FPS;
		double nextDrawTime = System.nanoTime() + drawnInterval;
		
		while(beholderThread !=null) {
			
			for (Entity entity : entities) {
				entity.update();
			}
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
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		for (Entity entity : entities) {
			entity.draw(g2);
		}
		
		g2.dispose();
	}
	
}
