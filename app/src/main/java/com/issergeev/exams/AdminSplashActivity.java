package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class AdminSplashActivity extends AppCompatActivity {
    private static final String DATA_PREFS_NAME = "Data";

    private static SharedPreferences examsData;
    private static SharedPreferences.Editor editor;

    private String loginText, passwordText, firstName, lastName, patronymic, salt;

    private RelativeLayout rootLayout;
    private AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        Intent intent = getIntent();

        loginText = intent.getStringExtra("Login");
        passwordText = intent.getStringExtra("Password");
        salt = intent.getStringExtra("Salt");

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        new SignIn().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class SignIn extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String LOGIN_URL = "http://exams-online.000webhostapp.com/sign_in_admin.php";
            final RequestQueue request = Volley.newRequestQueue(AdminSplashActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                    if (!response.equals("null")) {
                        try {
                            JSONObject serverResponse = new JSONObject(response);
                            JSONArray user = serverResponse.getJSONArray("Admin");
                            JSONObject data = user.getJSONObject(0);

                            firstName = data.getString("first_name");
                            lastName = data.getString("last_name");
                            patronymic = data.getString("patronymic");

                            editor.putString("LoginAdmin", loginText);
                            editor.putString("PasswordAdmin", passwordText);
                            editor.putString("firstNameAdmin", firstName);
                            editor.putString("lastNameAdmin", lastName);
                            editor.putString("patronymicAdmin", patronymic);
                            editor.apply();

                            finish();
                            startActivity(new Intent(AdminSplashActivity.this, AdminMainActivity.class));
                        } catch (JSONException e) {
                            Snackbar.make(rootLayout, R.string.unknown_response, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        editor.putString("Login", "");
                        editor.putString("Password", "");
                        editor.apply();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(AdminSplashActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(AdminSplashActivity.this);
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warning_title_text)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.user_not_found)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        }).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        int errorCode = error.networkResponse.statusCode;

                        switch (errorCode) {
                            case 302:
                                Snackbar.make(rootLayout, R.string.hotspot_error, Snackbar.LENGTH_SHORT).show();
                                break;
                            case 423:
                                Snackbar.make(rootLayout, R.string.server_sleeping_text, Snackbar.LENGTH_SHORT).show();
                                break;
                            default:
                                Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_SHORT).show();
                                break;
                        }
                    } catch (NullPointerException e) {
                        Snackbar.make(rootLayout, R.string.unknown_response, Snackbar.LENGTH_LONG).show();
                    }
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> userData = new HashMap<>();

                    userData.put("admin_verif", loginText);
                    userData.put("admin_auth", Encryption.EncryptWithSalt(passwordText, salt));

                    return userData;
                }
            };
            request.add(query);

            return null;
        }
    }
}