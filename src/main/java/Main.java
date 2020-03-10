import org.opencv.core.Core;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        ImageUtils image = new ImageUtils();
        byte[][][] lImg = image.readImg("src/main/resources/views/view0.png");
        byte[][][] rImg = image.readImg("src/main/resources/views/view2.png");

        long t1 = System.currentTimeMillis();

        byte[][][] m1 = new byte[][][]{{{1,1,1},{2,2,2},{3,3,3}}
                                            ,{{4,4,4},{5,5,5},{6,6,6},
                                            {7,7,7},{8,8,8},{9,9,9}}};
        byte[][][] m2 = new byte[][][]{{{10,10,10},{2,2,2},{3,3,3}}
                ,{{20,20,20},{5,5,5},{6,6,6}},
                {{40,40,40},{8,8,8},{9,9,9}}};


        ShiftMap shiftMap = new ShiftMap(10, 3, 3);
        byte[][][] bytes = shiftMap.buildShiftMap(m2, m1);
        System.out.println(System.currentTimeMillis() - t1);

        image.writeImg(bytes, "src/main/resources/views/vw1.png");
    }

}
