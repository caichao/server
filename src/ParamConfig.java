import javax.swing.*;
import java.io.*;

public class ParamConfig {
    private static String filePath = "config.txt";
    static File f = new File(filePath);//向指定文本框内写入
/*
    public void isFileThere(){
        if(!f.exists()){
            JFrame fileFrame = new JFrame("提示");
            fileFrame.setBounds(600*MainFrame.xTrans,400*MainFrame.yTrans,250*MainFrame.xTrans,150*MainFrame.yTrans);
            fileFrame.setResizable(false);
            fileFrame.setLayout(null);
            fileFrame.setVisible(true);

            JLabel fileTip = new JLabel("当前目录下缺少config.txt文件",JLabel.CENTER);
            fileFrame.add(fileTip);
        }
    }
*/


    public static void writeText(){
        try{
            FileWriter fw=new FileWriter(f);
            fw.write("{\r\n");
            fw.write("\"anchor0\":");
            fw.write("{\"x\":");
            fw.write("\""+MainFrame.textx0.getText()+"\",");
            fw.write("\"y\":");
            fw.write("\""+MainFrame.texty0.getText()+"\",");
            fw.write("\"z\":");
            fw.write("\""+MainFrame.textz0.getText()+"\"}");
            fw.write(",\r\n");
            fw.write("\"anchor1\":");
            fw.write("{\"x\":");
            fw.write("\""+MainFrame.textx1.getText()+"\",");
            fw.write("\"y\":");
            fw.write("\""+MainFrame.texty1.getText()+"\",");
            fw.write("\"z\":");
            fw.write("\""+MainFrame.textz1.getText()+"\"}");
            fw.write(",\r\n");
            fw.write("\"anchor2\":");
            fw.write("{\"x\":");
            fw.write("\""+MainFrame.textx2.getText()+"\",");
            fw.write("\"y\":");
            fw.write("\""+MainFrame.texty2.getText()+"\",");
            fw.write("\"z\":");
            fw.write("\""+MainFrame.textz2.getText()+"\"}");
            fw.write(",\r\n");
            fw.write("\"anchor3\":");
            fw.write("{\"x\":");
            fw.write("\""+MainFrame.textx3.getText()+"\",");
            fw.write("\"y\":");
            fw.write("\""+MainFrame.texty3.getText()+"\",");
            fw.write("\"z\":");
            fw.write("\""+MainFrame.textz3.getText()+"\"}");
            fw.write(",\r\n");
            fw.write("\"scale\":");
            fw.write("\""+MainFrame.scaleText.getText()+"\"");
            fw.write(",\r\n");
            fw.write("\"targetHeight\":");
            fw.write("\""+MainFrame.heightText.getText()+"\"");
            fw.write(",\r\n");
            fw.write("\"scheduleInterval\":");
            fw.write("\""+MainFrame.intervalText.getText()+"\"");
            fw.write("\r\n");
            fw.write("}");


            fw.close();
        }catch(Exception e){

        }
    }



    public static void loadText(){
        try{
            float [][] anchorLocation = JSONUtils.loadAnchorPosition(filePath);
            MainFrame.textx0.setText(""+anchorLocation[0][0]);
            MainFrame.texty0.setText(""+anchorLocation[0][1]);
            MainFrame.textz0.setText(""+anchorLocation[0][2]);
            MainFrame.textx1.setText(""+anchorLocation[1][0]);
            MainFrame.texty1.setText(""+anchorLocation[1][1]);
            MainFrame.textz1.setText(""+anchorLocation[1][2]);
            MainFrame.textx2.setText(""+anchorLocation[2][0]);
            MainFrame.texty2.setText(""+anchorLocation[2][1]);
            MainFrame.textz2.setText(""+anchorLocation[2][2]);
            MainFrame.textx3.setText(""+anchorLocation[3][0]);
            MainFrame.texty3.setText(""+anchorLocation[3][1]);
            MainFrame.textz3.setText(""+anchorLocation[3][2]);
            MainFrame.scaleText.setText(""+JSONUtils.getMapGUIScaleCoefficient(filePath));
            MainFrame.heightText.setText(""+JSONUtils.getTargetHeigh(filePath));
            MainFrame.intervalText.setText(""+JSONUtils.getScheduleInterval(filePath));
        }catch (Exception e){
        }

        }
    }







