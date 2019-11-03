package com.example.studentlunchjkl.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Laura Nurmi
 * Holds the data of meals for a single day
 */
public class RMenu {
    private String restaurant;
    private int restaurantId;
    private String date;
    private String dayOfWeek;
    private ArrayList<Meal> meals = new ArrayList<Meal>();

    public RMenu(){date = " 0 ";}

    public String getDayOfWeek(){return dayOfWeek;}

    public String getRestaurantName(){return restaurant;}

    public void setRestaurant(String name){restaurant= name;}

    public void addMeal(Meal meal){
        if(meal != null) meals.add(meal);
    }

    public int getMealCount(){return meals.size();}

    /**
     * Sets restaurant info from JSONdata
     * @param s JSONdata as string
     */
    public void setFromJSON(String s){
        //TODO: change to take JSONobject as parameter
        try {
            JSONObject reader = new JSONObject(s);
            JSONObject menuJson = reader.getJSONObject("LunchMenu");
            dayOfWeek = menuJson.getString("DayOfWeek");
            JSONArray mealsJson = menuJson.getJSONArray("SetMenus");
            for(int i = 0; i < mealsJson.length(); i++){
                Meal meal = new Meal();
                meal.setfromJson(mealsJson.getJSONObject(i));
                addMeal(meal);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a single meal from the list
     * @param i list place
     * @return a meal
     */
    public Meal getMeal(int i){
        if(i < meals.size()) return meals.get(i);
        return null;
    }
}
