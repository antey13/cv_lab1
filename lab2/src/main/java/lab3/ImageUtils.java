package lab3;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageUtils {

    public static void writeRect(SimpleMatrix R, String input, String output){
        R = R.invert();

        Mat matrix = Imgcodecs.imread(input);
        Mat rect = new Mat(matrix.rows(),matrix.cols(),matrix.type());

        for (int i = 0; i < matrix.rows(); i++) {
            for (int j = 0; j < matrix.cols(); j++) {
                SimpleMatrix xr = R.mult(new SimpleMatrix(3, 1, true, new double[]{j, i, 1}));
                if(xr.get(0,0) < matrix.cols() && xr.get(1,0) < matrix.rows() &&
                        xr.get(0,0) >= 0 && xr.get(1,0) >= 0){
                    rect.put(i,j,matrix.get((int) xr.get(1,0), (int)xr.get(0,0)));
                }
            }
        }

        Imgcodecs.imwrite(output, rect);
    }
}
