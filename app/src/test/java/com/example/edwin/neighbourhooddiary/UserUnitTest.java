package com.example.edwin.neighbourhooddiary;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserUnitTest {

    User user;

    @Before
    public void setUp(){
        user = new User();
    }

    @After
    public void tearDown(){
        user = null;
    }

    @Test
    public void addUserName()
    {
        String name = "Jane";
        user.setDisplayName(name);
        assertEquals(user.getDisplayName(),name);
    }

    @Test
    public void addEmailAddress()
    {
        String email = "Jane@gmail.com";
        user.setEmail(email);
        assertEquals(user.getEmail(),email);
    }

    @Test
    public void addLoginTime()
    {
        long timeInMilli = 1552746207593l;
        user.setTime(timeInMilli);
        assertEquals(user.getTime(),timeInMilli);
    }


}