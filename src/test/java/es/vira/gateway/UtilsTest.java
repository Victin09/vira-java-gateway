package es.vira.gateway;

import es.vira.gateway.util.MathUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * util test
 *
 * @author Víctor Gómez
 */
public class UtilsTest {
    @Test
    public void maxCommonDivisorTest() {
        Assert.assertEquals(5, MathUtils.maxCommonDivisor(15, 20));
    }
}
