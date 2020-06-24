package lab4;

import org.ejml.simple.SimpleMatrix;

public class IntrinsicMatrix extends SimpleMatrix {

    public IntrinsicMatrix(double focal, double pixel, double[] principal){
        super(3,3);
        this.setRow(0,0, focal*pixel,0, principal[0]);
        this.setRow(1,0,0,focal*pixel,focal*principal[1]);
        this.setRow(2,0,0,0,1);
    }
}


