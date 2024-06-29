package entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import audio.Sound;
import main.KeyHandler;
import main.MouseHandler;
import tpcCom.ServerUDP;
import utils.AngleUtils;
import utils.ByteUtils;

public class PlayerTank extends Entity{
	KeyHandler keyHand;
	MouseHandler mouseHand;
	
	public PlayerTank(KeyHandler keyHand,MouseHandler mouseHand) {
		this.keyHand = keyHand;
		this.mouseHand = mouseHand;
		DefaultValues();
		getPlayerImage();
	}
	
	public void DefaultValues() {
		x=500;
		y=400;
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
		//createSound();
		forwardSound = new Sound("tracks","res/tankAudio/small_tracks_rattle.wav", -20,-1,20);
	    rotationSound = new Sound("tracks","res/tankAudio/tank-rotation.wav", -25,-1,20);
	    engineLoopSound = new Sound("engine loop","res/tankAudio/engine-loop.wav", -10,-1,20);
	    engineLoopSound.setVolume(1);
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
	public void update() {
		 /*-----------------------------------------------------------------------------------
		 get direction inputs to  apply Rotation
		 -----------------------------------------------------------------------------------*/
		
		if(mouseHand.button1Pressed) {
			
			desiredDirection = AngleUtils.getAngleBetweenTwoPoints(y,x,mouseHand.button1LastY,mouseHand.button1LastX);
			//if the player steps back adjust the direction
			if(keyHand.downPressed) {
				desiredDirection=AngleUtils.reverseAngle(desiredDirection);
			}
		}
		applyRotation();
		
		 /*-----------------------------------------------------------------------------------
		 get movement inputs to  apply movement
		 -----------------------------------------------------------------------------------*/
		if(keyHand.upPressed || keyHand.downPressed) {
			if(Math.abs(desiredDirection - ActualDirection)>turnTolerance) {
				forwardCoefficient *=0.995;
			}else if(keyHand.upPressed) {
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
		
		/*
		if(keyHand.upPressed && Math.abs(desiredDirection - ActualDirection)<turnTolerance) { // "&& Math.abs(des...." to not forcing movement when rotating
			forwardCoefficient += forwardCoefficientStep;
			if(forwardCoefficient>1) {forwardCoefficient=1;}
		}else if(keyHand.downPressed && Math.abs(desiredDirection - ActualDirection)<turnTolerance) {
			forwardCoefficient -= forwardCoefficientStep;
			if(forwardCoefficient<-0.3f) {forwardCoefficient=-0.3f;}
		}else {
			if(Math.abs(forwardCoefficient)<0.03){
				forwardCoefficient=0;
			}else {
			forwardCoefficient *=0.99;
			}
		}
		*/
		move();
		
		 /*-----------------------------------------------------------------------------------
		 get turret rotation inputs to apply turret Rotation
		 -----------------------------------------------------------------------------------*/
		
		desiredTurretDirection = AngleUtils.getAngleBetweenTwoPoints(y,x,mouseHand.getMouseY(),mouseHand.getMouseX());
		
		applyTurretRotation();
		
		/*-----------------------------------------------------------------------------------
		 Update audio
		 -----------------------------------------------------------------------------------*/
		forwardSound.setVolume(Math.abs(forwardCoefficient));
		rotationSound.setVolumeWithSoftening(Math.abs(turnCoefficient),0.02f,turnCoefficientStep*1.5f);
		
		try {
			makeSentPacket();
		} catch (Exception e) {
			// TODO: handle exception
		}
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
