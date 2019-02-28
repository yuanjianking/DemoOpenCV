package org.opencv.samples.facedetect;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FaceMatcher {

    private static final String TAG = "FaceMatcher";
    private static int counter;
    public final int UNFINISHED = -2;
    public final int NO_MATCHER = -1;
    private final int MAX_COUNTER = 45;
    private final double MY_SIMILARITY = 0.8;
    private List<String> mPathList;

    public FaceMatcher() {
        counter = 0;
        mPathList = new ArrayList<>();
        mPathList.add(Environment.getExternalStorageDirectory()+File.separator+"abc"+"1.jpg");
    }

    public int histogramMatch(Bitmap bitmap) {
        if (counter < MAX_COUNTER) {
            Mat testMat = new Mat();
            Utils.bitmapToMat(bitmap, testMat);
            Imgproc.cvtColor(testMat, testMat, Imgproc.COLOR_RGB2GRAY);
            testMat.convertTo(testMat, CvType.CV_32F);
            for (int i = 0; i < mPathList.size(); i++) {
                try {
                    String path = mPathList.get(i);
                    Mat mat = Imgcodecs.imread(path);
                    Imgproc.resize(mat, mat, new Size(320, 320));
                    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
                    mat.convertTo(mat, CvType.CV_32F);
                    // 直方图比较
                    double similarity = Imgproc.compareHist(mat, testMat,
                            Imgproc.CV_COMP_CORREL);
                    System.out.println("------> " + similarity);
                    if (similarity >= MY_SIMILARITY) {
                        Log.e(TAG, "histogramMatch: " + similarity + ", " + i);
                        return i;
                    }
                    if (similarity < MY_SIMILARITY && i == mPathList.size() - 1) {
                        Log.e(TAG, "histogramMatch: " + counter);
                        counter++;
                    }
                } catch (Exception e) {
                    System.out.println("---->" + e.getMessage());
                }
            }
            return UNFINISHED;
        } else {
            Log.e(TAG, "histogramMatch: 匹配结束");
            return NO_MATCHER;
        }
    }



    public void saveImageToSD(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "abc");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "1.jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
