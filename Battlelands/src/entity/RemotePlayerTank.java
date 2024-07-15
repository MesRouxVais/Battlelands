package entity;

import utils.ByteUtils;

public class RemotePlayerTank extends ServerTank implements CanReceive{

	public int desiredAdvancementStatus;
	
	public RemotePlayerTank(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update() {
		/*-----------------------------------------------------------------------------------
		 apply Rotation from packetReception
		 -----------------------------------------------------------------------------------*/
		applyRotation();
		
		 /*-----------------------------------------------------------------------------------
		 get movement from packetReception
		 -----------------------------------------------------------------------------------*/
		if(desiredAdvancementStatus !=0) {
			if(Math.abs(desiredDirection - ActualDirection)>turnTolerance) {
				forwardCoefficient *=0.995;
			}else if(desiredAdvancementStatus == 1) {
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
		
		applyTurretRotation();
		makeSentPacket();
		updateSound();
		
	}

	@Override
	public void packetReception(byte[] bytes) {
		desiredAdvancementStatus = bytes[1];
		
		byte[] tempBytes = {bytes[2],bytes[3]};
		int tempInt;
		
		tempInt =ByteUtils.convertByteToInt(tempBytes);
		desiredDirection = ((double)tempInt/(double)65535)*Math.PI*2;
		
		tempBytes[0] = bytes[4];
		tempBytes[1] = bytes[5];
		tempInt =ByteUtils.convertByteToInt(tempBytes);
		desiredTurretDirection =  ((double)tempInt/(double)65535)*Math.PI*2;;
	}
	
	

}
