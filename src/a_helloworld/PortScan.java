package a_helloworld;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class PortScan extends Thread {
	
	public int port;
	
	@Override
	public void run() {
		testPort();
	}

	public void testPort() {
		try {
			Socket s = new Socket("localhost", port);
			System.out.println("Port " + port + " is open!");
			s.close();
		} catch (UnknownHostException e) {
			System.out.println("Unknown Host!");
		} catch (IOException e) {
			//System.out.println("Port " + port + " is closed.");
		}	
	}
	
	public static void main(String[] args) {
		
		for(int i=1; i<1024; i++) {
			PortScan t = new PortScan();
			t.port = i;
			t.start();
		}

	}

}
