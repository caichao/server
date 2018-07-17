import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Scanner;

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

    public static final String selfAnchorIdString = "selfAnchorId";
    public static final String capturedAnchorIdString = "capturedAnchorId";
    public static final String capturedSequenceString = "capturedSequence";
    public static final String  preambleIndexString = "preambleIndex";
    public static final String looperCounterString = "looperCounter";
    public static final String roleAnchorString = "anchor";
    public static final String roleTargetString = "target";
    public static final String roleString = "role";
    public static final String speedString = "speed";

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

    public CapturedBeaconMessage decodeCapturedBeaconMessage(String message) throws Exception{
        CapturedBeaconMessage capturedBeaconMessage = new CapturedBeaconMessage();
        JSONObject jsonObject = new JSONObject(message);
        capturedBeaconMessage.selfAnchorId = jsonObject.getInt(selfAnchorIdString);
        capturedBeaconMessage.capturedAnchorId = jsonObject.getInt(capturedAnchorIdString);
        capturedBeaconMessage.capturedSequence = jsonObject.getInt(capturedSequenceString);
        capturedBeaconMessage.preambleIndex = jsonObject.getInt(preambleIndexString);
        capturedBeaconMessage.looperCounter = jsonObject.getLong(looperCounterString);
        if(capturedBeaconMessage.selfAnchorId >= 100){
            capturedBeaconMessage.speed = (float) jsonObject.getDouble(speedString);
        }
        return  capturedBeaconMessage;
    }

    public int decodeRole(String message) throws Exception{

        JSONObject jsonObject = new JSONObject(message);
        int role = jsonObject.getInt(selfAnchorIdString);
        return role;
    }

    public static float[][] loadAnchorPosition(String fileName) throws Exception{

        float[][] anchorPositions = new float[4][2];
        String content = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
        JSONObject jsonObject = new JSONObject(content);
        JSONObject tmp = null;
        for(int i = 0; i < 4; i++){
            tmp = jsonObject.getJSONObject("anchor" + i);
            anchorPositions[i][0] = (float) tmp.getDouble("x");
            anchorPositions[i][1] = (float) tmp.getDouble("y");
        }
        return anchorPositions;
    }

    public static float getMapGUIScaleCoefficient(String fileName) throws Exception{
        float tmp = 0;
        String content = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
        JSONObject jsonObject = new JSONObject(content);
        tmp = (float) jsonObject.getDouble("scale");
        return tmp;
    }

    public class MessageFromClient{
        public int anchorID;
        public int timeStamps;
        //public List<Integer> timeStamps;
    }

}
