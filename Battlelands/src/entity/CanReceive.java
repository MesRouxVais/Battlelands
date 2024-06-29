package entity;

public interface CanReceive {
	
	public abstract void packetReception(byte[] bytes);
}
