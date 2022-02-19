package com.example.fuel_cell_mes_mobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static String custName;
    public static String custCode;
    private BackPressCloseSystem backPressCloseSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //텍스트 생성
        final TextView txtWelcome = (TextView) findViewById(R.id.txtWelcome);
        final TextView txtTop = (TextView) findViewById(R.id.txtTop);

        //버튼 생성
        final Button btnSearch_order = (Button) findViewById(R.id.btnSearch_order);
        final Button btnSearch_in = (Button) findViewById(R.id.btnSearch_in);
        final Button btnMarket = (Button) findViewById(R.id.btnMarket);
        final Button btnQuit = (Button) findViewById(R.id.btnQuit);

        //뒤로가기 버튼 이벤트 생성
        backPressCloseSystem = new BackPressCloseSystem(this);

        //환영합니다 텍스트에 거래처명 넣기
        txtWelcome.setText(custName + "님 환영합니다.");

        //종료버튼
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

        // 발주조회
        btnSearch_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderH_Activity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        // 입고요청 목록조회
        btnSearch_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RequestH_Activity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        // 장바구니
        btnMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Market_Activity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        backPressCloseSystem.onBackPressed();
    }
}