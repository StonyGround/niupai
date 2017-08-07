package com.jhjj9158.niupaivideo.utils.ffmpeg;

import android.os.Environment;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

/**
 * Created by sanqian on 28/06/2017.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AddWatermarkUtilTest {

    static File input;
    static File output;
    static File watermark;


    @Before
    public void before() {
        File cacheDir = Environment.getExternalStorageDirectory();
        input = new File(cacheDir, "inputvideo.mp4");
        output = new File(cacheDir, "outputvideo.mp4");
        watermark = new File(cacheDir, "watermark.png");

        if(output.exists()) {
            output.delete();
        }
    }


    public void store(InputStream initialStream, File targetFile) throws IOException {
        byte[] buffer = new byte[initialStream.available()];
        try {
            initialStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.close();
    }

    @Test
    public void addWatermark() throws Exception {
        InputStream inputVideoStream = getInstrumentation().getContext().getResources().openRawResource(com.jhjj9158.niupaivideo.test.R.raw.video);
        store(inputVideoStream, input);

        InputStream watermarkInputStream = getInstrumentation().getContext().getResources().openRawResource(com.jhjj9158.niupaivideo.test.R.raw.watermark);
        store(watermarkInputStream, watermark);

        AddWatermarkUtil.addWatermark(getInstrumentation().getTargetContext(), input.getPath(), output.getPath(), watermark.getPath());

        Thread.sleep(40000);
    }
}
