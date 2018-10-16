package c_MessageStore2;

public class Main extends Thread {

	private static int port = 7777;
	public boolean isServer = false;
	
	@Override
	public void run() {
		if(isServer) new MessageServer().start(port);
		else new MessageClient().start("localhost", port);
	}
	
	
	public static void main(String[] args) {
		Main thread1 = new Main();
		Main thread2 = new Main();
		
		thread1.isServer = true;
		thread1.start();
		
		thread2.isServer = false;
		thread2.start();

	}

}
