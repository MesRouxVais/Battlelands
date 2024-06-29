package tpcCom;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerComThread implements Runnable{
	
	int port;
	static DatagramSocket socket;
	private Thread serverComThread;
	
	InetAddress peerAddress;
	int peerPort;
	
	private final byte[] CONNECTION_CONFIRMED_CODE = new String("connection confirmed").getBytes();
	
	public void initialConnection() {
		try {
			socket = new DatagramSocket(port);
			//wait for initial connection
			
			byte[] receivedBytes = new byte[123];
			DatagramPacket receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
			socket.receive(receivedPacket);
			
			//call serverTCP that we have a connection
			peerAddress = receivedPacket.getAddress();
			peerPort = receivedPacket.getPort();
			ServerUDP.connectionNotification();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void confirmedConnectionReturn() {
		sendMessage(CONNECTION_CONFIRMED_CODE);
	}
	
	public ServerComThread(int port) {
		this.port = port;
		serverComThread = new Thread(this,"serverComThread");
		serverComThread.start();
	}
	
	@Override
	public void run() {
		initialConnection();
		confirmedConnectionReturn();
		
		while(true) {
			try {
				
				byte[] receivedBytes = new byte[9];
				
				DatagramPacket receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
				socket.receive(receivedPacket);
				ServerUDP.listeningThreadReturn(receivedPacket.getData());
				
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
