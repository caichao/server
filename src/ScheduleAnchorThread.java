import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class ScheduleAnchorThread extends Thread {
    // This thread is used to schedule the anchor for signal transmission

    private final int portNum = 22222;
    private boolean isScheduleLive = true;
    private boolean isTimeToBroadcastMessage = true;
    private String messageWrapper = null;
    private int bufferLength = 1024;
    //public static final int schedualInterval = 1000; // unit in microseconds
    private byte[] buffer = new byte[bufferLength];
    private static int sequence = 0;
    private int schedualInterval = 0;
    private String configFilePath = "config.txt";
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
            schedualInterval = JSONUtils.getScheduleInterval(configFilePath);
            List<Integer> scheduleTurns = new ArrayList<Integer>();
            for(int j = 0; j < 4; j++){
                scheduleTurns.add(j);
            }
            int i = 0;
            while (isScheduleLive){
                if(isTimeToBroadcastMessage){
                    //isTimeToBroadcastMessage = false;
//                    Thread.sleep(1000);
//                    packet.setData(broadcastMessageWrapper(0,2).getBytes());
//                    serverSocket.send(packet);
//                    Thread.sleep(1000);
//                    packet.setData(broadcastMessageWrapper(1,3).getBytes());
//                    serverSocket.send(packet);

                    //Collections.shuffle(scheduleTurns);

                    /*
                    * setData()用来设置数据包的规格
                    * */

                    packet.setData(schedualAnchor(scheduleTurns.get(0), sequence % 4).getBytes());
                    serverSocket.send(packet);
                    Thread.sleep(schedualInterval);

                    packet.setData(schedualAnchor(scheduleTurns.get(1), sequence % 4).getBytes());
                    serverSocket.send(packet);
                    Thread.sleep(schedualInterval);

                    packet.setData(schedualAnchor(scheduleTurns.get(2), sequence % 4).getBytes());
                    serverSocket.send(packet);
                    Thread.sleep(schedualInterval);

                    packet.setData(schedualAnchor(scheduleTurns.get(3), sequence % 4).getBytes());
                    serverSocket.send(packet);
                    Thread.sleep(schedualInterval);
                    sequence++;
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

    public static int getScheduleRound(){
        return sequence;
    }

    /**
     * deprecated method
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
        /*map.put("up", anchorID1);
        map.put("down", anchorID2);*/

        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

    /**
     * schedual the anchors with sequence ID
     * @param anchorId: the ID of which that is time to transmit beacon message
     * @param sequenceId: the sequence of the schedule round
     * @return json format information
     */
    public String schedualAnchor(int anchorId, int sequenceId){
        Map map = new HashMap();
        map.put("anchorId", anchorId); // used for the anchor to fast get the information without any exception handler
        map.put("sequenceId", sequenceId);
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
