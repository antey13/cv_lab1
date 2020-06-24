package lab3;

import com.fasterxml.jackson.databind.ObjectMapper;
import lab2.ImageUtils;
import lombok.SneakyThrows;
import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;
import org.javatuples.Pair;
import org.opencv.core.Core;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Lab3 {
    public static void main(String[] args) {
        SimpleMatrix epipolar = new SimpleMatrix(1, 3, true, new double[]{3.2200258892002892E16,11.085571517801375,1.0});
        SimpleMatrix F = new SimpleMatrix(3, 3, true, new double[]{
                0.0,-2.736526283353413E-15,3.327893516313907E-14,2.777292285038868E-15,1.4257258568184383E-17,-89.42953059701387,-3.078787225163637E-14,89.42953059701381,5.216937992713611E-13});

        Rectification retification = new Rectification(F, epipolar);
        retification.calculateMatrix();

        SimpleMatrix Rr = retification.getRr();
        SimpleMatrix M = retification.getM();
        SimpleMatrix T = retification.trans();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        int[][][] shiftMap = ImageUtils.readImg("src/main/resources/views/shift.png");
        String left = "src/main/resources/views/39LLine.jpg";
        final String right = "src/main/resources/views/39Line.jpg";
        int[][][] lImg = ImageUtils.readImg(left);

        SimpleMatrix A = new SimpleMatrix(3, 3);
        A.setRow(0, 0, 0, 0, 0);
        A.setRow(1, 0, 0, 1, 0);
        A.setRow(2, 0, 0, 0, 1);
        SimpleMatrix Rl = T.invert().mult(A.mult(M));
        SimpleMatrix xii = null;
        SimpleMatrix r = null;
        for (int i = 0; i < lImg.length; i++) {
            for (int j = 0; j < lImg[0].length; j++) {
                SimpleMatrix x = fromPoint(j, i);
                SimpleMatrix xr = fromPoint(j + shiftMap[i][j][0], i + shiftMap[i][j][2]);
                SimpleMatrix xi = M.mult(x.transpose());
                xi = xi.divide(xi.get(2, 0));

                SimpleMatrix z1 = Rr.mult(xr.transpose());
                double z = z1.divide(z1.get(2, 0)).get(0, 0);
                SimpleMatrix mult = mult(xi, z);
                if (r == null)
                    r = mult;
                else r = r.plus(mult);

                SimpleMatrix xixi = xi.mult(xi.transpose());
                if (xii == null)
                    xii = xixi;
                else xii = xii.plus(xixi);
            }
        }
        r = xii.invert().mult(r);
        double a = r.get(0, 0);
        double b = r.get(1, 0);
        double c = r.get(2, 0);
        A.setRow(0, 0, a, b, c);
        Rl = T.invert().mult(A.mult(M));
        Rr = T.invert().mult(Rr);
        System.out.println(Rr);
        System.out.println(Rl);
        int[][][] ints = ImageUtils.readImg(left);
        int[][][] img = new int[ints.length][ints[0].length][3];
        Rl = Rl.invert();

        for (int i = 0; i < img.length; i++) {
            for (int j = 0; j < img[0].length; j++) {
                SimpleMatrix xr = Rl.mult(new SimpleMatrix(3, 1, true, new double[]{j, i, 1}));
                xr = xr.divide(xr.get(2,0));
                if(xr.get(0,0) < ints[0].length && xr.get(1,0) < ints.length &&
                        xr.get(0,0) >= 0 && xr.get(1,0) >= 0){
                    img[i][j] = ints[(int) xr.get(1,0)][(int)xr.get(0,0)];
                }
            }
        }

        lab2.ImageUtils.writeImg(img, "src/main/resources/rect.png");
        ints = ImageUtils.readImg(right);
        img = new int[ints.length][ints[0].length][3];
        Rr = Rr.invert();
        for (int i = 0; i < img.length; i++) {
            for (int j = 0; j < img[0].length; j++) {
                SimpleMatrix xr = Rr.mult(new SimpleMatrix(3, 1, true, new double[]{j, i, 1}));
                xr = xr.divide(xr.get(2,0));
                if(xr.get(0,0) < ints[0].length && xr.get(1,0) < ints.length &&
                        xr.get(0,0) >= 0 && xr.get(1,0) >= 0){
                    img[i][j] = ints[(int) xr.get(1,0)][(int)xr.get(0,0)];
                }
            }
        }
        lab2.ImageUtils.writeImg(img, "src/main/resources/rectR.png");

    }

    private static SimpleMatrix fromPoint(int x, int y) {
        return new SimpleMatrix(1, 3, true, new double[]{x, y, 1});
    }

    private static SimpleMatrix mult(SimpleMatrix matrix, double k) {
        SimpleMatrix result = new SimpleMatrix(3, 3);
        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                result.set(i, j, matrix.get(i, j) * k);
            }
        }
        return result;
    }

    @SneakyThrows
    private static Pair<SimpleMatrix, SimpleMatrix> read() {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = Files.readString(Paths.get("src/main/resources/F.json"));
        Matrix f = objectMapper.readValue(json, org.ejml.data.DMatrixRMaj.class);
        SimpleMatrix F = new SimpleMatrix(f);
        return null;
    }
}
