package environment;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Main;
import utils.CollisionUtils;
import utils.GraphicUtils;

public class mapDisplay {
	private static File imageFile;
	private static BufferedImage image;
	private static byte[][] pixels;
	
	private static BufferedImage grass,sidewalk,road,road180;
	
	private final static int 			         GRASS_CODE = 14;
	private final static int 				  SIDEWALK_CODE = 99;
	private final static int 			     RIGHT_ROAD_CODE = 0;
	private final static int 			     LEFT_ROAD_CODE = 22;
	
	
	static double 		averageRenderTime;
	static double 		 averageCount = 0;
	
	
	public static void initiation() {
		try {
            imageFile = new File("./res/mapFolder/city-map-back.png");
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
		System.out.println("image load : " + image.getWidth() + " x "+ image.getHeight());
		 pixels = new byte[image.getWidth()][image.getHeight()];
		 for (int x = 0; x < image.getWidth(); x++) {
	            for (int y = 0; y < image.getHeight(); y++) {
	                // Récupère la valeur du pixel à la position (x, y)
	                pixels[x][y] = (byte) ((image.getRGB(x, y) & 0xFF0000) >> 16);
	            }
	     }
		 try {
			 grass = ImageIO.read(new File("./res/mapFolder/tiles/grass.png"));
			 sidewalk = ImageIO.read(new File("./res/mapFolder/tiles/sidewalk.png"));
			 road180 = GraphicUtils.rotate(ImageIO.read(new File("./res/mapFolder/tiles/road.png")), Math.PI);
			 road = ImageIO.read(new File("./res/mapFolder/tiles/road.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 Building.initiationBuildings();
	}
	
	public static void paintMap(Graphics2D g2,int relativeX,int relativeY) {
		//long startTime = System.nanoTime();
		
		int screenHeight = Main.window.getHeight();
		int screenWidth = Main.window.getWidth();
		final int TILE_SIZE = 120;
		boolean havefind=false;
		//find valid grid start
		
		for(int x = -TILE_SIZE; x < screenWidth; x++) {
			int positionRelative = x+relativeX-screenWidth/2;
			if(havefind) {
				break;
			}
			for(int y = -TILE_SIZE; y < screenWidth;y++) {
				int positionRelativeY = y+relativeY-screenHeight/2;
				//this point can be the grid start ?
				if(positionRelative % TILE_SIZE == 0 && positionRelativeY % TILE_SIZE == 0 &&positionRelative>=0&&positionRelativeY>=0) {
					//count the number of tiles to draw
					int xTileNumber = (screenWidth-x)/TILE_SIZE+1;
					int yTileNumber = (screenHeight-y)/TILE_SIZE+1;
					if(positionRelative/TILE_SIZE+xTileNumber < pixels.length && positionRelative/TILE_SIZE+yTileNumber < pixels.length) {
						for(int acutalXTile = 0;acutalXTile<xTileNumber;acutalXTile++){
							
							for(int acutalYTile = 0; acutalYTile< yTileNumber;acutalYTile++) {
								
								byte value = pixels[positionRelative/TILE_SIZE+acutalXTile][positionRelative/TILE_SIZE+xTileNumber];
								int tilePosX = x+acutalXTile*TILE_SIZE;
								int tilePosY = y+acutalYTile*TILE_SIZE;
								switch(value){
			            		   
			            	       case GRASS_CODE: 
			            	    	   g2.drawImage(grass, tilePosX, tilePosY, TILE_SIZE,TILE_SIZE,null);
			            	           break;
			            	   
			            	       case SIDEWALK_CODE:
			            	    	   g2.drawImage(sidewalk, tilePosX, tilePosY, TILE_SIZE,TILE_SIZE,null);
			            	           break;
			            	   
			            	       case RIGHT_ROAD_CODE:
			            	    	   g2.drawImage(road180, tilePosX, tilePosY, TILE_SIZE,TILE_SIZE,null);
			            	           break;
			            	           
			            	       case LEFT_ROAD_CODE:
			            	    	   g2.drawImage(road, tilePosX, tilePosY, TILE_SIZE,TILE_SIZE,null);
			            	           break;
			            	           
			            	       default:
			            	    	   g2.drawImage(grass, tilePosX, tilePosY, TILE_SIZE,TILE_SIZE,null);
			            	           break;
			            	   }
								havefind=true;
							}
						}
						break;
					}else {System.out.println("trop loin");}
				}
			}
		}
		/*
		long renderTime = System.nanoTime()-startTime;
		averageRenderTime = (renderTime+averageRenderTime*averageCount)/(averageCount+1);
		averageCount++;
		g2.setColor(new Color(0,255,0));
		g2.drawString("averageRenderTime : "+averageRenderTime + "            averageCount : "+averageCount, 100, 100);
		*/
	}
	public static void paintBuilding(Graphics2D g2) {
		Building.renderBuilding(g2);
	}
	
	
	public static boolean getEnvironmentCollisions(Point points[],Point normals[],double angle, int originX, int originY) {
		

		
		return Building.getBuildingsCollisions(points,normals,angle,originX,originY);
	}

}
