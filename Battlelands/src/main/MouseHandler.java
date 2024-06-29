package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class MouseHandler implements MouseListener{

	public JPanel gamePanel;
	public boolean button1Pressed;
	public int button1LastX;
	public int button1LastY;
	
	public MouseHandler(JPanel gp){
		gamePanel = gp;
	}
	
	public int getMouseX() {
		try {
			return gamePanel.getMousePosition().x;
		} catch (Exception e) {
			//System.out.println("Error MouseHandler getMouseX \"gamePanel.getMousePosition()\" is null");
			return 0;
		}
	}
	public int getMouseY() {
		try {
			return gamePanel.getMousePosition().y;
		} catch (Exception e) {
			//System.out.println("Error MouseHandler getMouseY \"gamePanel.getMousePosition()\" is null");
			return 0;
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton()==1) {
			button1Pressed =true;
			button1LastX = e.getX();
			button1LastY = e.getY();
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton()==1) {
			button1Pressed =false;
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {	
	}

}
