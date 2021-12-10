package com.example.tipsplit;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //Screen Variables
    private EditText billText;
    private TextView tipAmount;
    private TextView totalWithTip;
    private EditText peopleCount;
    private TextView costPerPerson;
    private TextView overageValue;
    private RadioGroup percentGroup;

    //Data Variables
    double totalBill;

    /**
    * Get the references to the EditText and TextView UI object, then stores them
    * for future method usage.
    **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        billText = findViewById(R.id.billTotal);
        tipAmount = findViewById(R.id.tipAmount);
        totalWithTip = findViewById(R.id.totalWithTip);
        peopleCount = findViewById(R.id.numberOfPeople);
        costPerPerson = findViewById(R.id.totalPerPerson);
        overageValue = findViewById(R.id.overage);
        percentGroup = findViewById(R.id.radioGroup);
    }

    /**
     * Creates a Bundle containing the text information from tipAmount, totalWithTip,
     * costPerPerson, and overageValue when the application's orientation is going to change.
     **/
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString("TipAmount", tipAmount.getText().toString());
        outState.putString("TotalWithTip", totalWithTip.getText().toString());
        outState.putString("CostPerPerson", costPerPerson.getText().toString());
        outState.putString("OverageValue", overageValue.getText().toString());

        super.onSaveInstanceState(outState);
    }

    /**
     * This method defines how the Bundle information will be redeployed in the program.
     **/
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){

        super.onRestoreInstanceState(savedInstanceState);

        tipAmount.setText(savedInstanceState.getString("TipAmount"));
        totalWithTip.setText(savedInstanceState.getString("TotalWithTip"));

        if(savedInstanceState.getString("TotalWithTip").length() > 0){
            totalBill = Double.parseDouble(savedInstanceState.getString("TotalWithTip").substring(1));
        }
        costPerPerson.setText(savedInstanceState.getString("CostPerPerson"));
        overageValue.setText(savedInstanceState.getString("OverageValue"));
    }

    /**
     * This method solves for tip and total with tip on radio selection. And then sets the text fields to those values.
     **/
    public void calculateTip(View v){
        String billString= billText.getText().toString();

        if (billString.length() > 0) {
            double tipPercent;
            double bill = Double.parseDouble(billString);

            if (v.getId() == R.id.radioButton1) {
                tipPercent = .12;
            } else if (v.getId() == R.id.radioButton2) {
                tipPercent = .15;
            } else if (v.getId() == R.id.radioButton3) {
                tipPercent = .18;
            } else {
                tipPercent = .2;
            }

            double tip = (Math.round((bill * tipPercent)*100));
            tip /= 100;
            totalBill = bill + tip;

            tipAmount.setText(String.format("$%.2f", tip));
            totalWithTip.setText(String.format("$%.2f", (totalBill)));
        } else {
            percentGroup.clearCheck();
        }
    }



    /**
     * This method uses the totalWithTip and numberOfPeople to solve the costPerPerson and the overageValue. Then sets the text fields
     * to those values.
     **/
    public void costPerPerson(View x){
        String peopleString = peopleCount.getText().toString();

        if(peopleString.length() > 0){
            Integer numberOfPeople = Integer.valueOf(peopleString);
            if(totalBill > 0 && numberOfPeople > 0) {
                double split = Math.ceil((totalBill / numberOfPeople) * 100);
                split /= 100;

                costPerPerson.setText(String.format("$%.2f", split));

                double overageDifference = (numberOfPeople * split) - totalBill;
                overageValue.setText((String.format("$%.2f", overageDifference)));
            }
        }
    }


    /**
     * This method clears all EventText, TextViews and Radiogroup.
     **/
    public void clear(View t){
        totalBill = 0;
        billText.getText().clear();
        tipAmount.setText("");
        totalWithTip.setText("");
        peopleCount.getText().clear();
        costPerPerson.setText("");
        overageValue.setText("");
        percentGroup.clearCheck();
    }
}