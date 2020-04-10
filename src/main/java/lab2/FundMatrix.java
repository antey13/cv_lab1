package lab2;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.mult.MatrixVectorMult_DDRM;
import org.ejml.simple.SimpleMatrix;
import org.javatuples.Pair;

import static lab2.Utils.*;

public class FundMatrix {
    private byte[][][] shiftMap;
    private byte[][][] lImg;
    private int maxH;
    private int maxV;
    private final double e = 0.01;
    private final int edge;

    public FundMatrix(byte[][][] shiftMap, byte[][][] lImg, int maxHShift, int maxVShift) {
        this.shiftMap = shiftMap;
        this.lImg = lImg;
        edge = lImg.length/10;
        maxH = maxHShift;
        maxV = maxVShift;
    }

    public SimpleMatrix getFundMatrix(long iterations) {
        SimpleMatrix F = null;
        Pair<SimpleMatrix, Integer> bestPair = new Pair<>(F,-1);

        for (int i = 0; i < iterations; i++) {
            SimpleMatrix fx = getFX();
            Pair<SimpleMatrix, SimpleMatrix> f = nullSpace(fx);
            F = checkAndCorrect(f);

            Pair<SimpleMatrix, Integer> pair = countTrue(F);
            if(bestPair.getValue1()<pair.getValue1()){
                bestPair = pair;
                System.out.println(pair.getValue1());
            }
        }

        return F;
    }

    private SimpleMatrix checkAndCorrect(Pair<SimpleMatrix, SimpleMatrix> f) {
        if(f.getValue0().determinant() < e){
            return f.getValue0();
        }
        System.out.println("NOT FIRST!!!");
        return f.getValue1();
    }

    private SimpleMatrix getFX(){
        final SimpleMatrix simpleMatrix = new SimpleMatrix(7, 9);

        for (int i = 0; i < 7; i++) {

            int h = (int) Math.round(Math.random() * (lImg.length - maxV - edge) + edge);
            int l = (int) Math.round(Math.random() * (lImg[0].length - maxH - edge) + edge);

            byte[] x1 = shiftMap[h][l];

            simpleMatrix.setRow(i,0,xFromPoints(new int[]{h, l}, new int[]{h + x1[0], l + x1[1]}));
        }
        return simpleMatrix;
    }

    private Pair<SimpleMatrix,SimpleMatrix> nullSpace(SimpleMatrix simpleMatrix){
        final SimpleMatrix v = simpleMatrix.svd().getV();

        SimpleMatrix f1 = v.extractVector(false, v.numCols()-1);
        SimpleMatrix f2 = v.extractVector(false, v.numCols()-2);

        f1.reshape(3,3);
        f2.reshape(3,3);
        return Pair.with(f1,f2);
    }

    private Pair<SimpleMatrix,Integer> countTrue(SimpleMatrix F){
        final DMatrixRMaj c = new SimpleMatrix(1, 9).getDDRM();
        int count = 0;

        F.reshape(1,9);

        for (int i = maxV; i < lImg.length; i++) {
            for (int j = maxH; j < lImg[0].length; j++) {
                byte[] shifts = shiftMap[i][j];
                MatrixVectorMult_DDRM.mult(F.getDDRM(),vectorFromPoints(new int[]{i, j}, new int[]{i + shifts[0], j + shifts[1]}).getDDRM(), c);

                if(normVector(c) < e)
                    count++;
            }
        }

        return new Pair<>(F,count);
    }
}
