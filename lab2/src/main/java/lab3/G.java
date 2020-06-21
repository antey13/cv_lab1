package lab3;

import org.ejml.simple.SimpleMatrix;

public class G extends SimpleMatrix {
    public G(double exx) {
        super(3,3,true,new double[]{
                1.0,0.0,0.0,0.0,1.0,0.0,exx,0,1
        });
    }
}
