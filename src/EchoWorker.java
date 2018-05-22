import org.json.JSONObject;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class EchoWorker implements Runnable {
    private List queue = new LinkedList();

    public void processData(NioServer server, SocketChannel socket, byte[] data, int count) {
        byte[] dataCopy = new byte[count];
        System.arraycopy(data, 0, dataCopy, 0, count);
        /**
         * this is where the client send the data to the server
         */
        synchronized(queue) {
            queue.add(new ServerDataEvent(server, socket, dataCopy));
            queue.notify();
        }
    }

    public void run() {
        ServerDataEvent dataEvent;

        while(true) {
            // Wait for data to become available
            synchronized(queue) {
                while(queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                    }
                }
                dataEvent = (ServerDataEvent) queue.remove(0);

                // TODO: we should process the receiverd data here
                // dataEvent.data is the data we should process or retrieve the tdoa information
            }
            //try {
                String tmp = new String(dataEvent.data);
                tmp = "server message : " + tmp;
                //JSONObject jsonObject = new JSONObject(tmp);
           // }catch (Exception e){
            //    e.printStackTrace();
            //}
            System.out.println("Recieve a message");
            // TODO: use the particle filter to obtain the locations of the target here

            // TODO: here, we should return the positions of the targets
            // Return to sender
            dataEvent.server.send(dataEvent.socket, tmp.getBytes());
        }
    }
}
