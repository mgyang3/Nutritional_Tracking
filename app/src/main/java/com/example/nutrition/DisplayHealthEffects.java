package com.example.nutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayHealthEffects extends AppCompatActivity {
    HashMap<String, ArrayList<String>> symps = new HashMap<>();
    ArrayList<Float> percents = new ArrayList<Float>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_health_effects);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String[] NutrientTracker = (String[]) intent.getSerializableExtra("Nutrient List");
        percents = (ArrayList<Float>) intent.getSerializableExtra("Percents");

        readFile();
        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.ShowFoodSymptoms);
        String show = "";

        for(String t: NutrientTracker){
            show = show.concat(t + "\n");
            if(!symps.containsKey(t))
                continue;
            ArrayList<String> values = symps.get(t);
            System.out.println(values.size() + " = size of string");

            for(int i=0;i<values.size();i++){
                if(i==0){
                    show = show.concat("Symptoms: "+values.get(i));
                }
                else if(i < 3){
                    show = show.concat(", " + values.get(i));
                }
                else if(i == 3){
                    show = show.concat("\n");
                    show =  show.concat("Foods: "+values.get(i));
                }
                else if(i >3){
                    show = show.concat(", " + values.get(i));
                }
            }//going through all the values
            show = show.concat("\n\n");
          //  System.out.println("This is the end?");

        }//ends for

        textView.setText(show);
        textView.setMovementMethod(new ScrollingMovementMethod());

    }

    public void readFile(){
        //reading into database
        InputStream inputStream = getResources().openRawResource(R.raw.symptoms);
        CSVFile csvFile = new CSVFile(inputStream);
        List scoreList = csvFile.read();

        for(int j=0; j<scoreList.size();j++) {
            //each individual nutrient + symptom
            String[] test = (String[]) scoreList.get(j);
            ArrayList<String> tba = new ArrayList<>();
            if(percents.get(j) >= 100)
                continue;
            for (int i = 0; i < test.length; i++) {
                if (test[i].contains("\"")) {
                    test[i] = test[i].replaceAll("\"", "");
                 //   System.out.println("Removed a quotation mark");
                }//ends if
                test[i] = test[i].trim();

                if(i > 0){
                    tba.add(test[i]);
                //    System.out.println("This got added: " + test[i]);
                }//not the first entry

            }//ends for

            symps.put(test[0], tba);

        }//ends for


    }
}