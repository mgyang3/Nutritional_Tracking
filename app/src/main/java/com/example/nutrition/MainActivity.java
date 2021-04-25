package com.example.nutrition;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;
import java.text.*;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.nutrition.MESSAGE";

    // variable for bar chart
    BarChart barChart;

    // variable for our bar data set.
    BarDataSet barDataSet1;

    // array list for storing entries.
    ArrayList barEntries;

    //Nutrient Requirements List - only carbs, fiber, and protein are in grams
    String[] reqs = new String[]{"Carbohydrate", "Fiber", "Protein", "Vitamin A","Vitamin B-6" ,"Vitamin B-12", "Vitamin C", "Vitamin E", "Copper", "Iodine", "Iron", "Magnesium", "Phosphorus",
            "Potassium", "Selenium","Zinc", "Thiamin", "Riboflavin", "Niacin", "Folate", "Vitamin K", "Calcium", "Sodium", "Theobromine"};
    double[] dailyReqs = new double[]{130.0, 38.0, 56.0, 0.9, 1.3, 0.0024, 90.0, 15.0, 0.9, 0.15, 8.0, 400.0, 700.0, 4700.0, 0.055, 11.0, 4.89, 1.3, 16.0, 0.4, 0.3, 2500.0, 1500.0, 1500.0};

    HashMap<String, Float> NutrientTracker = new HashMap<String, Float>();
    HashMap<String, Float> WeeklyReqs= new HashMap<String, Float>();

    EditText UserEntry;

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    String setDate = "04/16/2021";

    Date startDate = new Date();
    Date endDate;

    NutrientTable database;
    ArrayList<Float> percents = new ArrayList<Float>();
    ArrayList<String> FutureAdditions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //reading into database
        InputStream inputStream = getResources().openRawResource(R.raw.nutrientvalues);
        CSVFile csvFile = new CSVFile(inputStream);
        List scoreList = csvFile.read();
        database = new NutrientTable(scoreList);

        //making hashmap to store tracker values
        for(int i=0; i<reqs.length;i++){
            NutrientTracker.put(reqs[i],0f);
            WeeklyReqs.put(reqs[i], (float) (dailyReqs[i]*7));
        }

        // initializing graph
        createGraph();
    }
    public void showSymptoms(View view){
            Intent intent = new Intent(this, DisplayHealthEffects.class);
            String message = "Made a new screen";
            intent.putExtra(EXTRA_MESSAGE, message);
            intent.putExtra("Nutrient List", reqs);
            intent.putExtra("Percents", percents);
            startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void LogFood(View view) {

        boolean resetProgress = checkWeek();
        //reset graph tracker
        if (resetProgress == true)
            createGraph();

        //housekeeping of clearing entry for next
        UserEntry = (EditText)findViewById(R.id.FoodItem);
        String s = UserEntry.getText().toString();
        UserEntry.setText("");

        if(s.equals("")) { //user input empty string, so don't do anything
            System.out.println("empty string given");
            return;
        }
        //parse the information of the given string
        else{
            HashMap<String,Integer> nl = database.getNutrientList();
            ArrayList<Float> foodData = database.getNutrients(s);

            //not logging that food or couldn't find it
            if(foodData.size() <= 1) {
                System.out.println("Couldn't find food");
                FutureAdditions.add(s);
                return;
            }

            for(String nutrient: reqs){
               // System.out.println("nutrient i'm looking for:" + nutrient);
                if(nl.containsKey(nutrient)){

             //       System.out.println("nutrient is:" + nutrient);
                    int f = nl.get(nutrient) - 1;
             //       System.out.println("the index to check: " + f);
                    float t = foodData.get(f);
             //       System.out.println("nutrient from the food: "+t);
                    float oldValue = NutrientTracker.get(nutrient);

                    t = oldValue + t;

                    NutrientTracker.replace(nutrient, t);
             //       System.out.println("stored req value is:" + NutrientTracker.get(nutrient));
                }//checking if nutrient is in
            }//ends for
        }

        updateGraph(s);
    }//button click end function

    public boolean checkWeek(){
        boolean overWeek = false;
        try {
            startDate = df.parse(setDate);
            endDate = new Date();
          //  endDate = df.parse("04/29/2021"); //testing to see if it works

            long difference = Math.abs(startDate.getTime() - endDate.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);

            if(differenceDates >= 7) {
                setDate = df.format(endDate);
                overWeek = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return overWeek;
    }

    public void updateGraph(String s){
        BarDataSet newBarData = new BarDataSet(updateEntries(3f), "Nutritional Requirements");
        newBarData.setColor(getApplicationContext().getResources().getColor(R.color.purple_200));

        BarData replacement = new BarData(newBarData);
        replacement.setBarWidth(0.3f);

        barChart.setData(replacement);
        barChart.getXAxis().setAxisMinimum(0);

        barChart.animateXY(1000,2000);

        barChart.invalidate();
    }//what to do after button click

    private void createGraph() {
        barChart = findViewById(R.id.idBarChart);

        // creating a new bar data set.
        barDataSet1 = new BarDataSet(getBarEntries(), "Nutritional Requirements");
        barDataSet1.setColor(getApplicationContext().getResources().getColor(R.color.purple_200));

        //x axis labels
        XAxis xAxis = barChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(reqs.length);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(reqs));
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        // below line is to add bar data set to our bar data.
        BarData data = new BarData(barDataSet1);
        data.setBarWidth(0.30f); //thickness

        barChart.setData(data);
        barChart.setDragEnabled(true);

        //remove description
        barChart.getDescription().setEnabled(false);

        //set 0 to 100%
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setAxisMaximum(100f);
        barChart.getXAxis().setAxisMinimum(0);

        barChart.animate();

        barChart.invalidate();

    }

    // array list for set
    private ArrayList<BarEntry> getBarEntries() {

        //calculating the %
        Collection<Float> NutrientValues = NutrientTracker.values();
        Collection<Float> WeeklyValues = WeeklyReqs.values();

        ArrayList<Float> vals = new ArrayList<>(NutrientValues);
        ArrayList<Float> WeeklyVals = new ArrayList<>(WeeklyValues);


        barEntries = new ArrayList<>();

        for(int i=0;i<vals.size();i++){
            float x = ((vals.get(i)) / WeeklyVals.get(i) * 100);
            percents.add(x);
            //adding 1 to all values just to see the bars
            barEntries.add(new BarEntry( (float)i, (float)(x) ));
        }//ends for

        return barEntries;
    }
    private ArrayList<BarEntry> updateEntries(float z) {

        //calculating the %
        ArrayList<Float> percents = new ArrayList<>();
        barEntries = new ArrayList<>();

        for(String r:reqs){
            System.out.println("String I'm adding: "+ r);
            float track = NutrientTracker.get(r);
            float base = WeeklyReqs.get(r);
            float y = (track/base) * 100;
            System.out.println("Tracked value: " + track + ". Weekly value: "+ base + ". Percentage: " + y );
            percents.add(y);
        }
        for(int i=0; i< percents.size();i++ ) {

            barEntries.add(new BarEntry(i,percents.get(i)));
        }
        return barEntries;
    }


}//ends main class