package d_ProtocolUDP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UDP_Client {

	public static int port = 4998;
	
	public static void main(String[] args) {
		
		// Open UDP socket
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		
		while(true) {
			
			// Get user input
			String input = null;
			try {
				System.out.print(">");
				input = new BufferedReader(new InputStreamReader(System.in)).readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Send message as broadcast
			byte[] bytes = input.getBytes();
			try {
				System.out.println(bytes + " " + bytes.length);
				socket.setBroadcast(true);
				socket.send(new DatagramPacket(bytes, bytes.length, new InetSocketAddress("255.255.255.255", port)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
}
