package entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.imageio.ImageIO;

import main.KeyHandler;
import main.MouseHandler;
import tpcCom.BeholderUDP;
import utils.AngleUtils;
import utils.ByteUtils;

/**
 * 
 */
public class BeholderTank extends Entity{
	KeyHandler keyHand;
	MouseHandler mouseHand;
	byte desiredAdvancementStatus;
	
	int sentPacketNumber = 0;
	byte[] lastSentPacket;
	
	

	public BeholderTank(KeyHandler keyHand, MouseHandler mouseHand) {// null null = no this player
		this.keyHand = keyHand;
		this.mouseHand = mouseHand;
		getBeholderImage();
		x=800;
		y=500;
	}
	
	private void getBeholderImage() {
		try {
			
		    neutral = ImageIO.read(Files.newInputStream(Paths.get("./res/tank/KV-2_strip.png")));
		    turret = ImageIO.read(Files.newInputStream(Paths.get("./res/tank/KV-2_turret.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update() {
		if(keyHand!=null) {//so is player controlled
		//get desiredDirection
		if(mouseHand.button1Pressed) {
			
			desiredDirection = AngleUtils.getAngleBetweenTwoPoints(y,x,mouseHand.button1LastY,mouseHand.button1LastX);
			//if the player steps back adjust the direction
			if(keyHand.downPressed) {
				desiredDirection=AngleUtils.reverseAngle(desiredDirection);
			}
		}
		//get desiredTurretDirection
		desiredTurretDirection = AngleUtils.getAngleBetweenTwoPoints(y,x,mouseHand.getMouseY(),mouseHand.getMouseX());
		
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
	public void packetReception(byte[] bytes) {
		System.out.println("packetReception  beholder");
		byte[] tempBytes = {bytes[1],bytes[2]};
		int tempInt;
		
		x= ByteUtils.convertByteToInt(tempBytes);
		tempBytes[0] = bytes[3];
		tempBytes[1] = bytes[4];
		y= ByteUtils.convertByteToInt(tempBytes);
		
		tempBytes[0] = bytes[5];
		tempBytes[1] = bytes[6];
		
		tempInt =ByteUtils.convertByteToInt(tempBytes);
		ActualDirection = ((double)tempInt/(double)65535)*Math.PI*2;
		
		tempBytes[0] = bytes[7];
		tempBytes[1] = bytes[8];
		
		tempInt =ByteUtils.convertByteToInt(tempBytes);
		ActualTurretDirection =  ((double)tempInt/(double)65535)*Math.PI*2;
	}

}
