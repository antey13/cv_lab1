package lab4;

import lab2.ImageUtils;
import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Core;

public class Lab4 {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        double focal = 4.2;
        double pixelSize = 0.5624;

        int[][][] lImg = ImageUtils.readImg("src/main/resources/views/shift.png");
        var K = new IntrinsicMatrix(focal, pixelSize, new double[]{lImg[0].length / 2, lImg.length / 2});
        var F = new SimpleMatrix(3, 3, true, new double[]{
                0.0,-2.736526283353413E-15,3.327893516313907E-14,2.777292285038868E-15,1.4257258568184383E-17,-89.42953059701387,-3.078787225163637E-14,89.42953059701381,5.216937992713611E-13});

        var E = new EssentialMatrix(K, F);
        System.out.println("E:\n" + E);
        System.out.println("Check if it is essential: " + E.isTrueEssential());
        System.out.println("Vector c from E decomposition " + E.getC());
        System.out.println("Matrix R from E decomposition " + E.getR());
    }


}
