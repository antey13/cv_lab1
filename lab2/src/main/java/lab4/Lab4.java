package lab4;

import lab2.ImageUtils;
import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Core;

public class Lab4 {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        double focal = 50;
        double pixelSize = 0.292;

        int[][][] lImg = ImageUtils.readImg("src/main/resources/views/shift21.png");
        var K = new IntrinsicMatrix(focal,pixelSize,new double[]{lImg[0].length/2,lImg.length/2});
        var F = new SimpleMatrix(3, 3, true, new double[]{
                4.1067037194909145E-16,-9.880648816490423E-14,1.0987708847119049,
                9.895556596362098E-14,-5.595228599018787E-16,-1.0987708847383877,
                -1.0987708847114932,1.0987708847376687,1.098770884568383});

        var E = new EssentialMatrix(K,F);
        System.out.println(E.isTrueEssential());

    }


}
