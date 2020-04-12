package lab1;

import org.opencv.core.Core;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        byte[][][] lImg = ImageUtils.readImg("src/main/resources/views/view3.png");
        byte[][][] rImg = ImageUtils.readImg("src/main/resources/views/view4.png");

        long t1 = System.currentTimeMillis();

        ShiftMap shiftMap = new ShiftMap(10, 50, 1);
        byte[][][] bytes = shiftMap.buildShiftMap(lImg, rImg);

        System.out.println(System.currentTimeMillis() - t1);

        ImageUtils.writeImg(bytes, "src/main/resources/views/shift.png");
    }

}
