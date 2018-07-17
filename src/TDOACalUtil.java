import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TDOACalUtil {

    private Map<Integer, List> beaconMessageMap = null;
    private float tdoa = 0;
    private TDOAMeasurement tdoaMeasurement = null;
    private float tdoaDistance = 0;
    private int fs = 48000; // sampling rate
    private int c = 340; // speed of sound
    // for debug purpose
    private List<Float> distanceList = null;

    public TDOACalUtil(){
        parameterInitialization();
    }

    private void parameterInitialization(){
        this.beaconMessageMap = new HashMap<Integer, List>();
        this.distanceList = new ArrayList<Float>();
        this.tdoaMeasurement = new TDOAMeasurement();
    }

    public float getTdoa() {
        return this.tdoa;
    }
    public TDOAMeasurement getTdoaMeasurement(){
        return this.tdoaMeasurement;
    }

    public float getTdoaDistance() {
        this.tdoaDistance = this.tdoa * 340;
        return tdoaDistance;
    }

    public void pushNewBeaconMessage(CapturedBeaconMessage capturedBeaconMessage)
    {
        CapturedBeaconMessage newCapturedBeaconMessage = null;
        try {
            newCapturedBeaconMessage = (CapturedBeaconMessage) capturedBeaconMessage.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        // maintain a list of beacon message for each anchor
        List<CapturedBeaconMessage> list = beaconMessageMap.get(capturedBeaconMessage.selfAnchorId);
        // first determine whether there are associated list created
        if(list != null){
            if(list.size() >= 5 * 2){
                list.remove(0);
            }
            list.add(newCapturedBeaconMessage);
        }else{
            List<CapturedBeaconMessage> capturedBeaconMessageList = new ArrayList<CapturedBeaconMessage>();
            capturedBeaconMessageList.add(newCapturedBeaconMessage);
            beaconMessageMap.put(newCapturedBeaconMessage.selfAnchorId, capturedBeaconMessageList);
        }
    }

    public boolean isTDOAValid(int target){
        boolean isReady = false;
        List<CapturedBeaconMessage> targetList = beaconMessageMap.get(target);
        if(targetList.size() >= 2){
            // first retrieve the newest beacon message information
            CapturedBeaconMessage one = targetList.get(targetList.size() - 1);
            CapturedBeaconMessage two = targetList.get(targetList.size() - 2);

            // get the corresponding anchor list information
            int anchorOne = one.capturedAnchorId;
            int anchorTwo = two.capturedAnchorId;
            if(anchorOne == anchorTwo) {// the captured anchor information should be different
                return false;
            }
            long targetTdoa = tdoaCal(one, two);
            List<CapturedBeaconMessage> listOne = beaconMessageMap.get(anchorOne);
            List<CapturedBeaconMessage> listTwo = beaconMessageMap.get(anchorTwo);
            if(listOne == null || listTwo == null){
                return false;
            }
            if(listOne.size() < 2 || listTwo.size() < 2){
                return false;
            }
            CapturedBeaconMessage capturedBeaconMessagePairOne = null;
            CapturedBeaconMessage capturedBeaconMessagePairTwo = null;
            capturedBeaconMessagePairOne = isContainBeaconMessage(one, listOne);
            capturedBeaconMessagePairTwo = isContainBeaconMessage(two, listOne);
            if(capturedBeaconMessagePairOne == null || capturedBeaconMessagePairTwo == null){
                return false;
            }
            long oneValidTdoa = tdoaCal(capturedBeaconMessagePairOne, capturedBeaconMessagePairTwo);

            capturedBeaconMessagePairOne = isContainBeaconMessage(one, listTwo);
            capturedBeaconMessagePairTwo = isContainBeaconMessage(two, listTwo);
            if(capturedBeaconMessagePairOne == null || capturedBeaconMessagePairTwo == null){
                return false;
            }
            long twoValidTdoa = tdoaCal(capturedBeaconMessagePairOne, capturedBeaconMessagePairTwo);

            this.tdoa = oneValidTdoa / 2 + twoValidTdoa / 2 - targetTdoa;
            this.tdoa = this.tdoa / this.fs;
            this.tdoaMeasurement.anchorIDOne = anchorOne;
            this.tdoaMeasurement.anchorIDTwo = anchorTwo;
            this.tdoaMeasurement.tdoa = this.tdoa;
            isReady = true;
        }
        return isReady;
    }

    //********************************* the following are for TDOA calculation ***********************************************88

    public boolean isTDOAReady(int anchorIdOne, int anchorIdTwo, int target){
        boolean isReady = false;
        if(beaconMessageMap.size() >= 3){
            List<CapturedBeaconMessage> listOne = beaconMessageMap.get(anchorIdOne);
            List<CapturedBeaconMessage> listTwo = beaconMessageMap.get(anchorIdTwo);
            List<CapturedBeaconMessage> targetList = beaconMessageMap.get(target);

            if(listOne.size() >= 2 && listTwo.size() >= 2 && targetList.size() >= 2){
                // if target obtain two qualified beacon message, then we can calculate the TDOA
                CapturedBeaconMessage one = targetList.get(targetList.size() - 1);
                CapturedBeaconMessage two = targetList.get(targetList.size() - 2);
                long tdoaTarget = tdoaCal(one, two);
                if(one.capturedSequence == two.capturedSequence && one.capturedAnchorId != two.capturedAnchorId){
                    // target captured qualified beacon message, then check whether anchor captures the same beacon message
                    CapturedBeaconMessage firstCapturedMessage = isContainBeaconMessage(one, listOne);
                    CapturedBeaconMessage secondCapturedMessage = isContainBeaconMessage(two, listOne);

                    if(firstCapturedMessage != null && secondCapturedMessage != null){
                        if(Math.abs(listOne.indexOf(firstCapturedMessage) - listOne.indexOf(secondCapturedMessage)) >=4){
                            return false;
                        }
                        long tdoaAnchorOne = tdoaCal(firstCapturedMessage, secondCapturedMessage);
                        // then search whether there are qualified beacon message in the second buffer
                        firstCapturedMessage = isContainBeaconMessage(one, listTwo);
                        secondCapturedMessage = isContainBeaconMessage(two, listTwo);

                        if(firstCapturedMessage != null && secondCapturedMessage != null){
                            if(Math.abs(listOne.indexOf(firstCapturedMessage) - listOne.indexOf(secondCapturedMessage)) >=4){
                                return false;
                            }
                            long tdoaAnchorTwo = tdoaCal(firstCapturedMessage, secondCapturedMessage);

                            this.tdoa = tdoaAnchorOne / 2 + tdoaAnchorTwo / 2 - tdoaTarget;
                            this.tdoa = this.tdoa / this.fs;

                            this.tdoaMeasurement.anchorIDOne = anchorIdOne;
                            this.tdoaMeasurement.anchorIDTwo = anchorIdTwo;
                            this.tdoaMeasurement.tdoa = this.tdoa;

                            isReady = true;
                            //System.out.println("TDOA distance = " + this.tdoa * 340.0f / 48000);

//                            System.out.println("tdoaAnchorOne = " + tdoaAnchorOne);
//                            System.out.println("tdoaAnchorTwo = " + tdoaAnchorTwo);
//                            System.out.println("tdoaTarget = " + tdoaTarget);
                    /*if(isBeepBeepReady(0,3)){
                        //System.out.println("distance = " + getTdoa() / 48000 * 340 / 2 + " m");

                    } */
                        }
                    }
                }
            }
        }
        return isReady;
    }

    int anchorOne;
    int anchorTwo;
    public boolean isTargetCapturedTimeStampsReady(List<CapturedBeaconMessage> list){
        boolean isReady = false;
        CapturedBeaconMessage one = list.get(list.size() - 1);
        CapturedBeaconMessage two = list.get(list.size() - 2);
        if(one.capturedSequence == two.capturedSequence){
            anchorOne = one.capturedAnchorId;
            anchorTwo = two.capturedAnchorId;
            isReady = true;
        }
        return isReady;
    }

    //*************************** the following are for BeepBeep ranging ***************************************************
    public boolean isBeepBeepReady(int anchorIdOne, int anchorIdTwo){
        boolean isReady = false;
        if(beaconMessageMap.size() >= 2){ // store the tdoa information from at least two anchors
            List<CapturedBeaconMessage> listOne = beaconMessageMap.get(anchorIdOne);
            List<CapturedBeaconMessage> listTwo = beaconMessageMap.get(anchorIdTwo);
            if(listOne.size() >= 2 && listTwo.size() >= 2){
                // use the latest information to calculate the distance
                CapturedBeaconMessage one = listOne.get(listOne.size() - 1);
                CapturedBeaconMessage two = listOne.get(listOne.size() - 2);
                int index = listOne.indexOf(one);
                int sequenceId = one.capturedSequence;
                int preSequenceId = two.capturedSequence;
                if(one.capturedAnchorId != two.capturedAnchorId){
                    if(Math.abs(sequenceId - preSequenceId) <= 1 || (preSequenceId == 3 && sequenceId == 0)){
                        long tdoaOne = tdoaCal(one, two);
                        long tdoaTwo = 0;
                        // now we begin to search the capturedBeaconMessage in the second list to see whether we have captured valid beacon message for ranging
                        CapturedBeaconMessage capturedBeaconMessageOne = isContainBeaconMessage(one, listTwo);
                        CapturedBeaconMessage capturedBeaconMessageTwo = isContainBeaconMessage(two, listTwo);
                        int indexOne = listTwo.indexOf(capturedBeaconMessageOne);
                        int indexTwo = listTwo.indexOf(capturedBeaconMessageTwo);
                        if(capturedBeaconMessageOne != null && capturedBeaconMessageTwo != null && Math.abs(indexOne - indexTwo) <= 1 && Math.abs(index - indexOne) <= 8){
                            tdoaTwo = tdoaCal(capturedBeaconMessageOne, capturedBeaconMessageTwo);
                            this.tdoa = Math.abs(Math.abs(tdoaOne) - Math.abs(tdoaTwo));
                            this.tdoa = this.tdoa / this.fs;
                            isReady = true;

                            /*distanceList.add(this.tdoa * 340.0f / 48000);
                            if(distanceList.size() > 50){
                                FileUtils.saveFloatList(distanceList, "distance"+ ScheduleAnchorThread.getScheduleRound());
                                distanceList.clear();
                            }*/
                            if(this.tdoa * 340.0f  > 5){
                                int i = 0;
                                // here, we inspect large decoding error
                            }
                        }
                    }
                }
            }
        }
        return isReady;
    }

    private long tdoaCal(CapturedBeaconMessage one, CapturedBeaconMessage two){
        long tdoa = 0;
        tdoa = one.preambleIndex - two.preambleIndex + (one.looperCounter - two.looperCounter) * 4096;
        return tdoa;
    }


    private boolean isSameBeaconMessage(CapturedBeaconMessage one, CapturedBeaconMessage two){
        boolean isTheSame = false;
        if(one.capturedSequence == two.capturedSequence
                && one.capturedAnchorId == two.capturedAnchorId){
            isTheSame = true;
        }
        return  isTheSame;
    }

    private CapturedBeaconMessage isContainBeaconMessage(CapturedBeaconMessage capturedBeaconMessage, List<CapturedBeaconMessage> list){
        CapturedBeaconMessage matchedBeaconMessage = null;
        /*for(CapturedBeaconMessage beaconMessage : list){
            if(isSameBeaconMessage(beaconMessage, capturedBeaconMessage) == true){
                matchedBeaconMessage = beaconMessage;
            }
        }*/
        // retrieve the newest timestamps
        for(int i = list.size() - 1; i >= 0; i--){
            if(isSameBeaconMessage(capturedBeaconMessage, list.get(i))){
                matchedBeaconMessage = list.get(i);
                break;
            }
        }
        return matchedBeaconMessage;
    }
}
