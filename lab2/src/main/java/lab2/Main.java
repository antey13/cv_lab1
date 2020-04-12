package lab2;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Core;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        int[][][] shiftMap = ImageUtils.readImg("src/main/resources/views/shift.png");
        int[][][] lImg = ImageUtils.readImg("src/main/resources/views/view5.png");
        int[][][] rImg = ImageUtils.readImg("src/main/resources/views/view6.png");

        FundMatrix fundMatrix = new FundMatrix(shiftMap, lImg, 50, 20);
        SimpleMatrix F = fundMatrix.getFundMatrix(100);

//        final DMatrixRMaj c = new SimpleMatrix(1, 9).getDDRM();
//        MatrixVectorMult_DDRM.mult(simpleMatrix.getDDRM(),simpleMatrix.svd().getV().extractVector(false, 8).getDDRM(), c);
    }


}
