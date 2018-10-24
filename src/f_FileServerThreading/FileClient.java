package f_FileServerThreading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class FileClient {

	private static final int HOST_PORT = 5999;
	private static final int PACKET_SIZE = (int) Math.pow(2, 16) - 29;

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		// Open UDP socket
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			System.out.println("File Client started on port " + ds.getLocalPort());
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}

		
		// Enter main client loop
		while (true) {
			
			// Create packet that will be sent to server
			DatagramPacket dp = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE,
					new InetSocketAddress("localhost", HOST_PORT));
			
			
			// Try to get console input and fill packet with message
			try {
				System.out.print("> ");
				dp.setData(new BufferedReader(new InputStreamReader(System.in)).readLine().getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			
			// Try to send the package
			try {
				ds.send(dp);
			} catch (IOException e) {
				e.printStackTrace();
			}

			
			// Try to receive answer & print to console
			try {
				DatagramPacket answer = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
				ds.receive(answer);
				System.out.println("-> " + new String(answer.getData(), 0, answer.getLength()));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
