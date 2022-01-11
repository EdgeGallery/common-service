package org.edgegallery.commonservice.cbb.test.model;

import org.edgegallery.commonservice.cbb.model.User;
import org.junit.Assert;
import org.junit.Test;

public class UserTest {
    @Test
    public void testNewUser() throws Exception {
        User u1 = new User("userId", "userName");
        Assert.assertEquals("userId", u1.getUserId());
        User u2 = new User("userId", "userName", "userAuth");
        Assert.assertEquals("userName", u1.getUserName());
        User u3 = new User("userId", "userName", "userAuth", "token");
        Assert.assertEquals("token", u1.getToken());
    }
}
