package org.holer.common.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.util.Properties;

@RunWith(PowerMockRunner.class)
public class HolerConfigTest {

    @Test
    public void testStrValue1() {
        Assert.assertEquals("a\'b\'c", new HolerConfig(null)
                .getConfig(null).strValue("a\'b\'c", "a\'b\'c"));
    }

    @PrepareForTest(StringUtils.class)
    @Test
    public void testStrValue2() throws Exception {
        HolerConfig holerConfig = new HolerConfig();
        Properties properties = new Properties();
        FieldUtils.writeField(holerConfig, "holerConf", properties, true);

        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.when(StringUtils.isBlank("foo")).thenReturn(true);

        Assert.assertNull(holerConfig.strValue("foo", "bar"));
    }

    @PrepareForTest(StringUtils.class)
    @Test
    public void testIntValue1() throws Exception {
        HolerConfig holerConfig = new HolerConfig();
        Properties properties = new Properties();
        properties.setProperty("\u0000\u0000", "9");
        FieldUtils.writeField(holerConfig, "holerConf", properties, true);

        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.when(StringUtils.isNumeric("\u0000\u0000")).thenReturn(false);

        Assert.assertEquals(5, holerConfig.intValue("\u0000\u0000", 5));
    }

    @Test
    public void testIntValue2() throws Exception {
        HolerConfig holerConfig = new HolerConfig();
        Properties properties = new Properties();
        properties.setProperty("\u0000\u0000", "9");
        FieldUtils.writeField(holerConfig, "holerConf", properties, true);

        Assert.assertEquals(9, holerConfig.intValue("\u0000\u0000", 9));
        Assert.assertEquals(3, HolerConfig.getConfig(null).intValue("5", 3));
    }

    @Test
    public void testBoolValue1() throws Exception {
        HolerConfig holerConfig = new HolerConfig();
        Properties properties = new Properties();
        properties.setProperty("\u09f2\uf7db\u0830", "false");
        FieldUtils.writeField(holerConfig, "holerConf", properties, true);

        Assert.assertFalse(holerConfig.boolValue("\u09f2\uf7db\u0830", null));

        Assert.assertTrue(HolerConfig.getConfig().boolValue("false", true));
    }

    @Test
    public void testBoolValue2() throws Exception {
        HolerConfig holerConfig = new HolerConfig();
        Properties properties = new Properties();
        properties.setProperty("a/b/c", "BAZ");
        FieldUtils.writeField(holerConfig, "holerConf", properties, true);

        Assert.assertNull(holerConfig.boolValue("a/b/c"));
    }
}
