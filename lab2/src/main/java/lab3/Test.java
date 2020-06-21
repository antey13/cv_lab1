package lab3;

import lab2.ImageUtils;
import org.ejml.simple.SimpleMatrix;
import org.javatuples.Pair;
import org.opencv.core.Core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void weight(SimpleMatrix Rl, SimpleMatrix Rr) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        int[][][] shiftMap = ImageUtils.readImg("src/main/resources/views/shift21.png");
        String left = "src/main/resources/views/rectR.png";
        final String right = "src/main/resources/rect.png";
        int[][][] lImg = ImageUtils.readImg(left);
        int[][][] rImg = ImageUtils.readImg(right);

        List<Pair<Integer,Integer>> filtered = new ArrayList<>();
        for (int i = 1; i < shiftMap.length - 1; i++) {
            loop:
            for (int j = 1; j < shiftMap[0].length - 1; j++) {
                for (int k = 1; k <= 1; k++) {
                    if (!(Arrays.equals(shiftMap[i][j], shiftMap[i + k][j]) && Arrays.equals(shiftMap[i][j], shiftMap[i + k][j + k])
                            && Arrays.equals(shiftMap[i][j], shiftMap[i][j + k]) && Arrays.equals(shiftMap[i][j], shiftMap[i + k][j - k])
                            && Arrays.equals(shiftMap[i][j], shiftMap[i - k][j + k]) && Arrays.equals(shiftMap[i][j], shiftMap[i][j - k])
                            && Arrays.equals(shiftMap[i][j], shiftMap[i - k][j]) && Arrays.equals(shiftMap[i][j], shiftMap[i - k][j - k]))) {
                        continue loop;
                    }
                }
                filtered.add(Pair.with(i, j));
            }
        }
        Dob dob = new Dob();
        filtered.forEach(point -> {
            final Integer y = point.getValue0();
            final Integer x = point.getValue1();
            int[] shifts = shiftMap[y][x];
            SimpleMatrix l = Rl.mult(new SimpleMatrix(3,1,false,new double[]{x+ shifts[0], y+shifts[2],1}));
            l = l.divide(l.get(1,0));
            l = l.divide(l.get(2,0));
            SimpleMatrix r = Rr.mult(new SimpleMatrix(3,1,false,new double[]{x+shifts[0],y+shifts[2],1}));
            r = r.divide(r.get(1,0));
            r = r.divide(r.get(2,0));
            dob.w+=l.minus(r).normF();
        });
        System.out.println("Y error :"+dob.w);
    }

    private static class Dob {
        public double w = 0;
    }
}
