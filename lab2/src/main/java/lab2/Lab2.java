package lab2;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Core;

public class Lab2 {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        int[][][] shiftMap = ImageUtils.readImg("src/main/resources/views/shift21.png");
        int[][][] lImg = ImageUtils.readImg("src/main/resources/views/hangerL-small1.png");
        int[][][] rImg = ImageUtils.readImg("src/main/resources/views/hangerR-small1.png");

        FundMatrix fundMatrix = new FundMatrix(shiftMap, lImg, 25, 25);
        long t1 = System.currentTimeMillis();
        SimpleMatrix F = fundMatrix.getFundMatrix(1_000);
        System.out.println(System.currentTimeMillis()-t1);
        F.reshape(3,3);

        ImageUtils.drawEpipolars(rImg,shiftMap,fundMatrix.selectedPoints,F,"src/main/resources/views/39Line.jpg");
    }


}
