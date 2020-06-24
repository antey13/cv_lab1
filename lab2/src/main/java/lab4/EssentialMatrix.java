package lab4;

import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

public class EssentialMatrix extends SimpleMatrix {

    public EssentialMatrix(SimpleMatrix K, SimpleMatrix F) {
        super(K.transpose().mult(F).mult(K));
    }

    public boolean isTrueEssential() {
        SimpleSVD<SimpleMatrix> svd = this.svd();
        boolean rank = svd.rank() == 2;
        double[] singularValues = svd.getSingularValues();
        boolean values = singularValues.length == 3 && singularValues[2] == 0 && Math.abs(singularValues[1] - singularValues[0]) < 1.5 && singularValues[0] > 0;
        boolean eq = mult(this.mult(this.transpose()).mult(this),2).minus(mult(this,this.mult(this.transpose()).trace())).determinant() < 1e-6;
        return rank && values && eq;
    }

    private SimpleMatrix mult(SimpleMatrix matrix, double k) {
        SimpleMatrix res = new SimpleMatrix(matrix.numRows(), matrix.numCols());
        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                res.set(i, j, matrix.get(i, j) * k);
            }
        }
        return res;
    }

    private SimpleMatrix getC(){
        SimpleSVD<SimpleMatrix> svd = this.svd();
        var U = svd.getU();
        var W = U.mult(new SimpleMatrix(3,3,true, new double[]{0,1,0,-1,0,0,0,0,1}));
        double sigma = svd.getSingularValues()[0];
        SimpleMatrix v3 = svd.getV().extractVector(false, 2);
        v3 = mult(v3,sigma);
        SimpleMatrix c = mult(v3, W.determinant());
        return eToMatrix(c);
    }

    private SimpleMatrix eToMatrix(SimpleMatrix e) {
        SimpleMatrix simpleMatrix = new SimpleMatrix(3, 3);
        simpleMatrix.set(0, 0, 0);
        simpleMatrix.set(0, 1, -1 * e.get(0, 2));
        simpleMatrix.set(0, 2, e.get(0, 1));
        simpleMatrix.set(1, 0, e.get(0, 2));
        simpleMatrix.set(1, 1, 0);
        simpleMatrix.set(1, 2, -1 * e.get(0, 0));
        simpleMatrix.set(2, 0, -1 * e.get(0, 1));
        simpleMatrix.set(2, 1, e.get(0, 0));
        simpleMatrix.set(2, 2, 0);
        return simpleMatrix;
    }
}


