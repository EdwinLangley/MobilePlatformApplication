package com.example.edwin.neighbourhooddiary;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FirebaseUnitTest {

    FirebaseDatabase mMarkerReference;

//    @Before
//    public void setUp(){
//        mMarkerReference = FirebaseDatabase.getInstance();
//    }
//
//    @After
//    public void tearDown(){
//        mMarkerReference = null;
//    }

    @Test
    public void addition_isCorrect()
    {
        assertEquals(4, 2 + 2);
    }


}