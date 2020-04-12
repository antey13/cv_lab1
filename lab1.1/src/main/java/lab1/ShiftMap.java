package lab1;

import lab1.metrics.CostFunc;
import lab1.metrics.Metrics;
import lombok.Builder;

import java.util.stream.IntStream;

@Builder
public class ShiftMap {
    private int alpha;
    private int horizontalD;
    private int verticalD;
    private boolean parallel;
    private double[][][][] cache;
    private Metrics metrics = Metrics.Euclidean;
    private CostFunc g;

    int[][][] buildShiftMap(int[][][] lImg, int[][][] rImg) {
        fillG();

        int[][][] map = new int[lImg.length][lImg[0].length][2];

        IntStream rangeStream = IntStream.range(0, lImg.length);
        if(parallel){
            rangeStream = rangeStream.parallel();
        }
        rangeStream.forEach(rowIndex -> map[rowIndex] = getRowShift(lImg[rowIndex], rImg, rowIndex));

        return map;
    }

    public int[][] getRowShift(int[][] lRow, int[][][] rImg, int rowIndex) {
        final int width = lRow.length;
        double[][][] cache = fillCache(lRow, rImg, rowIndex);

        double[][][][] indexCache = new double[width][horizontalD][verticalD][3];
        indexCache[0][0][0][2] = -1;

        for (int i = 1; i < width; i++) {
            processPixel(indexCache, cache, i);
        }

        int[][] rowDepth = new int[width][2];

        int idx = rowDepth.length - 1;
        double min = indexCache[idx][0][0][2];
        rowDepth[idx][0] = (int) indexCache[idx][0][0][0];
        rowDepth[idx][1] = (int) indexCache[idx][0][0][1];

        for (int j = 0; j < horizontalD; j++) {
            for (int k = 0; k < verticalD; k++) {
                final double[] values = indexCache[idx][j][k];
                if (values[2] < min) {
                    rowDepth[idx][0] = (int) values[0];
                    rowDepth[idx][1] = (int) values[1];
                    min = values[2];
                }
            }
        }

        for (int i = rowDepth.length - 2; i >= 0; i--) {
            final int[] depth = rowDepth[i + 1];
            final double[] indexes = indexCache[i][depth[0]][depth[1]];
            rowDepth[i][0] = (byte) indexes[0];
            rowDepth[i][1] = (byte) indexes[1];
        }

        return rowDepth;
    }

    private double[][][] fillCache(int[][] lRow, int[][][] rImg, int rowIndex) {
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

    private void processPixel(double[][][][] indexCache, double[][][] cache, int i) {
        for (int j = 0; j < horizontalD; j++) {
            for (int k = 0; k < verticalD; k++) {
                double min = this.cache[0][0][k][j] + cache[0][0][i - 1];
                indexCache[i][j][k][0] = j;
                indexCache[i][j][k][1] = k;
                indexCache[i][j][k][2] = min;

                for (int l = 0; l < horizontalD; l++) {
                    for (int m = 0; m < verticalD; m++) {
                        double e = this.cache[m][l][k][j] + cache[l][m][i - 1];

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
    }


    private void fillG() {
        cache = new double[verticalD][horizontalD][verticalD][horizontalD];
        for (int i = 0; i < horizontalD; i++) {
            for (int j = 0; j < verticalD; j++) {
                for (int k = 0; k < horizontalD; k++) {
                    for (int l = 0; l < verticalD; l++) {
                        cache[j][i][l][k] = alpha * g.diff(new int[]{j, i}, new int[]{l, k});
                    }
                }
            }
        }
    }

}
