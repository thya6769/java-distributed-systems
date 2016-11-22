import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Date;

public class P2PServer implements Runnable{
    
    private int PORT;
    protected DatagramSocket socket_UDP  = null;

    public P2PServer(int port) {
        try {
            this.PORT = port;
            // this receive messages sent to own port
            socket_UDP = new DatagramSocket(PORT);
        }
        catch(Exception e){
            e.printStackTrace();
        } 
    }
    public void run() {
        byte[] receiveData = new byte[1024];
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                socket_UDP.receive(receivePacket);
                String message = new String(receiveData, 0, receivePacket.getLength());
//                System.out.println(message);
                listen(message);
//                System.out.println("Received");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void listen(String msg) {
        String[] token = msg.split("(?<!\\\\):");
        String unikey = token[0];
        String status = token[1];
//        System.out.println(token[1]);
        if(token.length == 3){
            int seqnum = Integer.parseInt(token[2].trim());
            int currNum = Integer.parseInt(P2PTwitter.map.get(unikey)[5]);
            
            if(seqnum >= currNum) {
                P2PTwitter.map.get(unikey)[5] = "" + seqnum;
                P2PTwitter.map.get(unikey)[3] = status;
                P2PTwitter.map.get(unikey)[4] = "" + new Date().getTime();
            }
        } else {
            P2PTwitter.map.get(unikey)[3] = status;
            P2PTwitter.map.get(unikey)[4] = "" + new Date().getTime();
        }
    }
    

}
