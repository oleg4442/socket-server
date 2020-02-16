import java.net.*;
import java.io.*;

public class PostingClient {
 
	private static final int CLIENT_COUNT = 5;
 
    public static void main(String[] args) {
        
		for (int i = 0; i < CLIENT_COUNT; i++){
			new Thread(new ClientThread()).start();
		}
    }	
}

class ClientThread implements Runnable {
	
	@Override
	public void run() {
		String hostname = "localhost";
		int port = 4000;
 
		long m = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			try (Socket socket = new Socket(hostname, port)) {
	 
				OutputStream output = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(output, true);
				
				writer.println(i);
				
				writer.close();			
				socket.close();
	 
			} catch (UnknownHostException ex) {
	 
				System.out.println("Server not found: " + ex.getMessage());
	 
			} catch (IOException ex) {
	 
				System.out.println("I/O error: " + ex.getMessage());
			}
		}
		System.out.println("Client worked: " + (System.currentTimeMillis() - m));
	}
}

