package zz_helper;

import java.io.IOException;
import java.net.Socket;

public abstract class Client {

	protected Connection connect(String host, int ip) {
		try {
			return new Connection(new Socket(host, ip));
		} catch (IOException e) {
			System.out.println("Connection could not be established.");
			return null;
		}
	}
	
	public abstract void start(String host, int port);
	
}
