package com.issergeev.exams;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeSplashActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor = WelcomeActivity.editor;

    private String loginText, passwordText, firstName, lastName, patronymic, groupNumber, salt;
    private int studentIDNumber;

    RelativeLayout rootLayout;
    Intent intent;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_splash);

        intent = getIntent();
        loginText = intent.getStringExtra("Login");
        passwordText = intent.getStringExtra("Password");
        salt = intent.getStringExtra("Salt");

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        new SignIn().execute();
    }

    class SignIn extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String loginURL = "http://exams-online.online/sign_in.php";
            final RequestQueue request = Volley.newRequestQueue(HomeSplashActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, loginURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                    if (!response.equals("null")) {

                        try {
                            JSONObject serverResponse = new JSONObject(response);
                            JSONArray user = serverResponse.getJSONArray("User");
                            JSONObject data = user.getJSONObject(0);

                            studentIDNumber = data.getInt("studentID_number");
                            firstName = data.getString("first_name");
                            lastName = data.getString("last_name");
                            patronymic = data.getString("patronymic");
                            groupNumber = data.getString("group_number");

                            editor.putString("Login", loginText);
                            editor.putString("Password", passwordText);
                            editor.putString("Name", lastName + " " + firstName + " " + patronymic);
                            editor.putInt("SIDNumber", studentIDNumber);
                            editor.putString("GroupNumber", groupNumber);
                            editor.commit();

                            finish();
                            startActivity(new Intent(HomeSplashActivity.this, HomeActivity.class));
                        } catch (JSONException e) {
                            Log.d("login", e.getMessage());
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(HomeSplashActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(HomeSplashActivity.this);
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warningTitleText)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.userNotFound)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int errorCode = error.networkResponse.statusCode;

                    switch (errorCode) {
                        case 302 :
                            Snackbar.make(rootLayout, R.string.hotspotError, Snackbar.LENGTH_SHORT).show();
                            break;
                        case 423 :
                            Snackbar.make(rootLayout, R.string.serverSleepingText, Snackbar.LENGTH_SHORT).show();
                            break;
                        default :
                            Snackbar.make(rootLayout, R.string.unknownErrorText, Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                    Log.d("login", error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> userData = new HashMap<>();

                    userData.put("student_verif", loginText);
                    userData.put("student_auth", Encryption.EncryptWithSalt(passwordText, salt));

                    return userData;
                }
            };
            request.add(query);
            return null;
        }
    }
}