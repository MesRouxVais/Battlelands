package entity;

import java.util.Arrays;

import main.BeholderPanel;
import main.KeyHandler;
import main.Main;
import main.MouseHandler;
import tpcCom.BeholderUDP;
import utils.AngleUtils;
import utils.ByteUtils;

public class PlayerClientTank extends ClientTank implements Camera{
	private KeyHandler keyHand;
	private MouseHandler mouseHand;
	
	private byte desiredAdvancementStatus;
	private int sentPacketNumber = 0;
	private byte[] lastSentPacket;
	
	public PlayerClientTank(int x, int y,BeholderPanel beholderPanel, KeyHandler keyHand, MouseHandler mouseHand) {
		super(x, y, beholderPanel);
		this.keyHand = keyHand;
		this.mouseHand = mouseHand;
	}
	
	
	@Override
	public void update() {
		if(keyHand!=null) {//so is player controlled
		//get desiredDirection
		if(mouseHand.button1Pressed) {
			
			desiredDirection = AngleUtils.getAngleBetweenTwoPoints(y,x,(Main.camera.getCameraY()-Main.window.getHeight()/2)+mouseHand.button1LastY,(Main.camera.getCameraX()-Main.window.getWidth()/2)+mouseHand.button1LastX);
			//if the player steps back adjust the direction
			if(keyHand.downPressed) {
				desiredDirection=AngleUtils.reverseAngle(desiredDirection);
			}
		}
		//get desiredTurretDirection
		desiredTurretDirection = AngleUtils.getAngleBetweenTwoPoints(y,x,(Main.camera.getCameraY()-Main.window.getHeight()/2)+mouseHand.getMouseY(),(Main.camera.getCameraX()-Main.window.getWidth()/2)+mouseHand.getMouseX());
		
		//desiredAdvancementStatus   1 if forward 0 if null 2 if -forward
		if(keyHand.upPressed) {
			desiredAdvancementStatus=1;
		}else if(keyHand.downPressed) {
			desiredAdvancementStatus=2;
		}else {
			desiredAdvancementStatus=0;
		}
		makeSentPacket();
		}
		
		forwardSound.setVolume(Math.abs(forwardCoefficient));
		rotationSound.setVolumeWithSoftening(Math.abs(turnCoefficient),0.02f,turnCoefficientStep*1.5f);
	}
	
	private void makeSentPacket() {
		byte[] sentPacket = ByteUtils.convertIntToByte(id, 1);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte(desiredAdvancementStatus, 1));
		
		
		int toIntDirection = (int)((desiredDirection/(2*Math.PI))*(double)65535);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte(toIntDirection, 2));
		
		toIntDirection =(int)((desiredTurretDirection/(2*Math.PI))*(double)65535);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte(toIntDirection, 2));
		
		if(!Arrays.equals(sentPacket, lastSentPacket)) {
			lastSentPacket = sentPacket;
			sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte(sentPacketNumber, 3));
			sentPacketNumber++;
			
			BeholderUDP.beholderComThread.sendMessage(sentPacket);
			//System.out.println(Arrays.toString(sentPacket) + " "+ sentPacketNumber);
		}
	}


	@Override
	public int getCameraX() {
		// TODO Auto-generated method stub
		return (int)x;
	}


	@Override
	public int getCameraY() {
		// TODO Auto-generated method stub
		return (int)y;
	}
}
