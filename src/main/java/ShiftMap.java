import metrics.Metrics;

import java.util.Arrays;
import java.util.stream.IntStream;

public class ShiftMap {
    private int alpha;
    private int horizontalD;
    private int verticalD;
    private Metrics metrics = Metrics.Euclidean;

    public ShiftMap(int alpha, int cacheSize, int verticalD) {
        this.alpha = alpha;
        this.horizontalD = cacheSize;
        this.verticalD = verticalD;
    }


    byte[][][] buildShiftMap(byte[][][] lImg, byte[][][] rImg) {
        byte[][][] map = new byte[lImg.length][lImg[0].length][2];

        IntStream.range(0, lImg.length).parallel().forEach(rowIndex -> {
            map[rowIndex] = getRowShift(lImg[rowIndex], rImg, rowIndex);
        });

        return map;
    }

    public byte[][] getRowShift(byte[][] lRow, byte[][][] rImg, int rowIndex) {
        final int width = lRow.length;
        double[][][] cache = fillCache(lRow, rImg, rowIndex);

        double[][][][] indexCache = new double[width][horizontalD][verticalD][3];
        indexCache[0][0][0][2] = -1;

        IntStream.range(1, width).forEachOrdered(i -> {
            for (int j = 0; j < horizontalD; j++) {
                for (int k = 0; k < verticalD; k++) {
                    double min = alpha * g(new int[]{0,0}, new int[]{k,j}) + cache[0][0][i - 1];
                    indexCache[i][j][k][0] = j;
                    indexCache[i][j][k][1] = k;
                    indexCache[i][j][k][2] = min;

                    for (int l = 0; l < horizontalD; l++) {
                        for (int m = 0; m < verticalD; m++) {
                            double e = alpha * g(new int[]{m,l}, new int[]{k,j}) + cache[l][m][i - 1];

                            if (e < min) {
                                min = e;
                                indexCache[i][j][k][0] = l;
                                indexCache[i][j][k][1] = m;
                                indexCache[i][j][k][2] = e;
                            }
                        }
                    }

                    cache[j][k][i] += indexCache[i][j][k][2];
                }
            }
        });

        byte[][] rowDepth = new byte[width][2];

        int idx = rowDepth.length - 1;
        double min = indexCache[idx][0][0][2];
        rowDepth[idx][0] = (byte) indexCache[idx][0][0][0];
        rowDepth[idx][1] = (byte) indexCache[idx][0][0][1];

        for (int j = 0; j < horizontalD; j++) {
            for (int k = 0; k < verticalD; k++) {
                if (indexCache[idx][j][k][2] < min) {
                    rowDepth[idx][0] = (byte) indexCache[idx][j][k][0];
                    rowDepth[idx][1] = (byte) indexCache[idx][j][k][1];
                    min = indexCache[idx][j][k][2];
                }
            }
        }

        for (int i = rowDepth.length - 2; i >= 0; i--) {
            rowDepth[i][0] = (byte) indexCache[i][rowDepth[i + 1][0]][rowDepth[i + 1][1]][0];
            rowDepth[i][1] = (byte) indexCache[i][rowDepth[i + 1][0]][rowDepth[i + 1][1]][1];
        }


        return rowDepth;
    }

    private double[][][] fillCache(byte[][] lRow, byte[][][] rImg, int rowIndex) {
        double[][][] cache = new double[horizontalD][verticalD][lRow.length];

        for (int i = 0; i < lRow.length; i++) {
            for (int j = 0; j < horizontalD; j++) {
                for (int k = 0; k < verticalD; k++) {
                    int idx = i - j;
                    int idy = rowIndex - k;
                    if (idx >= 0 && idy >= 0) {
                        cache[j][k][i] = metrics.diff(lRow[i], rImg[idy][idx]);
                    } else cache[j][k][i] = Integer.MAX_VALUE;
                }
            }
        }
        return cache;
    }

    private int g(int[] d, int[] d1) {
        return IntStream.range(0, d.length).map(i -> Math.abs(d[i] - d1[i])).sum();
    }

}
