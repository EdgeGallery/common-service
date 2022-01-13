package org.edgegallery.commonservice.cbb.test.util.security;

import org.edgegallery.commonservice.cbb.util.security.AccessUserUtil;
import org.junit.Assert;
import org.junit.Test;

public class AccessUserUtilTest {
    @Test
    public void test() {
        AccessUserUtil.setUser("userId", "userName");
        Assert.assertEquals("userId", AccessUserUtil.getUserId());
        AccessUserUtil.setUser("userId", "userName", "userAuth");
        Assert.assertNotNull(AccessUserUtil.getUser());
        AccessUserUtil.setUser("userId", "userName", "userAuth", "token");
        Assert.assertEquals("token", AccessUserUtil.getToken());
        AccessUserUtil.unload();
        Assert.assertNull(AccessUserUtil.getUser());
        Assert.assertNull(AccessUserUtil.getUserId());
        Assert.assertNull(AccessUserUtil.getToken());
    }
}
