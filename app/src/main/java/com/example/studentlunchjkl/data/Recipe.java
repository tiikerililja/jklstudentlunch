package com.example.studentlunchjkl.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Laura Nurmi
 * Dataclass for a single recipe
 */
public class Recipe {
    private int recipeId;
    private String name;
    private String ingredients;
    private String allergies;
    private String nutritions;

    /**
     *
     * @param name Recipe name
     * @param id Recipe id
     */
    public Recipe(String name, int id){
        this.name = name;
        recipeId = id;
    }

    public Recipe(){}

    public int getRecipeId(){return recipeId;}

    public String getRecipeName(){return name;}

    public String getIngredients(){return ingredients;}

    /**
     * Set name and id from JSON-data
     * @param jsonObject dataset
     * @throws JSONException
     */
    public void setFromJson(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("Name");
        recipeId = jsonObject.getInt("RecipeId");
    }

    /**
     * Sets ingredients from JSON
     * @param resultString JSONstring
     */
    public void setIngredientsFromJson(String resultString) {
        //TODO: change the parameter to JSONobject
        try {
            JSONObject stringJson = new JSONObject(resultString);
            String ingredientsJson = stringJson.getString("Ingredients");
            String[] splitString = ingredientsJson.split("\\n");
            ingredients = splitString[0];
            nutritions = splitString[1];
            allergies = splitString[2];
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if recipe includes any of the listed allergies
     * @param allergies allergies to check
     * @return true when includes at least one allergy ingredient
     */
    public boolean hasAllergies(String[] allergies) {
        String i = "";
        if(ingredients != null) i = ingredients.toLowerCase();
        String n = name.toLowerCase();
        for(String s : allergies){
            s = s.trim().toLowerCase();
            if(s.equals("")) continue;
            if(n.contains(s)) return true;
            if(i.contains(s)){
                return true;
            }
        }
        return false;
    }

}
