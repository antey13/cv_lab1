package lab2;

import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;
import org.opencv.core.Core;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class lab5 {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String left = "src/main/resources/views/panoram_left.png";
        String right = "src/main/resources/views/panoram_right.png";
        int[][][] lImg = ImageUtils.readImg(left);
        int[][][] rImg = ImageUtils.readImg(right);
        List<Point> pointsLeft = new ArrayList<>();
        double[][] selectedPoints2 = new double[][]{
                {638, 256, 1}, {1232, 576, 1}, {952, 474, 1}, {412, 868, 1}
        };
        double[][] selectedPoints1 = new double[][]{
                {364, 262, 1}, {922, 578, 1}, {678, 486, 1}, {112, 916, 1}
        };
        SimpleMatrix X = new SimpleMatrix(8, 9);
        for (int i = 0; i < 4; ++i) {
            SimpleMatrix xr = new SimpleMatrix(1, 3, true, selectedPoints2[i]);
            X.setRow(2 * i, 6, mult(xr, selectedPoints1[i][0]).getDDRM().data);
            X.setRow(2 * i, 3, 0, 0, 0);
            X.setRow(2 * i, 0, mult(xr, -1).getDDRM().data);
            X.setRow(2 * i + 1, 6, mult(xr, selectedPoints1[i][1]).getDDRM().data);
            X.setRow(2 * i + 1, 3, mult(xr, -1).getDDRM().data);
            X.setRow(2 * i + 1, 0, 0, 0, 0);
            pointsLeft.add(new Point(selectedPoints2[i][0],selectedPoints2[i][1]));
        }
        SimpleMatrix H = nullSpace(X);
        H.reshape(3, 3);
        H = H.divide(H.get(2, 2));
//        H = H.invert();

        selectedPoints2 = new double[][]{
                {38, 270, 1}, {398, 510, 1}, {902, 800, 1}, {878, 404, 1}
        };
        selectedPoints1 = new double[][]{
                {364, 262, 1}, {678, 486, 1}, {1212, 818, 1}, {1194, 392, 1}
        };
        X = new SimpleMatrix(8, 9);
        List<Point> pointsRight = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            SimpleMatrix xr = new SimpleMatrix(1, 3, true, selectedPoints2[i]);
            X.setRow(2 * i, 6, mult(xr, selectedPoints1[i][0]).getDDRM().data);
            X.setRow(2 * i, 3, 0, 0, 0);
            X.setRow(2 * i, 0, mult(xr, -1).getDDRM().data);
            X.setRow(2 * i + 1, 6, mult(xr, selectedPoints1[i][1]).getDDRM().data);
            X.setRow(2 * i + 1, 3, mult(xr, -1).getDDRM().data);
            X.setRow(2 * i + 1, 0, 0, 0, 0);
            pointsRight.add(new Point(selectedPoints2[i][0],selectedPoints2[i][1]));
        }
        SimpleMatrix H2 = nullSpace(X);
        H2.reshape(3, 3);
        H2 = H2.divide(H2.get(2, 2));
        Lab5Util.writeH(H,H2);
        int[][][] leftRes = new int[lImg.length][lImg[0].length][3];
        int[][][] rightRes = new int[lImg.length][lImg[0].length][3];
        int[][][] centerRes = new int[lImg.length][lImg[0].length][3];
        List<Point> ppt = new ArrayList<>();
        for (int i = 0; i < lImg.length; i++) {
            for (int j = 0; j < lImg[0].length; j++) {
                SimpleMatrix x = new SimpleMatrix(3, 1, false, new double[]{j, i, 1});
                SimpleMatrix mult = H.invert().mult(x);
                mult = mult.divide(mult.get(2, 0));
                SimpleMatrix mult2 = H2.invert().mult(x);
                mult2 = mult.divide(mult2.get(2, 0));
                SimpleMatrix mult3 = x;

                if (!(mult.get(1, 0) >= lImg.length || mult.get(0, 0) >= lImg[0].length || mult.get(0, 0) < 0 || mult.get(1, 0) < 0)) {
                    if(pointsLeft.contains(new Point(j,i))){
                        ppt.add(new Point(mult.get(0,0), mult.get(1,0)));
                    }
                    leftRes[i][j] = lImg[(int) mult.get(1, 0)][(int) mult.get(0, 0)];
                }
                if (!(mult2.get(1, 0) >= lImg.length || mult2.get(0, 0) >= lImg[0].length || mult2.get(0, 0) < 0 || mult2.get(1, 0) < 0)) {
                    rightRes[i][j] = rImg[(int) mult2.get(1, 0)][(int) mult2.get(0, 0)];
                }
            }
        }
        lab2.ImageUtils.writeImg(leftRes, "src/main/resources/views/leftREE.png");
        lab2.ImageUtils.writeImg(rightRes, "src/main/resources/views/rightREE.png");
        ImageUtils.writeDebug("src/main/resources/views/leftREE.png","src/main/resources/views/leftREE.png",ppt);
    }

    private static SimpleMatrix nullSpace(SimpleMatrix simpleMatrix) {
        SimpleSVD<SimpleMatrix> svd = simpleMatrix.svd();
        SimpleMatrix v = svd.getV();
        return v.extractVector(false, v.numCols() - 1);
    }

    private static SimpleMatrix mult(SimpleMatrix matrix, double k) {
        SimpleMatrix res = new SimpleMatrix(matrix.numRows(), matrix.numCols());
        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                res.set(i, j, matrix.get(i, j) * k);
            }
        }
        return res;
    }
}

/*

        for (int i = 0; i < lImg.length; i++) {
            for (int j = 0; j < lImg[0].length; j++) {
                SimpleMatrix x = new SimpleMatrix(3, 1, false, new double[]{j, i, 1});
                SimpleMatrix mult = H.invert().mult(x);
                SimpleMatrix mult2 = H2.invert().mult(x);
                SimpleMatrix mult3 = T.mult(x);

                if (!(mult.get(1, 0) > lImg.length || mult.get(0, 0) > lImg[0].length || mult.get(0, 0) < 0 || mult.get(1, 0) < 0)){
                    result[(int) mult.get(1, 0)][(int) mult.get(0, 0)] = lImg[i][j];
                } else if(!(mult2.get(1, 0) > lImg.length || mult2.get(0, 0) > lImg[0].length || mult2.get(0, 0) < 0 || mult2.get(1, 0) < 0)){
                    result[(int) mult2.get(1, 0)][(int) mult2.get(0, 0)] = rImg[i][j];
                } else if(!(mult3.get(1, 0) > lImg.length || mult3.get(0, 0) > lImg[0].length || mult3.get(0, 0) < 0 || mult3.get(1, 0) < 0)){
                    result[(int) mult3.get(1, 0)][(int) mult3.get(0, 0)] = center[i][j];
                }
                /*result[(int) mult.get(1, 0)][(int) mult.get(0, 0)] = lImg[i][j];
                result[(int) mult2.get(1, 0)][(int) mult2.get(0, 0)] = rImg[i][j];
                result[(int) mult3.get(1, 0)][(int) mult3.get(0, 0)] = center[i][j];*/
                /*result[i][j][0] = lImg[(int) mult.get(1, 0)][(int) mult.get(0, 0)][0] + rImg[(int) mult2.get(1, 0)][(int) mult2.get(0, 0)][0] + center[(int) mult3.get(1, 0)][(int) mult3.get(0, 0)][0];
                result[i][j][1] = lImg[(int) mult.get(1, 0)][(int) mult.get(0, 0)][1] + rImg[(int) mult2.get(1, 0)][(int) mult2.get(0, 0)][1] + center[(int) mult3.get(1, 0)][(int) mult3.get(0, 0)][1];
                result[i][j][2] = lImg[(int) mult.get(1, 0)][(int) mult.get(0, 0)][2] + rImg[(int) mult2.get(1, 0)][(int) mult2.get(0, 0)][2] + center[(int) mult3.get(1, 0)][(int) mult3.get(0, 0)][2];
                result[i][j][0] /= 3;
                result[i][j][1] /= 3;
                result[i][j][2] /= 3;*/
          /*  }
                    }
        */
