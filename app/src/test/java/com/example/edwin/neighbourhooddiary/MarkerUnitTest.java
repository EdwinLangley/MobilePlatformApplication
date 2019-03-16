package com.example.edwin.neighbourhooddiary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MarkerUnitTest {

    CustomMarker marker;

    @Before
    public void setUp(){
        marker = new CustomMarker();
        //marker.setla
    }

    @After
    public void tearDown()
    {
        marker = null;
    }

    @Test
    public void addOwner()
    {
        String name = "Jane";
        marker.setAddedBy(name);
        assertEquals(marker.getAddedBy(),name);
    }

    @Test
    public void addDescription()
    {
        String descrip = "This event is the best ever";
        marker.setDescrip(descrip);
        assertEquals(marker.getDescrip(),descrip);
    }

    @Test
    public void addEndTime()
    {
        long timeInMilli = 1552746207593l;
        marker.setEndTime(timeInMilli);
        assertEquals(marker.getEndTime(),timeInMilli);
    }

    @Test
    public void addEventName()
    {
        String name = "BestEvent";
        marker.setEventName(name);
        assertEquals(marker.getEventName(),name);
    }

    @Test
    public void addEventType()
    {
        String type = "PostBox";
        marker.setEventType(type);
        assertEquals(marker.getEventType(),type);
    }

    @Test
    public void addStartTime()
    {
        long timeInMilli = 1552746207593l;
        marker.setStartTime(timeInMilli);
        assertEquals(marker.getStartTime(),timeInMilli);
    }

    @Test
    public void addExpirable()
    {
        boolean expirable = true;
        marker.setExpirable(expirable);
        assertEquals(marker.isExpirable(),expirable);
    }

    @Test
    public void addGroups()
    {
        ArrayList<String> groups = new ArrayList<>();
        groups.add("Group1");
        groups.add("Group2");
        groups.add("Group3");
        marker.setGroupsWelcome(groups);
        assertEquals(marker.getGroupsWelcome(),groups);
    }


    @Test
    public void addLat()
    {
        double lat = 2.45434;
        marker.setLat(lat);
        assertEquals(marker.getLat(),lat,0.0001);
    }


    @Test
    public void addLong()
    {
        double lng = 2.45434;
        marker.setLng(lng);
        assertEquals(marker.getLng(),lng,0.0001);
    }


}