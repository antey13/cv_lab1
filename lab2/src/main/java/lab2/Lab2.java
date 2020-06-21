package lab2;

import lombok.SneakyThrows;
import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Core;

import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencv.core.Mat;

import java.util.stream.Collectors;

public class Lab2 {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        int[][][] shiftMap = ImageUtils.readImg("src/main/resources/views/shift21.png");
        int[][][] lImg = ImageUtils.readImg("src/main/resources/views/hangerL-small1.png");
        int[][][] rImg = ImageUtils.readImg("src/main/resources/views/hangerR-small1.png");

        FundMatrix fundMatrix = new FundMatrix(shiftMap, lImg, 25, 25);
        long t1 = System.currentTimeMillis();
        SimpleMatrix F = fundMatrix.getFundMatrix(4_000);
        System.out.println(System.currentTimeMillis()-t1);

        final SimpleMatrix epipolar = fundMatrix.epipolar;
        System.out.println(epipolar);
        if(epipolar.get(2,0) == 0){
            epipolar.set(0,0,epipolar.get(0,0)*1_000_000);
            epipolar.set(1,0,epipolar.get(1,0)*1_000_000);
            epipolar.set(2,0,1);
        }else {
            epipolar.set(0,0,epipolar.get(0,0)/epipolar.get(2,0));
            epipolar.set(1,0,epipolar.get(1,0)/epipolar.get(2,0));
            epipolar.set(2,0,1);
        }
        final SimpleMatrix left = fundMatrix.lepipolar;
        if(left.get(2,0) == 0){
            left.set(0,0,left.get(0,0)*1_000_000);
            left.set(1,0,left.get(1,0)*1_000_000);
            left.set(2,0,1);
        }else {
            left.set(0,0,left.get(0,0)/left.get(2,0));
            left.set(1,0,left.get(1,0)/left.get(2,0));
            left.set(2,0,1);
        }
        writeF(F,fundMatrix.epipolar);
        ImageUtils.drawEpipolars(rImg,shiftMap,fundMatrix.selectedPoints, epipolar,"src/main/resources/views/39Line.jpg");
        ImageUtils.drawEpipolars(lImg,shiftMap,fundMatrix.selectedPoints, left,"src/main/resources/views/39LLine.jpg");
        /*ImageUtils.writeDebug("src/main/resources/views/hangerL-small1.png","src/main/resources/views/LeftDebug.png",fundMatrix.selectedPoints);
        ImageUtils.writeDebug("src/main/resources/views/hangerR-small1.png","src/main/resources/views/RightDebug.png",fundMatrix.selectedPoints.stream().map(p ->{
            int[] sh = shiftMap[(int) Math.round(p.y)][(int) Math.round(p.x)];
            p.set(new double[]{p.x + sh[0], p.y + sh[2]});
            return p;
        }).collect(Collectors.toList()));*/
    }

    @SneakyThrows
    private static void writeF(SimpleMatrix F, SimpleMatrix e){
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(F.getMatrix());
        Files.write(Paths.get("src/main/resources/F.json"), json.getBytes());
        json = objectMapper.writeValueAsString(e.getMatrix());
        Files.write(Paths.get("src/main/resources/e.json"), json.getBytes());
    }

}
