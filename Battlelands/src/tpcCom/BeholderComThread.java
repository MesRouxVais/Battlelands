package tpcCom;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class BeholderComThread implements Runnable{
	
	int peerPort;
	static DatagramSocket socket;
	private Thread beholderComThread;
	
	InetAddress peerAddress;
	
	public BeholderComThread(int peerPort, String serverName) {
		this.peerPort = peerPort;
		try {
			this.peerAddress = InetAddress.getByName(serverName);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
		}
		beholderComThread = new Thread(this,"beholderComThread");
		beholderComThread.start();
		System.out.println("beholderComThread.start();");
	}
	
	public void initialConnectionRequest() {
		byte[] sentBytes = {0};
		try {
			socket = new DatagramSocket();
			
			sendMessage(sentBytes);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
	}
	
	
	@Override
	public void run() {
		initialConnectionRequest();
		
		while(true) {
			try {
				
				byte[] receivedBytes = new byte[11];
				
				DatagramPacket receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
				socket.receive(receivedPacket);
				BeholderUDP.listeningThreadReturn(receivedPacket.getData());
				System.out.println(receivedPacket.getData().length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void sendMessage(byte[] sentBytes){
		try {
			DatagramPacket sentPacket = new DatagramPacket(sentBytes, sentBytes.length, peerAddress,peerPort);
			socket.send(sentPacket);
		}catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
