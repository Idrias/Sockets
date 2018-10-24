package f_FileServerThreading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class FileWriteClass extends Thread {

	private String name;
	private String path;
	private int line;
	private String content;
	DatagramSocket socket;
	InetSocketAddress client;
	FileMonitor monitor;
	
	
	public FileWriteClass(String name, String path, int line, String content, DatagramSocket socket, InetSocketAddress client, FileMonitor monitor) {
		this.name = name;
		this.path = path;
		this.line = line;
		this.content = content;
		this.socket = socket;
		this.client = client;
		this.monitor = monitor;
	}
	
	@Override
	public void start() {
		monitor.startWrite();
		String returnLine = writeLine(name, content, line);
		monitor.endWrite();
		
		FileServer.sendMessage(socket, client, returnLine);
	}
	
	private String writeLine(String name, String content, int line) {

		// Write line to file on disk and return the written line
		
		try {
			
			// Ensure file exists
			new File(path+"/"+name).createNewFile();

			// Read all lines that currently exist in file
			ArrayList<String> lines = new ArrayList<>();

			BufferedReader reader = new BufferedReader(new FileReader(path + "/" + name));
			String readLine = null;
			while ((readLine = reader.readLine()) != null) {
				lines.add(readLine);
			}
			reader.close();
			
			// Make sure the file will be long enough
			while(lines.size() < line) {
				lines.add("");
			}
			

			// Write new contents to file
			PrintWriter writer = new PrintWriter(path + "/" + name);
			for (int i = 1; i <= lines.size(); i++) {
				
				// Get the line we read earlier
				String storedLine = lines.get(i - 1);
				
				if (i == line)
					// Write the line the user gave us, not old one
					writer.println(content);
				else
					// Write back old content we read above
					writer.println(storedLine);
			}
			
			// Close writer & read again to ensure line replacement worked
			writer.close();
			return content;

		} catch (IOException e) {
			return "ERROR: " + e.getMessage();
		}
	}
	
}
