package qwertyp4nts.pokedex;
//this class willl represent all of the data in our recyclerView

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> implements Filterable {
    public static class PokedexViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout containerView;
        public TextView textView;

        PokedexViewHolder(View view) { //viewholder holds on to the data on the screen so that you can manipulate it
            super(view); //just incase those super classes are doing something imprtant
            containerView = view.findViewById(R.id.pokedex_row); //unique id that android generated for me that represents that container
            textView = view.findViewById((R.id.pokedex_row_text_view));

            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Pokemon current = (Pokemon) containerView.getTag();
                    Intent intent = new Intent(view.getContext(), PokemonActivity.class);
                    intent.putExtra("url", current.getUrl());

                    view.getContext().startActivity(intent);
                }
            });
        }
        }
    @Override
    public Filter getFilter() {
        return new PokemonFilter();
    }

    private List<Pokemon> pokemon = new ArrayList<>();
    private RequestQueue requestQueue;
    private List<Pokemon> filtered = new ArrayList<>();

    PokedexAdapter(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        loadPokemon();
    }

    public void loadPokemon() {
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        //json request using volley library
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results"); //look for key called results
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        String name = result.getString("name");
                        //Boolean c = getPreferences(Context.MODE_PRIVATE).getBoolean("caught", caught);
                        pokemon.add(new Pokemon(
                                name.substring(0, 1).toUpperCase() + name.substring(1), //this will capitalise the first
                                //letter of the string 'name'. substring takes the start index 0 and end index 1 to
                                //get the first letter, then call it again and only pass 1 arg to get the rest
                                result.getString("url"),
                                false
                                ));
                    }

                    filtered = pokemon; //omg this fkn like took hours! this is all you needed to display
                    //pokemon list prior to filtering

                    notifyDataSetChanged(); //we need to tell our app to reload, as there is new data
                } catch (JSONException e) {
                    Log.e("cs50", "Json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon list error");
            }
        }); //get some data from url

        requestQueue.add(request);
    }

    private class PokemonFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Pokemon> filteredPokemon = new ArrayList<>();
            for (int i = 0; i < pokemon.size(); i++) {
                if (constraint.equals("")) {
                    filteredPokemon.add(pokemon.get(i));
                }
                else if (pokemon.get(i).getName().toLowerCase().startsWith(constraint.toString().toLowerCase()))
                    filteredPokemon.add(pokemon.get(i));
                }

            FilterResults results = new FilterResults();
            results.values = filteredPokemon;
            results.count = filteredPokemon.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered = (List<Pokemon>) results.values;

            //are the pokemon still visible? do we need to remove them or add them to screen?
            notifyDataSetChanged();
        }
    }

    //i typred oncreate and hit enter. it generated the following
    @NonNull
    @Override
    public PokedexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pokedex_row, parent, false);
        //now we have converted this xml file into a java object in memory (with inflate()
        return new PokedexViewHolder(view);
    }

    //typed onbind and hit enter for template
    @Override
    public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) {
        Pokemon current = filtered.get(position);
        holder.textView.setText(current.getName());
        holder.containerView.setTag(current);
    }

//item and enter
    @Override
    public int getItemCount() {
        return filtered.size();
    }
}

