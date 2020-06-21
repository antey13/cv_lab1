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
        SimpleMatrix epipolar = new SimpleMatrix(1, 3, true, new double[]{1.1163981897637021E13,1.1163981897370068E13,1.0});
        SimpleMatrix F = new SimpleMatrix(3, 3, true, new double[]{
                4.1067037194909145E-16,-9.880648816490423E-14,1.0987708847119049,9.895556596362098E-14,-5.595228599018787E-16,-1.0987708847383877,-1.0987708847114932,1.0987708847376687,1.098770884568383});

        Rectification retification = new Rectification(F, epipolar);
        retification.calculateMatrix();

        SimpleMatrix Rr = retification.getRr();
        SimpleMatrix M = retification.getM();
        SimpleMatrix T = retification.trans();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        int[][][] shiftMap = ImageUtils.readImg("src/main/resources/views/shift21.png");
        String left = "src/main/resources/views/39LLine.jpg";
        final String right = "src/main/resources/views/39Line.jpg";
        int[][][] lImg = ImageUtils.readImg(left);
        int[][][] rImg = ImageUtils.readImg(right);

        SimpleMatrix A = new SimpleMatrix(3, 3);
        A.setRow(0, 0, 0, 0, 0);
        A.setRow(1, 0, 0, 1, 0);
        A.setRow(2, 0, 0, 0, 1);
        SimpleMatrix Rl = T.invert().mult(A.mult(M));
        Test.weight(Rl,Rr);
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
        Test.weight(Rl, Rr);
        Rl = T.invert().mult(A.mult(M));
        Rr = T.invert().mult(Rr);
//        System.out.println(Rr.invert().transpose().mult(F).mult(Rl.invert()));
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
//        lab3.ImageUtils.writeRect(Rr, right, "src/main/resources/rectRR.png");
//         lab3.ImageUtils.writeRect(Rl, left, "src/main/resources/rect1.png");

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
//        Files.write(Paths.get("src/main/resources/e.json"));
        return null;
    }
}
/*int count = 0;
        int fc = 0;
        for (int i = 25; i < lImg.length ; i++) {
            for (int j = 25; j < lImg[0].length; j++) {
                SimpleMatrix x = fromPoint(j, i);
                SimpleMatrix xr = fromPoint(j + shiftMap[i][j][0], i + shiftMap[i][j][2]);
                if (Math.abs(x.mult(F).dot(xr.transpose())) < 1.0e-5) {
                    fc++;
                    if (Math.abs((Rr.mult(xr.transpose())).transpose().mult(F).dot(Rl.mult(x.transpose()))) < 1.0e-5) {
                        count++;
                    }
                }
            }
        }
        System.out.println(count + " " + fc);*/
