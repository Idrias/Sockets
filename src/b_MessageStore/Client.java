package b_MessageStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	private static Socket socket;
	private static final int port = 7777;

	public static void main(String[] args) {
		while (true) {
			try {
				// Get user input
				System.out.print("> ");
				String input = new BufferedReader(new InputStreamReader(System.in)).readLine();

				// Open connection to server
				socket = new Socket("localhost", port);

				// Write command
				PrintWriter out = new PrintWriter(socket.getOutputStream());
				out.println(input);
				out.flush();

				// Read message
				System.out.println(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
