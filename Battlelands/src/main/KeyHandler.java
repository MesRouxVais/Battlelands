package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import nonPlayableMenu.ReceiveKeyboardNotification;

public class KeyHandler implements KeyListener{
	
	public boolean upPressed, downPressed, leftPressed, rightPressed, cPressed;
	ReceiveKeyboardNotification interfaceToNotified;
	
	public void addNotification(ReceiveKeyboardNotification interfaceToNotified) {
		this.interfaceToNotified=interfaceToNotified;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		if(interfaceToNotified == null) {
		//ne rien faire
		}else {
			interfaceToNotified.receiveKeyboardNotification(e.getKeyChar());
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		switch (code) {
			case KeyEvent.VK_Z:
				upPressed = true;
				break;
			case KeyEvent.VK_S:
				downPressed = true;
				break;
			case KeyEvent.VK_Q:
				leftPressed = true;
				break;
			case KeyEvent.VK_D:
				rightPressed = true;
				break;
			case KeyEvent.VK_C:
				cPressed = true;
				break;
			default:
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		switch (code) {
			case KeyEvent.VK_Z:
				upPressed = false;
				break;
			case KeyEvent.VK_S:
				downPressed = false;
				break;
			case KeyEvent.VK_Q:
				leftPressed = false;
				break;
			case KeyEvent.VK_D:
				rightPressed = false;
				break;
			case KeyEvent.VK_C:
				cPressed = false;
				break;
			default:
				break;
		}
		
	}

}
