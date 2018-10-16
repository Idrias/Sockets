package zz_helper;

import java.io.IOException;
import java.net.ServerSocket;

public abstract class Server {

	private ServerSocket socket;
	private boolean started = false;
	
	public void start(int port) {
		try {
			socket = new ServerSocket(port);
			started = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public Connection acceptClient() {
		if(!started) notStarted();
		else {
			try {
				return new Connection(socket.accept());
			} catch (IOException e) {
				System.out.println("Could not accept new connection.");
			}
		}
		return null;
	}
	
	
	private void notStarted() {
		System.out.println("Error: Server is not started yet!");
	}
	
}
