package lab2;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.mult.MatrixVectorMult_DDRM;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;
import org.javatuples.Pair;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static lab2.Utils.*;

public class FundMatrix {
    private int[][][] shiftMap;
    private int[][][] lImg;
    private int maxH;
    private int maxV;
    private final double e = 0.000000001;
    public List<Point> selectedPoints = new ArrayList<>();
    public List<double[]> selectedXs = new ArrayList<>();

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
            if(!check(F)){
                System.err.println("FFF");
            }

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
                System.out.println( (i * 100)/iterations + "%");
        }

        System.out.println(bestPair.getValue1());
        System.err.println(mininliners);
        return F;
    }


    private SimpleMatrix checkAndCorrect(Pair<SimpleMatrix, SimpleMatrix> f) {
        if(!check(f.getValue0())){
            System.err.println("F1");
        }
        if(!check(f.getValue1())){
            System.err.println("F2");
        }
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

             if(detA*Math.pow(x1,3.0) + x1*x1*detB*tr2 + x1*detA*tr1 + detA > e){
                 System.err.println("EQUATION SOLVING");
             }

            mult(B, x1);

            return A.plus(B);
        } catch (Exception e) {
            return null;
        }/*  1.9894E-05 -2.6043E-04  4.8795E-02
 2.4516E-04 -1.8371E-05  9.5672E-03
-5.2647E-02 -6.2926E-03  9.9735E-01

    -3.7955E-04 -2.7535E-02  3.4674E+00
 2.8223E-02 -2.0877E-04 -5.1652E+00
-3.1467E+00  4.6931E+00 -2.5660E-01  */
    }

    private SimpleMatrix getFX() {
        final SimpleMatrix simpleMatrix = new SimpleMatrix(7, 9);

        for (int i = 0; i < 7; i++) {

            int h = (int) Math.round(Math.random() * (lImg.length - maxV));
            int l = (int) Math.round(Math.random() * (lImg[0].length - maxH));

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

        for(int i=0;i<7;i++){
            if(svd.getSingleValue(i) == 0.0)
                System.err.println("ERRRR"+simpleMatrix.svd().getSingularValues().length);
        }

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

    private boolean check(SimpleMatrix fx){
        fx.reshape(9,1);

        DMatrixRMaj c = new SimpleMatrix(0, 9).getDDRM();
        for (int i = 0; i < 7; i++) {
            MatrixVectorMult_DDRM.mult(fx.transpose().getDDRM(), xToVector(selectedXs.get(i)).getDDRM(), c);

            if(normVector(c) > e){
                fx.reshape(3,3);
                return false;
            }
        }
        fx.reshape(3,3);
        return true;
    }
}
