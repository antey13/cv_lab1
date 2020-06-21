package lab3;

import org.ejml.data.DMatrixRMaj;
import org.ejml.simple.SimpleMatrix;

public class Rectification {
    private SimpleMatrix F;
    private SimpleMatrix e;
    private SimpleMatrix M;
    private SimpleMatrix Rr;

    public Rectification(SimpleMatrix F, SimpleMatrix e) {
        this.F = F;
        this.e = e;
    }

    public void calculateMatrix() {
        R r = new R(e);
        SimpleMatrix re = r.mult(e.transpose());
        G g = new G(-1 * e.get(0, 2) / re.get(0, 0));
        SimpleMatrix T = trans();
        Rr = g.mult(r).mult(T);
        SimpleMatrix ex = eToMatrix(e);
        SimpleMatrix M = Rr.mult(ex).mult(F);
        this.M = convertM(M);
    }

    public SimpleMatrix trans(){
        return new SimpleMatrix(3,3,true,new double[]{1,0,-160,0,1,-90,0,0,1});
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

    private SimpleMatrix convertM(SimpleMatrix M) {
        SimpleMatrix my = M.extractVector(true, 1);
        SimpleMatrix mz = M.extractVector(true, 2);

        SimpleMatrix simpleMatrix = new SimpleMatrix(3, 3);
        simpleMatrix.setRow(1, 0, ((DMatrixRMaj) my.getMatrix()).data);
        simpleMatrix.setRow(2, 0, ((DMatrixRMaj) mz.getMatrix()).data);

        SimpleMatrix mult = cross(my,mz); //eToMatrix(mz).transpose().mult(my.transpose());
        mult.divide(mult.normF());
        simpleMatrix.setRow(0, 0, ((DMatrixRMaj) mult.getMatrix()).data);

        return simpleMatrix;
    }

    public SimpleMatrix getM() {
        return M;
    }

    public SimpleMatrix getRr() {
        return Rr;
    }

    public SimpleMatrix cross(SimpleMatrix x1, SimpleMatrix x2){
        SimpleMatrix x = new SimpleMatrix(1, 3);
        x.set(0,0,x1.get(0,1)*x2.get(0,2) - x1.get(0,2)*x2.get(0, 1));
        x.set(0,1,x1.get(0,2)*x2.get(0,1) - x1.get(0,0)*x2.get(0, 2));
        x.set(0,2,x1.get(0,0)*x2.get(0,1) - x1.get(0,1)*x2.get(0, 2));
        return x;
    }
}
