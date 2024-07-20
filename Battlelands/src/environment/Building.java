package environment;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import main.Main;
import utils.CollisionUtils;


public class Building {
	//static---------------------------------------------
	private static BufferedImage bat, shad;
	private static ArrayList<Building> buildings = new ArrayList<Building>();
	//static---------------------------------------------
	private static final int SCALE_COEF = 2;
	//Param
	
	private Rectangle[] collisionBox;
	private int x,y;
	
	
	static void initiationBuildings() {
		try {
			 bat = ImageIO.read(new File("./res/mapFolder/bat-1.png"));
			 shad = ImageIO.read(new File("./res/mapFolder/shad-1.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buildings.add(new Building(1000,800));
		buildings.add(new Building(1000,1400));
	}
	
	public static void renderBuilding(Graphics2D g2) {
		
		float referenceX = Main.camera.getCameraX();
		float referenceY = Main.camera.getCameraY();
		float screenMiddleHeight = Main.window.getHeight()/2;
		float screenMiddleWidth =  Main.window.getWidth()/2;
		
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
		for(int i = 0; i < buildings.size(); i++) {
			float difX = buildings.get(i).x-referenceX;
			float difY = buildings.get(i).y-referenceY;
			int localx = (int)(screenMiddleWidth+difX);
			int localy = (int)(screenMiddleHeight+difY);
			
			g2.drawImage(shad, localx, localy, shad.getWidth()/SCALE_COEF,shad.getHeight()/SCALE_COEF,null);
		}
		
		
		g2.setColor(Color.cyan);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		for (int i = 0; i < buildings.size(); i++) {
			float difX = buildings.get(i).x-referenceX;
			float difY = buildings.get(i).y-referenceY;
			int localx = (int)(screenMiddleWidth+difX);
			int localy = (int)(screenMiddleHeight+difY);
			
			g2.drawImage(bat, localx, localy, bat.getWidth()/SCALE_COEF,bat.getHeight()/SCALE_COEF,null);
			
			for (int j = 0; j < buildings.get(i).collisionBox.length; j++) {
				
				g2.drawRect(localx+buildings.get(i).collisionBox[j].x, localy+buildings.get(i).collisionBox[j].y, buildings.get(i).collisionBox[j].width, buildings.get(i).collisionBox[j].height);
			}
		}
		
	}
	
	
	public Building(int x, int y) {
		this.x = x;
		this.y = y;
		
		collisionBox = new Rectangle[5];
		
		collisionBox[0] = new Rectangle(7, 150, 230, 350);
		collisionBox[1] = new Rectangle(7+230, 150-30, 220, 410);
		collisionBox[2] = new Rectangle(7+230+220, 150, 220, 350);
		collisionBox[3] = new Rectangle(7+230+220+220, 150-30, 220, 410);
		collisionBox[4] = new Rectangle(7+230+220+220+220, 150, 230, 350);
	}
	
	public static boolean getBuildingsCollisions(Point points[], Point normals[],double angle, int originX, int originY) {
		
		//tank points in (most case)
		for (int i = 0; i < buildings.size(); i++) {
			for (int j = 0; j < buildings.get(i).collisionBox.length; j++) {
				for (int l = 0; l < points.length; l++) {
					
					if(CollisionUtils.isInsideSimpleRectangle(points[l].x,points[l].y,
							buildings.get(i).collisionBox[j].x+buildings.get(i).x,
							buildings.get(i).collisionBox[j].y+buildings.get(i).y,
							buildings.get(i).collisionBox[j].x+buildings.get(i).collisionBox[j].width+buildings.get(i).x,
							buildings.get(i).collisionBox[j].y+buildings.get(i).collisionBox[j].height+buildings.get(i).y
					)) return true;
					
				}
			}
		}
		//bat points in
		for (int i = 0; i < buildings.size(); i++) {
			
			for (int j = 0; j < buildings.get(i).collisionBox.length; j++) {
				
				Point[] boxCorners = new Point[4];
				
				boxCorners[0] = new Point(buildings.get(i).collisionBox[j].x+buildings.get(i).x,buildings.get(i).collisionBox[j].y+buildings.get(i).y);
				boxCorners[1] = new Point(buildings.get(i).collisionBox[j].x+buildings.get(i).x+buildings.get(i).collisionBox[j].width,buildings.get(i).collisionBox[j].y+buildings.get(i).y);
				boxCorners[2] = new Point(buildings.get(i).collisionBox[j].x+buildings.get(i).x,buildings.get(i).collisionBox[j].y+buildings.get(i).y+buildings.get(i).collisionBox[j].height);
				boxCorners[3] = new Point(buildings.get(i).collisionBox[j].x+buildings.get(i).x+buildings.get(i).collisionBox[j].width,buildings.get(i).collisionBox[j].y+buildings.get(i).y+buildings.get(i).collisionBox[j].height);
				
				for (int k = 0; k < boxCorners.length; k++) {
					if(CollisionUtils.inInsideByRotation(normals, angle, originX, originY, boxCorners[k].x,  boxCorners[k].y)) return true;
				}
			}
			
		}
		
		return false;
	}
	
}
