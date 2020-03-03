import org.opencv.core.Core;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        ImageUtils image = new ImageUtils();
        byte[][][] lImg = image.readImg("src/main/resources/views/aloe1.png");
        byte[][][] rImg = image.readImg("src/main/resources/views/aloe2.png");

        long t1 = System.currentTimeMillis();
       ShiftMap shiftMap = new ShiftMap(10, 50);
        byte[][] bytes = shiftMap.buildShiftMap(lImg, rImg);
        System.out.println(System.currentTimeMillis() - t1);

        image.writeImg(bytes,"src/main/resources/views/vw1.png");
    }

}
