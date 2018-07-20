import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public static final String targetHeightStr = "targetHeight";
    public static final String scaleStr = "scale";
    public static final String scheduleIntervalStr = "scheduleInterval";

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

        float[][] anchorPositions = new float[4][3];
        String content = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
        JSONObject jsonObject = new JSONObject(content);
        JSONObject tmp = null;
        for(int i = 0; i < 4; i++){
            tmp = jsonObject.getJSONObject("anchor" + i);
            anchorPositions[i][0] = (float) tmp.getDouble("x");
            anchorPositions[i][1] = (float) tmp.getDouble("y");
            anchorPositions[i][2] = (float) tmp.getDouble("z");
        }
        return anchorPositions;
    }

    public static float getMapGUIScaleCoefficient(String fileName) throws Exception{
        return getFixParameters(fileName, scaleStr);
    }

    public static float getTargetHeigh(String fileName) throws Exception{
        return getFixParameters(fileName, targetHeightStr);
    }

    public static int getScheduleInterval(String fileName) throws Exception{
        return (int)getFixParameters(fileName, scheduleIntervalStr);
    }

    private static float getFixParameters(String fileName, String parameter) throws Exception{
        float tmp = 0;
        String content = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
        JSONObject jsonObject = new JSONObject(content);
        if(parameter.equals(scheduleIntervalStr)){
            int scheduleInterval = (Integer)jsonObject.getInt(parameter);
            return scheduleInterval;
        }
        tmp = (float) jsonObject.getDouble(parameter);
        return tmp;
    }

    public class MessageFromClient{
        public int anchorID;
        public int timeStamps;
        //public List<Integer> timeStamps;
    }
    public static int getType(Class<?> type)
    {
        if(type!=null&&(String.class.isAssignableFrom(type)||Character.class.isAssignableFrom(type)||Character.TYPE.isAssignableFrom(type)||char.class.isAssignableFrom(type)))
            return 0;
        if(type!=null&&(Byte.TYPE.isAssignableFrom(type)||Short.TYPE.isAssignableFrom(type)||Integer.TYPE.isAssignableFrom(type)||Integer.class.isAssignableFrom(type)||Number.class.isAssignableFrom(type)||int.class.isAssignableFrom(type)||byte.class.isAssignableFrom(type)||short.class.isAssignableFrom(type)))
            return 1;
        if(type!=null&&(Long.TYPE.isAssignableFrom(type)||long.class.isAssignableFrom(type)))
            return 2;
        if(type!=null&&(Float.TYPE.isAssignableFrom(type)||float.class.isAssignableFrom(type)))
            return 3;
        if(type!=null&&(Double.TYPE.isAssignableFrom(type)||double.class.isAssignableFrom(type)))
            return 4;
        if(type!=null&&(Boolean.TYPE.isAssignableFrom(type)||Boolean.class.isAssignableFrom(type)||boolean.class.isAssignableFrom(type)))
            return 5;
        if(type!=null&&type.isArray())
            return 6;
        if(type!=null&&Connection.class.isAssignableFrom(type))
            return 7;
        if(type!=null&&JSONArray.class.isAssignableFrom(type))
            return 8;
        if(type!=null&&List.class.isAssignableFrom(type))
            return 9;
        if(type!=null&&Map.class.isAssignableFrom(type))
            return 10;
        return 11;
    }

    public static String toJson(Object obj)throws IllegalAccessException,JSONException
    {
        JSONObject json=new JSONObject();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            switch(getType(field.getType()))
            {
                case 0:
                    json.put(field.getName(),(field.get(obj)==null?"":field.get(obj)));
                    break;
                case 1:
                    json.put(field.getName(),(int)(field.get(obj)==null?0:field.get(obj)));
                    break;
                case 2:
                    json.put(field.getName(),(long)(field.get(obj)==null?0:field.get(obj)));
                    break;
                case 3:
                    json.put(field.getName(),(float)(field.get(obj)==null?0:field.get(obj)));
                    break;
                case 4:
                    json.put(field.getName(),(double)(field.get(obj)==null?0:field.get(obj)));
                    break;
                case 5:
                    json.put(field.getName(),(boolean)(field.get(obj)==null?false:field.get(obj)));
                    break;
                case 6:
                case 7:
                case 8://JsonArray
                    json.put(field.getName(),(field.get(obj)==null?null:field.get(obj)));
                    break;
                case 9:
                    json.put(field.getName(),  new JSONArray((List<?>)field.get(obj)));
                    break;
                case 10:
                    json.put(field.getName(),new  JSONObject((HashMap<?, ?>)field.get(obj)));
                    break;
            }
        }
        return json.toString();
    }
}
