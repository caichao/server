import java.io.*;

public class ParamConfig {
    static File f = new File("e:/文本框.txt");//向指定文本框内写入




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
            float [][] anchorLocation = JSONUtils.loadAnchorPosition("e:/文本框1.txt");
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
            MainFrame.scaleText.setText(""+JSONUtils.getMapGUIScaleCoefficient("e:/文本框1.txt"));
            MainFrame.heightText.setText(""+JSONUtils.getTargetHeigh("e:/文本框1.txt"));
            MainFrame.intervalText.setText(""+JSONUtils.getScheduleInterval("e:/文本框1.txt"));
        }catch (Exception e){
        }

        }
    }







