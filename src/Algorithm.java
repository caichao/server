import java.util.Random;

import static java.lang.Math.pow;

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

    public static float[] getMinCoordinates(float[][] coordinates){
        float[] positions = new float[2];
        positions[0] = coordinates[0][0];
        positions[1] = coordinates[0][1];
        for(int i = 1; i < coordinates.length; i++){
            if(positions[0] > coordinates[i][0]){
                positions[0] = coordinates[i][0];
            }
            if(positions[1] > coordinates[i][1]){
                positions[1] = coordinates[i][1];
            }
        }
        return positions;
    }

    public static float[] getMaxCoordinates(float[][] coordinates){
        float[] positions = new float[2];
        positions[0] = coordinates[0][0];
        positions[1] = coordinates[0][1];
        for(int i = 1; i < coordinates.length; i++){
            if(positions[0] < coordinates[i][0]){
                positions[0] = coordinates[i][0];
            }
            if(positions[1] < coordinates[i][1]){
                positions[1] = coordinates[i][1];
            }
        }
        return positions;
    }

    /**
     * use guassian function to generate weights corresponding to the measurements
     * @param m
     * @return
     */
    public static float guassian(float m, float mu, float sigma){   //calculate weights of the particle
        float r = (float) Math.exp(pow((m - mu)/sigma, 2) / (-2));
        r = (float) (1.0f / Math.sqrt(2 * Math.PI) / sigma * r);
        return r;
    }

    /**
     * calculate the euclidian distance between two points
     * @param x: point one in 3D, coordinates for the particles
     * @param y: point two in 3D, coordinates for anchors
     * @return distance in float format
     */
    public static float euclidianDistance(float x[], float y[]){        //欧几里得距离
        return (float) Math.sqrt(Math.pow(x[0] - y[0], 2) + Math.pow(x[1] - y[1], 2)); //  + Math.pow(this.z - y[2], 2));
    }

    /**
     * find the index of the top k value in w
     * a naive solution, can be improve by heap based solution
     * @param w: array
     * @param n: the n top value
     * @return: the index of the top n value
     */
    public static int[] topK(float w[], int n){

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
    public static boolean isInSet(int index, int[] s){
        for(int i = 0; i < s.length; i++){
            if(index == s[i]){
                return true;
            }
        }
        return false;
    }

    public static int minIndex(int [] s){
        float min = s[0];
        int index = 0;
        for(int i = 1; i < s.length; i++){
            if(min > s[i]){
                index = i;
            }
        }
        return index;
    }
}
