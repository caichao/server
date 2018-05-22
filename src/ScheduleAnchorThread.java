import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class ScheduleAnchorThread extends Thread {
    // This thread is used to schedule the anchor for signal transmission

    private final int portNum = 12000;
    private boolean isScheduleLive = true;
    private boolean isTimeToBroadcastMessage = true;
    private String messageWrapper = null;
    private int bufferLength = 1024;
    private byte[] buffer = new byte[bufferLength];

    public void broadcastMessage(){
        synchronized (this){
            isTimeToBroadcastMessage = true;

        }
    }

    @Override
    public void run() {
        super.run();
        try{
            DatagramSocket serverSocket = new DatagramSocket();
            // broadcast ip address
            InetAddress destination = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, destination, portNum);
            int i = 0;
            while (isScheduleLive){
                if(isTimeToBroadcastMessage){
                    //isTimeToBroadcastMessage = false;
                    Thread.sleep(2000);
                    packet.setData(broadcastMessageWrapper(0,2).getBytes());
                    serverSocket.send(packet);

                    Thread.sleep(2000);
                    packet.setData(broadcastMessageWrapper(1,3).getBytes());
                    serverSocket.send(packet);
                    System.out.println("Send a broadcast message" + (++i));
                }
            }

            serverSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void close(){
        synchronized (this){
            isScheduleLive = false;
        }
    }

    /**
     * schedule anchor information
     *  in the first round, we should schedule anchors with odd number
     *  in the second round, we schedule all the anchors with even number
     * @param anchorID1
     * @param anchorID2
     * @return
     */
    public String broadcastMessageWrapper(int anchorID1, int anchorID2){
        Map map = new HashMap();
        map.put("parity", anchorID1%2); // used for the anchor to fast get the information without any exception handler
        map.put(anchorID1, "up");
        map.put(anchorID2, "down");

        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

    /*

    this is the client side examples
public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket(9999);
        while (true) {
            DatagramPacket packet = new DatagramPacket(new byte[256], 256);
            clientSocket.receive(packet);
            System.out.println(new String(packet.getData()).trim());
        }
    }

    * */
}
