package com.example.edwin.neighbourhooddiary;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


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

    @Test
    public void acceptableRemoveName()
    {
        String testResult = user.removeNameFromList("a§b§c§d§e§","c");
        System.out.println(testResult);
        assertEquals(testResult, "a§b§d§e§");
    }

    @Test
    public void unacceptableRemoveName()
    {
        String testResult = user.removeNameFromList("a§b§c§d§e§","§");
        System.out.println(testResult);
        assertNull(testResult);
    }

    @Test
    public void edgeRemoveName()
    {
        String testResult = user.removeNameFromList("a","a");
        System.out.println(testResult);
        assertEquals(testResult, "a");
    }


}