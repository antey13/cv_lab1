package lab2;

import org.ejml.data.DMatrixRMaj;
import org.ejml.simple.SimpleMatrix;

import java.util.Arrays;

public class Utils {

    public static double[] xFromPoints(int[] x1, int[] x2) {
        double[] x = new double[9];
        x[0] = x1[0] * x2[0];
        x[1] = x1[0] * x2[1];
        x[2] = x1[0];
        x[3] = x1[1] * x2[0];
        x[4] = x1[1] * x2[1];
        x[5] = x1[1];
        x[6] = x2[0];
        x[7] = x2[1];
        x[8] = 1;

        SimpleMatrix simpleMatrix = new SimpleMatrix(9, 1);
        for (int j = 0; j < 9; j++) {
            simpleMatrix.set(j, 0, x[j]);
        }
        return x;
    }

    public static SimpleMatrix xToVector(double[] x){
        return new SimpleMatrix(9,1,false,x);
    }

    public static SimpleMatrix vectorFromPoints(int[] x1, int[] x2){
        return xToVector(xFromPoints(x1,x2));
    }

    public static double normVector(DMatrixRMaj v){
        return Math.sqrt(Arrays.stream(v.getData()).map(d -> d*d).sum());
    }
}
