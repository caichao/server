
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



/*
* 该类用于初始化主面板的大的框架
* */

public class MainFrame extends JFrame implements Runnable,ActionListener,Observer {

    //private String imgFilePath = "images/map1.jpg";
    static DrawMainFrame drawMainFrame = null;
    private ParticleFilter particleFilter = null;
    private boolean isThreaAlive = true;
    static int xTrans = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width/1440;
    static int yTrans = Toolkit.getDefaultToolkit().getScreenSize().height/900;




    CapturedBeaconMessage capturedBeaconMessage = null;
    private volatile boolean isNewMessageCome = false;
    //boolean[] isAnchorWorking = new boolean[]{false,false,false,false};
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
    static int drawCnt = 0;



    /*
    * MainFrame类的构造函数
    * 形成了主面板的的大部分框架
    * */


    public MainFrame(ParticleFilter particleFilter) {

        //显示当前屏幕分辨率
//        ScreenSize ss = new ScreenSize();
        int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

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
        * 绘制anchor state面板上的标签
        * */
        JLabel cmdStr = new JLabel("Commands:");
        cmdStr.setBounds(1000*screenWidth/1440,5*screenHeight/900,120*screenWidth/1440,50*screenHeight/900);
        cmdStr.setFont(new   java.awt.Font("Dialog",   2,   19));
        this.getContentPane().add(cmdStr);

        JLabel stateStr = new JLabel("Anchor State");
        stateStr.setBounds(1100*screenWidth/1440,100*screenHeight/900,120*screenWidth/1440,40*screenHeight/900);
        stateStr.setFont(new   java.awt.Font("Dialog",   2,   19));
        //stateStr.setBorder(BorderFactory.createRaisedBevelBorder());
        this.getContentPane().add(stateStr);

        JLabel anchor0Str = new JLabel("Anchor 0");
        anchor0Str.setBounds(1000*screenWidth/1440,150*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        anchor0Str.setFont(new   java.awt.Font("Dialog",   2,   16));
        this.getContentPane().add(anchor0Str);

        JLabel anchor1Str = new JLabel("Anchor 1");
        anchor1Str.setBounds(1000*screenWidth/1440,200*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        anchor1Str.setFont(new   java.awt.Font("Dialog",   2,   16));
        this.getContentPane().add(anchor1Str);

        JLabel anchor2Str = new JLabel("Anchor 2");
        anchor2Str.setBounds(1000*screenWidth/1440,250*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        anchor2Str.setFont(new   java.awt.Font("Dialog",   2,   16));
        this.getContentPane().add(anchor2Str);

        JLabel anchor3Str = new JLabel("Anchor 3");
        anchor3Str.setBounds(1000*screenWidth/1440,300*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        anchor3Str.setFont(new   java.awt.Font("Dialog",   2,   16));
        this.getContentPane().add(anchor3Str);

        JLabel locationStr = new JLabel("Target Location");
        locationStr.setBounds(1075*screenWidth/1440,355*screenHeight/900,150*screenWidth/1440,30*screenHeight/900);
        locationStr.setFont(new   java.awt.Font("Dialog",   2,   19));
        //locationStr.setBorder(BorderFactory.createRaisedBevelBorder());
        this.getContentPane().add(locationStr);






        /*
        * 开始画参数编辑区域
        * */
        JPanel paramConfig = new JPanel();
        paramConfig.setBackground(Color.white);
        paramConfig.setLayout(new GridLayout());
        paramConfig.setBounds(150*screenWidth/1440,560*screenHeight/900,650*screenWidth/1440,262*screenHeight/900);
        paramConfig.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black) );
        this.getLayeredPane().add(paramConfig);
        paramConfig.setLayout(null);

        JLabel location = new JLabel("Location",JLabel.CENTER);
        paramConfig.add(location);
        location.setFont(new   java.awt.Font("Dialog",   2,   20));
        location.setBounds(5*screenWidth/1440,5*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        location.setBorder(BorderFactory.createRaisedBevelBorder());



        JLabel x = new JLabel("X",JLabel.CENTER);
        x.setFont(new   java.awt.Font("Dialog",   1,   15));
        paramConfig.add(x);
        //x.setBorder(BorderFactory.createRaisedBevelBorder());
        x.setBounds(new Rectangle(118*screenWidth/1440, 10*screenHeight/900, 50*screenWidth/1440, 20*screenHeight/900));


        JLabel y = new JLabel("Y",JLabel.CENTER);
        y.setFont(new   java.awt.Font("Dialog",   1,   15));
        paramConfig.add(y);
        //y.setBorder(BorderFactory.createRaisedBevelBorder());
        y.setBounds(new Rectangle(218*screenWidth/1440, 10*screenHeight/900, 50*screenWidth/1440, 20*screenHeight/900));

        JLabel z = new JLabel("Z",JLabel.CENTER);
        z.setFont(new   java.awt.Font("Dialog",   1,   15));
        paramConfig.add(z);
        //z.setBorder(BorderFactory.createRaisedBevelBorder());
        z.setBounds(new Rectangle(318*screenWidth/1440, 10*screenHeight/900, 50*screenWidth/1440, 20*screenHeight/900));

        JLabel anchorItem0 = new JLabel("anchor 0",JLabel.CENTER);
        paramConfig.add(anchorItem0);
        anchorItem0.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem0.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem0.setBounds(new Rectangle(10*screenWidth/1440, 51*screenHeight/900, 80*screenWidth/1440, 30*screenHeight/900));

        //JTextField textx0 = new JTextField();//x0
        paramConfig.add(textx0);
        textx0.setBounds(101*screenWidth/1440,51*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textx0.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty0 = new JTextField();//y0
        paramConfig.add(texty0);
        texty0.setBounds(201*screenWidth/1440,51*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        texty0.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz0 = new JTextField();//z0
        paramConfig.add(textz0);
        textz0.setBounds(301*screenWidth/1440,51*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textz0.setHorizontalAlignment(JTextField.CENTER);

        JLabel anchorItem1 = new JLabel("anchor 1",JLabel.CENTER);
        paramConfig.add(anchorItem1);
        anchorItem1.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem1.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem1.setBounds(10*screenWidth/1440,101*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);

        //JTextField textx1 = new JTextField();//x1
        paramConfig.add(textx1);
        textx1.setBounds(101*screenWidth/1440,101*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textx1.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty1 = new JTextField();//y1
        paramConfig.add(texty1);
        texty1.setBounds(201*screenWidth/1440,101*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        texty1.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz1 = new JTextField();//z1
        paramConfig.add(textz1);
        textz1.setBounds(301*screenWidth/1440,101*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textz1.setHorizontalAlignment(JTextField.CENTER);

        JLabel anchorItem2 = new JLabel("anchor 2",JLabel.CENTER);
        paramConfig.add(anchorItem2);
        anchorItem2.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem2.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem2.setBounds(10*screenWidth/1440,151*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);

        //JTextField textx2 = new JTextField();//x2
        paramConfig.add(textx2);
        textx2.setBounds(101*screenWidth/1440,151*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textx2.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty2 = new JTextField();//y2
        paramConfig.add(texty2);
        texty2.setBounds(201*screenWidth/1440,151*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        texty2.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz2= new JTextField();//z2
        paramConfig.add(textz2);
        textz2.setBounds(301*screenWidth/1440,151*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textz2.setHorizontalAlignment(JTextField.CENTER);

        JLabel anchorItem3 = new JLabel("anchor 3",JLabel.CENTER);
        paramConfig.add(anchorItem3);
        anchorItem3.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem3.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem3.setBounds(10*screenWidth/1440,201*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);


        //JTextField textx3 = new JTextField();//x3
        paramConfig.add(textx3);
        textx3.setBounds(101*screenWidth/1440,201*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textx3.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty3 = new JTextField();//y3
        paramConfig.add(texty3);
        texty3.setBounds(201*screenWidth/1440,201*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        texty3.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz3= new JTextField();//z3
        paramConfig.add(textz3);
        textz3.setBounds(301*screenWidth/1440,201*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textz3.setHorizontalAlignment(JTextField.CENTER);

        JLabel title2 = new JLabel("Other Parameters",JLabel.CENTER);
        paramConfig.add(title2);
        title2.setBorder(BorderFactory.createRaisedBevelBorder());
        title2.setFont(new   java.awt.Font("Dialog",   2,   19));
        title2.setBounds(new Rectangle(402*screenWidth/1440, 10*screenHeight/900, 200*screenWidth/1440, 30*screenHeight/900));

        JLabel textScale = new JLabel("Scale :",JLabel.CENTER);
        paramConfig.add(textScale);
        textScale.setFont(new   java.awt.Font("Dialog",   1,   13));
        textScale.setBounds(new Rectangle(402*screenWidth/1440, 51*screenHeight/900, 80*screenWidth/1440, 30*screenHeight/900));
        //textScale.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel textTargetHeight = new JLabel("Target Height :",JLabel.CENTER);
        paramConfig.add(textTargetHeight);
        textTargetHeight.setFont(new   java.awt.Font("Dialog",   1,   13));
        textTargetHeight.setBounds(new Rectangle(412*screenWidth/1440, 101*screenHeight/900, 100*screenWidth/1440, 30*screenHeight/900));
        //textTargetHeight.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel textInterval = new JLabel("Schedule Interval :",JLabel.CENTER);
        paramConfig.add(textInterval);
        textInterval.setFont(new   java.awt.Font("Dialog",   1,   13));
        textInterval.setBounds(new Rectangle(402*screenWidth/1440, 151*screenHeight/900, 130*screenWidth/1440, 30*screenHeight/900));
        textInterval.setHorizontalAlignment(JTextField.CENTER);
        //textInterval.setBorder(BorderFactory.createRaisedBevelBorder());

        //JTextField scaleText = new JTextField();  //scale
        paramConfig.add(scaleText);
        scaleText.setBounds(542*screenWidth/1440,51*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);
        scaleText.setHorizontalAlignment(JTextField.CENTER);

        //JTextField heightText = new JTextField();  //height
        paramConfig.add(heightText);
        heightText.setBounds(542*screenWidth/1440,101*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);
        heightText.setHorizontalAlignment(JTextField.CENTER);

        //JTextField intervalText = new JTextField();  //interval
        paramConfig.add(intervalText);
        intervalText.setBounds(542*screenWidth/1440,151*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);
        intervalText.setHorizontalAlignment(JTextField.CENTER);



        JButton config = new JButton("Save");
        config.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ParamConfig.writeText();
                JFrame dialogFrame = new  JFrame("提示");
                dialogFrame.setBounds(600*screenWidth/1440,400*screenHeight/900,250*screenWidth/1440,150*screenHeight/900);
                dialogFrame.setResizable(false);
                dialogFrame.setLayout(null);
                dialogFrame.setVisible(true);


                JLabel tip = new JLabel("参数设置成功!");
                tip.setBounds(72*screenWidth/1440,15*screenHeight/900,110*screenWidth/1440,20*screenHeight/900);
                tip.setFont(new   java.awt.Font("Dialog",   2,   16));
                //tip.setBorder(BorderFactory.createRaisedBevelBorder());
                dialogFrame.add(tip);


                JButton ok = new JButton("OK");
                ok.setBounds(87*screenWidth/1440,60*screenHeight/900,60*screenWidth/1440,20*screenHeight/900);
                ok.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialogFrame.dispose();
                        //repaint();
                    }

                });
                dialogFrame.add(ok);


            }
        });
        paramConfig.add(config);
        config.setBounds(402*screenWidth/1440,201*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        config.setFont(new   java.awt.Font("Dialog",   2,   18));
        config.setBorder(BorderFactory.createRaisedBevelBorder());
        config.addActionListener(this);



        JButton load = new JButton("Load");

        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ParamConfig.loadText();
                JFrame dialogFrame = new  JFrame("提示");
                dialogFrame.setBounds(600*screenWidth/1440,400*screenHeight/900,250*screenWidth/1440,150*screenHeight/900);
                dialogFrame.setResizable(false);
                dialogFrame.setLayout(null);
                dialogFrame.setVisible(true);


                JLabel tip = new JLabel("获取参数成功!");
                tip.setBounds(67*screenWidth/1440,15*screenHeight/900,110*screenWidth/1440,20*screenHeight/900);
                tip.setFont(new   java.awt.Font("Dialog",   2,   16));
                //tip.setBorder(BorderFactory.createRaisedBevelBorder());
                dialogFrame.add(tip);


                JButton ok = new JButton("OK");
                ok.setBounds(83*screenWidth/1440,60*screenHeight/900,60*screenWidth/1440,20*screenHeight/900);
                ok.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialogFrame.dispose();
                    }
                });
                dialogFrame.add(ok);


            }
        });

        paramConfig.add(load);
        load.setBounds(522*screenWidth/1440,201*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        load.setFont(new   java.awt.Font("Dialog",   2,   18));
        load.setBorder(BorderFactory.createRaisedBevelBorder());






        /*
         * 画小区域的组件
         * */
        JPanel drawCurve = new JPanel();
        drawCurve.setBackground(Color.white);
        drawCurve.setLayout(new GridLayout());
        drawCurve.setBounds(1000*screenWidth/1440,560*screenHeight/900,270*screenWidth/1440,180*screenHeight/900);
        drawCurve.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black) );
        this.getLayeredPane().add(drawCurve);
        drawCurve.setLayout(null);

        JLabel dataProcs = new JLabel("Data Process",JLabel.CENTER);
        drawCurve.add(dataProcs);
        dataProcs.setFont(new   java.awt.Font("Dialog",   2,   20));
        dataProcs.setBounds(35*xTrans,5*yTrans,200*xTrans,35*yTrans);
        //dataProcs.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton drawTrace = new JButton("Draw Trace");
        drawCurve.add(drawTrace);
        drawTrace.setFont(new   java.awt.Font("Dialog",   2,   18));
        drawTrace.setBounds(55*xTrans,55*yTrans,150*xTrans,30*yTrans);

        drawTrace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //DrawSmoothCurve.createAndShowGui();
                DrawSmoothCurve d = new DrawSmoothCurve();
                d.createAndShowGui();

            }
        });



        /*
         * 在anchor状态区设置按钮
         * */
        JButton LaunchAll = new JButton("launchAll");
        //LaunchAll.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        LaunchAll.setFont(new   java.awt.Font("Dialog",   1,   14));
        LaunchAll.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton KillAll = new JButton("killAll");
        KillAll.setFont(new   java.awt.Font("Dialog",   1,   14));
        //KillAll.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        KillAll.setBorder(BorderFactory.createRaisedBevelBorder());

        this.getLayeredPane().add(LaunchAll);
        LaunchAll.setBounds(1120*xTrans, 20*yTrans, 110*xTrans, 40*yTrans);
        this.getLayeredPane().add(KillAll);
        KillAll.setBounds(1120*xTrans, 60*yTrans, 110*xTrans, 40*yTrans);
        //加入监听器
        Listener1 L1 = new Listener1();
        LaunchAll.addActionListener(L1);
        Listener2 L2 = new Listener2();
        KillAll.addActionListener(L2);



/*
* 设置主Frame
* */

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

        //DrawSmoothCurve.createAndShowGui();


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
}
//■●





//以下代码用于获取当前屏幕分辨率
//class ScreenSize {
//    private int screenWidth;
//    private int screenHeight;
//
//    public int getScreenWidth() {
//
//        setScreenWidth(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
//        return screenWidth;
//    }
//
//    public void setScreenWidth(int screenWidth) {
//        this.screenWidth = screenWidth;
//    }
//
//    public int getScreenHeight() {
//        setScreenHeight(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
//        return screenHeight;
//    }
//
//    public void setScreenHeight(int screenHeight) {
//        this.screenHeight = screenHeight;
//    }
//}









