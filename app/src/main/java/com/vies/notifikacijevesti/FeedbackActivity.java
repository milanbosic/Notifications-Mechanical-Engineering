package com.vies.notifikacijevesti;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {
    public RatingBar ratingBar;
    private String[] items;
    private EditText editText;
    private RequestQueue requestQueue;
    public static final String VolleyTag = "volleyTag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final MaterialSpinner spinner = findViewById(R.id.categories_spinner);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating(5);
        editText = findViewById(R.id.textPoruka);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        items = new String[] {"Utisak", "Prijavi grešku", "Sugestije"};
        spinner.setItems(items);

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if (position == 1 || position == 2){
                    ratingBar.setVisibility(View.GONE);
                } else {
                    if (ratingBar.getVisibility() == View.GONE) ratingBar.setVisibility(View.VISIBLE);
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                if (editText.getText().length() <= 500){
                    String url ="http://165.227.154.9:8082/api/feedback";

                    HashMap<String, String> params = new HashMap<String, String>();

                    params.put("token", FirebaseInstanceId.getInstance().getToken());

                    params.put("category", items[spinner.getSelectedIndex()]);

                    if (spinner.getSelectedIndex() == 1 || spinner.getSelectedIndex() == 2){
                        params.put("rating", "0");
                    } else {
                        params.put("rating", "" + ratingBar.getRating());
                    }

                    params.put("description", editText.getText().toString());

                    Response.ErrorListener errorListen = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.v("responseJson: ", error);
                                serverErrorDialog("Došlo je do greške u povezivanju sa serverom. Molimo pokušajte ponovo", view);

                        }
                    };

                    Response.Listener<JSONObject> responseListen = new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                Log.d("Response: ", response.getString("message"));
                                if (response.getString("message").contains("Success")){

                                    Snackbar.make(view, "Uspešno sačuvano", Snackbar.LENGTH_LONG).show();
                                } else{
                                    serverErrorDialog("Došlo je do greške u povezivanju sa serverom. Molimo pokušajte ponovo.", view);
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                                serverErrorDialog("Došlo je do greške u povezivanju sa serverom. Molimo pokušajte ponovo.", view);
                            }
                        }
                    };

                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                    CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, responseListen, errorListen);
                    jsObjRequest.setTag(VolleyTag);

                    requestQueue.add(jsObjRequest);
                } else {
                    serverErrorDialog("Poruka mora sadržati maksimum 500 karaktera.", view);
                }
            }
        });

    }

    protected void onStop() {

        super.onStop();
        if (requestQueue != null){

            requestQueue.cancelAll(VolleyTag);
        }

    }

    private void serverErrorDialog(String str, View view){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(view.getContext());
        }
        builder.setTitle("Greška")
                .setMessage(str)
                .setPositiveButton("U redu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.ic_alert)
                .show();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
