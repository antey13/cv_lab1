package lab1;


import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageUtils {
    @SuppressWarnings("all")
    public static byte[][][] readImg(String path) {

        Imgcodecs imageCodecs = new Imgcodecs();
        Mat matrix = imageCodecs.imread(path);

        byte[][][] image = new byte[matrix.rows()][matrix.cols()][3];
        for (int i = 0; i < matrix.rows(); i++) {
            for (int j = 0; j < matrix.cols(); j++) {
                image[i][j][0] = (byte) matrix.get(i,j)[0];
                image[i][j][1] = (byte) matrix.get(i,j)[1];
                image[i][j][2] = (byte) matrix.get(i,j)[2];
            }
        }
        System.out.println("Reading completed.");
        return image;
    }

    public static void writeImg(byte[][][] image, String path) {
        Mat matrix = new Mat(image.length,image[0].length,16);

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                byte[] pixel = new byte[3];
                pixel[0] = image[i][j][0];
//                pixel[1] = image[i][j];
                pixel[2] = image[i][j][1];
                matrix.put(i, j,pixel);
            }
        }

        if( Imgcodecs.imwrite(path, matrix)){
            System.out.println("Writing complete.");
        } else {
            System.out.println("Error!");
        }
    }
}
