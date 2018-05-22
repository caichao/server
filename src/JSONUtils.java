import org.json.JSONObject;

import java.util.List;

public class JSONUtils {

    public static final String anchorIDHead = "anchorID";
    public static final String tdoaMeasurementHead = "tdoaMeasurement";

    // ack to the client may not be needed as we use tcp protocol
    public static final String messageTypeHead = "messageType";
    public static final String ackMessage ="ack";
    public static final String shedualMessage = "schedual";

    public static final String signalTypeMessage = "signalType";
    public static final String upChirpMessage = "up";
    public static final String downChirpMessage = "down";


    public JSONObject encodeJsonMessage(String signal) throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(messageTypeHead, shedualMessage);
        jsonObject.put(signalTypeMessage, signal);
        return jsonObject;
    }

    public MessageFromClient decodeJsonMessage(String message) throws Exception{
        MessageFromClient messageFromClient = new MessageFromClient();
        JSONObject jsonObject = new JSONObject(message);
        messageFromClient.anchorID = jsonObject.getInt(anchorIDHead);
        messageFromClient.timeStamps = jsonObject.getInt(tdoaMeasurementHead);
        return messageFromClient;
    }

    public class MessageFromClient{
        public int anchorID;
        public int timeStamps;
        //public List<Integer> timeStamps;
    }
}
