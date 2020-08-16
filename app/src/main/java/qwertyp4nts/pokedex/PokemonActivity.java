package qwertyp4nts.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class PokemonActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private String url;
    private RequestQueue requestQueue;
    private Button catchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        url = getIntent().getStringExtra("url"); //to getintent that started this activity
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);
        catchButton = findViewById(R.id.catchButton);

        load();
    }

    public void load() {
        type1TextView.setText("");
        type2TextView.setText("");
        //json request using volley library
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    nameTextView.setText(response.getString("name"));
                    String pokemonNum = String.format("#%03d", response.getInt("id"));
                    numberTextView.setText(pokemonNum);

                    JSONArray typeEntries = response.getJSONArray("types");
                    for (int i = 0; i <typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");

                        if (slot == 1) {
                            type1TextView.setText(type);
                        }
                        else if (slot == 2) {
                            type2TextView.setText(type);
                        }

                        loadData(pokemonNum); //load CATCH state from SharedPreferences

                    }
                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon Json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon details error");
            }
        }); //get some data from url

        requestQueue.add(request);
    }

    public void toggleCatch(View view) {
        TextView pokemon_number = findViewById(R.id.pokemon_number);
        boolean caught = false;
        if (catchButton.getText().toString().equals("Catch")) {
            catchButton.setText("Release");
            caught = true;
        }

        else if (catchButton.getText().toString().equals("Release")) {
            catchButton.setText("Catch");
            caught = false;
        }
        saveData(pokemon_number, caught); //Save CATCH state to SharedPreferences
    }

    //saves CATCH state of pokemon when its state changes
    public void saveData(TextView pokemonNum, boolean caught) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String pknum = pokemonNum.getText().toString();
        getPreferences(Context.MODE_PRIVATE).edit().putBoolean(pknum, caught).apply();
        editor.apply();
    }

    //loads CATCH state of pokemon when that pokemon is clicked on
    public void loadData(String pokemonNumber) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean state = sharedPreferences.getBoolean(pokemonNumber, false);
        String StrState;
        if (state == true) {
            StrState = "Release";
        }
        else {
            StrState = "Catch";
        }
        catchButton.setText(StrState);
    }
}