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

    public BioServer(int port){
        this.port = port;
        clientThreadPoo = new ArrayList();
        messageQueue = new ArrayList();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
        }catch (Exception e){
            e.printStackTrace();
        }

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
