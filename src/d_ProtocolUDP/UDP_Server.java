package d_ProtocolUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDP_Server {

	public static int port = 4998;
	public static int buffSize = 1024;
	
	public static void main(String[] args) {
		
		// Open UDP socket
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
			socket.setBroadcast(true);
		} catch (SocketException e1) {
			e1.printStackTrace();
			return;
		}

		
		while(true) {
		try {
				// Receive datagram packet 
				DatagramPacket dp = new DatagramPacket(new byte[buffSize], buffSize);
				socket.receive(dp);
				
				// Print received message
				String message = new String(dp.getData());
				System.out.println("Got report from " + dp.getAddress() + ": " + message);
				
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		
	}
	
}
