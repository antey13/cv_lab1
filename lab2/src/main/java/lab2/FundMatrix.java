package lab2;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.mult.MatrixVectorMult_DDRM;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;
import org.javatuples.Pair;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lab2.Utils.*;

public class FundMatrix {
    private int[][][] shiftMap;
    private int[][][] lImg;
    private int maxH;
    private int maxV;
    private final double e = 1.0e-5;
    private List<Pair<Integer, Integer>> filtered;
    public List<Point> selectedPoints = new ArrayList<>();
    public List<double[]> selectedXs = new ArrayList<>();
    public SimpleMatrix epipolar;
    public SimpleMatrix lepipolar;

    public FundMatrix(int[][][] shiftMap, int[][][] lImg, int maxHShift, int maxVShift) {
        this.shiftMap = shiftMap;
        this.lImg = lImg;
        maxH = maxHShift;
        maxV = maxVShift;
    }

    public SimpleMatrix getFundMatrix(long iterations) {
        filterPoints(2);
        SimpleMatrix F = null;
        Pair<SimpleMatrix, Integer> bestPair = new Pair<>(F, -1);
        int mininliners = 1000;
        for (int i = 0; i < iterations; i++) {
            SimpleMatrix fx = getFX();
            Pair<SimpleMatrix, SimpleMatrix> f = nullSpace(fx);
            F = checkAndCorrect(f);

            if (F == null)
                continue;

            Pair<SimpleMatrix, Integer> pair = countTrue(F);
            if (pair.getValue1() < mininliners) {
                mininliners = pair.getValue1();
            }
            if (bestPair.getValue1() < pair.getValue1()) {
                bestPair = pair;
                System.out.println("Inliners: " + pair.getValue1());
            }

            selectedXs.clear();

            if (i % 100 == 0 && i != 0)
                System.out.println((i * 100) / iterations + "%");
        }

        System.out.println(bestPair.getValue1());
        System.err.println(mininliners);
        F = bestPair.getValue0();
        calculateEpipolarPoint(F);
        return F;
    }

    private SimpleMatrix checkAndCorrect(Pair<SimpleMatrix, SimpleMatrix> f) {
        if (f.getValue0().determinant() < e && new SimpleSVD<>(f.getValue0().getMatrix(), false).rank() == 2) {
            return f.getValue0();
        }
        if (f.getValue1().determinant() < e && new SimpleSVD<>(f.getValue1().getMatrix(), false).rank() == 2) {
            return f.getValue1();
        }
        SimpleMatrix F = calculateF(f);
        return F;
    }

    private SimpleMatrix calculateF(Pair<SimpleMatrix, SimpleMatrix> pair) {
        SimpleMatrix A = pair.getValue0();
        SimpleMatrix B = pair.getValue1();

        double detA = A.determinant();
        double detB = B.determinant();
        try {
            double tr1 = B.mult(A.invert()).trace();
            double tr2 = A.mult(B.invert()).trace();

            Cubic cubic = new Cubic();
            cubic.solve(detB, detB * tr2, detA * tr1, detA);
            double x1 = cubic.x1;

            if (detB * Math.pow(x1, 3.0) + x1 * x1 * detB * tr2 + x1 * detA * tr1 + detA > e) {
                System.err.println("EQUATION SOLVING");
            }

            mult(B, x1);

            return A.plus(B);
        } catch (Exception e) {
            return null;
        }
    }

    private SimpleMatrix getFX() {
        final SimpleMatrix simpleMatrix = new SimpleMatrix(7, 9);

        for (int i = 0; i < 7; i++) {
            int index = (int) Math.round(Math.random() * (filtered.size() - 1));
            final Pair<Integer, Integer> pair = filtered.get(index);
            int h = pair.getValue0();
            int l = pair.getValue1();
           /* int h = (int) Math.round(Math.random() * (lImg.length - 21)+20);
            int l = (int) Math.round(Math.random() * (lImg[0].length -21)+20);*/

            int[] x1 = shiftMap[h][l];

            selectedPoints.add(new Point(l, h));

            double[] x = xFromPoints(new int[]{l, h}, new int[]{l + x1[0], h + x1[2]});
            selectedXs.add(x);

            simpleMatrix.setRow(i, 0, x);
        }
        return simpleMatrix;
    }

    private Pair<SimpleMatrix, SimpleMatrix> nullSpace(SimpleMatrix simpleMatrix) {
        final SimpleSVD<SimpleMatrix> svd = simpleMatrix.svd();
        final SimpleMatrix v = svd.getV();


        SimpleMatrix f1 = v.extractVector(false, v.numCols() - 1);
        SimpleMatrix f2 = v.extractVector(false, v.numCols() - 2);

        f1.reshape(3, 3);
        f2.reshape(3, 3);

        return Pair.with(f1, f2);
    }

    private Pair<SimpleMatrix, Integer> countTrue(SimpleMatrix F) {
        DMatrixRMaj c = new SimpleMatrix(0, 9).getDDRM();
        int count = 0;

        for (int i = maxV; i < lImg.length; i++) {
            for (int j = maxH; j < lImg[0].length; j++) {
                int[] shifts = shiftMap[i][j];
//                MatrixVectorMult_DDRM.mult(F.getDDRM(), vectorFromPoints(new int[]{j, i}, new int[]{j + shifts[0], i + shifts[2]}).getDDRM(), c);

                final SimpleMatrix xl = vectorX(new int[]{j, i});
                final SimpleMatrix xr = vectorX(new int[]{j + shifts[0], i + shifts[2]});
                if (Math.abs(xl.mult(F).dot(xr.transpose())) < e)
                    count++;
            }
        }

        return new Pair<>(F, count);
    }

    private void mult(SimpleMatrix matrix, double k) {
        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                matrix.set(i, j, matrix.get(i, j) * k);
            }
        }
    }

    private void filterPoints(int radius) {
        filtered = new ArrayList<>();
        for (int i = radius; i < shiftMap.length - radius; i++) {
            loop:
            for (int j = radius; j < shiftMap[0].length - radius; j++) {
                for (int k = 1; k <= radius; k++) {
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
    }

    private void calculateEpipolarPoint(SimpleMatrix F){
        SimpleMatrix v = F.svd().getV();
        epipolar = v.extractVector(false, v.numCols() - 1);
        v = F.transpose().svd().getV();
        lepipolar = v.extractVector(false, v.numCols() - 1);
        /*double z = simpleMatrix.get(2, 0);
        if(z != 1 && z!=0){
            epipolar = new Point(simpleMatrix.get(0,0)/z,simpleMatrix.get(1,0)/z);
        } else {
            epipolar = new Point(simpleMatrix.get(0,0)*1_000_000,simpleMatrix.get(0,1)*1_000_000);
        }*/
    }
}
