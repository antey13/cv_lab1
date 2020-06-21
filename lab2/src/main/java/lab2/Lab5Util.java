package lab2;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Core;

public class Lab5Util {
    private static String left = "src/main/resources/views/panoram_left.png";
    private static String right = "src/main/resources/views/panoram_right.png";
    private static String central = "src/main/resources/views/panoram_central.png";
    private static int[][][] lImg = ImageUtils.readImg(left);
    private static int[][][] rImg = ImageUtils.readImg(right);
    private static int[][][] center = ImageUtils.readImg(central);

    public static void writeH(SimpleMatrix HLeft, SimpleMatrix Hright){
        int[][][] result = new int[(int) (lImg.length * 1.5)][lImg[0].length * 3][3];
        SimpleMatrix T = new SimpleMatrix(new double[][]{{1, 0, (result[0].length / 2 - center[0].length / 2)}, {0, 1, 0}, {0, 0, 1}});

        int c = 0;
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                SimpleMatrix x = new SimpleMatrix(3, 1, false, new double[]{j, i, 1});
                SimpleMatrix multLeft = T.mult(HLeft).invert().mult(x);
                SimpleMatrix multCenter = T.invert().mult(x);
                SimpleMatrix multRight = T.mult(Hright).invert().mult(x);

                multLeft = multLeft.divide(multLeft.get(2, 0));
                multRight = multLeft.divide(multRight.get(2, 0));
                multCenter = multCenter.divide(multCenter.get(2, 0));
                int k = 0;
                if (!(multLeft.get(1, 0) >= lImg.length || multLeft.get(0, 0) >= lImg[0].length || multLeft.get(0, 0) < 0 || multLeft.get(1, 0) < 0)) {
                    k++;
                    result[i][j] = sum(lImg[(int) multLeft.get(1, 0)][(int) multLeft.get(0, 0)], result[i][j]);
                }
                if (!(multRight.get(1, 0) >= lImg.length || multRight.get(0, 0) >= lImg[0].length || multRight.get(0, 0) < 0 || multRight.get(1, 0) < 0)) {
                    k++;
                    result[i][j] = sum(rImg[(int) multRight.get(1, 0)][(int) multRight.get(0, 0)], result[i][j]);
                }
                if (!(multCenter.get(1, 0) >= lImg.length || multCenter.get(0, 0) >= lImg[0].length || multCenter.get(0, 0) < 0 || multCenter.get(1, 0) < 0)) {
                    k++;
                    result[i][j] = sum(center[(int) multCenter.get(1, 0)][(int) multCenter.get(0, 0)], result[i][j]);
                }
                if (k == 0) {
                    c++;
                    continue;
                }
                result[i][j][0] /= k;
                result[i][j][1] /= k;
                result[i][j][2] /= k;
            }
        }
        System.out.println(c);
        System.out.println(c * 1.0 / (result.length * result[0].length));
        lab2.ImageUtils.writeImg(result, "src/main/resources/views/panoram.png");
    }

    private static int[] sum(int[] arr1, int[] arr2) {
        int[] avg = new int[3];
        for (int i = 0; i < 3; i++) {
            avg[i] = arr1[i] + arr2[i];
        }
        return avg;
    }
}
