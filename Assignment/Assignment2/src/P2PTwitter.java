import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class P2PTwitter {
	
    public static String unikey;
    public static HashMap<String, String[]> map; // map to store unikeys and message
    private static InetAddress[] ipAddresses;
    private static int[] ports;
    
    public P2PTwitter(String key) {
        unikey = key;
        map = new HashMap<String, String[]>();
        readFile();
        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                System.out.println("Uncaught exception: " + ex);
            }
        };
            Thread t1 = new Thread(new P2PServer(Integer.parseInt(map.get(unikey)[2])));
            t1.setUncaughtExceptionHandler(h);
            t1.start();
        Thread t2 = new Thread(new P2PClient(ports, ipAddresses));
        t2.setUncaughtExceptionHandler(h);
        t2.start();
    }
    private void readFile() {

        try {
            Properties configuration = new Properties();
            InputStream inputStream = new FileInputStream(new File("participants.properties"));
            configuration.load(inputStream);
            
            String participants[] = (configuration.getProperty("participants")).split(",");
            String peer;
            String[] values;
            String key;
            int j = 0;
            ipAddresses = new InetAddress[participants.length - 1];
            ports = new int[participants.length - 1];
            for(int i = 0; i < participants.length; i++){
                values = new String[6];
                peer = participants[i];
                key = configuration.getProperty(peer + ".unikey");

                if(!key.equals(unikey)){
                    ipAddresses[j] = InetAddress.getByName(configuration.getProperty(peer + ".ip"));
                    ports[j] = Integer.parseInt(configuration.getProperty(peer + ".port"));
                    j++;
                }
//              values[0] = configuration.getProperty(peer + ".ip");
                values[1] = configuration.getProperty(peer + ".pseudo");
                values[2] = configuration.getProperty(peer + ".port");
                values[3] = "isUninitialized"; // status
                values[4] = "" + new Date().getTime(); // time
                values[5] = "0"; // sequence number
                map.put(key, values);
            }
            
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    /*
     * Main method
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            String usage = "Usage: input unikey as argument"; 
            System.err.println(usage);
            System.exit(1);
        }
        new P2PTwitter(args[0]);
    }
}
