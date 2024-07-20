package entity;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import main.BeholderPanel;
import main.KeyHandler;
import main.Main;
import main.MouseHandler;
import tpcCom.BeholderUDP;
import utils.AngleUtils;
import utils.ByteUtils;
import utils.GraphicUtils;

public class PlayerClientTank extends ClientTank implements Camera{
	private KeyHandler keyHand;
	private MouseHandler mouseHand;
	
	private byte desiredAdvancementStatus;
	private int sentPacketNumber = 0;
	private byte[] lastSentPacket;
	
	BufferedImage cursor;
	BufferedImage turretAngleCursor;
	
	public PlayerClientTank(int x, int y,BeholderPanel beholderPanel, KeyHandler keyHand, MouseHandler mouseHand) {
		super(x, y, beholderPanel);
		this.keyHand = keyHand;
		this.mouseHand = mouseHand;
		
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0,0), "blank cursor");
		Main.window.getContentPane().setCursor(blankCursor);
		
		try {
			cursor = ImageIO.read(new File("./res/mapFolder/crossBot.png"));
			turretAngleCursor = ImageIO.read(new File("./res/mapFolder/crossTop.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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


	@Override
	public void drawUI(Graphics2D g2) {
		float referenceX = Main.camera.getCameraX();
		float referenceY = Main.camera.getCameraY();
		int screenMiddleHeight = Main.window.getHeight()/2;
		int screenMiddleWidth =  Main.window.getWidth()/2;
		int difX = (int) (x-referenceX);
		int difY = (int) (y-referenceY);
		
		
		g2.drawImage(GraphicUtils.rotate(cursor, desiredTurretDirection+Math.toRadians(90)), mouseHand.getMouseX(), mouseHand.getMouseY(), 20,20,null);
		
		int positionSourisX= mouseHand.getMouseX()+Main.camera.getCameraX()-screenMiddleWidth;
		int positionSourisY= mouseHand.getMouseY()+Main.camera.getCameraY()-screenMiddleHeight;
		
		int dis = 10+(int) Math.sqrt(Math.pow(x-positionSourisX, 2)+Math.pow(y-positionSourisY, 2));
		g2.drawImage(GraphicUtils.rotate(turretAngleCursor, ActualTurretDirection+Math.toRadians(90)), (int)(screenMiddleWidth+difX+Math.cos(ActualTurretDirection)*dis), (int)(screenMiddleHeight+difY+Math.sin(ActualTurretDirection)*dis), 20,20,null);
		
	}
}
