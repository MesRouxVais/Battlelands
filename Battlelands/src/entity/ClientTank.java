package entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import utils.ByteUtils;

public class ClientTank extends Entity implements CanReceive{
	
	
	public ClientTank(int x, int y) {
		super(x, y);
		getImage();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update() {
		//for audio and other..
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
	}

}
