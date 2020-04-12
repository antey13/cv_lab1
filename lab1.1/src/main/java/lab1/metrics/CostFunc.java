package lab1.metrics;

public enum CostFunc implements Difference {
    MinL1((d1,d2)->{
        double res = 0;
        for (int i = 0; i < d1.length; i++) {
            double d = d1[i] - d2[i];
            res += Math.abs(d);
        }
        return res;
    }),
    MinL2((d1,d2)->{
        double res = 0;
        for (int i = 0; i < d1.length; i++) {
            double d = d1[i] - d2[i];
            res += d*d;
        }
        return Math.sqrt(res);
    });

    public Difference difference;
    private Difference oldDif;

    CostFunc(Difference diff) {
        this.difference = diff;
    }

    public void setBetta(double betta) {
        oldDif = difference;
        this.difference = (d1,d2) -> Math.min(betta,oldDif.diff(d1,d2));
    }


    @Override
    public double diff(int[] p1, int[] p2) {
        return difference.diff(p1, p2);
    }
}
