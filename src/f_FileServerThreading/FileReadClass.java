package f_FileServerThreading;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class FileReadClass extends Thread {

	private String name;
	private int line;
	private DatagramSocket socket;
	private InetSocketAddress client;
	private String path;
	private FileMonitor monitor;
	
	public FileReadClass(String name, String path,  int line, DatagramSocket socket, InetSocketAddress client, FileMonitor monitor) {
		this.name = name;
		this.path = path;
		this.line = line;
		this.socket = socket;
		this.client = client;
		this.monitor = monitor;
	}
	
	@Override
	public void run() {
		monitor.startRead();
		String returnString = readLine(name, line);
		monitor.endRead();
		
		FileServer.sendMessage(socket, client, returnString);
	}
	
	private String readLine(String name, int line) {

		// Reads line from file on disk
		
		if (line < 1)
			return "ERROR: This line number is not allowed!";
		
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(path + "/" + name));
			
			try {
				// Skip all lines before the one we need
				for (int i = 1; i < line; i++)
					br.readLine();

				// Read the relevant line
				String retLine = br.readLine();
				
				// Close reader & return result
				br.close();
				return (retLine);
			} catch (IOException e) {
				e.printStackTrace();
				return "ERROR: " + e.getMessage();
			}
			
		} catch (FileNotFoundException e) {
			return "ERROR: File " + name + " could not be found.";
		}
	}
	
}
