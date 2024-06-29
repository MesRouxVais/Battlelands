package entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import tpcCom.ServerUDP;
import utils.ByteUtils;

public abstract class ServerTank extends Entity{

	public ServerTank(int x, int y) {
		super(x, y);
		getImage();
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
	
	
	protected void makeSentPacket() {
		byte[] sentPacket = ByteUtils.convertIntToByte(id, 1);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte((int)x, 2));
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte((int)y, 2));
		
		int toIntAngle = (int)((ActualDirection/(2*Math.PI))*(double)65535);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte(toIntAngle, 2));
		
		toIntAngle = (int)((ActualTurretDirection/(2*Math.PI))*(double)65535);
		sentPacket = ByteUtils.linkArrays(sentPacket, ByteUtils.convertIntToByte(toIntAngle, 2));
		
		ServerUDP.serverComThread.sendMessage(sentPacket);
	}
}
