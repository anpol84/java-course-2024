package utils;

import edu.java.bot.utils.StackTraceUtil;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class StakeTraceUtilTest {
    @Test
    void testGetStringStackTrace() {
        Exception exception = new Exception("Test exception");
        List<String> stackTrace = StackTraceUtil.getStringStakeTrace(exception);
        assertThat(stackTrace).isNotEmpty();
        assertEquals(stackTrace.get(0),"utils.StakeTraceUtilTest.testGetStringStackTrace" +
            "(StakeTraceUtilTest.java:13)" );

    }
}
