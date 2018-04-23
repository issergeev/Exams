package com.issergeev.exams;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegActivity extends AppCompatActivity implements View.OnLongClickListener {
    private String SIDText = "", loginText = "", passwordText = "";
    private int progressBarVisibility = View.GONE;

    RelativeLayout rootLayout;
    EditText SIDInput, loginInput, passwordInput;
    Button createButton;
    ProgressBar progressBar;

    InputMethodManager imm;
    TextMask mask;
    Animation shakeAnimation;
    SharedPreferences examsData = LoginActivity.examsData;
    SharedPreferences.Editor editor = LoginActivity.editor;

    @Override
    protected void onResume() {
        super.onResume();

        loginText = examsData.getString("Login", "");
        passwordText = examsData.getString("Password", "");
        progressBarVisibility = examsData.getInt("progressBarVisibility", View.GONE);
        loginInput.setText(loginText);
        passwordInput.setText(passwordText);
        progressBar.setVisibility(progressBarVisibility);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.reversed_activity_appear_anim, R.anim.reversed_activity_diapear_anim);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        SIDInput = (EditText) findViewById(R.id.SIDInput);
        SIDInput.setOnLongClickListener(this);
        loginInput = (EditText) findViewById(R.id.loginInput);
        loginInput.setOnLongClickListener(this);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        passwordInput.setOnLongClickListener(this);
        createButton = (Button) findViewById(R.id.regButton);



        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                lockScreenOrientation();
                createUser();
            }
        });

        mask = new TextMask(SIDInput, "###-##/##");
//        try {
//            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(SIDInput.getWindowToken(), 0);
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.forbid_anim);
    }

    private void createUser() {
        final String createURL = "http://exams-online.000webhostapp.com/add_new_user.php";

        RequestQueue request = Volley.newRequestQueue(RegActivity.this);
        StringRequest query = new StringRequest(Request.Method.POST, createURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    progressBar.setVisibility(View.GONE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }

                switch (response) {
                    case "Success" :
                        Snackbar.make(rootLayout, R.string.createText, Snackbar.LENGTH_SHORT).show();
                        saveData();
                        break;
                    case "Login exists" :
                        Snackbar.make(rootLayout, R.string.userExistsText, Snackbar.LENGTH_LONG).show();
                        break;
                    case "User exists" :
                        Snackbar.make(rootLayout, R.string.studentIDExistsText, Snackbar.LENGTH_LONG).show();
                        break;
                    case "No such user" :
                        Snackbar.make(rootLayout, R.string.notSuchUser, Snackbar.LENGTH_LONG).show();
                        break;
                    default :
                        Log.i("net", response);
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    int errorCode = error.networkResponse.statusCode;

                    progressBar.setVisibility(View.GONE);

                    if (errorCode == 423) {
                        Snackbar.make(rootLayout, R.string.serverSleepingText, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(rootLayout, R.string.unknownErrorText, Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(rootLayout, R.string.connectionErrorText, Snackbar.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> data = new HashMap<>();

                loginText = loginInput.getText().toString();
                passwordText = Encryption.Encrypt(passwordInput.getText().toString());
                SIDText = SIDInput.getText().toString().replace("-", "")
                        .replace("/", "");

                data.put("studentid_number", SIDText);
                data.put("student_verif", loginText);
                data.put("student_auth", passwordText);

                return data;
            }
        };

        request.add(query);
    }

    private void saveData() {
        loginText = loginInput.getText().toString();
        passwordText = passwordInput.getText().toString();

        editor.putString("Login", loginText);
        editor.putString("Password", passwordText);
        editor.putInt("progressBarVisibility", progressBar.getVisibility());
        editor.apply();
    }

    @Override
    public boolean onLongClick(View view) {
        view.startAnimation(shakeAnimation);

        SIDText = SIDInput.getText().toString();
        Toast.makeText(getApplicationContext(), SIDText, Toast.LENGTH_SHORT).show();
        return true;
    }

    private void lockScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        switch (rotation) {
            case Surface.ROTATION_0 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Surface.ROTATION_90 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case Surface.ROTATION_180 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case Surface.ROTATION_270 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        editor.putInt("progressBarVisibility", progressBar.getVisibility());
        editor.apply();
    }
}