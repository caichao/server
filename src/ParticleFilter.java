import org.json.JSONObject;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.*;

public class ParticleFilter extends Thread implements Observer{

    private int numberOfParticles = 2000;

    private Random random = null;
    private float particles[][] = null; // all the particles
    private float weights[] = null; // weights for all the particles
    private float errors[] = null;
    private float intermediateWeights[] = null; // weights for the topParticleNumber
    private float x = 0;
    private float y = 0;
    private float z = 1;
    public static final int topParticleNumber = 50;
    private int numberOfValidParticles = numberOfParticles;

    private float c = 340;

    // coordinates of the anchors
    private float landmarks[][] = null; // new float[][]{{0, 0}, {6.8f, 0}, {6.8f, 2.3f}, {0, 2.3f}};

    // parameter for the guassian function
    private float mu = 0;
    private float sigma = 0.5f;

    //******************************** about message handler ***************************
    private Subject subject = null;
    private boolean isParticleThreadAlive = true;
    private boolean isTimeToPerformEstimation = false;

    //config file
    private String configFilePath = "config.txt";
    private int scheduleInterval = 0;
    // decoding uploaded information
    JSONUtils jsonUtils = null;
    MainFrame mainFrame = null;

    public void setMainFrame(MainFrame mainFrame){
        this.mainFrame = mainFrame;
    }

    public void setSubject(Subject subject){
        this.subject = subject;
        subject.addObserver(this);
    }

    public void setNumberOfParticles(int numberOfParticles){
        this.numberOfParticles = numberOfParticles;
    }

    public void close(){
        synchronized (this){
            isParticleThreadAlive = false;
        }
    }

    public void performParticleUpdate(){
        synchronized (this){
            isTimeToPerformEstimation = true;
        }
    }

    /**
     * default initialization constructor
     */
    public ParticleFilter(){
        prameterInitialization();
    }

    /**
     * initialize the particle filter with particle number
     * @param particleNumber
     */
    public ParticleFilter(int particleNumber){
        this.numberOfParticles = particleNumber;
        prameterInitialization();
    }

    void prameterInitialization(){
        random = new Random();
        particles = new float[numberOfParticles][2];
        weights = new float[numberOfParticles];
        errors = new float[numberOfParticles];
        intermediateWeights = new float[topParticleNumber];
        jsonUtils = new JSONUtils();
        try {

                landmarks = jsonUtils.loadAnchorPosition(configFilePath);
                this.z = JSONUtils.getTargetHeigh(configFilePath);
                scheduleInterval = JSONUtils.getScheduleInterval(configFilePath);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * generate particles with loaded anchor coordinates
     */
    public void generateUniformParticles(){
        float maxXCoordinates = 0;
        float maxYCoordinates = 0;
        float maxZCoordinates = 0;

        for(int i = 0; i < landmarks.length; i++){
            if(landmarks[i][0] > maxXCoordinates){
                maxXCoordinates = landmarks[i][0];
            }
            if(landmarks[i][1] > maxYCoordinates){
                maxYCoordinates = landmarks[i][1];
            }
        }

        generateUniformParticles(maxXCoordinates, maxYCoordinates);
        //generateUniformParticles(8, 5);
    }

    /**
     * generate particles with input x and y value
     * @param x - maximum value on x axis
     * @param y - maximum value on y axis
     */
    public void generateUniformParticles(float x, float y){
        for(int i = 0; i < numberOfParticles; i++){
            particles[i][0] = x * random.nextFloat();
            particles[i][1] = y * random.nextFloat();
        }
    }

    /**
     * update the anchor position based on the estimated speed
     * @param speed: units in meters per second
     * @param anchorId: captured anchor ID
     */
    public void predict(float speed, int anchorId){
        // to achieve computation efficiency, we only update the particles with top weights
        float denominator = 0.0f;
        float displacement = speed * scheduleInterval;
        float distance = 0;
        float coeff = 0;
        for(int i = 0; i < topParticleNumber; i++){
            // project the speed to 2D plane
            //distance = euclidianDistance(particles[i], landmarks[anchorId]);
//            distance += Math.pow(particles[i][0] - landmarks[anchorId][0], 2);
//            distance += Math.pow(particles[i][1] - landmarks[anchorId][1], 2);
//            distance += Math.pow(this.z - landmarks[anchorId][2], 2);
//            coeff = Math.abs(landmarks[anchorId][2] - this.z);
//            coeff = coeff / distance;
//            coeff = (float) Math.sqrt(1 - coeff * coeff);
//            speed = speed * coeff;

            denominator = (float) Math.sqrt(Math.pow(particles[i][0] - landmarks[anchorId][0], 2) + Math.pow(particles[i][1] - landmarks[anchorId][1], 2));
            particles[i][0] += displacement / denominator * (landmarks[anchorId][0] - particles[i][0]);
            particles[i][1] += displacement / denominator * (landmarks[anchorId][1] - particles[i][1]);
        }
    }

    /**
     * update the weights of the particles based on multiple tdoa measurements
     * @param tdoaMeasurement
     */
    public void update(TDOAMeasurement tdoaMeasurement[], int numberOfParticles){
        // reinitiale the weights
        for(int i = 0; i < numberOfParticles ; i++){
            weights[i] = 1.0f;
            //errors[i] = 0.0f;
        }
        // reweight the samples according to the measuremnents
        for(int i = 0; i < tdoaMeasurement.length; i++){
            float d1 = 0;
            float d2 = 0;
            float error = 0;
            for(int j = 0; j < numberOfParticles; j++){
                d1 = Algorithm.euclidianDistance(particles[j], landmarks[tdoaMeasurement[i].anchorIDOne]);
                d2 = Algorithm.euclidianDistance(particles[j], landmarks[tdoaMeasurement[i].anchorIDTwo]);
                error = (float)Math.abs((d1 - d2) - this.c * tdoaMeasurement[i].tdoa);
                weights[j] *= Algorithm.guassian((float) pow(error, 2), mu,sigma);
            }
        }

        // update the weight
//        for(int j = 0; j < numberOfParticles; j++) {
//            weights[j] *= guassian(errors[j]);
//        }
        // avoid round-off error
        for(int i = 0; i < numberOfParticles ; i++){
            weights[i] += 1e-30;
        }
        // normalize the weights
        float sum = 0;
        for(int i = 0; i < numberOfParticles; i++){
            sum += weights[i];
        }
        for(int j = 0; j < numberOfParticles; j++){
            weights[j] /= sum;
        }
    }

    public void update(List<TDOAMeasurement> tdoaMeasurement, int numberOfParticles){
        // reinitiale the weights
        for(int i = 0; i < numberOfParticles ; i++){
            weights[i] = 1.0f;
            //errors[i] = 0.0f;
        }
        // reweight the samples according to the measuremnents
        for(int i = 0; i < tdoaMeasurement.size(); i++){
            float d1 = 0;
            float d2 = 0;
            float error = 0;
            for(int j = 0; j < numberOfParticles; j++){
                d1 = Algorithm.euclidianDistance(particles[j], landmarks[tdoaMeasurement.get(i).anchorIDOne]);
                d2 = Algorithm.euclidianDistance(particles[j], landmarks[tdoaMeasurement.get(i).anchorIDTwo]);
                error = (float)Math.abs((d1 - d2) + this.c * tdoaMeasurement.get(i).tdoa);   //(13)
                weights[j] *= Algorithm.guassian((float) pow(error, 2),mu, sigma);   //weights of the ith particle  式(14)
            }
        }

        // update the weight
//        for(int j = 0; j < numberOfParticles; j++) {
//            weights[j] *= guassian(errors[j]);
//        }
        // avoid round-off error
        for(int i = 0; i < numberOfParticles ; i++){
            weights[i] += 1e-30;    // ξ***
        }
        // normalize the weights
        float sum = 0;
        for(int i = 0; i < numberOfParticles; i++){
            sum += weights[i];
        }
        for(int j = 0; j < numberOfParticles; j++){
            weights[j] /= sum;
        }
    }

    /**
     * update the particle state based on one TDOA measurement
     * @param tdoaMeasurement
     */
    public void updateOneShot(TDOAMeasurement tdoaMeasurement, int numberOfParticles){
        // reinitiale the weights
        for(int i = 0; i < numberOfParticles ; i++){
            weights[i] = 1.0f;
        }
        // reweight the samples according to the measuremnents
            float d1 = 0;
            float d2 = 0;
            float error = 0;
            for(int j = 0; j < numberOfParticles; j++){
                d1 = Algorithm.euclidianDistance(particles[j], landmarks[tdoaMeasurement.anchorIDOne]);
                d2 = Algorithm.euclidianDistance(particles[j], landmarks[tdoaMeasurement.anchorIDTwo]);
                error = Math.abs(d1 - d2 - c * tdoaMeasurement.tdoa);

                // update the weight
                weights[j] *= Algorithm.guassian(error, mu, sigma);
            }

        // avoid round-off error
        for(int i = 0; i < numberOfParticles ; i++){
            weights[i] += 1e-30;
        }
        // normalize the weights
        float sum = 0;
        for(int i = 0; i < numberOfParticles; i++){
            sum += weights[i];
        }
        for(int j = 0; j < numberOfParticles; j++){
            weights[j] /= sum;
        }
    }

    /**
     * regenerate the new particles surround the particles with the higheset weights
     * @param indexes: index for the particles with highest particles
     * @param numberOfParticles: number of newly generated particles
     * @param scale: variance of newly generated particles
     *  return: the return value are the truly valable number of particles
     */
    public int resampleAndRegenerate(int[] indexes, int numberOfParticles, float scale){
        // to be memory efficient, here we reuse the particles and weights

        // first normalize the weights
        float sum = 0;
        for(int i = 0; i < indexes.length; i++){
            sum += weights[indexes[i]];
        }
        for(int i = 0; i < indexes.length; i++){
            weights[indexes[i]] /= sum;
        }
        // end of the normalization process

        // replace the particles and weights
        for(int i = 0; i < indexes.length; i++){
            weights[i] = weights[indexes[i]];
            particles[i] = particles[indexes[i]];
        }
        int availableNumberOfParticles = 0;
        int shift = indexes.length;
        for(int i = 0; i < indexes.length; i++){
            // the number of newly generated particles are also based on their weights
            int num = (int) Math.floor(weights[i] * numberOfParticles); //返回最大的Double值

            for(int j = 0; j < num ; j++){
                particles[(shift + availableNumberOfParticles) % this.numberOfParticles][0] =  (2 * random.nextFloat() - 1 ) * scale + particles[i][0];
                particles[(shift + availableNumberOfParticles) % this.numberOfParticles][1] =  (2 * random.nextFloat() - 1 ) * scale + particles[i][1];
                //particles[(shift + availableNumberOfParticles) % this.numberOfParticles][2] =  (2 * random.nextFloat() - 1 ) * scale + particles[i][2];
            }

            availableNumberOfParticles += num;
        }

        return availableNumberOfParticles;
    }

    public void estimateIntermediate(){
        float xx = 0;
        float yy = 0;
        float zz = this.z;
        float sum = 0.0f;

        for(int i = 0; i < topParticleNumber; i++){
            intermediateWeights[i] = weights[i];
        }
        // we should first normalize the weights
        for(int i = 0; i < topParticleNumber; i++)
            sum += intermediateWeights[i];
        for(int i = 0; i < topParticleNumber; i++){
            intermediateWeights[i] /= sum;
        }
        for(int i = 0; i < topParticleNumber; i++){
            xx += intermediateWeights[i] * particles[i][0];
            yy += intermediateWeights[i] * particles[i][1];
            //zz += intermediateWeights[i] * particles[i][2];
        }
        /*x = xx / numberOfParticles;
        y =  yy / numberOfParticles; */
        this.x = xx;
        this.y = yy;
        //this.z = zz;
    }

    /**
     * estimate the final location based on all the available particles and the corresponding weight
     */
    public void estimate(int numberOfParticles){
        float xx = 0;
        float yy = 0;
        float zz = this.z;
        if(numberOfParticles > this.numberOfValidParticles)
            numberOfParticles = this.numberOfValidParticles;

        float sum = 0.0f;
        // we should first normalize the weights
        for(int i = 0; i < numberOfParticles; i++)
            sum += weights[i];
        for(int i = 0; i < numberOfParticles; i++){
            weights[i] /= sum;
        }
        for(int i = 0; i < numberOfParticles; i++){
            xx += weights[i] * particles[i][0];
            yy += weights[i] * particles[i][1];
            //zz += weights[i] * particles[i][2];
        }
        /*x = xx / numberOfParticles;
        y =  yy / numberOfParticles; */
        this.x = xx;
        this.y = yy;
        //this.z = zz;
    }

    /**
     * get the coordinates of x
     * @return
     */
    public float getX() {
        return x;
    }

    /**
     * get the coordinate of y
     * @return
     */
    public float getY() {
        return y;
    }

    public float getZ(){
        return z;
    }

    //******************************** the following part are for debuging purpose****************************
    public float[][] getParticles(){
        return particles;
    }
    public float[][] getLandmarks(){
        return landmarks;
    }

    public float getC() {
        return c;
    }
    public int getNumberOfParticles(){
        return numberOfParticles;
    }
    public float[] getWeights(){
        return weights;
    }
    //******************************* end of the debuging process**********************************************

    public void updateCoordinates(float[] coordinates, int anchorId, float speed){
        float d = speed / 100.0f * scheduleInterval;
        float tmpX = coordinates[0] - landmarks[anchorId][0];
        tmpX = tmpX * tmpX;
        float tmpY = coordinates[1] - landmarks[anchorId][1];
        tmpY = tmpY * tmpY;
        float denominator = (float) Math.sqrt(tmpX + tmpY);
        coordinates[0] = (landmarks[anchorId][0] - coordinates[0]) * d / denominator;
        coordinates[1] = (landmarks[anchorId][1] - coordinates[1]) * d / denominator;
    }


    private void debugTestCode(){
        float groundTruthx = 1.5f;
        float groundTruthy = 2.8f;
        float groundTruthz = this.z;

        TDOAMeasurement one = new TDOAMeasurement();
        one.anchorIDOne = 0;
        one.anchorIDTwo = 2;

        float []coordinates = new float[3];
        coordinates[0] = groundTruthx;
        coordinates[1] = groundTruthy;
        coordinates[2] = groundTruthz;
        float d1 = Algorithm.euclidianDistance(coordinates, landmarks[one.anchorIDOne])+0.2f;
        float d2 = Algorithm.euclidianDistance(coordinates, landmarks[one.anchorIDTwo]) ;
        //float d1 = (float) Math.sqrt(pow(landmarks[one.anchorIDOne][0] - groundTruthx, 2) + pow(landmarks[one.anchorIDOne][1] - groundTruthy, 2) + pow(landmarks[one.anchorIDOne][2] - groundTruthz, 2));
        //float d2 = (float) Math.sqrt(pow(landmarks[one.anchorIDTwo][0] - groundTruthx, 2) + pow(landmarks[one.anchorIDTwo][1] - groundTruthy, 2) + pow(landmarks[one.anchorIDTwo][2] - groundTruthz, 2));

        one.tdoa = (d1 - d2) / this.getC();

        TDOAMeasurement two = new TDOAMeasurement();
        two.anchorIDOne = 1;
        two.anchorIDTwo = 3;
        d1 = Algorithm.euclidianDistance(coordinates, landmarks[two.anchorIDOne]) + 0.2f;
        d2 = Algorithm.euclidianDistance(coordinates, landmarks[two.anchorIDTwo]) ;
        //d1 = (float) Math.sqrt(pow(landmarks[two.anchorIDOne][0] - groundTruthx, 2) + pow(landmarks[two.anchorIDOne][1] - groundTruthy, 2) + pow(landmarks[two.anchorIDOne][2] - groundTruthz, 2));
        //d2 = (float) Math.sqrt(pow(landmarks[two.anchorIDTwo][0] - groundTruthx, 2) + pow(landmarks[two.anchorIDTwo][1] - groundTruthy, 2) + pow(landmarks[two.anchorIDTwo][2] - groundTruthz, 2));

        two.tdoa = (d1 - d2) / this.getC();

        TDOAMeasurement tdoaMeasurement[] = new TDOAMeasurement[2];
        tdoaMeasurement[0] = one;
        tdoaMeasurement[1] = two;

        locationEstimationRoutineWithMultipleTdoa(tdoaMeasurement);
    }

    void debugWithInput(){
        TDOAMeasurement one = new TDOAMeasurement();
        one.anchorIDOne = 2;
        one.anchorIDTwo = 1;
        one.tdoa = 0.0018541666f;
        TDOAMeasurement two = new TDOAMeasurement();
        two.anchorIDOne = 3;
        two.anchorIDTwo = 2;
        two.tdoa = -0.0031458333f;
        TDOAMeasurement tdoaMeasurement[] = new TDOAMeasurement[2];
        tdoaMeasurement[0] = one;
        tdoaMeasurement[1] = two;
        locationEstimationRoutineWithMultipleTdoa(tdoaMeasurement);
    }

    private void debugOneMeasurement(){
        float groundTruthx = 1.0f;
        float groundTruthy = 1.0f;

        TDOAMeasurement one = new TDOAMeasurement();
        one.anchorIDOne = 2;
        one.anchorIDTwo = 0;
        float d1 = (float) Math.sqrt(pow(landmarks[one.anchorIDOne][0] - groundTruthx, 2) + pow(landmarks[one.anchorIDOne][1] - groundTruthy, 2));
        float d2 = (float) Math.sqrt(pow(landmarks[one.anchorIDTwo][0] - groundTruthx, 2) + pow(landmarks[one.anchorIDTwo][1] - groundTruthy, 2));

        one.tdoa = (d1 - d2) / this.getC();
        locationEstimationRoutineWithOneTdoa(one);
    }

    public void locationEstimationRoutineWithOneTdoa(TDOAMeasurement tdoaMeasurement){
        updateOneShot(tdoaMeasurement, this.getNumberOfParticles());
        int newNum = 0;
        for(int i = 0; i < 5; i++){
            int index[] = Algorithm.topK(this.getWeights(), topParticleNumber);

            newNum = this.resampleAndRegenerate(index, this.getNumberOfParticles()/(i+1), 0.5f/(float)Math.pow(i+1, 2));
            this.updateOneShot(tdoaMeasurement, newNum);

            //this.estimate(topParticleNumber);
            this.estimateIntermediate();
            //FileUtils.saveParticles(this.getParticles(), index,"loop"+i);

        }
        this.estimate(topParticleNumber);
    }

    /**
     * deprecated method
     * @param tdoaMeasurements
     */
    public void locationEstimationRoutineWithMultipleTdoa(TDOAMeasurement []tdoaMeasurements){
        this.update(tdoaMeasurements, this.getNumberOfParticles());
        //FileUtils.saveParticles(this.getParticles(), this.topK(this.getWeights(), 100), "top100");
        int newNum = 0;
        int j = 5;
        while(j-- > 0) {
            for (int i = 0; i < 5; i++) {
                int index[] = Algorithm.topK(this.getWeights(), topParticleNumber);
                newNum = this.resampleAndRegenerate(index, this.getNumberOfParticles() / (i + 1), 0.5f / (i + 1) / (i + 1));
                this.update(tdoaMeasurements, newNum);
                //this.estimate(topParticleNumber);
                this.estimateIntermediate();
                //FileUtils.saveParticles(this.getParticles(), index,"loop"+i);

            }
            this.estimate(topParticleNumber);
        }
        //this.estimate(topParticleNumber);
    }

    public void locationEstimationRoutineWithMultipleTdoa(List<TDOAMeasurement> tdoaMeasurements){
        this.update(tdoaMeasurements, this.getNumberOfParticles());
        //FileUtils.saveParticles(this.getParticles(), this.topK(this.getWeights(), 100), "top100");
        int newNum = 0;
        int j = 5;
        while(j-- > 0) {
            for (int i = 0; i < 5; i++) {
                int index[] = Algorithm.topK(this.getWeights(), topParticleNumber);
                newNum = this.resampleAndRegenerate(index, this.getNumberOfParticles() / (i + 1), 0.5f / (i + 1) / (i + 1));
                this.update(tdoaMeasurements, newNum);
                //this.estimate(topParticleNumber);
                this.estimateIntermediate();
                //FileUtils.saveParticles(this.getParticles(), index,"loop"+i);

            }
            this.estimate(topParticleNumber);
        }
        //this.estimate(topParticleNumber);
    }


    @Override
    public void run() {
        super.run();
        String message;
        CapturedBeaconMessage temp = new CapturedBeaconMessage();
        TDOACalUtil tdoaCalUtil = new TDOACalUtil();

        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");//设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("MM_dd_HH_mm");//设置日期格式
        String logFileName = "results/localization_" + df.format(new Date()) + ".txt";
        //String logFileName = "results/localization_" + MainFrame.count + ".txt";
//        TDOAMeasurement[] tdoaMeasurements = new TDOAMeasurement[2];
//        int captureCount = 0;
        int currentScheduleSequence = 0;
        while(isParticleThreadAlive){
            try{
                Thread.sleep(10);
                //debugWithInput();
//                debugTestCode();
//                Thread.sleep(2000);
                if(messageQueue.size() >= 1){
                    // TODO here: run the particle filter iteration here
                    //System.out.println("Receive enough information for particle filter iteration");
                    synchronized (this){
                        message = (String) messageQueue.remove(0);
                    }

                    temp = jsonUtils.decodeCapturedBeaconMessage(message);
                    currentScheduleSequence = ScheduleAnchorThread.getScheduleRound();  //获取当前轮数
                    if(temp.capturedSequence == currentScheduleSequence % 4){
                        temp.capturedSequence = currentScheduleSequence; // change the schedule sequence to avoid consistency collision
                    }else if(temp.capturedSequence == 3 && (currentScheduleSequence - 1)%4 == temp.capturedSequence){
                        temp.capturedSequence = currentScheduleSequence;
                    }

                    FileUtils.saveBeaconMessage("debug/anchor"+temp.selfAnchorId+".txt", temp);

                    tdoaCalUtil.pushNewBeaconMessage(temp);
//                    tdoaCalUtil.isBeepBeepReady(1, 2);
//                    tdoaCalUtil.isBeepBeepReady(1, 2);
//                    tdoaCalUtil.isBeepBeepReady(0, 2);
//                    tdoaCalUtil.isBeepBeepReady(1, 3);
                    if(temp.selfAnchorId >= 100){ // target captured beacon message
                        //long startTime=System.currentTimeMillis();
                        //**************** begin of bodest strategy that use two tdoas *****************
//                        if(tdoaCalUtil.isTDOAValid(temp.selfAnchorId)){
//                            tdoaMeasurements[captureCount++] = tdoaCalUtil.getTdoaMeasurement();
//                            if(captureCount >= 2){
//                                captureCount = 0;
//                                locationEstimationRoutineWithMultipleTdoa(tdoaMeasurements);
//                            }
//                            tdoaCalUtil.isTDOAValid(temp.selfAnchorId);
//                        }
                        //**************** end of bodest strategy***************************************
                        //**************** begin of hungry strategy that using as much tdoa as possible*************************
                        if(tdoaCalUtil.checkTimestamps(temp.selfAnchorId)){
                            locationEstimationRoutineWithMultipleTdoa(tdoaCalUtil.getTimestampsList());
                            FileUtils.saveLocalizationResults(getX(), getY(), getZ(),logFileName);
                        }
                        //if(temp.speed >= 10){
                        predict(temp.speed / 100.0f, temp.capturedAnchorId);
                        //}
                        //*************** end of hungry strategy **********************************************
                        //long endTime=System.currentTimeMillis(); //获取结束时间
                    }
                }

                    /*debugTestCode();
                    Thread.sleep(1000);
                FileUtils.saveLocalizationResults(getX(), getY(), getZ(),logFileName);*/
                        /*if(temp.speed > 10){ // the resolution is 3cm/s
                            predict(temp.speed / 100, temp.capturedAnchorId);
                            estimate(topParticleNumber);
                            FileUtils.saveLocalizationResults(getX(), getY(), getZ(),logFileName);
                        }*/
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    List messageQueue = new ArrayList();




    @Override
    public void update(String msg){        //这个update是观察者模式的

        synchronized (this){
            messageQueue.add(msg);
        }
        //System.out.println("I am in the particle filter = "+msg);
        System.out.println(msg);
        try {
            CapturedBeaconMessage beaconMessage = jsonUtils.decodeCapturedBeaconMessage(msg);
            mainFrame.getCapturedBeaconMessage(beaconMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
