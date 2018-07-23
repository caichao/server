import java.util.Random;

public class Algorithm {
    /**
     * shuffer the array randomly
     * @param arr
     * @return
     */
    public static int []  shufferArray(int [] arr) {
        int [] arr2 =new int[arr.length];
        int count = arr.length;
        int cbRandCount = 0;// 索引
        int cbPosition = 0;// 位置
        int k =0;
        do {
            //runCount++;
            Random rand = new Random(System.currentTimeMillis());
            int r = count - cbRandCount;
            cbPosition = rand.nextInt(r);
            arr2[k++] = arr[cbPosition];
            cbRandCount++;
            arr[cbPosition] = arr[r - 1];// 将最后一位数值赋值给已经被使用的cbPosition
        } while (cbRandCount < count);
        //System.out.println("m3运算次数  = "+runCount);
        return arr2;
    }
}
