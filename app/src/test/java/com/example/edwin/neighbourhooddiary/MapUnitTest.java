package com.example.edwin.neighbourhooddiary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MapUnitTest {

    private MapsActivity mapsActivity;

    @Before
    public void setUp(){
        mapsActivity = new MapsActivity();
    }

    @After
    public void tearDown(){
        mapsActivity = null;
    }

    @Test
    public void addition_isCorrect()
    {
        assertEquals(4, 2 + 2);
    }
}