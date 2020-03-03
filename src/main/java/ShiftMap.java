import metrics.Metrics;

import java.util.stream.IntStream;

public class ShiftMap {
    private int alpha;
    private int cacheSize;
    private Metrics metrics = Metrics.Euclidean;

    public ShiftMap(int alpha, int cacheSize) {
        this.alpha = alpha;
        this.cacheSize = cacheSize;
    }


    byte[][] buildShiftMap(byte[][][] lImg, byte[][][] rImg) {
        byte[][] map = new byte[lImg.length][lImg[0].length];

        IntStream.range(0, lImg.length).parallel().forEach(rowIndex -> {
            map[rowIndex] = getRowShift(lImg[rowIndex], rImg[rowIndex]);
        });

        return map;
    }

    public byte[] getRowShift(byte[][] lRow, byte[][] rRow) {
        final int width = lRow.length;
        double[][] cache = fillCache(lRow, rRow);

        double[][][] indexCache = new double[width][cacheSize][2];
        indexCache[0][0][1] = -1;

        IntStream.range(1, width).forEachOrdered(i -> {
            for (int j = 0; j < cacheSize; j++) {

                double min = alpha * g(0, j) + cache[0][i - 1];
                indexCache[i][j][0] = j;
                indexCache[i][j][1] = min;

                for (int k = 0; k < cacheSize; k++) {
                    double e = alpha * g(k, j) + cache[k][i - 1];

                    if (e < min) {
                        min = e;
                        indexCache[i][j][0] = k;
                        indexCache[i][j][1] = e;
                    }
                }

                cache[j][i] += indexCache[i][j][1];
            }
        });

        byte[] rowDepth = new byte[width];

        int idx = rowDepth.length - 1;
        double min = indexCache[idx][0][1];
        rowDepth[idx] = (byte) indexCache[idx][0][0];

        for (int j = 0; j < cacheSize; j++) {
            if (indexCache[idx][j][1] < min) {
                rowDepth[idx] = (byte) indexCache[idx][j][0];
                min = indexCache[idx][j][1];
            }
        }

        for (int i = rowDepth.length - 2; i > 0; i--) {
            rowDepth[i] = (byte) indexCache[i][rowDepth[i + 1]][0];
        }


        return rowDepth;
    }

    private double[][] fillCache(byte[][] lRow, byte[][] rRow) {
        double[][] cache = new double[cacheSize][lRow.length];

        for (int i = 0; i < cache[0].length; i++) {
            for (int j = 0; j < cache.length; j++) {
                int idx = i - j;
                if (idx >= 0) {
                    cache[j][i] = metrics.diff(lRow[i], rRow[idx]);
                } else cache[j][i] = Integer.MAX_VALUE;
            }
        }
        return cache;
    }

    private int g(int d, int d1) {
        return Math.abs(d - d1);
    }

}
