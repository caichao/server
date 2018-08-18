import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ParamConfig {
    static File f=new File("e:/文本框.txt");//向指定文本框内写入




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



    public void readTxt(File file){
        try{
            File f=new File("e:/文本框.txt");//向指定文本框内写入

        }catch(Exception e){

        }
    }

}
