package com.example.nutrition;

import java.util.*;

public class NutrientTable {
    HashMap<String, Integer> NutrientList = new HashMap<String, Integer>();
    HashMap<String, ArrayList<Float>> databaseTracker = new HashMap<String, ArrayList<Float>>();

    public NutrientTable(List db) {

        for(int i=0;i<db.size();i++){
            String[] s = (String[]) db.get(i);
                if (i == 0) {
                    for(int j=0;j<s.length;j++) {
                        NutrientList.put(s[j], j);

                    }//ends for
                }//ends if
                else{
                    //not recording nutrient values
                    ArrayList<Float> list = new ArrayList<Float>();
                    for(int j=1;j<s.length;j++){
                        list.add(Float.parseFloat(s[j]));
                    }//adding values into array list
                    databaseTracker.put(s[0],list);
                }//ends else

        }//ends for

    } //end constructor

    //method to get labels in database
    public HashMap<String, Integer> getNutrientList(){return NutrientList;}

    //methods to return one search of database
    public ArrayList<Float> getNutrients(String s){
        ArrayList<Float> found = new ArrayList<Float>();

        if(databaseTracker.containsKey(s))
            found = databaseTracker.get(s);
        return found;
    }

}//ends class