import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerThread extends Thread implements Subject{

    private String ip;
    private int port;
    private boolean isThreadAlive = true;

    private ServerSocketChannel mServerSocketChannel = null;
    private InetSocketAddress mInetSocketAddress = null;
    private Selector mSelector = null;
    //private SelectionKey mSelectionKey = null;
    private ByteBuffer mReceiveBuffer = null;
    private ByteBuffer mSendBuffer = null;

    private Map<Integer,SelectionKey> clientPools = null;


    public ServerThread(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    private void initServer() throws Exception{
        // Selector: multiplexor of SelectableChannel objects
        mSelector = Selector.open(); // selector is open

        // ServerSocketChannel: selectable channel for stream-oriented listening sockets
        mServerSocketChannel = ServerSocketChannel.open();
        mInetSocketAddress = new InetSocketAddress(this.port);

        // Binds the channel's socket to a local address and configures the socket to listen for connections
        mServerSocketChannel.bind(mInetSocketAddress);

        // Adjusts this channel's blocking mode.
        mServerSocketChannel.configureBlocking(false);

        int ops = mServerSocketChannel.validOps();
        mServerSocketChannel.register(mSelector, ops,null);

        //clientPools = new HashMap<>();
    }

    @Override
    public void run() {
        super.run();
        try {
            initServer();
        }catch (Exception e){
            e.printStackTrace();
        }
        while (isThreadAlive){
            try {
                // Selects a set of keys whose corresponding channels are ready for I/O operations
                mSelector.select();
                // token representing the registration of a SelectableChannel with a Selector
                Set<SelectionKey> selectionKeys = mSelector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();


                // process the received message
                while (selectionKeyIterator.hasNext() && isThreadAlive) {
                    SelectionKey myKey = selectionKeyIterator.next();
                    selectionKeyIterator.remove();;
                    // Tests whether this key's channel is ready to accept a new socket connection
                    if(!myKey.isValid())
                        continue;
                    if (myKey.isValid() && myKey.isAcceptable()) {
                        SocketChannel socketChannel = mServerSocketChannel.accept();

                        // Adjusts this channel's blocking mode to false
                        socketChannel.configureBlocking(false);

                        // Operation-set bit for read operations
                        socketChannel.register(mSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        //log("Connection Accepted: " + crunchifyClient.getLocalAddress() + "\n");

                        // Tests whether this key's channel is ready for reading
                        // if the connection is closed by the remote client, the key is still readable
                        continue;
                    }
                    if (myKey.isValid() && myKey.isReadable()) {
                        //if(myKey.isConnectable()){
                            SocketChannel socketChannel = (SocketChannel) myKey.channel();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                            if(socketChannel != null && socketChannel.isConnected()){
                                int count = socketChannel.read(byteBuffer);
                                if(count == -1){
                                    // the only way to close a remote connection
                                    //socketChannel.getRemoteAddress();
                                    socketChannel.close();
                                }
                            }else {
                                myKey.cancel();
                            }
                            String result = new String(byteBuffer.array()).trim();

                            // notify the anchor messages and distributed to the observer
                            /****************************************************************************************/
                            setNotifyMessage(result);

                            System.out.println("Rec from client = " + result);
                            //mSelectionKey.interestOps(SelectionKey.OP_WRITE);
                            if(myKey.isWritable()){

                                ByteBuffer buffer = ByteBuffer.allocate(256);
                                //byteBuffer = "ack".getBytes();
                                Map map = new HashMap<>();
                                map.put("taretId",1234);
                                map.put("x", 1.0f);
                                map.put("y", 2.3f);
                                JSONObject jsonObject = new JSONObject(map);
                                socketChannel.write(buffer.wrap(jsonObject.toString().getBytes()));
                                System.out.println("I can also send message to you");

                            }

                    }
                    //selectionKeyIterator.remove();

                }
                mServerSocketChannel.close();

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }


    public void close(){
        synchronized (this) {
            isThreadAlive = false;
        }
    }

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

    public void setNotifyMessage(String msg){
        this.notifyMessage = msg;
        notifyObserver();
    }

    /*public class ClientTag{
        public int anchorID;
        public SocketChannel socketChannel;
    }*/
}
