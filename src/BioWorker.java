import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;

public class BioWorker implements Runnable{

    private Socket client = null;
    private List queue = null;
    public BioWorker(Socket client, List queue){
        this.client = client;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            PrintStream out = new PrintStream(client.getOutputStream());
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
            char[] msg = new char[1024];
            String msgStr = null;
            while (true){
                buf.read(msg);
                msgStr = new String(msgStr);
                synchronized (this){
                    queue.add(msgStr);
                }
                out.println("Receive your message");
                System.out.println(msgStr);
            }
        }catch (Exception e){
            queue.remove(this); // remove in the queue
            e.printStackTrace();
        }
    }

}
