import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.json.JSONObject;

import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements Runnable,ActionListener,Observer {

    //private String imgFilePath = "images/map1.jpg";
    private DrawMainFrame drawMainFrame = null;
    private ParticleFilter particleFilter = null;
    private boolean isThreaAlive = true;
    float scale = 185;              //放大尺度
    int originX = 35;               //原点的x
    int originY = 520;              //原点的y
    int xAxisLength = 900;         //X轴长度
    int yAxisLength = 510;          //Y轴长度
    int AnchorScale = 20;           //正方形Anchor边长的一半
    CapturedBeaconMessage capturedBeaconMessage = null;
    private volatile boolean isNewMessageCome = false;
    boolean[] isAnchorWorking = new boolean[]{false,false,false,false};
    static JTextField textx0 = new JTextField();//x0
    static JTextField texty0 = new JTextField();//y0
    static JTextField textz0 = new JTextField();//z0
    static JTextField textx1 = new JTextField();//x1
    static JTextField texty1 = new JTextField();//y1
    static JTextField textz1 = new JTextField();//z1
    static JTextField textx2 = new JTextField();//x2
    static JTextField texty2 = new JTextField();//y2
    static JTextField textz2 = new JTextField();//z2
    static JTextField textx3 = new JTextField();//x3
    static JTextField texty3 = new JTextField();//y3
    static JTextField textz3 = new JTextField();//z3
    static JTextField scaleText = new JTextField();  //scale
    static JTextField heightText = new JTextField();  //height
    static JTextField intervalText = new JTextField();  //interval
    //textx0.setText()

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


        Container contain = this.getContentPane();



        /*
        * 画出anchor状态区域
        * */
        JPanel showFrame = new JPanel();
        showFrame.setLayout(null);
        showFrame.setBounds(1000,300,500,500);
        showFrame.setBackground(Color.white);
        this.getLayeredPane().add(showFrame);
//            g.setStroke(new BasicStroke(1f));
//            g.setFont(new   java.awt.Font("Dialog",   3,   15));
//            g.setColor(Color.BLACK);
//            g.drawString("Anchor state",1150,140);
//            g.drawString("Anchor_0:",1000,180);
//            g.drawString("Anchor_1:",1000,230);
//            g.drawString("Anchor_2:",1000,280);
//            g.drawString("Anchor_3:",1000,330);






        /*
        * 画出参数编辑区域
        * */
        JPanel paramConfig = new JPanel();
        paramConfig.setBackground(Color.white);
        paramConfig.setLayout(new GridLayout());
        paramConfig.setBounds(50,560,640,252);
        paramConfig.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black) );
        this.getLayeredPane().add(paramConfig);
        paramConfig.setLayout(null);

        JLabel location = new JLabel("Location",JLabel.CENTER);
        paramConfig.add(location);
        location.setFont(new   java.awt.Font("Dialog",   2,   20));
        location.setBounds(5,5,100,30);
        location.setBorder(BorderFactory.createRaisedBevelBorder());



//        JTextField j1 = new JTextField("Location");
//        j1.setEditable(false);
//        paramConfig.add(j1);
//        //j1.setSize(50,30);
//        //j1.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
//        j1.setBounds(new Rectangle(5, 10, 100, 30));


        JLabel x = new JLabel("X",JLabel.CENTER);
        x.setFont(new   java.awt.Font("Dialog",   1,   15));
        paramConfig.add(x);
        //x.setBorder(BorderFactory.createRaisedBevelBorder());
        x.setBounds(new Rectangle(118, 10, 50, 20));


        JLabel y = new JLabel("Y",JLabel.CENTER);
        y.setFont(new   java.awt.Font("Dialog",   1,   15));
        paramConfig.add(y);
        //y.setBorder(BorderFactory.createRaisedBevelBorder());
        y.setBounds(new Rectangle(218, 10, 50, 20));

        JLabel z = new JLabel("Z",JLabel.CENTER);
        z.setFont(new   java.awt.Font("Dialog",   1,   15));
        paramConfig.add(z);
        //z.setBorder(BorderFactory.createRaisedBevelBorder());
        z.setBounds(new Rectangle(318, 10, 50, 20));

        JLabel anchorItem0 = new JLabel("anchor 0",JLabel.CENTER);
        paramConfig.add(anchorItem0);
        anchorItem0.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem0.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem0.setBounds(new Rectangle(10, 51, 80, 30));

        //JTextField textx0 = new JTextField();//x0
        paramConfig.add(textx0);
        textx0.setBounds(101,51,85,30);
        textx0.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty0 = new JTextField();//y0
        paramConfig.add(texty0);
        texty0.setBounds(201,51,85,30);
        texty0.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz0 = new JTextField();//z0
        paramConfig.add(textz0);
        textz0.setBounds(301,51,85,30);
        textz0.setHorizontalAlignment(JTextField.CENTER);

        JLabel anchorItem1 = new JLabel("anchor 1",JLabel.CENTER);
        paramConfig.add(anchorItem1);
        anchorItem1.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem1.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem1.setBounds(10,101,80,30);

        //JTextField textx1 = new JTextField();//x1
        paramConfig.add(textx1);
        textx1.setBounds(101,101,85,30);
        textx1.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty1 = new JTextField();//y1
        paramConfig.add(texty1);
        texty1.setBounds(201,101,85,30);
        texty1.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz1 = new JTextField();//z1
        paramConfig.add(textz1);
        textz1.setBounds(301,101,85,30);
        textz1.setHorizontalAlignment(JTextField.CENTER);

        JLabel anchorItem2 = new JLabel("anchor 2",JLabel.CENTER);
        paramConfig.add(anchorItem2);
        anchorItem2.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem2.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem2.setBounds(10,151,80,30);

        //JTextField textx2 = new JTextField();//x2
        paramConfig.add(textx2);
        textx2.setBounds(101,151,85,30);
        textx2.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty2 = new JTextField();//y2
        paramConfig.add(texty2);
        texty2.setBounds(201,151,85,30);
        texty2.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz2= new JTextField();//z2
        paramConfig.add(textz2);
        textz2.setBounds(301,151,85,30);
        textz2.setHorizontalAlignment(JTextField.CENTER);

        JLabel anchorItem3 = new JLabel("anchor 3",JLabel.CENTER);
        paramConfig.add(anchorItem3);
        anchorItem3.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem3.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem3.setBounds(10,201,80,30);


        //JTextField textx3 = new JTextField();//x3
        paramConfig.add(textx3);
        textx3.setBounds(101,201,85,30);
        textx3.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty3 = new JTextField();//y3
        paramConfig.add(texty3);
        texty3.setBounds(201,201,85,30);
        texty3.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz3= new JTextField();//z3
        paramConfig.add(textz3);
        textz3.setBounds(301,201,85,30);
        textz3.setHorizontalAlignment(JTextField.CENTER);

        JLabel title2 = new JLabel("Other Parameters",JLabel.CENTER);
        paramConfig.add(title2);
        title2.setBorder(BorderFactory.createRaisedBevelBorder());
        title2.setFont(new   java.awt.Font("Dialog",   2,   19));
        title2.setBounds(new Rectangle(402, 10, 200, 30));

        JLabel textScale = new JLabel("Scale :",JLabel.CENTER);
        paramConfig.add(textScale);
        textScale.setFont(new   java.awt.Font("Dialog",   1,   13));
        textScale.setBounds(new Rectangle(402, 51, 80, 30));
        //textScale.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel textTargetHeight = new JLabel("Target Height :",JLabel.CENTER);
        paramConfig.add(textTargetHeight);
        textTargetHeight.setFont(new   java.awt.Font("Dialog",   1,   13));
        textTargetHeight.setBounds(new Rectangle(412, 101, 100, 30));
        //textTargetHeight.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel textInterval = new JLabel("Schedule Interval :",JLabel.CENTER);
        paramConfig.add(textInterval);
        textInterval.setFont(new   java.awt.Font("Dialog",   1,   13));
        textInterval.setBounds(new Rectangle(402, 151, 130, 30));
        textInterval.setHorizontalAlignment(JTextField.CENTER);
        //textInterval.setBorder(BorderFactory.createRaisedBevelBorder());

        //JTextField scaleText = new JTextField();  //scale
        paramConfig.add(scaleText);
        scaleText.setBounds(542,51,80,30);
        scaleText.setHorizontalAlignment(JTextField.CENTER);

        //JTextField heightText = new JTextField();  //height
        paramConfig.add(heightText);
        heightText.setBounds(542,101,80,30);
        heightText.setHorizontalAlignment(JTextField.CENTER);

        //JTextField intervalText = new JTextField();  //interval
        paramConfig.add(intervalText);
        intervalText.setBounds(542,151,80,30);
        intervalText.setHorizontalAlignment(JTextField.CENTER);



        JButton config = new JButton("Save");
        config.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ParamConfig.writeText();
                JFrame dialogFrame = new  JFrame("提示");
                dialogFrame.setBounds(600,400,250,150);
                dialogFrame.setResizable(false);
                dialogFrame.setLayout(null);
                dialogFrame.setVisible(true);


                JLabel tip = new JLabel("参数设置成功!");
                tip.setBounds(52,10,90,20);
                tip.setFont(new   java.awt.Font("Dialog",   2,   14));
                //tip.setBorder(BorderFactory.createRaisedBevelBorder());
                dialogFrame.add(tip);


                JButton ok = new JButton("OK");
                ok.setBounds(63,40,60,20);
                ok.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialogFrame.dispose();
                    }
                });
                dialogFrame.add(ok);


            }
        });
        paramConfig.add(config);
        config.setBounds(402,201,100,30);
        config.setFont(new   java.awt.Font("Dialog",   2,   18));
        config.setBorder(BorderFactory.createRaisedBevelBorder());
        config.addActionListener(this);



        JButton load = new JButton("Load");

        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ParamConfig.loadText();
                JFrame dialogFrame = new  JFrame("提示");
                dialogFrame.setBounds(600,400,250,150);
                dialogFrame.setResizable(false);
                dialogFrame.setLayout(null);
                dialogFrame.setVisible(true);


                JLabel tip = new JLabel("获取参数成功!");
                tip.setBounds(52,10,90,20);
                tip.setFont(new   java.awt.Font("Dialog",   2,   14));
                //tip.setBorder(BorderFactory.createRaisedBevelBorder());
                dialogFrame.add(tip);


                JButton ok = new JButton("OK");
                ok.setBounds(63,40,60,20);
                ok.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialogFrame.dispose();
                    }
                });
                dialogFrame.add(ok);


            }
        });

        paramConfig.add(load);
        load.setBounds(522,201,100,30);
        load.setFont(new   java.awt.Font("Dialog",   2,   18));
        load.setBorder(BorderFactory.createRaisedBevelBorder());



        //ParamConfig.writeText();













//        JButton j = new JButton("123");
//        //j.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
//        this.getLayeredPane().add(j);
//        j.setBounds(1000,800,30,30);






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




        //((JPanel) contain).setOpaque(false);
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
            float maxXCoordinates = 0;
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
            g2.setFont(new   java.awt.Font("Dialog",   2,   18));
            drawLabels(particleFilter.getX(), particleFilter.getY(), String.format( "x = %.2f y = %.2f \r\n z = %.2f", particleFilter.getX(), particleFilter.getY(), particleFilter.getZ()), g2);

            //画Anchor
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            drawAnchor(AnchorScale, g2);
            initAnchorState(g2);
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



        //画当前发送消息的anchor的指示灯
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


        public void initAnchorState(Graphics2D g){
            //g.setFont(new Font("宋体", Font.BOLD, 20));

//            g.setStroke(new BasicStroke(1f));
//            g.setFont(new   java.awt.Font("Dialog",   3,   15));
//            g.setColor(Color.BLACK);
//            g.drawString("Anchor state",1150,140);
//            g.drawString("Anchor_0:",1000,180);
//            g.drawString("Anchor_1:",1000,230);
//            g.drawString("Anchor_2:",1000,280);
//            g.drawString("Anchor_3:",1000,330);



            g.setFont(new Font("宋体", Font.BOLD, 40));
            g.setColor(Color.red);
            for(int i =0;i<4;i++){
                g.drawString("■",1200,185+i*50);
            }

        }


        public void drawAnchorWorkingState(Graphics2D g)
        {

            for(int i =0;i<4;i++){
                g.setFont(new Font("宋体", Font.BOLD, 40));
                if(isAnchorWorking[i]==true){
                    g.setColor(Color.green);
                    g.drawString("●",1200,185+i*50);
                }
                else{
                    g.setColor(Color.red);
                    g.drawString("■",1200,185+i*50);
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







