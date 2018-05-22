import org.json.JSONObject;

import java.util.List;

public class BioProcessor implements Runnable{

    private List queue = null;
    private String msg = null;

    public BioProcessor(List queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true){
            if(queue.size() > 0){
                msg = (String)queue.get(0);
                synchronized (this){
                    queue.remove(0);
                }

                System.out.println("message from client:" + msg);
            }
            try {
                // a delay is a must, or the loop will not run
                Thread.sleep(50);
                JSONObject jsonObject = new JSONObject(msg);
                System.out.println("identity = " + jsonObject.getInt("identity"));
                System.out.println("tdoa = " + jsonObject.getInt("tdoa"));

                // TODO: we run the particle filter here
            }catch (Exception e){
                e.printStackTrace();
            }
//            System.out.println("I am running");
//            System.out.println("queue size = " + queue.size());

        }
    }
}
