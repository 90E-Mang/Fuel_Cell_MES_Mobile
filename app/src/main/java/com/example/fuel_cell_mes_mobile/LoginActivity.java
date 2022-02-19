package com.example.fuel_cell_mes_mobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    public AlertDialog dialog;
    public static boolean loginChk;
    public static String userID;
    public static String userPWD;
    String[] user_Info = new String[2];
    Context context;
    private BackPressCloseSystem backPressCloseSystem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backPressCloseSystem = new BackPressCloseSystem(this);


        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final Button btnDoLogin = (Button) findViewById(R.id.btnDoLogin);
        final Button btnQuit = (Button) findViewById(R.id.btnQuit);

        passwordText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    btnDoLogin.performClick();
                    return true;
                }
                return false;
            }
        });

        btnDoLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userID = idText.getText().toString();
                userPWD = passwordText.getText().toString();
                //loginChk = false;
                user_Info[0] = userID;
                user_Info[1] = userPWD;

                Request();
            }
        });
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("앱을 종료하시겠습니까?");
                builder.setTitle("종료 알림창")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("종료 알림창");
                alert.show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        backPressCloseSystem.onBackPressed();
    }
    void Request(){
        new AsyncTask<String, Void, String>(){
            Context context = LoginActivity.this;
            ProgressDialog pd;
            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(context);
                pd.setTitle("로딩중");
                pd.setMessage("잠시만 기다려주세요.");
                pd.setMax(10);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setButton(ProgressDialog.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                pd.show();
            }
            @Override
            protected String doInBackground(String... user_Info) {
                StringBuffer Buffer = new StringBuffer();
                String get_userID = user_Info[0];
                String get_userPWD = user_Info[1];
                String get_json = "";
                try {
                    String urlAddr = "http://123.248.155.8:9900/LoginRequest.jsp?&id=" + get_userID + "&pwd=" + get_userPWD;
                    URL url = new URL(urlAddr);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    publishProgress();
                    if (conn != null) {
                        conn.setConnectTimeout(20000);
                        conn.setUseCaches(false);
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            // 서버에서 읽어오기 위한 스트림 객체
                            InputStreamReader isr = new InputStreamReader(
                                    conn.getInputStream());
                            // 줄단위로 읽어오기 위해 BufferReader로 감싼다.
                            BufferedReader br = new BufferedReader(isr);
                            // 반복문 돌면서읽어오기
                            while (true) {
                                String line = br.readLine();
                                if (line == null) {
                                    break;
                                }
                                Buffer.append(line);
                            }
                            br.close();
                            conn.disconnect();
                        }
                    }
                    get_json = Buffer.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return get_json;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONArray jarray = new JSONObject(result).getJSONArray("loginChk");
                    if (jarray != null) {
                        JSONObject jsonObject = jarray.getJSONObject(0);
                        MainActivity.custName = jsonObject.getString("custName");
                        MainActivity.custCode = jsonObject.getString("userID");
                        LoginActivity.loginChk = true;
                    } else {
                        LoginActivity.loginChk = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Dologin();
                pd.dismiss();
            }
        }.execute(user_Info);
    }
    void Dologin(){
        boolean login = LoginActivity.loginChk;

        try {
            if (login) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                dialog = builder.setMessage("로그인에 성공했습니다.").setPositiveButton("확인", null).create();
                dialog.show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                dialog = builder.setMessage("아이디, 비밀번호를 확인해주세요.").setNegativeButton("다시시도", null).create();
                dialog.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
