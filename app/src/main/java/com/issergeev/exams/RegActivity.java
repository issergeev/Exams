package com.issergeev.exams;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    private String loginText = "", passwordText = "";
    private int progressBarVisibility = View.GONE;

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
                createUser();
            }
        });

        mask = new TextMask(SIDInput, "###-##/##");
        try {
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(SIDInput.getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.unknownError, Toast.LENGTH_SHORT).show();
        }

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.forbid_anim);
    }

    private void createUser() {
        final String createURL = "http://exams-online.000webhostapp.com/add_new_user.php";

        final RequestQueue request = Volley.newRequestQueue(RegActivity.this);
        StringRequest query = new StringRequest(Request.Method.POST, createURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch (response) {
                    case "Success" :
                        Toast.makeText(RegActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                        loginText = loginInput.getText().toString();
                        passwordText = passwordInput.getText().toString();

                        editor.putString("Login", loginText);
                        editor.putString("Password", passwordText);
                        editor.putInt("progressBarVisibility", progressBar.getVisibility());
                        editor.apply();
                        break;
                    default :
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> data = new HashMap<>();
                data.put("studentid_number", SIDInput.getText().toString());
                data.put("student_verif", loginInput.getText().toString());
                data.put("student_auth", passwordInput.getText().toString());

                return data;
            }
        };

        progressBar.setVisibility(View.GONE);

        request.add(query);
    }

    @Override
    public boolean onLongClick(View view) {
        view.startAnimation(shakeAnimation);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        editor.putInt("progressBarVisibility", progressBar.getVisibility());
        editor.apply();
    }
}