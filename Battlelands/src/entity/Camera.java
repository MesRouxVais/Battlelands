package entity;

import java.awt.Graphics2D;

public interface Camera {
	
	public abstract int getCameraX();
	public abstract int getCameraY();
	
	
	public void drawUI(Graphics2D g2);
}
