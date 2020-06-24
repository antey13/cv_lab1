package lab1;

import lab1.metrics.CostFunc;
import lab1.metrics.Metrics;
import org.opencv.core.Core;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        int[][][] rImg = ImageUtils.readImg("src/main/resources/rightRes.jpg");

        int[][][] lImg = ImageUtils.readImg("src/main/resources/leftRes.jpg");

        boolean parallel = true;
        int maxHorizontalShift = 21;
        int maxVerticalShift = 19;//minimum 1
        int alpha = 5;

        ImageUtils.idx = 10;//lImg.length/maxHorizontalShift;
        Metrics metrics = Metrics.Euclidean; //distance between pixels (Euclidean, Manhattan)
        CostFunc g = CostFunc.MinL1; //now returns |d - d'|, to make it return min(B,|..|) call g.setBetta(double betta); (MinL1, MinL2)
        //g.setBetta(0);

        long t1 = System.currentTimeMillis();

        ShiftMap shiftMap = ShiftMap.builder().alpha(alpha).horizontalD(maxHorizontalShift).verticalD(maxVerticalShift)
                .parallel(parallel).metrics(metrics).g(g).build();

        int[][][] bytes = shiftMap.buildShiftMap(lImg, rImg);

        System.out.println(System.currentTimeMillis() - t1);


        ImageUtils.writeImg(bytes, "src/main/resources/views/shift.png");
        ImageUtils.idx = 10;
        ImageUtils.writeNorm(bytes,"src/main/resources/views/shift21.jpg");
    }

}
