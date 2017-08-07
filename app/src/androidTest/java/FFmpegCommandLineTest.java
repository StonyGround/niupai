import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class FFmpegCommandLineTest {


    @Test
    public void commandline() throws IOException, InterruptedException {
        String command = "ffmpeg -i video.mp4 -i watermark.png -filter_complex \"[0:v][1:v] overlay=25:25:enable='between(t,0,20)'\" -pix_fmt yuv420p -c:a copy output.mp4";
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);

        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }

    }
}
