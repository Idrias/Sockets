package zz_helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	
	public static PrintWriter getWriter( OutputStream stream ) {
		return new PrintWriter( stream );
	}
	
	
	public static BufferedReader getReader( InputStream stream ) {
		return new BufferedReader( new InputStreamReader(stream) );
	}
	
	
	public Connection(Socket socket) {
		this.socket = socket;
		try {
			out = getWriter(socket.getOutputStream());
			in = getReader(socket.getInputStream());
		} catch(IOException e) {
			System.out.println("Error getting IO streams of socket " + socket.getRemoteSocketAddress() + "!");
		}
	}
	
	
	public void sendMessage(String message) {
		if(out == null) {
			System.out.println("Can't send " + message + ". PrintWriter is null.");
			return;
		} 
		else {
			out.println(message);
			out.flush();
		}
	}
	
	
	public String readMessage() {
		if(in == null) {
			System.out.println("Can't read message. BufferedReader is null.");
		}
		else {
			try {
				return in.readLine();
			} catch (IOException e) {
				System.out.println("IOException during reading message from " + socket.getRemoteSocketAddress());
			}
		}
		return null;
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Could not close socket.");
		}
	}
	
	

}
