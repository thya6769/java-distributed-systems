import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MySMTPServer{
	
	public static void main(String[] args) {
		try {
			int port = Integer.parseInt(args[0]);
			ServerSocket sock = new ServerSocket(port);
			
			// listen for new connections
			while(true) {
				Socket client = sock.accept();
				Thread t = new Thread(new ServerThread(client));
				t.start();
			}
		} catch(IOException e){
			System.err.println(e.getMessage());
		} 
		
	}
	
}

