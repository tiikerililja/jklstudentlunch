package com.example.studentlunchjkl.data;


import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.studentlunchjkl.MainActivity;
import com.example.studentlunchjkl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Laura Nurmi
 * Class that holds information and all recipies for a single meal
 * TODO: is at this point used to deliver restaurant name to listView
 */
public class Meal {
    private String name = "Lunch";
    private String price = "";
    private ArrayList<Recipe> recipes = new ArrayList<Recipe>();
    private boolean isAllowed = true;
    private boolean isRestaurantName = false;

    public Meal(){}

    /**
     * Constructor with meal name
     * @param name
     */
    public Meal(String name){this.name=name;}

    public int getRecipeCount(){return recipes.size();}
    public String getName(){return name;}
    public boolean isRestaurantName(){return isRestaurantName;}

    public void addRecipe(Recipe recipe){
        if(recipe != null)recipes.add(recipe);
    }

    /**
     * Returns a single recipe from the list
     * @param i list place
     * @return a recipe
     */
    public Recipe getRecipe(int i){
        if(i<recipes.size()) return recipes.get(i);
        return null;
    }

    /**
     * Returns the meal name and ingredient info
     * @return ArraysList of the recipies and ingredients
     */
    public ArrayList<String> getIngredientInfo(){
        ArrayList mealInfo = new ArrayList();
        mealInfo.add(name);
        if(recipes.size()==0){
            mealInfo.add(R.string.not_available);
            return mealInfo;
        }
        for(Recipe r : recipes){
            mealInfo.add(r.getRecipeName());
            mealInfo.add(r.getIngredients());
            mealInfo.add("\n");
        }
        return mealInfo;
    }

    /**
     * Returns price and names of recipies in a single String
     * @return a string with info
     */
    public String getMealContent() {
        if(recipes.size() < 1) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(price +"\n");
        sb.append(recipes.get(0).getRecipeName());
        for(int i = 1; i < recipes.size();i++){
            sb.append("\n");
            sb.append(recipes.get(i).getRecipeName());
        }
        return sb.toString();
    }

    /**
     * Sets meal data from JSON
     * @param o JSONobject with data
     * @throws JSONException
     */
    public void setfromJson(JSONObject o) throws JSONException {
        //TODO: check null values from JSON
        name = o.getString("Name");
        price = o.getString("Price");
        JSONArray meals = o.getJSONArray("Meals");
        if(meals.length()==0) addRecipe((new Recipe("Ei saatavilla",-1)));
        for(int i = 0; i < meals.length(); i++){
            Recipe recipe = new Recipe();
            recipe.setFromJson(meals.getJSONObject(i));
            addRecipe(recipe);
        }
    }

    /**
     * Checks if any of the meal recipes include allergy ingredients listed in the preferences. Updates the
     * isAllowed value if needed
     * @param allergyList list of allergy ingredients
     * @return true if meal has no allergy ingredients
     */
    public boolean checkAllergies(String[] allergyList) {

        for(int i = 0; i < recipes.size(); i++){
            if(recipes.get(i).hasAllergies(allergyList)) isAllowed = false;
        }
        return isAllowed;
    }


    public boolean isAllowed() {
        return isAllowed;
    }

    public void setAsRestaurantName() {
        isRestaurantName = true;
    }
}
