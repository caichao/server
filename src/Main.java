public class Main {

    public static void main(String args[]){
        ParticleFilter particleFilter = new ParticleFilter(5000);
        new Thread(particleFilter).start();

        MainFrame mainFrame = new MainFrame(particleFilter);
        new Thread(mainFrame).start();

        particleFilter.generateUniformParticles();
        /*System.out.println("This is a java test!");

        System.out.println("guassian test = " + particleFilter.guassian(0.5f));

        Random random = new Random();

        System.out.println("Random = " + (random.nextFloat() * 2 - 1) );*/

        // the first step is to randomly generate new particles

        //particleFilter.generateUniformParticles(6.8f, 3.2f);
        //FileUtils.saveParticles(particleFilter.getParticles(), 2000, "InitialParticles");
        //log("particles save ok ");

        // the second step is to renew the weight based on the tdoa measurements
        // simulate a tdoa measurements
        /*
        float groundTruthx = 1.0f;
        float groundTruthy = 1.0f;
        float landmark[][] = particleFilter.getLandmarks();
        TDOAMeasurement one = new TDOAMeasurement();
        one.anchorIDOne = 0;
        one.anchorIDTwo = 2;
        float d1 = (float) Math.sqrt(Math.pow(landmark[one.anchorIDOne][0] - groundTruthx, 2) + Math.pow(landmark[one.anchorIDOne][1] - groundTruthy, 2));
        float d2 = (float) Math.sqrt(Math.pow(landmark[one.anchorIDTwo][0] - groundTruthx, 2) + Math.pow(landmark[one.anchorIDTwo][1] - groundTruthy, 2));

        one.tdoa = (d1 - d2) / particleFilter.getC();

        TDOAMeasurement two = new TDOAMeasurement();
        two.anchorIDOne = 1;
        two.anchorIDTwo = 3;
        d1 = (float) Math.sqrt(Math.pow(landmark[two.anchorIDOne][0] - groundTruthx, 2) + Math.pow(landmark[two.anchorIDOne][1] - groundTruthy, 2));
        d2 = (float) Math.sqrt(Math.pow(landmark[two.anchorIDTwo][0] - groundTruthx, 2) + Math.pow(landmark[two.anchorIDTwo][1] - groundTruthy, 2));

        two.tdoa = (d1 - d2) / particleFilter.getC();

        TDOAMeasurement tdoaMeasurement[] = new TDOAMeasurement[2];
        tdoaMeasurement[0] = one;
        tdoaMeasurement[1] = two;
        particleFilter.update(tdoaMeasurement, particleFilter.getNumberOfParticles());
        FileUtils.saveParticles(particleFilter.getParticles(), particleFilter.topK(particleFilter.getWeights(), 100), "top100");
        int newNum = 0;
        for(int i = 0; i < 5; i++){
            int index[] = particleFilter.topK(particleFilter.getWeights(), 100);

            newNum = particleFilter.resampleAndRegenerate(index, particleFilter.getNumberOfParticles()/(i+1), 0.5f/(i+1)/(i+1));
            particleFilter.update(tdoaMeasurement, newNum);

            //particleFilter.estimate(100);
            particleFilter.estimateIntermediate();
            //FileUtils.saveParticles(particleFilter.getParticles(), index,"loop"+i);

        }
        particleFilter.estimate(100);
        log("newNum = " + newNum);
        log("x = "+particleFilter.getX() + " y = "+ particleFilter.getY());

        */

        /*float x[] = new float[]{3.0f, 3.0f};
        float y[] = new float[]{1.0f, 1.0f};
        log("euclidian distance test =" + particleFilter.euclidianDistance(x, y));*/
        //log("guassian function = " + particleFilter.guassian(0.1f));
        /*float k[] = new float[]{0.1f,0.6f,0.4f,0.8f,0.3f,0.9f,0.2f,4.0f};
        int [] r = particleFilter.topK(k, 3);
        for(int i = 0; i < r.length; i++){
            log(""+r[i]);
        }*/
        //int w[] = new int[]{2,6,8,9,3,2,6,5,4};
        //log("" + particleFilter.isInSet(2,w));

        // to test the server thread
/*        ServerThread serverThread = new ServerThread("192.168.1.101", 22222);
        serverThread.start();
        log("server started ---------------");

        // to test the schedule thread
        ScheduleAnchorThread scheduleAnchorThread = new ScheduleAnchorThread();
        scheduleAnchorThread.start();
        log("Sechdule thread is started");
//        SimulateAnchor simulateAnchor = new SimulateAnchor();
//        simulateAnchor.start();
//        log("I am waiting ----------");

        ParticleFilter particleFilter = new ParticleFilter(5000);
        particleFilter.setSubject(serverThread); */

        /*try {
            NioClient client = new NioClient(InetAddress.getByName("192.168.1.101"), 33333);
            Thread t = new Thread(client);
            t.setDaemon(true);
            t.start();
            RspHandler handler = new RspHandler();
            client.send("GET / HTTP/1.0\r\n\r\n".getBytes(), handler);
            handler.waitForResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /*try {
            EchoWorker worker = new EchoWorker();
            new Thread(worker).start();
            new Thread(new NioServer(null, 33333, worker)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        ScheduleAnchorThread scheduleAnchorThread = new ScheduleAnchorThread();
        scheduleAnchorThread.start();
        log("Sechdule thread is started");

        BioServer bioServer = new BioServer(33333, particleFilter);
        new Thread(bioServer).start();
        log("server started succussfully");

    }

    public static void log(String s){
        System.out.println(s);
    }
}
