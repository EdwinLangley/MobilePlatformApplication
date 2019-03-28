package com.example.edwin.neighbourhooddiary;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String displayName;
    public String email;
    public long time;

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String friends;




    public Map<String, String> gpsLocations;

    public Map<String, String> getGpsLocations() {
        return gpsLocations;
    }

    public void setGpsLocations(Map<String, String> gpsLocations) {
        this.gpsLocations = gpsLocations;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public User() {

    }

    public User(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
        Date date = new Date();
        time = date.getTime();

    }

    public String removeNameFromList(String listNames, String name){
        if(((listNames != null) || (name != null)) && (!name.equals("§"))){
            if(!listNames.contains("§")){
                return listNames;
            }
            String[] splitList = listNames.split("§");
            List<String> wordListTemp = Arrays.asList(splitList);
            ArrayList<String> wordList = new ArrayList(wordListTemp);

            int elementToRemove = 0;

            for(int i = 0; i < wordList.size(); i++){
                if(wordList.get(i).equals(name)){
                    elementToRemove = i;
                }
            }

            wordList.remove(elementToRemove);
            String returnString = "";

            for(String s : wordList){
                returnString+= s + "§";
            }
            return returnString;
        }
        return null;
    }

}

