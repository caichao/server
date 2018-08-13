import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class ButtonThread extends Thread {
    // This thread is used to schedule the anchor for signal transmission

    private final int portNum = 55555;
    private boolean isScheduleLive = true;
    private boolean isTimeToBroadcastMessage = true;
    private String messageWrapper = null;
    private int bufferLength = 1024;
    //public static final int schedualInterval = 1000; // unit in microseconds
    private byte[] buffer = new byte[bufferLength];
    private String command = null;

    public static final String launchAll = "tmux new -s main -d \"sudo ./main\"";
    public static final String killAll = "tmux sudo kill-session -t main 2> /dev/null";

    public ButtonThread(String c){
        this.command = c;
    }
    public void setCommands(String c){
        this.command = c;
    }

    @Override
    public void run() {
        super.run();
        try{

            DatagramSocket socket = new DatagramSocket();
            byte[] arr = this.command.getBytes();
            DatagramPacket packet = new DatagramPacket(arr, arr.length,InetAddress.getByName("255.255.255.255") , portNum);

            int i = 0;
            socket.send(packet);
//            System.out.println("Send a broadcast command message" + (++i));
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /*
    private void close(){
        synchronized (this){
            isScheduleLive = false;
        }
    }
    */
}
