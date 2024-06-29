package nonPlayableMenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

public class GradientButton {
	
	private int  buttonWidth;
	private int  buttonHeight;
	
	private int x;
	private int y;
	public boolean mouseIn;
	private float allCoef;
	
	private String name;
	
	public GradientButton(String name) {
		this.name = name;
	}
	
	public void updateButtonGraphics(float relativeXposition,float relativeYposition, Dimension windowDimension, float relativeWidth, float relativeSizeHeight) {
		buttonWidth=(int)(relativeWidth*windowDimension.width);
		buttonHeight=(int)(relativeSizeHeight*windowDimension.height);
		
		x=(int)(relativeXposition*windowDimension.width)-buttonWidth/2;
		y=(int)(relativeYposition*windowDimension.height)-buttonHeight/2;
	}
	
	public void updateButtonStade(int mouseX, int mouseY) {
		if(mouseX>x && mouseX<x+buttonWidth && mouseY<y+buttonHeight && mouseY>y) {
			allCoef = allCoef +0.05f;
			if(allCoef>1f) {
				allCoef=1f;
			}
			mouseIn = true;
			
		}else {
			allCoef = allCoef -0.05f;
			if(allCoef<0f) {
				allCoef=0f;
			}
			mouseIn = false;
		}
	}
	
	public void paintButton(Graphics2D g2) {
		int whitePart = (int)(buttonWidth*allCoef);
		int redPart = (int)(buttonWidth*(1-allCoef));
		
		for (int i = 0; i < whitePart; i++) {
			
			float alpha = 1f - (float)i/whitePart;
			
			
			g2.setColor(new Color(1*200f/255f,1*125f/255f,1*22f/255f,alpha));
			g2.drawLine(x+buttonWidth-i, y, x+buttonWidth-i, y+buttonHeight);
		}
		for (int i = 0; i < redPart; i++) {
			
			float alpha = 1f - (float)i/redPart;
			
			
			g2.setColor(new Color(1*207f/255f,1*75f/255f,1*48f/255f,alpha));
			g2.drawLine(x+i, y, x+i, y+buttonHeight);
		}
		g2.setColor(new Color(232,220,194));
		g2.setFont(new Font("Capture smallz", g2.getFont().getStyle(), (int)(buttonHeight*0.75f)));
		g2.drawString(name, (int)(x+buttonWidth/8 + (buttonWidth*(allCoef*0.1))), (int)(y+g2.getFont().getSize()*1.1));
		
	}
	
}
