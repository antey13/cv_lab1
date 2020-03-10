import org.opencv.core.Core;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        ImageUtils image = new ImageUtils();
        byte[][][] lImg = image.readImg("src/main/resources/views/view0.png");
        byte[][][] rImg = image.readImg("src/main/resources/views/view2.png");

        long t1 = System.currentTimeMillis();

        ShiftMap shiftMap = new ShiftMap(20, 50, 1);
        byte[][][] bytes = shiftMap.buildShiftMap(lImg, rImg);
        System.out.println(System.currentTimeMillis() - t1);

        image.writeImg(bytes, "src/main/resources/views/vw1.png");
    }

}
