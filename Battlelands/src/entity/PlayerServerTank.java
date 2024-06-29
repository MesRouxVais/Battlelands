package entity;

import audio.Sound;
import main.KeyHandler;
import main.MouseHandler;
import utils.AngleUtils;

public class PlayerServerTank extends ServerTank{

	private KeyHandler keyHand;
	private MouseHandler mouseHand;

	public PlayerServerTank(int x, int y,KeyHandler keyHand,MouseHandler mouseHand) {
		super(x, y);
		this.keyHand = keyHand;
		this.mouseHand = mouseHand;
		
		//createSound();
		forwardSound = new Sound("tracks","res/tankAudio/small_tracks_rattle.wav", -20,-1,20);
	    rotationSound = new Sound("tracks","res/tankAudio/tank-rotation.wav", -25,-1,20);
	    engineLoopSound = new Sound("engine loop","res/tankAudio/engine-loop.wav", -10,-1,20);
	    engineLoopSound.setVolume(1);
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
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

}
