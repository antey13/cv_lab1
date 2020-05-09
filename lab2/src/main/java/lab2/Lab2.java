package lab2;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Core;

public class Lab2 {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        int[][][] shiftMap = ImageUtils.readImg("src/main/resources/views/shift1.png");
        int[][][] lImg = ImageUtils.readImg("src/main/resources/views/38.jpg");
        int[][][] rImg = ImageUtils.readImg("src/main/resources/views/39.jpg");

        FundMatrix fundMatrix = new FundMatrix(shiftMap, lImg, 50, 10);
        long t1 = System.currentTimeMillis();
        SimpleMatrix F = fundMatrix.getFundMatrix(100);
        System.out.println(System.currentTimeMillis()-t1);
        F.reshape(3,3);

        ImageUtils.drawEpipolars(rImg,shiftMap,F,"src/main/resources/views/39Line.jpg");
    }


}