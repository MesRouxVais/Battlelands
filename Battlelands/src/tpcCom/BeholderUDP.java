package tpcCom;

import entity.Entity;
import main.BeholderPanel;

public class BeholderUDP {
	
	public static BeholderComThread beholderComThread;
	static  BeholderPanel beholderPanel;
	
	public BeholderUDP(int port, String serverName, BeholderPanel beholderPanel) {
		BeholderUDP.beholderPanel = beholderPanel;
		beholderComThread = new BeholderComThread(port, serverName);
	}

	public static void listeningThreadReturn(byte[] receivedBytes) {
		//System.out.println("received : " + receivedBytes.length +" byte(s)");
		if(receivedBytes.length == 9) {
			for(Entity entity : beholderPanel.entities) {
				if(entity.id == receivedBytes[0]) {
					entity.packetReception(receivedBytes);
				}
			}
		}
	}
}
