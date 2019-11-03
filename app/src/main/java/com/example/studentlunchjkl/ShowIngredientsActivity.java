package com.example.studentlunchjkl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Laura Nurmi
 * Small activity that shows the ingredients of a chosen meal
 */
public class ShowIngredientsActivity extends AppCompatActivity {
    private ArrayList<String> mealInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ingredients);

        Intent intent = getIntent();
        mealInfo = intent.getStringArrayListExtra(MainActivity.EXTRA_RESTAURANT_INFO);
        setIngredientInfo();

    }

    /**
     * Sets the information to the textView
     */
    private void setIngredientInfo() {
        if(mealInfo == null) return;
        TextView textView = findViewById(R.id.textViewName);
        textView.setText(mealInfo.get(0));
        StringBuilder ingredients = new StringBuilder();
        for(int i = 1; i < mealInfo.size(); i++){
            ingredients.append(mealInfo.get(i));
            ingredients.append("\n");
        }
        EditText ingredientText = findViewById(R.id.editTextSIAIngredients);
        ingredientText.setText(ingredients.toString());
    }
}
