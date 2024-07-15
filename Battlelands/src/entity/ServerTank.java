package entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import audio.Sound;
import main.GamePanel;
import tpcCom.ServerUDP;
import utils.ByteUtils;

public abstract class ServerTank extends Entity{

	public ServerTank(int x, int y) {
		super(x, y);
		getImage();
		
		forwardSound = new Sound("tracks","res/tankAudio/small_tracks_rattle.wav", -20,-1,20);
		rotationSound = new Sound("tracks","res/tankAudio/tank-rotation.wav", -25,-1,20);
		engineLoopSound = new Sound("engine loop","res/tankAudio/engine-loop.wav", -10,-1,20);
		engineLoopSound.setVolume(1);
	}
	public void getImage() {
		try {
		    neutral = ImageIO.read(Files.newInputStream(Paths.get("./res/tank/KV-2_strip.png")));
		    turret = ImageIO.read(Files.newInputStream(Paths.get("./res/tank/KV-2_turret.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	abstract public void update();
	
	protected void updateSound() {
		
		int distance = (int)(Math.sqrt(Math.pow(Math.abs(x-GamePanel.soundReference.x), 2)+Math.pow(Math.abs(y-GamePanel.soundReference.y), 2)));
		if(distance >1000 ) {
			distance = 1000;
		}
		forwardSound.setVolume(Math.abs(forwardCoefficient*(1-distance/1000f)));
		rotationSound.setVolumeWithSoftening(Math.abs(turnCoefficient*(1-distance/1000f)),0.02f,turnCoefficientStep*1.5f);
		engineLoopSound.setVolume(1*(1-distance/1000f));
		
	}
	
	protected void makeSentPacket() {
		byte[] sentPacket = ByteUtils.convertIntToByte(id, 1);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte((int)x, 2));
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte((int)y, 2));
		
		int toIntAngle = (int)((ActualDirection/(2*Math.PI))*(double)65535);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte(toIntAngle, 2));
		
		toIntAngle = (int)((ActualTurretDirection/(2*Math.PI))*(double)65535);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte(toIntAngle, 2));
		
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte((int)(Math.abs(forwardCoefficient)*255f), 1));
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte((int)(Math.abs(turnCoefficient)*255f), 1));
		ServerUDP.serverComThread.sendMessage(sentPacket);
	}
}
