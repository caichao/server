

import org.json.JSONObject;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class MessageHandler {

    private SelectionKey mSelectionKey = null;
    private Selector mSelector = null;
    ServerSocketChannel server = null;
    SocketChannel client = null;


    public MessageHandler(){

    }

    public MessageHandler(SelectionKey selectionKey, Selector selector){
        this.mSelectionKey = selectionKey;
        this.mSelector = selector;
    }

    public void setmSelectionKey(SelectionKey mSelectionKey) {
        this.mSelectionKey = mSelectionKey;
    }

    public void setmSelector(Selector selector){
        this.mSelector = selector;
    }

    public void acceptConnection() throws Exception{
        if (this.mSelectionKey == null && this.mSelector == null)
            return;

        if(this.mSelectionKey.isAcceptable()){
            // Tests whether this key's channel is ready to accept a new socket connection
            server = (ServerSocketChannel)mSelectionKey.channel();
            client = server.accept();
            client.configureBlocking(false);
            client.register(mSelector, SelectionKey.OP_READ
                    | SelectionKey.OP_WRITE);
        }
    }

    public void sendMessage(JSONObject jsonObject){
        if(this.mSelectionKey == null || mSelector == null)
            return;

        if(this.mSelectionKey.isWritable()){


        }
    }
}
