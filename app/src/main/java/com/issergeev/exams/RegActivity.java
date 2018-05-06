package com.issergeev.exams;

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
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private String SIDText = "", loginText = "", passwordText = "", appendixText = "";
    private int progressBarVisibility = View.GONE;

    static String[] passData = new String[2];

    RelativeLayout rootLayout;
    EditText SIDInput, loginInput, passwordInput;
    Button createButton;
    ProgressBar progressBar;

    AlertDialog.Builder alert;

    TextMask mask;
    Animation shakeAnimation;
    SharedPreferences examsData = LoginStudentActivity.examsData;
    SharedPreferences.Editor editor = LoginStudentActivity.editor;

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
                loginText = loginInput.getText().toString();
                passwordText = passwordInput.getText().toString();
                SIDText = SIDInput.getText().toString().replace("-", "")
                        .replace("/", "");

                if (userDataChecker(SIDText, loginText, passwordText)) {
                    createButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    lockScreenOrientation();

                    new Registrar().execute();
                }
            }
        });

        mask = new TextMask(SIDInput, "###-##/##");
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.forbid_anim);
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

        return true;
    }

    private void lockScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        switch (rotation) {
            case Surface.ROTATION_0 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Surface.ROTATION_90 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case Surface.ROTATION_180 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case Surface.ROTATION_270 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
        }
    }

    private boolean userDataChecker(String SIDText ,String loginText, String passwordText) {
        final String[] UNAVALIABLE_LOGINS = new String[] {"administrator", "admin", "webmaster", "login"};
        final String[] UNSAFE_PASSWORDS = new String[] {"qwerty", "password", "p@ssw0rd", "12345"};

        if (SIDText.length() != 7) {
            Snackbar.make(rootLayout, R.string.SIDLength, Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!Character.isLetter(loginText.charAt(0))) {
            Snackbar.make(rootLayout, R.string.loginBeginningInvalid, Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (loginText.length() < 4) {
            Snackbar.make(rootLayout, R.string.loginShort, Toast.LENGTH_LONG).show();
            return false;
        }

        if (loginText.length() > 32) {
            Snackbar.make(rootLayout, R.string.loginLong, Snackbar.LENGTH_LONG).show();
        }

        for (String s : UNAVALIABLE_LOGINS) {
            if (loginText.toLowerCase().contains(s)) {
                Snackbar.make(rootLayout, R.string.unavailableLogin, Snackbar.LENGTH_LONG).show();
                return false;
            }
        }

        if (passwordText.length() < 5) {
            Snackbar.make(rootLayout, R.string.passwordShort, Toast.LENGTH_LONG).show();
            return false;
        }

        if (passwordText.length() > 64) {
            Snackbar.make(rootLayout, R.string.passwordLong, Snackbar.LENGTH_LONG).show();
        }

        for (String s : UNSAFE_PASSWORDS) {
            if (passwordText.equals(s)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alert = new AlertDialog.Builder(RegActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    alert = new AlertDialog.Builder(RegActivity.this);
                }
                alert.setCancelable(true)
                        .setTitle(R.string.warningTitleText)
                        .setMessage(R.string.passwordUnsafeMessage)
                        .setPositiveButton(R.string.changeText, null)
                        .setNegativeButton(R.string.declineText, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressBar.setVisibility(View.VISIBLE);
                                lockScreenOrientation();

                                new Registrar().execute();
                            }
                        }).show();

                return false;
            }
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        editor.putInt("progressBarVisibility", progressBar.getVisibility());
        editor.apply();
    }

    class Registrar extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
                final String createURL = "http://exams-online.000webhostapp.com/add_new_user.php";

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
                                    Snackbar.make(rootLayout, R.string.createText, Snackbar.LENGTH_SHORT).setAction("Sign In now", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            onBackPressed();
                                        }
                                    }).show();
                                    saveData();
                                    break;
                                case "Login exists":
                                    Snackbar.make(rootLayout, R.string.userExistsText, Snackbar.LENGTH_LONG).show();
                                    break;
                                case "User exists":
                                    Snackbar.make(rootLayout, R.string.studentIDExistsText, Snackbar.LENGTH_LONG).show();
                                    break;
                                case "No such user":
                                    Snackbar.make(rootLayout, R.string.notSuchUser, Snackbar.LENGTH_LONG).show();
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
                                Snackbar.make(rootLayout, R.string.serverSleepingText, Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(rootLayout, R.string.unknownErrorText, Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                alert = new AlertDialog.Builder(RegActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                alert = new AlertDialog.Builder(RegActivity.this);
                            }
                            alert.setCancelable(true)
                                    .setTitle(R.string.warningTitleText)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setMessage(R.string.connectionErrorText)
                                    .setPositiveButton(R.string.acceptText, new DialogInterface.OnClickListener() {
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