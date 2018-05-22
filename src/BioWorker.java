import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;

public class BioWorker implements Runnable{

    private Socket client = null;
    private List queue = null;
    private boolean isThreadAlive = true;
    public BioWorker(Socket client, List queue){
        this.client = client;
        this.queue = queue;
    }

    private void resetBuf(char[] s){
        for(int i = 0; i < s.length; i++){
            s[i] = '\0';
        }
    }
    @Override
    public void run() {
        try {
            PrintStream out = new PrintStream(client.getOutputStream());
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
            char[] msg = new char[1024];
            String msgStr = null;
            while (isThreadAlive){
                resetBuf(msg);
                int num = buf.read(msg);
                if(num == -1) { // this is a must to handle the cases, if not, the size of queue will explode
                    isThreadAlive = false;
                    break;
                }
                msgStr = new String(msg);
                synchronized (this){
                    queue.add(msgStr);
                }
                //out.println("Receive your message");
                //System.out.println(msgStr);
                //System.out.println("woker thread queue size = " + queue.size());
            }
        }catch (Exception e){
            //queue.remove(this); // remove in the queue
            e.printStackTrace();
            try {
                client.close();
                client = null;
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

}
