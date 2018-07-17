import org.json.JSONObject;

import java.util.*;

import static java.lang.Math.pow;

public class ParticleFilter extends Thread implements Observer{

    private int numberOfParticles = 2000;

    private Random random = null;
    private float particles[][] = null; // all the particles
    private float weights[] = null; // weights for all the particles
    private float intermediateWeights[] = null; // weights for the topParticleNumber
    private float x = 0;
    private float y = 0;
    public static final int topParticleNumber = 100;
    private int numberOfValidParticles = numberOfParticles;

    private float c = 340;

    // coordinates of the anchors
    private float landmarks[][] = null; // new float[][]{{0, 0}, {6.8f, 0}, {6.8f, 2.3f}, {0, 2.3f}};

    // parameter for the guassian function
    private float mu = 0;
    private float sigma = 0.3f;

    //******************************** about message handler ***************************
    private Subject subject = null;
    private boolean isParticleThreadAlive = true;
    private boolean isTimeToPerformEstimation = false;


    // decoding uploaded information
    JSONUtils jsonUtils = null;
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
        intermediateWeights = new float[topParticleNumber];
        jsonUtils = new JSONUtils();
        try {
            landmarks = jsonUtils.loadAnchorPosition("anchorPosition.txt");
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
        for(int i = 0; i < landmarks.length; i++){
            if(landmarks[i][0] > maxXCoordinates){
                maxXCoordinates = landmarks[i][0];
            }
            if(landmarks[i][1] > maxYCoordinates){
                maxYCoordinates = landmarks[i][1];
            }
        }
        generateUniformParticles(maxXCoordinates, maxYCoordinates);
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
     * @param anchorId
     */
    public void predict(float speed, int anchorId){
        // to achieve computation efficiency, we only update the particles with top weights
        float denominator = 0.0f;
        float displacement = speed * ScheduleAnchorThread.schedualInterval;
        for(int i = 0; i < topParticleNumber; i++){
            denominator = (float) Math.sqrt(Math.pow(particles[i][0] - landmarks[anchorId][0], 2) + Math.pow(particles[i][1] - landmarks[anchorId][1], 2));
            particles[i][0] = displacement / denominator * (landmarks[anchorId][0] - particles[i][0]);
            particles[i][1] = displacement / denominator * (landmarks[anchorId][1] - particles[i][1]);
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
        }
        // reweight the samples according to the measuremnents
        for(int i = 0; i < tdoaMeasurement.length; i++){
            float d1 = 0;
            float d2 = 0;
            float error = 0;
            for(int j = 0; j < numberOfParticles; j++){
                d1 = euclidianDistance(particles[j], landmarks[tdoaMeasurement[i].anchorIDOne]);
                d2 = euclidianDistance(particles[j], landmarks[tdoaMeasurement[i].anchorIDTwo]);
                error = Math.abs(d1 - d2 - c * tdoaMeasurement[i].tdoa);

                // update the weight
                weights[j] *= guassian(error);
            }
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
                d1 = euclidianDistance(particles[j], landmarks[tdoaMeasurement.anchorIDOne]);
                d2 = euclidianDistance(particles[j], landmarks[tdoaMeasurement.anchorIDTwo]);
                error = Math.abs(d1 - d2 - c * tdoaMeasurement.tdoa);

                // update the weight
                weights[j] *= guassian(error);
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
            int num = (int) Math.floor(weights[i] * numberOfParticles);

            for(int j = 0; j < num ; j++){
                particles[(shift + availableNumberOfParticles) % this.numberOfParticles][0] =  (2 * random.nextFloat() - 1 ) * scale + particles[i][0];
                particles[(shift + availableNumberOfParticles) % this.numberOfParticles][1] =  (2 * random.nextFloat() - 1 ) * scale + particles[i][1];
            }

            availableNumberOfParticles += num;
        }

        return availableNumberOfParticles;
    }

    public void estimateIntermediate(){
        float xx = 0;
        float yy = 0;

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
        }
        /*x = xx / numberOfParticles;
        y =  yy / numberOfParticles; */
        this.x = xx;
        this.y = yy;
    }

    /**
     * estimate the final location based on all the available particles and the corresponding weight
     */
    public void estimate(int numberOfParticles){
        float xx = 0;
        float yy = 0;
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
        }
        /*x = xx / numberOfParticles;
        y =  yy / numberOfParticles; */
        this.x = xx;
        this.y = yy;
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
        float d = speed / 100.0f * ScheduleAnchorThread.schedualInterval;
        float tmpX = coordinates[0] - landmarks[anchorId][0];
        tmpX = tmpX * tmpX;
        float tmpY = coordinates[1] - landmarks[anchorId][1];
        tmpY = tmpY * tmpY;
        float denominator = (float) Math.sqrt(tmpX + tmpY);
        coordinates[0] = (landmarks[anchorId][0] - coordinates[0]) * d / denominator;
        coordinates[1] = (landmarks[anchorId][1] - coordinates[1]) * d / denominator;
    }

    /**
     * use guassian function to generate weights corresponding to the measurements
     * @param m
     * @return
     */
    public float guassian(float m){
        float r = (float) Math.exp(pow((m - mu)/sigma, 2) / (-2));
        r = (float) (1.0f / Math.sqrt(2 * Math.PI) / sigma * r);
        return r;
    }

    /**
     * calculate the euclidian distance between two points
     * @param x: point one in 2D
     * @param y: point two in 2D
     * @return distance in float format
     */
    public float euclidianDistance(float x[], float y[]){
        return (float) Math.sqrt(pow(x[0] - y[0], 2) + pow(x[1] - y[1], 2));
    }

    /**
     * find the index of the top k value in w
     * a naive solution, can be improve by heap based solution
     * @param w: array
     * @param n: the n top value
     * @return: the index of the top n value
     */
    public int[] topK(float w[], int n){

        int index[] = new int[n];
        // first initialize the data
        for(int i = 0; i < n; i++){
            index[i] = i;
        }

        for(int i = n; i < w.length; i++){
            for(int j = 0; j < n; j++){
                int min = minIndex(index);
                if(w[i] > w[min] && !isInSet(i, index)){
                    // should replace the smallest one
                    index[min] = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * determine whether index is in set s
     * @param index: input index
     * @param s: a set of value
     * @return true if index is in s, or otherwise false
     */
    public boolean isInSet(int index, int[] s){
        for(int i = 0; i < s.length; i++){
            if(index == s[i]){
                return true;
            }
        }
        return false;
    }

    public int minIndex(int [] s){
        float min = s[0];
        int index = 0;
        for(int i = 1; i < s.length; i++){
            if(min > s[i]){
                index = i;
            }
        }
        return index;
    }

    private void debugTestCode(){
        float groundTruthx = 1.0f;
        float groundTruthy = 1.0f;

        TDOAMeasurement one = new TDOAMeasurement();
        one.anchorIDOne = 0;
        one.anchorIDTwo = 2;
        float d1 = (float) Math.sqrt(pow(landmarks[one.anchorIDOne][0] - groundTruthx, 2) + pow(landmarks[one.anchorIDOne][1] - groundTruthy, 2));
        float d2 = (float) Math.sqrt(pow(landmarks[one.anchorIDTwo][0] - groundTruthx, 2) + pow(landmarks[one.anchorIDTwo][1] - groundTruthy, 2));

        one.tdoa = (d1 - d2) / this.getC();

        TDOAMeasurement two = new TDOAMeasurement();
        two.anchorIDOne = 1;
        two.anchorIDTwo = 3;
        d1 = (float) Math.sqrt(pow(landmarks[two.anchorIDOne][0] - groundTruthx, 2) + pow(landmarks[two.anchorIDOne][1] - groundTruthy, 2));
        d2 = (float) Math.sqrt(pow(landmarks[two.anchorIDTwo][0] - groundTruthx, 2) + pow(landmarks[two.anchorIDTwo][1] - groundTruthy, 2));

        two.tdoa = (d1 - d2) / this.getC();

        TDOAMeasurement tdoaMeasurement[] = new TDOAMeasurement[2];
        tdoaMeasurement[0] = one;
        tdoaMeasurement[1] = two;
        this.update(tdoaMeasurement, this.getNumberOfParticles());
        //FileUtils.saveParticles(this.getParticles(), this.topK(this.getWeights(), 100), "top100");
        int newNum = 0;
        for(int i = 0; i < 5; i++){
            int index[] = this.topK(this.getWeights(), topParticleNumber);

            newNum = this.resampleAndRegenerate(index, this.getNumberOfParticles()/(i+1), 0.5f/(i+1)/(i+1));
            this.update(tdoaMeasurement, newNum);

            //this.estimate(topParticleNumber);
            this.estimateIntermediate();
            //FileUtils.saveParticles(this.getParticles(), index,"loop"+i);

        }
        this.estimate(topParticleNumber);
    }

    private void debugOneMeasurement(){
        float groundTruthx = 1.0f;
        float groundTruthy = 1.0f;

        TDOAMeasurement one = new TDOAMeasurement();
        one.anchorIDOne = 0;
        one.anchorIDTwo = 2;
        float d1 = (float) Math.sqrt(pow(landmarks[one.anchorIDOne][0] - groundTruthx, 2) + pow(landmarks[one.anchorIDOne][1] - groundTruthy, 2));
        float d2 = (float) Math.sqrt(pow(landmarks[one.anchorIDTwo][0] - groundTruthx, 2) + pow(landmarks[one.anchorIDTwo][1] - groundTruthy, 2));

        one.tdoa = (d1 - d2) / this.getC();
        locationEstimationRoutineWithOneTdoa(one);
    }

    public void locationEstimationRoutineWithOneTdoa(TDOAMeasurement tdoaMeasurement){
        updateOneShot(tdoaMeasurement, this.getNumberOfParticles());
        int newNum = 0;
        for(int i = 0; i < 5; i++){
            int index[] = this.topK(this.getWeights(), topParticleNumber);

            newNum = this.resampleAndRegenerate(index, this.getNumberOfParticles()/(i+1), 0.5f/(i+1)/(i+1));
            this.updateOneShot(tdoaMeasurement, newNum);

            //this.estimate(topParticleNumber);
            this.estimateIntermediate();
            //FileUtils.saveParticles(this.getParticles(), index,"loop"+i);

        }
        this.estimate(topParticleNumber);
    }

    public void locationEstimationRoutineWithMultipleTdoa(TDOAMeasurement []tdoaMeasurements){
        this.update(tdoaMeasurements, this.getNumberOfParticles());
        //FileUtils.saveParticles(this.getParticles(), this.topK(this.getWeights(), 100), "top100");
        int newNum = 0;
        for(int i = 0; i < 5; i++){
            int index[] = this.topK(this.getWeights(), topParticleNumber);
            newNum = this.resampleAndRegenerate(index, this.getNumberOfParticles()/(i+1), 0.5f/(i+1)/(i+1));
            this.update(tdoaMeasurements, newNum);
            //this.estimate(topParticleNumber);
            this.estimateIntermediate();
            //FileUtils.saveParticles(this.getParticles(), index,"loop"+i);
        }
        this.estimate(topParticleNumber);
    }

    @Override
    public void run() {
        super.run();
        String message = "";
        CapturedBeaconMessage temp = new CapturedBeaconMessage();
        TDOACalUtil tdoaCalUtil = new TDOACalUtil();
        TDOAMeasurement tdoaMeasurement = new TDOAMeasurement();
        TDOAMeasurement[] measurements = new TDOAMeasurement[2];
        int captured = 0;
        int debugIndex = 0;
        while(isParticleThreadAlive){
            /*if(isTimeToPerformEstimation){
                isTimeToPerformEstimation = false;
            }*/
            try{
                Thread.sleep(10);
                if(messageQueue.size() >= 1){
                    // TODO here: run the particle filter iteration here
                    //System.out.println("Receive enough information for particle filter iteration");
                    synchronized (this){
                        message = (String) messageQueue.remove(0);
                    }

                    temp = jsonUtils.decodeCapturedBeaconMessage(message);

                    if(temp.capturedSequence == ScheduleAnchorThread.getScheduleRound() % 4){
                        temp.capturedSequence = ScheduleAnchorThread.getScheduleRound(); // change the schedule sequence to avoid consistency collision
                    }
                    //debugOneMeasurement();
                    tdoaCalUtil.pushNewBeaconMessage(temp);
                    if(temp.selfAnchorId >= 100){ // target captured beacon message
                        //System.out.println("captured beacon message");
                        /*if(tdoaCalUtil.isTDOAReady(0, 3, temp.selfAnchorId)){
                            System.out.println("tdoa distance = " + (tdoaCalUtil.getTdoaDistance()));

                            tdoaMeasurement = tdoaCalUtil.getTdoaMeasurement();
                            locationEstimationRoutine(tdoaMeasurement);
                        }*/
                        debugIndex++;
                        if(debugIndex >=2 ){
                            debugIndex = 0;
                        }
                        if(tdoaCalUtil.isTDOAValid(temp.selfAnchorId)){
                            //tdoaMeasurement = tdoaCalUtil.getTdoaMeasurement();
                            //locationEstimationRoutineWithOneTdoa(tdoaMeasurement);
                            tdoaMeasurement = tdoaCalUtil.getTdoaMeasurement();
                            measurements[captured++] = (TDOAMeasurement) tdoaMeasurement.clone();
                            if(captured >= 2){
                                captured = 0;
                                locationEstimationRoutineWithMultipleTdoa(measurements);
                            }

                            if(temp.speed > 3){ // the resolution is 3cm/s
                                predict(temp.speed / 100, temp.capturedAnchorId);
                            }
                        }
                    }
                }
                    /*debugTestCode();
                    */

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    List messageQueue = new ArrayList();
    @Override
    public void update(String msg){

        synchronized (this){
            messageQueue.add(msg);
        }
        //System.out.println("I am in the particle filter = "+msg);
    }
}
