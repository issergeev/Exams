package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegActivity extends AppCompatActivity implements View.OnLongClickListener {
    private static final String DATA_PREFS_NAME = "Data";
    private final String TEXT_MASK = "###-##/##";

    private String SIDText = "", loginText = "", passwordText = "", appendixText = "";
    static String[] passData = new String[2];
    private int progressBarVisibility = View.GONE;

    private static SharedPreferences examsData;
    private static SharedPreferences.Editor editor;

    RelativeLayout rootLayout;
    EditText SIDInput, loginInput, passwordInput;
    Button createButton;
    ProgressBar progressBar;

    AlertDialog.Builder alert;

    private TextMask mask;
    Animation shakeAnimation;

    @Override
    protected void onResume() {
        super.onResume();

        loginText = examsData.getString("Login", "");
        passwordText = examsData.getString("Password", "");
        loginInput.setText(loginText);
        passwordInput.setText(passwordText);
        progressBar.setVisibility(progressBarVisibility);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        editor.putString("Login", loginInput.getText().toString());
        editor.putString("Password", passwordInput.getText().toString());
        editor.apply();
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

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

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
                SIDText = SIDInput.getText().toString();

                for (int i = 0; i < SIDText.length(); i++) {
                    Character SIDChar = SIDText.charAt(i);
                    Character maskChar = TEXT_MASK.charAt(i);

                    if (maskChar.compareTo('#') != 0 && SIDChar.compareTo(maskChar) != 0) {
                        Snackbar.make(rootLayout, R.string.sid_length, Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }

                loginText = loginInput.getText().toString().trim();
                passwordText = passwordInput.getText().toString().trim();
                SIDText = SIDInput.getText().toString().replaceAll("-", "")
                        .replaceAll("/", "");

                if (userDataChecker(SIDText, loginText, passwordText)) {
                    createButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

                    new Registrar().execute();
                }
            }
        });

        mask = new TextMask(SIDInput, TEXT_MASK);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.forbid_anim);
    }

    private void saveData() {
        loginText = loginInput.getText().toString();
        passwordText = passwordInput.getText().toString();

        editor.putString("Login", loginText);
        editor.putString("Password", passwordText);
        editor.apply();
    }

    @Override
    public boolean onLongClick(View view) {
        view.startAnimation(shakeAnimation);

        return true;
    }

    private boolean userDataChecker(String SIDText ,String loginText, String passwordText) {
        final String[] UNAVALIABLE_LOGINS = new String[] {"administrator", "admin", "webmaster", "login"};
        final String[] UNSAFE_PASSWORDS = new String[] {"qwerty", "password", "p@ssw0rd", "12345"};

        if (SIDText.length() != 7) {
            Snackbar.make(rootLayout, R.string.sid_length, Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (loginText.length() < 4) {
            Snackbar.make(rootLayout, R.string.login_short, Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (loginText.length() > 32) {
            Snackbar.make(rootLayout, R.string.login_long, Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (passwordText.length() < 5) {
            Snackbar.make(rootLayout, R.string.password_short, Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (passwordText.length() > 64) {
            Snackbar.make(rootLayout, R.string.password_long, Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!Character.isLetter(loginText.charAt(0))) {
            Snackbar.make(rootLayout, R.string.login_beginning_invalid, Snackbar.LENGTH_LONG).show();
            return false;
        }

        for (String s : UNAVALIABLE_LOGINS) {
            if (loginText.toLowerCase().contains(s)) {
                Snackbar.make(rootLayout, R.string.unavailable_login, Snackbar.LENGTH_LONG).show();
                return false;
            }
        }

        for (String s : UNSAFE_PASSWORDS) {
            if (passwordText.equals(s)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alert = new AlertDialog.Builder(RegActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    alert = new AlertDialog.Builder(RegActivity.this);
                }
                alert.setCancelable(true)
                        .setTitle(R.string.warning_title_text)
                        .setMessage(R.string.password_unsafe_message)
                        .setPositiveButton(R.string.change_text, null)
                        .setNegativeButton(R.string.decline_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                createButton.setEnabled(false);
                                progressBar.setVisibility(View.VISIBLE);
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

                                new Registrar().execute();
                            }
                        }).show();

                return false;
            }
        }

        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class Registrar extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String createURL = "http://exams-online.online/add_new_user.php";

            RequestQueue request = Volley.newRequestQueue(RegActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, createURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        createButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                        switch (response) {
                            case "Success":
                                Snackbar.make(rootLayout, R.string.create_text, Snackbar.LENGTH_SHORT).setAction("Sign In now", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        onBackPressed();
                                    }
                                }).show();
                                saveData();
                                break;
                            case "Login exists":
                                Snackbar.make(rootLayout, R.string.user_exists_text, Snackbar.LENGTH_LONG).show();
                                break;
                            case "User exists":
                                Snackbar.make(rootLayout, R.string.student_ID_exists_text, Snackbar.LENGTH_LONG).show();
                                break;
                            case "No such user":
                                Snackbar.make(rootLayout, R.string.not_such_user, Snackbar.LENGTH_LONG).show();
                                break;
                            default:
                                Log.i("net", response);
                                break;
                        }
                    }
                }
            }, new Response.ErrorListener()

            {
                @Override
                public void onErrorResponse (VolleyError error){
                    if (error.networkResponse != null) {
                        createButton.setEnabled(true);

                        int errorCode = error.networkResponse.statusCode;

                        if (errorCode == 423) {
                            Snackbar.make(rootLayout, R.string.server_sleeping_text, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(RegActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(RegActivity.this);
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warning_title_text)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.connection_error_text)
                                .setPositiveButton(R.string.accept_text, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> data = new HashMap<>();

                    passData = Encryption.Encrypt(passwordText).split("[\\ ]");
                    appendixText = passData[0];
                    passwordText = passData[1];

                    data.put("studentid_number", SIDText);
                    data.put("student_verif", loginText);
                    data.put("student_auth", passwordText);
                    data.put("student_appendix", appendixText);

                    return data;
                }
            };
            request.add(query);

            return null;
        }
    }
}