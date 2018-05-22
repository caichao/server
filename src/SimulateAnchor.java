import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class SimulateAnchor extends Thread{
    private final int portNum = 12000;
    private byte[] buffer = null;
    private int bufferLength = 1024;
    @Override
    public void run() {
        super.run();
        try {
            DatagramSocket clientSocket = new DatagramSocket(portNum);
            buffer = new byte[bufferLength];
            DatagramPacket packet = new DatagramPacket(buffer,bufferLength);
            String recvMsg = "";
            while (true){
                // clear the buffer
                for(int i = 0; i < bufferLength; i++){
                    buffer[i] = 0;
                }
                clientSocket.receive(packet);
                recvMsg = new String(packet.getData()).trim();
                System.out.println(recvMsg);
                decodeScheduleMessage(recvMsg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void decodeScheduleMessage(String s) throws Exception{
        JSONObject jsonObject = new JSONObject(s);
        int parity = jsonObject.getInt("parity");
        System.out.println("parity = " + parity);
        if(parity == 0){
            System.out.println(jsonObject.get("0"));
            System.out.println(jsonObject.get("2"));
        }else {
            System.out.println(jsonObject.get("1"));
            System.out.println(jsonObject.get("3"));
        }
    }
}
