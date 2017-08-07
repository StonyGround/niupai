import android.os.Environment;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.xiuxiu.util.AVProcessing;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static com.jhjj9158.niupaivideo.test.R.raw;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AVProcessingTest {

    File input = new File(Environment.getExternalStorageDirectory(), "inputvideo.mp4");
    File output = new File(Environment.getExternalStorageDirectory(), "outputvideo.mp4");
    File watermark = new File(Environment.getExternalStorageDirectory(), "watermark.png");


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
    public void getVideoFrameRate() throws IOException {
        System.out.println("getVideoFrameRate");

        InputStream inputStream = getInstrumentation().getContext().getResources().openRawResource(raw.video);
        store(inputStream, input);

        int videoFrameRate = AVProcessing.getVideoFrameRate(input.getPath());
        System.out.println("videoFrameRate: " + videoFrameRate);
    }


    volatile int length = 4096;
    volatile byte[] src = new byte[length];
    volatile byte[] dest = new byte[length];

    @Test
    public void saveFrameData() {
        System.out.println("saveDataFrame test");

        for (int i = 0; i < length; i++) {
            src[i] = (byte) i;
        }

        int handle = AVProcessing.saveDataFrame(src, length);

        System.out.println("handle: " + handle);

        byte[] bytes = AVProcessing.copyByteArray(handle, length);

        for(int i=0; i<length; i++) {
            System.out.println(src[i] + " - " + bytes[i]);
        }
    }
}
