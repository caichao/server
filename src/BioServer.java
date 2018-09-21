import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BioServer implements Runnable{

    private int port = 0;
    private List clientThreadPoo = null;
    private ServerSocket server = null;
    private Socket client = null;
    private List messageQueue = null;
    private Observer observer = null;

    //private BioProcessor bioProcessor = null;

    public BioServer(int port){
        this.port = port;
        this.clientThreadPoo = new ArrayList();
        this.messageQueue = new ArrayList();
        //bioProcessor = new BioProcessor(messageQueue);
    }

    public BioServer(int port, Observer observer){
        this.port = port;
        this.clientThreadPoo = new ArrayList();
        this.messageQueue = new ArrayList();
        this.observer = observer;
    }

    public void setObserver(Observer observer){
        this.observer = observer;
    }



    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
        }catch (Exception e){
            e.printStackTrace();
        }
        new Thread(new BioProcessor(this.messageQueue, this.observer)).start();
        while (true){
            try {
                client = server.accept();

                new Thread(new BioWorker(client, messageQueue)).start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
