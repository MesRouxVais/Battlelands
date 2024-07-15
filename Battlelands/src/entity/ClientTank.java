package entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import audio.Sound;
import main.BeholderPanel;
import utils.ByteUtils;

public class ClientTank extends Entity implements CanReceive{
	BeholderPanel beholderPanel;
	
	public ClientTank(int x, int y, BeholderPanel beholderPanel) {
		super(x, y);
		this.beholderPanel=beholderPanel;
		getImage();
		// TODO Auto-generated constructor stub
		//createSound();
		forwardSound = new Sound("tracks","res/tankAudio/small_tracks_rattle.wav", -20,-1,20);
		rotationSound = new Sound("tracks","res/tankAudio/tank-rotation.wav", -25,-1,20);
		engineLoopSound = new Sound("engine loop","res/tankAudio/engine-loop.wav", -10,-1,20);
		engineLoopSound.setVolume(1);
	}

	@Override
	public void update() {
		updateSound();
	}
	
	protected void updateSound() {
		
		//distance = (int)(Math.sqrt(Math.abs(x-gamePanel.soundReference.x)+Math.abs(y-gamePanel.soundReference.y)));
		int distance = (int)(Math.sqrt(Math.pow(Math.abs(x-beholderPanel.soundReference.x), 2)+Math.pow(Math.abs(y-beholderPanel.soundReference.y), 2)));
		if(distance >1000 ) {
			distance = 1000;
		}
		forwardSound.setVolume(Math.abs(forwardCoefficient*(1-distance/1000f)));
		rotationSound.setVolumeWithSoftening(Math.abs(turnCoefficient*(1-distance/1000f)),0.02f,turnCoefficientStep*1.5f);
		engineLoopSound.setVolume(1*(1-distance/1000f));
		System.out.println("[client tank]engineLoopSound.setVolume : "+1*(1-distance/1000f));
	}
	
	public void getImage() {
		try {
		    neutral = ImageIO.read(Files.newInputStream(Paths.get("./res/tank/KV-2_strip.png")));
		    turret = ImageIO.read(Files.newInputStream(Paths.get("./res/tank/KV-2_turret.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void packetReception(byte[] bytes) {
		System.out.println("packetReception  beholder");
		byte[] tempBytes = {bytes[1],bytes[2]};
		int tempInt;
		
		x= ByteUtils.convertByteToInt(tempBytes);
		tempBytes[0] = bytes[3];
		tempBytes[1] = bytes[4];
		y= ByteUtils.convertByteToInt(tempBytes);
		
		tempBytes[0] = bytes[5];
		tempBytes[1] = bytes[6];
		
		tempInt =ByteUtils.convertByteToInt(tempBytes);
		ActualDirection = ((double)tempInt/(double)65535)*Math.PI*2;
		
		tempBytes[0] = bytes[7];
		tempBytes[1] = bytes[8];
		
		tempInt =ByteUtils.convertByteToInt(tempBytes);
		ActualTurretDirection =  ((double)tempInt/(double)65535)*Math.PI*2;
		
		
		forwardCoefficient = ByteUtils.convertByteToInt(bytes[9])/255f;
		turnCoefficient = ByteUtils.convertByteToInt(bytes[10])/255f;
	}

}
