package metrics;

public enum Metrics implements Difference{
    Euclidean((p1, p2) -> {
        double res = 0;
        for (int i = 0; i < p1.length; i++) {
            double d = p1[i] - p2[i];
            res += d * d;
        }
        return Math.sqrt(res);
    });

    public Difference difference;

    Metrics(Difference diff) {
        this.difference = diff;
    }


    @Override
    public double diff(byte[] p1, byte[] p2) {
        return difference.diff(p1, p2);
    }
}
