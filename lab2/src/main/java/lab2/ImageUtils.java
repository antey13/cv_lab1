package lab2;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class ImageUtils {
    @SuppressWarnings("all")
    public static int[][][] readImg(String path) {

        Imgcodecs rImgCodecs = new Imgcodecs();
        Mat matrix = rImgCodecs.imread(path);

        int[][][] rImg = new int[matrix.rows()][matrix.cols()][3];
        for (int i = 0; i < matrix.rows(); i++) {
            for (int j = 0; j < matrix.cols(); j++) {
                rImg[i][j][0] = (int) matrix.get(i, j)[0];
                rImg[i][j][1] = (int) matrix.get(i, j)[1];
                rImg[i][j][2] = (int) matrix.get(i, j)[2];
            }
        }
        System.out.println("Reading completed.");
        return rImg;
    }

    public static void writeImg(int[][][] rImg, String path) {
        Mat matrix = new Mat(rImg.length, rImg[0].length, CvType.CV_32SC3);

        for (int i = 0; i < rImg.length; i++) {
            for (int j = 0; j < rImg[0].length; j++) {
                int[] pixel = new int[3];
                pixel[0] = rImg[i][j][0];
//                pixel[1] = rImg[i][j];
                pixel[2] = rImg[i][j][1];
                matrix.put(i, j, pixel);
            }
        }

        if (Imgcodecs.imwrite(path, matrix)) {
            System.out.println("Writing complete.");
        } else {
            System.out.println("Error!");
        }
    }


    public static void drawEpipolars(int[][][] rImg, int[][][] shiftMap,List<Point> points, SimpleMatrix f, String outputPath) {
        Mat mat = new Mat(rImg.length, rImg[0].length, CvType.CV_32SC3);

        for (int i = 0; i < rImg.length; i++) {
            for (int j = 0; j < rImg[0].length; j++) {
                int[] pixel = new int[3];
                pixel[0] = rImg[i][j][0];
                pixel[2] = rImg[i][j][1];
                mat.put(i, j, pixel);
            }
        }

        addEpipolars(mat,shiftMap,f);
       // addPoints(mat, points);

        if (Imgcodecs.imwrite(outputPath, mat)) {
            System.out.println("Writing complete.");
        } else {
            System.out.println("Error!");
        }
    }

    private static void addEpipolars(Mat mat, int[][][] shiftMap, SimpleMatrix F) {
        for (int i = 10; i < 180; i+=12) {
            Point[] points = toPoints(i, i, i + shiftMap[i][i][0], i + shiftMap[i][i][2], F);
            Imgproc.line(mat, points[0], points[1], new Scalar(244, 252, 3), 2);
            Imgproc.line(mat, points[2], points[2], new Scalar(255, 0, 0), 2);
        }
    }

    private static void addPoints(Mat mat,List<Point> points){
        points.forEach(point -> {
            Imgproc.line(mat, point, point, new Scalar(255, 155, 155), 2);
        });
    }

    private static Point[] toPoints(int x, int y, int xs, int ys, SimpleMatrix F) {
        SimpleMatrix simpleMatrix = new SimpleMatrix(3, 1);
        simpleMatrix.set(0, 0, x);
        simpleMatrix.set(1, 0, y);
        simpleMatrix.set(2, 0, 1);

        SimpleMatrix line = F.mult(simpleMatrix);

        double x1 = -1000;
        double y1 = (-line.get(2) - line.get(0) * x1) / line.get(1);

        double x2 = 3000;
        double y2 = (-line.get(2) - line.get(0) * x2) / line.get(1);

        Point p1 = new Point(x1, y1);
        Point p2 = new Point(x2, y2);

        Point p3 = new Point(xs, ys);

        return new Point[]{p1, p2, p3};
    }
}
