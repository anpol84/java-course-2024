package edu.java.scrapper.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import edu.java.utils.StackTraceUtil;
import org.junit.jupiter.api.Test;
import java.util.List;


public class StakeTraceUtilTest {
    @Test
    void testGetStringStackTrace() {
        Exception exception = new Exception("Test exception");
        List<String> stackTrace = StackTraceUtil.getStringStakeTrace(exception);
        assertThat(stackTrace).isNotEmpty();
        assertEquals(stackTrace.get(0),"edu.java.scrapper.utils.StakeTraceUtilTest.testGetStringStackTrace" +
            "(StakeTraceUtilTest.java:13)" );
    }
}
