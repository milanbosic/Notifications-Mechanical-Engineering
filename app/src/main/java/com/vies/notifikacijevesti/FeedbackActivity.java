package com.vies.notifikacijevesti;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Feedback Activity for sending feedback information
 */
public class FeedbackActivity extends AppCompatActivity {
    public RatingBar ratingBar;
    private String[] items;
    private EditText editText;
    private RequestQueue requestQueue;
    private TinyDB tinyDB;
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
        // Set the default number of stars for the rating bar
        ratingBar.setRating(5);
        editText = findViewById(R.id.textPoruka);

        // Initialize database
        tinyDB = new TinyDB(getApplicationContext());

        // Hide the keyboard by touching anything outside the editText (the relative layout)
        // * Acessibility warning *
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayoutFeedback);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });

//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    hideSoftKeyboard(v);
//                }
//            }
//        });
        items = new String[]{"Utisak", "Prijavi grešku", "Sugestije"};
        spinner.setItems(items);

        // Only one item on the spinner is supposed to be pair with a ratingBar
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if (position == 1 || position == 2) {
                    ratingBar.setVisibility(View.GONE);
                } else {
                    if (ratingBar.getVisibility() == View.GONE)
                        ratingBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // Floating action button that sends an HTTP request with information
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (editText.getText().length() <= 500) {
                    String url = "http://" + tinyDB.getString("serverIP") + "/api/feedback";

                    Log.d("SERVER_IP", "feedback url: " + url);

                    HashMap<String, String> params = new HashMap<String, String>();

                    params.put("token", FirebaseInstanceId.getInstance().getToken());

                    params.put("category", items[spinner.getSelectedIndex()]);

                    if (spinner.getSelectedIndex() == 1 || spinner.getSelectedIndex() == 2) {
                        params.put("rating", "0");
                    } else {
                        params.put("rating", "" + ratingBar.getRating());
                    }

                    params.put("description", editText.getText().toString());

                    Response.ErrorListener errorListen = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            VolleyLog.v("responseJson: ", error);
                            serverErrorDialog("Došlo je do greške u povezivanju sa serverom. Molimo pokušajte ponovo", view);

                        }
                    };

                    Response.Listener<JSONObject> responseListen = new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("message").contains("Success")) {

                                    Snackbar.make(view, "Poruka uspešno poslata.", Snackbar.LENGTH_LONG).show();
                                } else if (response.getString("message").contains("ticket")) {
                                    serverErrorDialog("Vaša poruka je prihvaćena, molimo sačekajte da se obradi.", view);
                                } else {
                                    serverErrorDialog("Došlo je do greške u povezivanju sa serverom. Molimo pokušajte ponovo.", view);
                                }
                            } catch (JSONException e) {
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

    // Cancel volley request on application exit
    protected void onStop() {

        super.onStop();
        if (requestQueue != null) {

            requestQueue.cancelAll(VolleyTag);
        }

    }

    // Alert dialog to show when an error happens
    private void serverErrorDialog(String str, View view) {
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

}
