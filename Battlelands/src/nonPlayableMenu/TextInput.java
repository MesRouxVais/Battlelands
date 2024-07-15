package nonPlayableMenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import main.KeyHandler;

public class TextInput implements ReceiveKeyboardNotification{
	
	private int  buttonWidth;
	private int  buttonHeight;
	
	private int x;
	private int y;
	public boolean mouseIn;
	
	KeyHandler keyHandler;
	String value= "";
	
	float allCoef = 0;
	
	int mouseX;
	
	int offset = 0;
	int offsetPosition = 0;
	
	boolean recalculateOffset = false;
	boolean recalculateOffsetPosition = false;
	int relativeTime = 0;
	int delayOffsetDisplay = 40;
	
	
	public TextInput(KeyHandler keyHandler) {
		this.keyHandler = keyHandler;
		keyHandler.addNotification(this);
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
		
		this.mouseX = mouseX;
		relativeTime++;
		System.out.println(value);
	}
	
	public void paintButton(Graphics2D g2) {
		
		g2.setColor(new Color(1f,1f,1f,0.2f));
		g2.fillRect(x, y, buttonWidth, buttonHeight);
		g2.setColor(Color.gray);
		
		g2.setFont(new Font("arial", g2.getFont().getStyle(), buttonHeight));
		g2.setColor(new Color(allCoef*200f/255f + 0.5f*(1-allCoef),allCoef*125f/255f + 0.5f*(1-allCoef) ,allCoef*22f/255f + 0.5f*(1-allCoef),1));
		g2.drawString(value, (int)(x+buttonWidth/8), y+buttonHeight-buttonHeight/5);
		
		if(value == "") {
			g2.setColor(Color.gray);
			g2.drawString("host ip...", (int)(x+buttonWidth/8), y+buttonHeight-buttonHeight/5);
		}
		
		if(mouseIn) {
			
			g2.setColor(new Color(1*200f/255f,1*125f/255f,1*22f/255f,allCoef));
			g2.fillRect(x, y, buttonWidth-buttonHeight/10, buttonHeight/10);
			g2.fillRect(x, y+buttonHeight, buttonWidth, buttonHeight/10);
			g2.fillRect(x, y, buttonHeight/10, buttonHeight);
			g2.fillRect(x+buttonWidth-buttonHeight/10, y, buttonHeight/10, buttonHeight);
			
		}
		
		//fontMetrics = g2.getFontMetrics();
        //bounds = fontMetrics.getStringBounds(value, g2);

        // La largeur totale de la chaîne de caractères
        //System.out.println(bounds.getWidth());
		if(recalculateOffset) {
			tempExpe(g2);
		}else if(recalculateOffsetPosition) {
			calculateOffsetPosition(g2);
		}
		
		if(relativeTime>delayOffsetDisplay) {
			g2.setColor(new Color(1,1,1,1f));
			g2.fillRect(offsetPosition+(int)(x+buttonWidth/8), y+2, 2, buttonHeight-4);
			if(relativeTime>2*delayOffsetDisplay) {
				relativeTime = 0;
			}
		}
	}

	@Override
	public void receiveKeyboardNotification(char key) {
		String stringStart ="";
		String stringEnd="";
		
		stringStart =  String.copyValueOf(value.toCharArray(), 0, offset);
		stringEnd = String.copyValueOf(value.toCharArray(), offset, value.length()-stringStart.length());
		
		if(mouseIn) {
			if(key == 8) {
				stringStart = removeLastChar(stringStart);
				value = stringStart + stringEnd;
				offset--;
				if(offset<0) {
					offset=0;
				}
				if(value.length()<=0) {
					value="";
				}
				recalculateOffsetPosition = true;
				
			}else {
				stringStart += key;
				value = stringStart + stringEnd; 
				offset++;
				recalculateOffsetPosition = true;
			}
		}
	}
	
	public static String removeLastChar(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, str.length() - 1);
    }
	
	public void tempExpe(Graphics2D g2) {
		if(mouseIn) {
			int relativeMouseX = mouseX-(int)(x+buttonWidth/8);
			FontMetrics fM = g2.getFontMetrics();
			for(int i = 0; i< value.length();i++) {
				String copyString = String.copyValueOf(value.toCharArray(), 0, i);
				
				int width = (int) (fM.getStringBounds(copyString, g2)).getWidth();
				offsetPosition = width;
				if(width > relativeMouseX) {
					offset = copyString.length();
					break;
				}else {
					offset=value.length();
					width = (int) (fM.getStringBounds(value, g2)).getWidth();
					offsetPosition = width;
				}
			}
		}
		recalculateOffset = false;
		recalculateOffsetPosition = false;
	}
	
	public void calculateOffsetPosition(Graphics2D g2){
		String copyString = String.copyValueOf(value.toCharArray(), 0, offset);
		FontMetrics fM = g2.getFontMetrics();
		int width = (int) (fM.getStringBounds(copyString, g2)).getWidth();
		offsetPosition = width;
	}
}
