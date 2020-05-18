package lab2;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.mult.MatrixVectorMult_DDRM;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;
import org.javatuples.Pair;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

import static lab2.Utils.*;

public class FundMatrix {
    private int[][][] shiftMap;
    private int[][][] lImg;
    private int maxH;
    private int maxV;
    private final double e = 0.0001;
    public List<Point> selectedPoints = new ArrayList<>();

    public FundMatrix(int[][][] shiftMap, int[][][] lImg, int maxHShift, int maxVShift) {
        this.shiftMap = shiftMap;
        this.lImg = lImg;
        maxH = maxHShift;
        maxV = maxVShift;
    }

    public SimpleMatrix getFundMatrix(long iterations) {
        SimpleMatrix F = null;
        Pair<SimpleMatrix, Integer> bestPair = new Pair<>(F, -1);
        int mininliners = 1000;
        for (int i = 0; i < iterations; i++) {
            SimpleMatrix fx = getFX();
            Pair<SimpleMatrix, SimpleMatrix> f = nullSpace(fx);
            F = checkAndCorrect(f);

            if(F == null)
                continue;

            Pair<SimpleMatrix, Integer> pair = countTrue(F);
            if (pair.getValue1() < mininliners){
                mininliners = pair.getValue1();
            }
            if (bestPair.getValue1() < pair.getValue1()) {
                bestPair = pair;
                System.out.println(pair.getValue1());
            }
        }

        System.out.println(bestPair.getValue1());
        return F;
    }


    private SimpleMatrix checkAndCorrect(Pair<SimpleMatrix, SimpleMatrix> f) {
        if (f.getValue0().determinant() < e && new SimpleSVD<>(f.getValue0().getMatrix(), false).rank() == 2) {
            return f.getValue0();
        }
        if (f.getValue1().determinant() < e && new SimpleSVD<>(f.getValue1().getMatrix(), false).rank() == 2) {
            return f.getValue1();
        }
        return calculateF(f);
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
            cubic.solve(detA, detB * tr2, detA * tr1, detA);
            double x1 = cubic.x1;
            mult(B, x1);

            return A.plus(B);
        }catch (Exception e){
            return null;
        }
    }

    private SimpleMatrix getFX() {
        final SimpleMatrix simpleMatrix = new SimpleMatrix(7, 9);

        for (int i = 0; i < 7; i++) {

            int h = (int) Math.round(Math.random() * (lImg.length - maxV ));
            int l = (int) Math.round(Math.random() * (lImg[0].length - maxH ));

            int[] x1 = shiftMap[h][l];

            selectedPoints.add(new Point(l,h));

            simpleMatrix.setRow(i, 0, xFromPoints(new int[]{l, h}, new int[]{l + x1[0], h + x1[2]}));
        }
        return simpleMatrix;
    }

    private Pair<SimpleMatrix, SimpleMatrix> nullSpace(SimpleMatrix simpleMatrix) {
        final SimpleMatrix v = simpleMatrix.svd().getV();

        SimpleMatrix f1 = v.extractVector(false, v.numCols() - 1);
        SimpleMatrix f2 = v.extractVector(false, v.numCols() - 2);

        f1.reshape(3, 3);
        f2.reshape(3, 3);

        return Pair.with(f1, f2);
    }

    private Pair<SimpleMatrix, Integer> countTrue(SimpleMatrix F) {
        final DMatrixRMaj c = new SimpleMatrix(1, 9).getDDRM();
        int count = 0;

        F.reshape(1, 9);

        for (int i = maxV; i < lImg.length; i++) {
            for (int j = maxH; j < lImg[0].length; j++) {
                int[] shifts = shiftMap[i][j];
                MatrixVectorMult_DDRM.mult(F.getDDRM(), vectorFromPoints(new int[]{j, i}, new int[]{j + shifts[0], i + shifts[2]}).getDDRM(), c);

                if (normVector(c) < e)
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
}
