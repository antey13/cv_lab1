package lab2;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Core;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        List<Point> ppt = new ArrayList<>();
        double[][] selectedPoints1 = new double[][]{
                {638, 256, 1}, {1232, 576, 1}, {952, 474, 1}, {412, 868, 1}
        };
        for (int i = 0; i < selectedPoints1.length; i++) {
            ppt.add(new Point(selectedPoints1[i][0],selectedPoints1[i][1]));
        }
        ImageUtils.writeDebug("src/main/resources/views/panoram_left.png","src/main/resources/views/leftPoints.png",ppt);
    }
}
