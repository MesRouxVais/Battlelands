package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import audio.Sound;
import utils.GraphicUtils;


public class Entity {
	
	public float x,y;
	public float speed, forwardCoefficient, forwardCoefficientStep, turnCoefficient, turnCoefficientStep, turretTurnCoefficient, turretTurnCoefficientStep;
	
	public BufferedImage neutral, turret;
	public double ActualDirection;
	public double desiredDirection;
	
	public double ActualTurretDirection;
	public double desiredTurretDirection;
	
	public float turnRate;
	public float turnTolerance;
	
	public int id;
	
	//sound
	Sound engineLoopSound, forwardSound, rotationSound;
	protected void applyRotation() {
		if(Math.abs(desiredDirection - ActualDirection)>turnTolerance) {
			if(desiredDirection>ActualDirection && Math.toDegrees(desiredDirection-ActualDirection)<180 || Math.toDegrees(desiredDirection-ActualDirection)<-180) {
				turnCoefficient += turnCoefficientStep;
				if(turnCoefficient>1) {turnCoefficient=1;}
			}else {
				turnCoefficient -= turnCoefficientStep;
				if(turnCoefficient<-1) {turnCoefficient=-1;}
			}
			ActualDirection += turnRate * turnCoefficient;
			if(ActualDirection<0) {
				ActualDirection=Math.toRadians(360)-ActualDirection;
			}
			if(ActualDirection>Math.toRadians(361)) {//360 so as not to get stuck in these two conditions
				ActualDirection=ActualDirection-Math.toRadians(360);
			}
		}else {turnCoefficient =0;}
	}
	
	protected void move() {
		x += Math.cos(ActualDirection)*speed * forwardCoefficient;
		y += Math.sin(ActualDirection)*speed * forwardCoefficient;
	}
	
	protected void applyTurretRotation() {
		if(Math.abs(desiredTurretDirection - ActualTurretDirection)>turnTolerance) {
			if(desiredTurretDirection>ActualTurretDirection && Math.toDegrees(desiredTurretDirection-ActualTurretDirection)<180 || Math.toDegrees(desiredTurretDirection-ActualTurretDirection)<-180) {
				turretTurnCoefficient += turretTurnCoefficientStep;
				if(turretTurnCoefficient>1) {turretTurnCoefficient=1;}
			}else {
				turretTurnCoefficient -= turretTurnCoefficientStep;
				if(turretTurnCoefficient<-1) {turretTurnCoefficient=-1;}
			}
			ActualTurretDirection += turnRate *(turretTurnCoefficient + turnCoefficient);
			if(ActualTurretDirection<0) {
				ActualTurretDirection=Math.toRadians(360)-ActualTurretDirection;
			}
			if(ActualTurretDirection>Math.toRadians(361)) {//360 so as not to get stuck in these two conditions
				ActualTurretDirection=ActualTurretDirection-Math.toRadians(360);
			}
			
		}else {turretTurnCoefficient =0;}
	}
	public void update() {
	}
	
	public void draw(Graphics2D g2) {
		int localx = (int)x-200/2;
		int localy = (int)y-200/2;
		
		
		
		g2.drawImage(GraphicUtils.rotate(neutral, ActualDirection+Math.toRadians(270)), localx, localy, 200,200,null);
		g2.drawImage(GraphicUtils.rotate(turret, ActualTurretDirection+Math.toRadians(270)), localx, localy, 200,200,null);
		
		
		//temp draw
		DecimalFormat df = new DecimalFormat("##");
		g2.setColor(Color.white);
		g2.drawRect((int)x, (int)y, 1, 1);
		g2.drawString("x : " + df.format(x), (int)x+15, (int)y+95);
		g2.drawString("y : " + df.format(y), (int)x+60, (int)y+95);
		g2.setColor(Color.red);
		g2.drawRect((int)x-200/2, (int)y-200/2, 200, 200);
		df = new DecimalFormat("##.##");
		g2.drawString("forCoef  : " + df.format(forwardCoefficient), localx, localy-5);
		g2.drawString("turnCoef : " + df.format(turnCoefficient), localx, localy-20);
		g2.drawString("TureCoef : " + df.format(turretTurnCoefficient), localx, localy-35);
	}
	
	//temp
	public void packetReception(byte[] bytes) {
		
	}
}
