import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class P2PClient implements Runnable {
	private DatagramSocket clientSocket = null;
	private InetAddress[] ipAddresses;
	private int[] PORTS;
	public static int seqnum;
	public static String currStatus;
	
	public P2PClient(int[] port, InetAddress[] ip) {
		try {
			clientSocket = new DatagramSocket();
			ipAddresses = ip;
			PORTS = port;
			currStatus = "isUninitialized";
			seqnum = 0;
		} catch (Exception e) {

		}
	}

	public void run() {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Status: ");
		String status = "isUninitialized";
		String message = P2PTwitter.unikey + ":" + status + ":" + seqnum;

		while (true) {
			try {
				if (br.ready()) {
					status = br.readLine();
					if (status.length() > 140) {
						System.out.println("Status is too long, 140 characters max. Retry.");
					} else if (status.isEmpty()) {
						System.out.println("Status is empty. Retry.");
					} else {
						currStatus = status;
						if (status.contains(":")) {
							status = status.replace(":", "\\:");
						}
						// update message if its correct
						message = P2PTwitter.unikey + ":" + status + ":" + seqnum;
						seqnum++;
						sendMessage(message);
						checkTimes(); // update status according to times
					}
					System.out.print("Status: ");
				} else {
					if(!status.equals("isUninitialized")){
						Thread.sleep(1000);
						seqnum++;
						sendMessage(message);
					}
				}
			} catch (Exception e) {

			}
		}
	}

	private static void checkTimes() {
		System.out.println("### P2P tweets ###");
		for (String unikey : P2PTwitter.map.keySet()) {
			String pseudo = P2PTwitter.map.get(unikey)[1];

			if (unikey.equals(P2PTwitter.unikey)) {
				System.out.println("# " + pseudo + " (myself): " + currStatus);
			} else {
				String status = P2PTwitter.map.get(unikey)[3];
				if (status.contains("\\:")) {
					status = status.replace("\\:", ":");
				}
				long lasttime = Long.parseLong(P2PTwitter.map.get(unikey)[4]);
				long elapsedTime = (new Date().getTime() - lasttime) / 1000;
				// System.out.println(elapsedTime);
				if (elapsedTime >= 10 && elapsedTime < 20) {
					// change the status to idle
					P2PTwitter.map.get(unikey)[3] = "isIdle";
					System.out.println("# [" + pseudo + " (" + unikey + "): idle]");
				} else if (elapsedTime >= 20) {
					P2PTwitter.map.get(unikey)[3] = "isNoStatus";
					continue;
				} else {
					if(status.equals("isUninitialized")){
						System.out.println("# [" + pseudo + " (" + unikey + "): not yet initialized]");
					} else {
						System.out.println("# " + pseudo + " (" + unikey + "): " + status);
					}
				}
			}
		}
		System.out.println("### End tweets ###");

	}

	private void sendMessage(String message) {
		byte[] sendData = new byte[1024];
		try {
			sendData = message.getBytes("ISO-8859-1");
			// send it to different ipAddress and ports
			for (int i = 0; i < ipAddresses.length; i++) {
				// System.out.println(ipAddresses[i] + " " + PORTS[i]);
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddresses[i], PORTS[i]);
				clientSocket.send(sendPacket);
			}
		} catch (Exception e) {

		}
	}
}
