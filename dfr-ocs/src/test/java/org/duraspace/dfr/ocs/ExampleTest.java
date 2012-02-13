package org.duraspace.dfr.ocs;

import org.junit.Assert;
import org.junit.Test;

public class ExampleTest {

    @Test
    public void getValueReturnsOriginalValue() {
        Assert.assertEquals("test", new Example("test").getValue());
    }

    @Test (expected=UnsupportedOperationException.class)
    public void setValueUnsupported() {
        new Example("test").setValue("new value");
    }
}
