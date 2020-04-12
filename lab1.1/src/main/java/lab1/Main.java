package lab1;

import lab1.metrics.CostFunc;
import lab1.metrics.Metrics;
import org.opencv.core.Core;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        int[][][] lImg = ImageUtils.readImg("src/main/resources/views/view3.png");
        int[][][] rImg = ImageUtils.readImg("src/main/resources/views/view4.png");

        boolean parallel = true;
        int maxHorizontalShift = 50;
        int maxVerticalShift = 1;
        int alpha = 10;
        Metrics metrics = Metrics.Euclidean; //distance between pixels (Euclidean, Manhattan)
        CostFunc g = CostFunc.MinL1; //now returns |d - d'|, to make it return min(B,|..|) call g.setBetta(double betta); (MinL1, MinL2)
        g.setBetta(0);

        long t1 = System.currentTimeMillis();

        ShiftMap shiftMap = ShiftMap.builder().alpha(alpha).horizontalD(maxHorizontalShift).verticalD(maxVerticalShift)
                .parallel(parallel).metrics(metrics).g(g).build();

        int[][][] bytes = shiftMap.buildShiftMap(lImg, rImg);

        System.out.println(System.currentTimeMillis() - t1);

        ImageUtils.writeImg(bytes, "src/main/resources/views/shift.png");
    }

}
