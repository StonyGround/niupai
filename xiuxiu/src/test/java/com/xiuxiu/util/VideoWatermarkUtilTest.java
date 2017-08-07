package com.xiuxiu.util;


import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VideoWatermarkUtilTest {

    @Mock
    Context mMockContext;

    @Test
    public void getImages() {
        System.out.println("getImages test");
        new VideoWatermarkUtil().getImagesFromVideo();

    }
}
