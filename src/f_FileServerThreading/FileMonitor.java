package f_FileServerThreading;

public class FileMonitor {

	private String fileName;
	private int readingCtr = 0;
	private boolean writerActive = false;
	
	FileMonitor(String fileName) {
		this.fileName = fileName;
	}
	
	public synchronized void startRead() {
		// Wait as long as a writer is active
		while(writerActive)
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		readingCtr++;
	}
	
	public synchronized void startWrite() {
		// Wait as long a writer or any reader is active
		while(writerActive || readingCtr > 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			writerActive = true;
		}
	}
	
	public synchronized void endRead() {
		readingCtr--;
		this.notifyAll();
	}
	
	public synchronized void endWrite() {
		writerActive = false;
		this.notifyAll();
	}
	
	
	public String getName() {
		return fileName;
	}
	
}
