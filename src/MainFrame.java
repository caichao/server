import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements Runnable,ActionListener,Observer {

    //private String imgFilePath = "images/map1.jpg";
    private DrawMainFrame drawMainFrame = null;
    private ParticleFilter particleFilter = null;
    private boolean isThreaAlive = true;
    float scale = 185;
    int originX = 35;               //原点的x
    int originY = 520;              //原点的y
    int xAxisLength = 900;         //X轴长度
    int yAxisLength = 510;          //Y轴长度
    int AnchorScale = 20;           //正方形Anchor边长的一半
    CapturedBeaconMessage capturedBeaconMessage = null;
    private volatile boolean isNewMessageCome = false;
    boolean[] isAnchorWorking = new boolean[]{false,false,false,false};


    public MainFrame(ParticleFilter particleFilter) {

        //显示当前屏幕分辨率
        ScreenSize ss = new ScreenSize();
        int screenWidth = ss.getScreenWidth();
        int screenHeight = ss.getScreenHeight();
        System.out.println("屏幕宽为：" + screenWidth + "---屏幕高为：" + screenHeight);

        //ImageIcon img = new ImageIcon(imgFilePath);
        //要设置的背景图片
        JLabel imgLabel = new JLabel();
        //img.setImage(img.getImage().getScaledInstance(1000,574,Image.SCALE_DEFAULT));
        //imgLabel.setIcon(img);
        //将背景图放在图片标签里。
        this.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));
        //将背景标签添加到jfram的LayeredPane面板里。

        //imgLabel.setBounds(290, 20, screenWidth, screenHeight);//图片标签(地图区域)的位置和大小



        /*
         * 设置按钮
         * */
        JButton LaunchAll = new JButton("launchAll");
        LaunchAll.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        JButton KillAll = new JButton("killAll");
        KillAll.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        this.getLayeredPane().add(LaunchAll);
        LaunchAll.setBounds(1200, 50, 90, 30);
        this.getLayeredPane().add(KillAll);
        KillAll.setBounds(1200, 100, 90, 30);
        //加入监听器
        Listener1 L1 = new Listener1();
        LaunchAll.addActionListener(L1);
        Listener2 L2 = new Listener2();
        KillAll.addActionListener(L2);


        //new BulbControl();


        //this.getLayeredPane().add(new MyPanel2(), new Integer(Integer.MIN_VALUE));

        // 设置背景标签的位置
        Container contain = this.getContentPane();

        ((JPanel) contain).setOpaque(false);
        // 将内容面板设为透明。将LayeredPane面板中的背景显示出来。

        //this.add(new MyPanel2());
        this.particleFilter = particleFilter;
        drawMainFrame = new DrawMainFrame(this.particleFilter);
        this.add(drawMainFrame);

        this.setVisible(true);
        this.setResizable(false);
        //this.setSize(1500,800);//整个窗体的大小
        this.setBounds(0, 0, screenWidth, screenHeight);
        this.setTitle("Asynchronous Localization Project: by cc at HUST");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);//指定界面默认关闭选项  EXIT_ON_CLOSE为关闭时退出程序
        //this.setLocationRelativeTo(null);// 把窗口位置设置到屏幕的中心
    }

    public void getCapturedBeaconMessage(CapturedBeaconMessage beaconMessage) {
        this.capturedBeaconMessage = beaconMessage;
        isNewMessageCome = true;
    }

    @Override//信号灯事件
    public void actionPerformed(ActionEvent e) {


    }

    @Override
    public void update(String msg) {

    }

    /*
     * 给按钮创建监听器
     * */
    class Listener1 implements ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            System.out.println("666");
            ButtonThread buttonThread1 = new ButtonThread(ButtonThread.launchAll);
            buttonThread1.start();

        }
    }

    class Listener2 implements ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            System.out.println("777");
            ButtonThread buttonThread2 = new ButtonThread(ButtonThread.killAll);
            buttonThread2.start();
        }
    }


    public void refresh() {

    }

    @Override
    public void run() {
        drawMainFrame.initDrawingParameters();
        while (isThreaAlive) {
            if(isNewMessageCome == true){
                isNewMessageCome = false;
                drawMainFrame.prepareAnchorState(capturedBeaconMessage);
            }
            drawMainFrame.repaint();
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DrawMainFrame extends JPanel {

        ParticleFilter particleFilter = null;
        BasicStroke basicStroke = null;       // 笔画的轮廓（画笔宽度/线宽为3px）
        BasicStroke boldStroke = null;

        float[][] particles = null;
        float[][] allParticles = null;

        float maxXCoordinates = 0;
        float landmarks[][] = null;
        JSONUtils jsonUtils = null;
        String configFilePath = "config.txt";
        private int currentActivateAnchorId = 0;
        private boolean isMessageRecv = false;

        int numberOfParticles = ParticleFilter.topParticleNumber;

        {
            /*
            try {
                scale = JSONUtils.getMapGUIScaleCoefficient("config.txt");
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
        }

        int xInerAxis = 0;//(int) (getMaxXCoordinates() * scale);
        int yInerAxis = 0; //(int) (getMaxYCoordinates() * scale);

        Font font = null;

        /*public drawMainFrame(){ // for debug test
            this.setOpaque(false);
            //this.setBackground(Color.WHITE);
            particleFilter = new ParticleFilter(5000);
            particleFilter.generateUniformParticles(4.2f, 4.5f);
            basicStroke = new BasicStroke(5);
            particles = particleFilter.getParticles();

            copyTopParticles(numberOfParticles);
        }*/

        public void refreshParticleLocation() {
            repaint();
        }

        public float getMaxXCoordinates() {

            for (int i = 0; i < landmarks.length; i++) {
                if (landmarks[i][0] > maxXCoordinates) {
                    maxXCoordinates = landmarks[i][0];
                }
            }
            return maxXCoordinates;//4.623
        }

        public float getMaxYCoordinates() {
            float maxYCoordinates = 0;
            for (int i = 0; i < landmarks.length; i++) {
                if (landmarks[i][1] > maxYCoordinates) {
                    maxYCoordinates = landmarks[i][1];
                }

            }
            return maxYCoordinates;//2.662
        }


        public DrawMainFrame(ParticleFilter particleFilter) {
            this.particleFilter = particleFilter;
            this.setOpaque(false);
            initDrawingParameters();
        }

        public void initDrawingParameters() {
            basicStroke = new BasicStroke(3);
            boldStroke = new BasicStroke(5);
            font = new Font("宋体", Font.BOLD, 15);
            //particles = particleFilter.getParticles().clone(); // deep copy
            //int index[] = particleFilter.topK(particleFilter.getWeights(), numberOfParticles);
            particles = new float[numberOfParticles][2];
            allParticles = particleFilter.getParticles();

            try {
                landmarks = jsonUtils.loadAnchorPosition(configFilePath);
            } catch (Exception e) {

            }
            xInerAxis = (int) (getMaxXCoordinates() * scale);
            yInerAxis =  (int) (getMaxYCoordinates() * scale);
        }

        public void copyTopParticles(int n) {
            int index[] = Algorithm.topK(particleFilter.getWeights(), n);
            for (int i = 0; i < n; i++) {
                particles[i] = allParticles[index[i]];
            }
        }


        /*
         * 所有东西都在这里面画
         * */
        public void paint(Graphics g) {

            super.paint(g);

            Graphics2D g2 = (Graphics2D) g;

            g2.setStroke(basicStroke);
            g2.setColor(Color.WHITE);
            // first erase the last particles 擦除旧点
            if (particles != null) {
                for (int i = 0; i < this.particles.length; i++) {
                    drawPoints(particles[i][0], particles[i][1], g2);
                }
            }

            copyTopParticles(numberOfParticles);
            // second draw the new particles  画新点
            g2.setColor(Color.RED);
            for (int i = 0; i < particles.length; i++) {
                drawPoints(particles[i][0], particles[i][1], g2);
            }

            // draw the estimated locations
            g2.setColor(Color.GREEN);
            g2.setStroke(boldStroke);
            //drawEstimatedLocation(, 1, g2);画估计点
            drawEstimatedLocation(particleFilter.getX(), particleFilter.getY(), g2);
            //画坐标轴
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2.0f));
            drawAxis(originX, originY, g2);

            //用虚线画上下边界
            g2.setColor(Color.CYAN);
            //g2.setStroke(new BasicStroke(2.0f));
            Stroke dash = new BasicStroke(2.5f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND, 3.5f, new float[]{5, 5,},
                    0f);
            g2.setStroke(dash);
            drawBorder(originX, originY, g2);

            // add labels for the estimated locations
            g2.setColor(Color.BLUE);

            drawLabels(particleFilter.getX(), particleFilter.getY(), String.format("当前位置：" + "x = %.2f y = %.2f \r\n z = %.2f", particleFilter.getX(), particleFilter.getY(), particleFilter.getZ()), g2);

            //画Anchor
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            drawAnchor(AnchorScale, g2);
            if(isMessageRecv) {
                //标记当前发出消息的anchor
                freshAnchorState(g2, currentActivateAnchorId);
                //画出各个anchor的工作状态
                drawAnchorWorkingState(g2);
            }

            repaint();
        }

        public void prepareAnchorState(CapturedBeaconMessage capturedBeaconMessage){
            isAnchorWorking[capturedBeaconMessage.selfAnchorId] = true;
            currentActivateAnchorId = capturedBeaconMessage.capturedAnchorId;
            isMessageRecv = true;
        }

        //画点
        public void drawPoints(float x, float y, Graphics2D g) {
            g.drawLine((int) (x * scale + originX), (int) (originY - y * scale), (int) (x * scale + originX), (int) (originY - y * scale));
        }
        /*
        public void drawEstimatedLocation(float x, float y, Graphics2D g){
            g.drawLine((int)(x * scale) - 5,(int)(y * scale), (int)(x * scale)+5,(int)(y * scale));
            g.drawLine((int)(x * scale), (int)(y * scale)+5, (int)(x * scale), (int)(y * scale)-5);
        }
        */

        //画估计位置
        public void drawEstimatedLocation(float x, float y, Graphics2D g) {
            g.drawLine((int) (x + originX) - 5, (int) (originY - y), (int) (x + originX) + 5, (int) (originY - y));
            g.drawLine((int) (x + originX), (int) (originY - y) + 5, (int) (x + originX), (int) (originY - y) - 5);
        }

        public void drawAxis(float x, float y, Graphics2D g) {
            drawAxisX(x, y, g);
            drawAxisY(x, y, g);
        }

        //画x轴
        public void drawAxisX(float x, float y, Graphics2D g) {
            g.drawLine((int) x, (int) y, (int) x + xAxisLength, (int) y);
            g.drawLine((int) x + xAxisLength, (int) y, (int) x + xAxisLength - 5, (int) y - 2);
            g.drawLine((int) x + xAxisLength, (int) y, (int) x + xAxisLength - 5, (int) y + 2);
            g.setFont(new Font("宋体", Font.BOLD, 20));
            g.drawString("X", (int) x + xAxisLength + 5, (int) y);

        }

        //画y轴
        public void drawAxisY(float x, float y, Graphics2D g) {
            g.drawLine((int) x, (int) y, (int) x, (int) y - yAxisLength);
            g.drawLine((int) x, (int) y - yAxisLength, (int) x - 2, (int) y - yAxisLength + 5);
            g.drawLine((int) x, (int) y - yAxisLength, (int) x + 2, (int) y - yAxisLength + 5);
            g.setFont(new Font("宋体", Font.BOLD, 20));
            g.drawString("Y", (int) x - 15, (int) y - yAxisLength + 5);
        }

        //画上右边界
        public void drawBorder(float x, float y, Graphics2D g) {
            g.drawLine((int) x + 2, (int) y - yInerAxis, (int) x + xInerAxis, (int) y - yInerAxis);//上边界
            g.drawLine((int) x + xInerAxis, (int) y, (int) x + xInerAxis, (int) y - yInerAxis);//右边界
            g.setFont(new Font("宋体", Font.BOLD, 15));
            g.setColor(Color.black);
            g.drawString(String.valueOf(getMaxXCoordinates()) + "m", (int) x - 13 + xInerAxis, originY + 16);
            g.drawString(String.valueOf(getMaxYCoordinates()) + "m", originX + 3, (int) y - yInerAxis - 5);
        }

        //画坐标标签
        public void drawLabels(float x, float y, String msg, Graphics2D g) {
            g.setFont(font);
            g.drawString(msg, 1000, 500);
        }

        //画Anchor
        public void drawAnchor(int r, Graphics2D g) {
            String ss[] = new String[]{"0", "1", "2", "3"};
            for (int i = 0; i < 4; i++) {
                g.drawRect((int) (landmarks[i][0] * scale + originX - r / 2), (int) (originY - landmarks[i][1] * scale - r / 2), r, r);
                g.drawString(ss[i], (landmarks[i][0] * scale + originX) + r / 4 - r / 2, (originY - landmarks[i][1] * scale) + r - 4 - r / 2);
            }

        }

        public void freshAnchorState(Graphics2D g,int AnchorNum) {
            g.setFont(new Font("MS 明朝", Font.BOLD, 20));
            g.setStroke(new BasicStroke(4f));

            //清除上一次的指示灯
            g.setColor(Color.WHITE);
                for(int i = 0;i<4;i++)
                {
                     g.drawRect((int) (landmarks[i][0] * scale + originX - AnchorScale / 2) - 3, (int) (originY - landmarks[i][1] * scale - AnchorScale / 2) - 3, AnchorScale + 7, AnchorScale + 7);
                 }


                 //画出正在发送信息的Anchor指示灯(绿)
                 g.setColor(Color.GREEN);
                switch(AnchorNum){
                    case 0:
                    {g.drawRect((int) (landmarks[0][0] * scale + originX - AnchorScale / 2) - 3, (int) (originY - landmarks[0][1] * scale - AnchorScale / 2) - 3, AnchorScale + 7, AnchorScale + 7);
                        isAnchorWorking[0]=true;
                        break;}
                    case 1:
                    {g.drawRect((int) (landmarks[1][0] * scale + originX - AnchorScale / 2) - 3, (int) (originY - landmarks[1][1] * scale - AnchorScale / 2) - 3, AnchorScale + 7, AnchorScale + 7);
                        isAnchorWorking[1]=true;
                        break;}
                    case 2:
                    {g.drawRect((int) (landmarks[2][0] * scale + originX - AnchorScale / 2) - 3, (int) (originY - landmarks[2][1] * scale - AnchorScale / 2) - 3, AnchorScale + 7, AnchorScale + 7);
                        isAnchorWorking[2]=true;
                        break;}
                    case 3:
                    {g.drawRect((int) (landmarks[3][0] * scale + originX - AnchorScale / 2) - 3, (int) (originY - landmarks[3][1] * scale - AnchorScale / 2) - 3, AnchorScale + 7, AnchorScale + 7);
                        isAnchorWorking[3]=true;
                        break;}
                }




        }

        public void drawAnchorWorkingState(Graphics2D g)
        {
            g.setFont(new Font("宋体", Font.BOLD, 40));
            g.setColor(Color.BLACK);
            g.drawString("Anchor_0:",1000,150);
            g.drawString("Anchor_1:",1000,200);
            g.drawString("Anchor_2:",1000,250);
            g.drawString("Anchor_3:",1000,300);
            for(int i =0;i<4;i++){
                if(isAnchorWorking[i]==true){
                    g.setColor(Color.green);
                    g.drawString("●",1100,150+i*50);
                }
                else{
                    g.setColor(Color.red);
                    g.drawString("■",1100,150+i*50);
                }
            }
        }
    }

}
//■●


//以下代码用于显示当前屏幕分辨率
class ScreenSize {
    private int screenWidth;
    private int screenHeight;

    public int getScreenWidth() {

        setScreenWidth(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        setScreenHeight(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }
}







