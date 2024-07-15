package main;


import javax.swing.JFrame;

import entity.Camera;
import nonPlayableMenu.HomePanel;

public class Main {
	
	public static JFrame window;
	public static HomePanel homePanel;
	public static BeholderPanel beholderPanel;
	
	static public Camera camera;
	
	public static void main(String[] args) {
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);
		window.setTitle("Battlelands home menu");

		homePanel = new HomePanel();
		window.add(homePanel);
		window.pack();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	//from homePanel
	public static void playClicked() {
		window.setTitle("Server");
		window.remove(homePanel);
		GamePanel gamePanel = new GamePanel();
		window.add(gamePanel);
		window.pack();
		gamePanel.startGameThread();
		

	}
	
public static void MutliplayerClicked(String hostname) {
		window.setTitle("Client");
		window.remove(homePanel);
		BeholderPanel beholderPanel = new BeholderPanel(hostname);
		window.add(beholderPanel);
		window.pack();
		beholderPanel.startBeholderThread();
		

	}
}
