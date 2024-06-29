package entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import tpcCom.ServerUDP;
import utils.ByteUtils;

public class RemoteTank extends Entity {
	
	public int desiredAdvancementStatus;
	
	public RemoteTank() {
		x = 800;
		y = 500;
		
		speed = 2;
		
		ActualDirection =0;
		turnRate=0.01f;
		turnTolerance=0.02f;
		
		forwardCoefficientStep = 0.01f;
		forwardCoefficient = 0f;
		
		turnCoefficientStep = 0.01f;
		turnCoefficient = 0f;
		
		//for turret-----------------------
		
		turretTurnCoefficientStep = 0.01f;
		turretTurnCoefficient = 0f;
		
		ActualTurretDirection =0;
		
		
		
		getPlayerImage();
	}
	public void getPlayerImage() {
		try {
			
		    neutral = ImageIO.read(Files.newInputStream(Paths.get("./res/tank/KV-2_strip.png")));
		    turret = ImageIO.read(Files.newInputStream(Paths.get("./res/tank/KV-2_turret.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void packetReception(byte[] bytes) {
		desiredAdvancementStatus = bytes[1];
		
		byte[] tempBytes = {bytes[2],bytes[3]};
		int tempInt;
		
		tempInt =ByteUtils.convertByteToInt(tempBytes);
		desiredDirection = ((double)tempInt/(double)65535)*Math.PI*2;
		
		tempBytes[0] = bytes[4];
		tempBytes[1] = bytes[5];
		tempInt =ByteUtils.convertByteToInt(tempBytes);
		desiredTurretDirection =  ((double)tempInt/(double)65535)*Math.PI*2;;
	}
	
	@Override
	public void update() {
		/*-----------------------------------------------------------------------------------
		 apply Rotation from packetReception
		 -----------------------------------------------------------------------------------*/
		applyRotation();
		
		 /*-----------------------------------------------------------------------------------
		 get movement from packetReception
		 -----------------------------------------------------------------------------------*/
		if(desiredAdvancementStatus !=0) {
			if(Math.abs(desiredDirection - ActualDirection)>turnTolerance) {
				forwardCoefficient *=0.995;
			}else if(desiredAdvancementStatus == 1) {
				forwardCoefficient += forwardCoefficientStep;
				if(forwardCoefficient>1) {forwardCoefficient=1;}
			}else {
				forwardCoefficient -= forwardCoefficientStep;
				if(forwardCoefficient<-0.3f) {forwardCoefficient=-0.3f;}
			}
			
		}else if(Math.abs(forwardCoefficient)<0.03){
			forwardCoefficient=0;
		}else {
			forwardCoefficient *=0.99;
		}
		
		move();
		
		applyTurretRotation();
		makeSentPacket();
	}
	
	private void makeSentPacket() {
		byte[] sentPacket = ByteUtils.convertIntToByte(id, 1);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte((int)x, 2));
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte((int)y, 2));
		
		int toIntAngle = (int)((ActualDirection/(2*Math.PI))*(double)65535);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte(toIntAngle, 2));
		
		toIntAngle = (int)((ActualTurretDirection/(2*Math.PI))*(double)65535);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte(toIntAngle, 2));
		
		ServerUDP.serverComThread.sendMessage(sentPacket);
	}
}
