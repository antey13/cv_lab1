package lab3;

import org.ejml.simple.SimpleMatrix;

public class R extends SimpleMatrix {
    public R(SimpleMatrix epipolar) {
        super(3,3);
        double at = Math.atan2(epipolar.get(0, 1), epipolar.get(0, 0));
        set(0,0,Math.cos(at));
        set(0,1,Math.sin(at));
        set(0,2,0);
        set(1,0,(-1)*Math.sin(at));
        set(1,1,Math.cos(at));
        set(1,2,0);
        set(2,0,0);
        set(2,1,0);
        set(2,2,1);
    }

}
