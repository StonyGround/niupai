import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UnitTestSample {

    private static final String FAKE_STRING = "HELLO WORLD";

    @Mock
    Context mMockContext;

    @Test
    public void sayHello() {
        System.out.println("This is UnitTestSample");


        assertThat("This is not HELLO WORLD", is(FAKE_STRING));
//        assertThat("HELLO WORLD", is(FAKE_STRING));
    }
}