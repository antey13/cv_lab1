import org.opencv.core.Core;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        ImageUtils image = new ImageUtils();
        byte[][][] lImg = image.readImg("src/main/resources/views/38.jpg");
        byte[][][] rImg = image.readImg("src/main/resources/views/39.jpg");

        long t1 = System.currentTimeMillis();

        ShiftMap shiftMap = new ShiftMap(10, 20, 1);
        byte[][][] bytes = shiftMap.buildShiftMap(lImg, rImg);

        System.out.println(System.currentTimeMillis() - t1);

        image.writeImg(bytes, "src/main/resources/views/shift.png");
    }

}
