package tpcCom;


import entity.CanReceive;
import entity.Entity;
import main.GamePanel;

public class ServerUDP {

	public static ServerComThread serverComThread;
	static GamePanel gamePanel;
	
	public static void startServer(int port, GamePanel gamePanel) {
		serverComThread = new ServerComThread(port);
		ServerUDP.gamePanel = gamePanel;
	}
	
	public static void connectionNotification() {
		System.out.println("connection make");
		gamePanel.createsRemotePlayer();
	}
	
	public static void listeningThreadReturn(byte[] bytes) {
		//System.out.println("received : " +Arrays.toString(bytes));
		if(bytes.length == 9) {
			for(Entity entity : gamePanel.entities) {
				if(entity.id == bytes[0]) {
					((CanReceive) entity).packetReception(bytes);
				}
			}
		}
	}
}
