package com.example.fuel_cell_mes_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RequsetD_Activity extends AppCompatActivity {

    ListView listView1;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requset_d);

    }
}