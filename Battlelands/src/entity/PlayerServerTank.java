package entity;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import environment.mapDisplay;
import main.KeyHandler;
import main.Main;
import main.MouseHandler;
import utils.AngleUtils;
import utils.GraphicUtils;

public class PlayerServerTank extends ServerTank implements Camera{

	private KeyHandler keyHand;
	private MouseHandler mouseHand;
	
	private float 		cameraX;
	private float 		cameraY;
	private float 		cameraSpeed = 6;
	private float 		camSpeedCoef = 0.05f;
	private boolean 	freeCam = false;
	private boolean 	cVirtualTrigger = false;
	
	BufferedImage cursor;
	BufferedImage turretAngleCursor;
	
	//-----------------------------------------------------------------------hit box setting
	final float  		TANK_HALF_WIDTH = 90;
	final float  		TANK_HALF_HEIGH = 42;
	final double 		c = Math.sqrt(Math.pow(TANK_HALF_WIDTH, 2) + Math.pow(TANK_HALF_HEIGH, 2));
	final double 		angleTop=Math.atan(Math.tan(TANK_HALF_HEIGH/TANK_HALF_WIDTH));
	//-----------------------------------------------------------------------hit box setting
	
	
	public PlayerServerTank(int x, int y, KeyHandler keyHand,MouseHandler mouseHand) {
		super(x, y);
		this.keyHand = keyHand;
		this.mouseHand = mouseHand;
		cameraX=x;
		cameraY=y;
		
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
		
		if(!keyHand.cPressed) {
			cVirtualTrigger=true;
		}else if(keyHand.cPressed && cVirtualTrigger) {
			freeCam = !freeCam;
			cVirtualTrigger=false;
		}
		
		
		
		
		 /*-----------------------------------------------------------------------------------
		 get direction inputs to  apply Rotation
		 -----------------------------------------------------------------------------------*/
		
		if(mouseHand.button1Pressed) {
			desiredDirection = AngleUtils.getAngleBetweenTwoPoints(y,x,(Main.camera.getCameraY()-Main.window.getHeight()/2)+mouseHand.button1LastY,(Main.camera.getCameraX()-Main.window.getWidth()/2)+mouseHand.button1LastX);
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
		
		move();
		
		 /*-----------------------------------------------------------------------------------
		 get turret rotation inputs to apply turret Rotation
		 -----------------------------------------------------------------------------------*/
		
		desiredTurretDirection = AngleUtils.getAngleBetweenTwoPoints(y,x,(Main.camera.getCameraY()-Main.window.getHeight()/2)+mouseHand.getMouseY(),(Main.camera.getCameraX()-Main.window.getWidth()/2)+mouseHand.getMouseX());
		applyTurretRotation();
		
		/*-----------------------------------------------------------------------------------
		Update audio
		-----------------------------------------------------------------------------------*/
		updateSound();
		
		/*-----------------------------------------------------------------------------------
		Update Camera
		-----------------------------------------------------------------------------------*/
		
		if(freeCam) {
			if(mouseHand.getMouseX()>Main.window.getWidth()*0.80f || mouseHand.getMouseX()<Main.window.getWidth()*0.20f || mouseHand.getMouseY()>Main.window.getHeight()*0.80f || mouseHand.getMouseY()<Main.window.getHeight()*0.20f) {
				if(mouseHand.getMouseX()<Main.window.getWidth()*0.20f) {
					cameraX-= cameraSpeed*camSpeedCoef;
				}else if(mouseHand.getMouseX()>Main.window.getWidth()*0.80f){
					cameraX+= cameraSpeed*camSpeedCoef;
				}
				if(mouseHand.getMouseY()>Main.window.getHeight()*0.80f) {
					cameraY+= cameraSpeed*camSpeedCoef;
				}else if (mouseHand.getMouseY()<Main.window.getHeight()*0.20f){
					cameraY-= cameraSpeed*camSpeedCoef;
				}
				
				camSpeedCoef*=1.05f;
				if(camSpeedCoef>1) {
					camSpeedCoef=1;
				}
			}else {
				camSpeedCoef=0.05f;
			}
		}else {
			int dis = (int) Math.sqrt(Math.pow(x-cameraX, 2)+Math.pow(y-cameraY, 2));
			if(dis>10) {
				double camAngle = AngleUtils.getAngleBetweenTwoPoints(cameraY,cameraX, y,x);
					System.out.println(camAngle);
					cameraX += Math.cos(camAngle)*dis*0.1f;
					cameraY += Math.sin(camAngle)*dis*0.1f;

			}else {
				cameraX = x;
				cameraY= y;
			}
		}
		
		try {
		makeSentPacket();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public int getCameraX() {
		
		return (int)cameraX;
	}

	@Override
	public int getCameraY() {
		
		return (int)cameraY;
	}
	
	public void draw(Graphics2D g2) {
		super.draw(g2);
		int screenMiddleHeight = Main.window.getHeight()/2;
		int screenMiddleWidth =  Main.window.getWidth()/2;
		
		//tempcolli
		g2.setColor(Color.green);
		Point[] pointsList = new Point[4];
		Point[] normalPointsList = new Point[4];
		
		//----angle points
		double angleBot = ActualDirection-angleTop;
		int offsetX = (int)(Math.cos(angleBot)*c);
		int offsetY = (int)(Math.sin(angleBot)*c);
		pointsList[0]= new Point((int)x+offsetX,(int)y+offsetY);
		pointsList[1]= new Point((int)x-offsetX,(int)y-offsetY);
		angleBot = ActualDirection+angleTop;
		offsetX = (int)( Math.cos(angleBot)*c);
		offsetY = (int)(Math.sin(angleBot)*c);
		pointsList[2]= new Point((int)x+offsetX,(int)y+offsetY);
		pointsList[3]= new Point((int)x-offsetX,(int)y-offsetY);
		
		for (int i = 0; i < pointsList.length; i++) {
			g2.drawOval(pointsList[i].x-5+screenMiddleWidth-(int)x, pointsList[i].y-5+screenMiddleHeight-(int)y, 10, 10);
		}
		//----angle points
		//----normal points
		normalPointsList[0]= new Point((int)x+(int)TANK_HALF_WIDTH,(int)y+(int)TANK_HALF_HEIGH);
		normalPointsList[1]= new Point((int)x+(int)TANK_HALF_WIDTH,(int)y-(int)TANK_HALF_HEIGH);
		normalPointsList[2]= new Point((int)x-(int)TANK_HALF_WIDTH,(int)y+(int)TANK_HALF_HEIGH);
		normalPointsList[3]= new Point((int)x-(int)TANK_HALF_WIDTH,(int)y-(int)TANK_HALF_HEIGH);
		
		for (int i = 0; i < normalPointsList.length; i++) {
			g2.drawOval(normalPointsList[i].x-5+screenMiddleWidth-(int)x, normalPointsList[i].y-5+screenMiddleHeight-(int)y, 10, 10);
		}
		
		//----normal points
		
		g2.setColor(Color.black);
		g2.fillRect(110+screenMiddleWidth-6, -110+screenMiddleHeight-6, 12, 12);
		
		g2.setColor(Color.red);
		if(mapDisplay.getEnvironmentCollisions(pointsList, normalPointsList,ActualDirection,(int)x,(int)y)){
			g2.setColor(Color.GREEN);
		}
		g2.fillRect(110+screenMiddleWidth-5, -110+screenMiddleHeight-5, 10, 10);
		
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
