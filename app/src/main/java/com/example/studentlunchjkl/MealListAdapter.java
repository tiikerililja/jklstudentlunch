package com.example.studentlunchjkl;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.example.studentlunchjkl.data.Meal;

import java.util.ArrayList;

/**
 * @author Laura Nurmi
 *
 * Adapter-class to set data in ListView
 */
public class MealListAdapter extends RecyclerView.Adapter<MealListAdapter.MyViewHolder> {
    private ArrayList<Meal> mDataset;
    private Resources res;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        //public View myLayout;
        public TextView textViewName;
        public TextView textViewMeals;
        public TextView textViewAllowed;
        private Meal meal;

        public MyViewHolder(View v) {
            super(v);

            textViewName = v.findViewById(R.id.textViewName);
            textViewMeals = v.findViewById(R.id.textViewMeals);
            textViewAllowed = v.findViewById(R.id.textViewAllowed);
        }

        public void bind(Meal m, final Context context){
            meal = m;
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(context, ShowIngredientsActivity.class);
                    intent.putExtra(MainActivity.EXTRA_RESTAURANT_INFO, meal.getIngredientInfo());
                    context.startActivity(intent);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MealListAdapter(ArrayList<Meal> myDataset, Resources re, Context c) {
        mDataset = myDataset;
        res = re;
        context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_list_element, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;

    }


    /**
     * Sets the data of each meal into the listView. Now also used to set restaurant names
     * @param holder list element
     * @param position which item
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Meal meal = mDataset.get(position);
        holder.bind(meal,context);
        holder.textViewName.setText(meal.getName());
        //t = (TextView) holder.myLayout.getViewById(R.id.textViewMeals);
        holder.textViewMeals.setText(meal.getMealContent());
        if(!meal.isAllowed()) {
            holder.textViewAllowed.setText(R.string.forbidden);
            holder.textViewName.setTextColor(res.getColor(R.color.colorForbidden,null));
        }
        else holder.textViewAllowed.setText(R.string.allowed);
        if(meal.isRestaurantName()){
            holder.textViewName.setTextColor(res.getColor(R.color.colorAccent));
            holder.textViewName.setTextSize(20);
        }

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
