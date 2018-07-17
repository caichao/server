import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BioProcessor implements Runnable, Subject {

    private List queue = null;
    private String msg = null;

    public BioProcessor(List queue){
        this.queue = queue;
    }

    public BioProcessor(List queue, Observer observer){
        this.queue = queue;
        this.addObserver(observer);
    }

    @Override
    public void run() {
        while (true){
            if(queue.size() > 0){
                msg = (String)queue.get(0);
                synchronized (this){
                    queue.remove(0);
                }

                //System.out.println("message from client:" + msg);
                try {
                    //JSONObject jsonObject = new JSONObject(msg);
                    //System.out.println("identity = " + jsonObject.getInt("identity"));
                    //System.out.println("tdoa = " + jsonObject.getInt("tdoa"));
                    // the following line send the data to the particle filter thread
                    this.setNotificationMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            try {
                // a delay is a must, or the loop will not run
                Thread.sleep(10);

            }catch (Exception e){
                e.printStackTrace();
            }
//            System.out.println("I am running");
//            System.out.println("queue size = " + queue.size());

        }
    }

    /**********************************************************/
    // notify the message to the observer using observer-subject pattern
    private List<Observer> observerList = new ArrayList<>();
    private String notifyMessage = "";

    @Override
    public void addObserver(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        int index = observerList.indexOf(observer);
        if( index >= 0 ){
            observerList.remove(index);
        }
    }

    @Override
    public void notifyObserver() {
        for(Observer observer : observerList){
            observer.update(notifyMessage);
        }
    }

    public void setNotificationMessage(String msg){
        this.notifyMessage = msg;
        notifyObserver();
    }

    /*@Override
    public void addObserver(Observer observer) {

    }

    @Override
    public void removeObserver(Observer observer) {

    }

    @Override
    public void notifyObserver() {

    }
    */
}
