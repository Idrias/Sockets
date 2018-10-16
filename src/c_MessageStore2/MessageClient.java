package c_MessageStore2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import zz_helper.Client;
import zz_helper.Connection;
public class MessageClient extends Client {
	
	public void start(String host, int port) {
		while(true) {
			try {
				// Get user input
				System.out.print("> ");
				String input = new BufferedReader(new InputStreamReader(System.in)).readLine();

				// Connect
				Connection c = connect(host, port);
				if(c == null) continue;
				
				// Send command
				c.sendMessage(input);
				
				// Read message
				System.out.println(c.readMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	
		
	}

}
