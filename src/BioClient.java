import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class BioClient {

    private String ip;
    private int port;

    public BioClient(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void send(){
        try {
            Socket client = new Socket(ip, port);
            client.setSoTimeout(10000);
            //获取键盘输入
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            //获取Socket的输出流，用来发送数据到服务端
            PrintStream out = new PrintStream(client.getOutputStream());
            out.println("Hello, server");
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
