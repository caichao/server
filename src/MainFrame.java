import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements Runnable{

    private String imgFilePath = "images/map1.jpg";
    private DrawParticles drawParticles = null;
    private ParticleFilter particleFilter = null;
    private boolean isThreaAlive = true;
    public MainFrame(ParticleFilter particleFilter){

        ImageIcon img = new ImageIcon(imgFilePath);
        //要设置的背景图片
        JLabel imgLabel = new JLabel(img);
        //将背景图放在标签里。
        this.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));
        //将背景标签添加到jfram的LayeredPane面板里。
        imgLabel.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());

        //this.getLayeredPane().add(new MyPanel2(), new Integer(Integer.MIN_VALUE));

        // 设置背景标签的位置
        Container contain = this.getContentPane();

        ((JPanel) contain).setOpaque(false);
        // 将内容面板设为透明。将LayeredPane面板中的背景显示出来。

        //this.add(new MyPanel2());
        this.particleFilter = particleFilter;
        drawParticles = new DrawParticles(this.particleFilter);
        this.add(drawParticles);

        this.setVisible(true);
        this.setResizable(false);
        this.setSize(img.getIconWidth(),img.getIconHeight());
        this.setTitle("Asynchronous Localization Project: by cc at HUST");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);//指定界面默认关闭选项  EXIT_ON_CLOSE为关闭时退出程序
        this.setLocationRelativeTo(null);// 把窗口位置设置到屏幕的中心
    }

    public void refresh(){

    }

    @Override
    public void run() {
        drawParticles.initDrawingParameters();
        while(isThreaAlive){
            drawParticles.repaint();
            try{
                Thread.sleep(10);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class DrawParticles extends JPanel{

        ParticleFilter particleFilter = null;
        BasicStroke basicStroke = null;       // 笔画的轮廓（画笔宽度/线宽为3px）
        BasicStroke boldStroke = null;
        float[][] particles = null;
        float[][] allParticles = null;
        int numberOfParticles = 100;
        int scale = 100;
        Font font = null;

        /*public DrawParticles(){ // for debug test
            this.setOpaque(false);
            //this.setBackground(Color.WHITE);
            particleFilter = new ParticleFilter(5000);
            particleFilter.generateUniformParticles(4.2f, 4.5f);
            basicStroke = new BasicStroke(5);
            particles = particleFilter.getParticles();

            copyTopParticles(numberOfParticles);
        }*/

        public void refreshParticleLocation(){
            repaint();
        }

        public DrawParticles(ParticleFilter particleFilter){
            this.particleFilter = particleFilter;
            this.setOpaque(false);
            initDrawingParameters();
        }

        public void initDrawingParameters(){
            basicStroke = new BasicStroke(3);
            boldStroke = new BasicStroke(5);
            font = new Font("宋体",Font.BOLD,40);
            //particles = particleFilter.getParticles().clone(); // deep copy
            //int index[] = particleFilter.topK(particleFilter.getWeights(), numberOfParticles);
            particles = new float[numberOfParticles][2];
            allParticles = particleFilter.getParticles();
        }

        public void copyTopParticles(int n){
            int index[] = particleFilter.topK(particleFilter.getWeights(),  n);
            for(int i = 0; i < n ; i++){
                particles[i] = allParticles[index[i]];
            }
        }

        public void paint(Graphics g){

            super.paint(g);

            Graphics2D g2 = (Graphics2D) g;

            g2.setStroke(basicStroke);
            g2.setColor(Color.WHITE);
            // first erase the last particles
            if(particles != null) {
                for (int i = 0; i < this.particles.length; i++) {
                    drawPoints(particles[i][0], particles[i][1], g2);
                }
            }

            copyTopParticles(numberOfParticles);
            // second draw the new particles
            //particles = particleFilter.getParticles().clone();
            g2.setColor(Color.RED);
            for(int i = 0; i < particles.length ; i++){
                drawPoints(particles[i][0], particles[i][1], g2);
            }

            // draw the estimated locations
            g2.setColor(Color.GREEN);
            g2.setStroke(boldStroke);
            drawEstimatedLocation(particleFilter.getX(), particleFilter.getY(), g2);

            // add labels for the estimated locations
            g2.setColor(Color.BLUE);

            drawLabels(particleFilter.getX(), particleFilter.getY(), String.format("x = %.2f y = %.2f", particleFilter.getX(), particleFilter.getY()), g2);
            repaint();
        }

        public void drawPoints(float x, float y, Graphics2D g){
            g.drawLine((int)(x * scale), (int)(y * scale), (int)(x * scale), (int)(y * scale));
        }

        public void drawEstimatedLocation(float x, float y, Graphics2D g){
            g.drawLine((int)(x * scale) - 5,(int)(y * scale), (int)(x * scale)+5,(int)(y * scale));
            g.drawLine((int)(x * scale), (int)(y * scale)+5, (int)(x * scale), (int)(y * scale)-5);
        }

        public void drawLabels(float x, float y, String msg, Graphics2D g){
            g.setFont(font);
            g.drawString(msg, (int)(x * scale) + 20, (int)(y * scale) + 20);
        }
    }

    //重写容器类，比如JPanel类的PaintCoponent()方法绘制图形
    class BackgroundPannel extends JPanel {
        Image img;
        public BackgroundPannel(Image img){
            this.img = img;
            this.setOpaque(false);//设置透明度
        }
        //绘制容器
        public void PaintCoponent(Graphics g)
        {
            super.paintComponent(g);//获取父类原来的绘制组件的方法
            //第一个参数是要作为背景的图片，第2/3代表开始坐标，第4/5代表图片的宽度和高度
            g.drawImage(img,0,0,this.getWidth(),this.getHeight(),this);
        }
    }

    // for debug test
    class MyPanel2 extends JPanel{
        //定义一个乌龟
        Tortoise t = null;

        //构造函数
        public MyPanel2(){
            t = new  Tortoise(100,100);
            this.setOpaque(false);//设置透明度，this is of vital importance
        }

        //画乌龟
        public void drawTortoise(int x, int y, Graphics g){
            //1.画脸
            g.setColor(Color.green);
            g.fillOval(x+60, y, 30, 15);
            //2.画左眼
            g.setColor(Color.black);
            g.fillOval(x+65, y+3, 5, 5);
            //3.画右眼
            g.fillOval(x+78, y+3, 5, 5);
            //4.画脖子
            g.setColor(Color.green);
            g.fillOval(x+70, y, 10, 42);
            //5.画乌龟壳
            g.setColor(Color.red);
            g.fillOval(x+40, y+40, 70, 100);
            //6.画左上脚
            g.setColor(Color.green);
            g.fillOval(x+15, y+60, 30, 10);
            //7.画右上脚
            g.fillOval(x+105, y+60, 30, 10);
            //8.画左下脚
            g.fillOval(x+15, y+110, 30, 10);
            //9.画右下脚
            g.fillOval(x+105, y+110, 30, 10);
            //10.画尾巴
            g.setColor(Color.black);
            g.drawLine(x+70,y+140,x+130,y+210);
            g.drawOval(x+95, y+150, 30, 30);
        }


        //覆盖JPanel的paint方法
        //Graphics 是绘图的重要类。你可以把他理解成一只画笔
        public void paint(Graphics g){
            //1.调用父类函数完成初始化任务
            //这句话不能少
            super.paint(g);
            //2.画乌龟，调用方法即可
            this.drawTortoise(50, 50, g);
        }

    }

    //定义一个乌龟类
    class Tortoise {
        //表示乌龟的横坐标
        int x = 0;

        //表示乌龟的纵坐标
        int y = 0;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
        public Tortoise(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
}

