package com.example.fuel_cell_mes_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OrderH_Activity extends AppCompatActivity {
    String custCode = MainActivity.custCode;  //거래처코드
    String MTRL_ORDERNO;  //발주번호
    String PLANT_CODE;   // 공장코드
    String STARTDATE;    // 조회시작일자
    String ENDDATE;      // 조회 끝 일자

    // Date Picker 생성
    Calendar myCalendar = Calendar.getInstance();
    Calendar myCalendar2 = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener startDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };
    DatePickerDialog.OnDateSetListener endDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar2.set(Calendar.YEAR, year);
            myCalendar2.set(Calendar.MONTH, month);
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel2();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_h);

        //텍스트 생성
        final EditText orderNo = (EditText) findViewById(R.id.txtOrderNo);
        final EditText startDate = (EditText) findViewById(R.id.StartDate);
        final EditText endDate = (EditText) findViewById(R.id.EndDate);

        // 공장 검색용 spinner 생성
        Spinner spnPlantCode = (Spinner) findViewById(R.id.spnPlantCode);
        bindSpinner();
        // 버튼생성
        final Button btnDoInquire = (Button) findViewById(R.id.btnDoInquire);
        final Button btnBack = (Button) findViewById(R.id.btnOBack);
        final Button btnMarket = (Button) findViewById(R.id.btnOMarket);

        //스크롤, 테이블 레이아웃
        final HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.scroll);
        final TableLayout table = (TableLayout) findViewById(R.id.order_H_List);
        final TableRow trow = (TableRow) findViewById(R.id.trow);



        // 발주일자 검색 텍스트뷰 클릭반응
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(OrderH_Activity.this,startDatePicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog makeEnddate;
                makeEnddate = new DatePickerDialog(OrderH_Activity.this,endDatePicker,myCalendar2.get(Calendar.YEAR),myCalendar2.get(Calendar.MONTH), myCalendar2.get(Calendar.DAY_OF_MONTH));
                makeEnddate.show();
            }
        });
        //뒤로가기 버튼 기능 구현
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderH_Activity.this, MainActivity.class);
                OrderH_Activity.this.startActivity(intent);
            }
        });

        // 장바구니 버튼 기능 구현
        btnMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderH_Activity.this, Market_Activity.class);
                OrderH_Activity.this.startActivity(intent);
            }
        });

        //스피너 아이템 선택 이벤트
        spnPlantCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                KeyValueArrayAdapter adapter = (KeyValueArrayAdapter) parent.getAdapter();
                PLANT_CODE = adapter.getEntryValue(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 조회버튼 기능 구현
        btnDoInquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                table.removeViews(1, table.getChildCount()-1);
                //텍스트뷰 입력 값 가져오기
                if(orderNo.getText().equals(null)){
                    MTRL_ORDERNO = "";
                }
                MTRL_ORDERNO = orderNo.getText().toString().trim();
                if(spnPlantCode.getSelectedItem().equals(null)){
                    PLANT_CODE = "";
                }

                if(startDate.getText().equals(null)){
                    STARTDATE = "";
                }
                STARTDATE = startDate.getText().toString().trim();

                if(endDate.getText().equals(null)){
                    ENDDATE = "";
                }
                ENDDATE = endDate.getText().toString().trim();
                // 파라미터로 보낼 배열 선언
                String[] params = new String[5];
                params[0] = custCode;
                params[1] =MTRL_ORDERNO;
                params[2] = PLANT_CODE;
                params[3] = STARTDATE;
                params[4] = ENDDATE;
                Request(params);
            }
        });
    }
    // 공장코드 스피너 데이터 바인딩
    void bindSpinner(){
        new AsyncTask<Context, Void, String>(){
            Context context = OrderH_Activity.this;
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
            protected String doInBackground(Context... context) {
                StringBuffer Buffer = new StringBuffer();
                String get_json = "";
                try {
                    String urlAddr = "http://123.248.155.8:9900/BindSpinner.jsp";
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
                    JSONArray jarray = new JSONObject(result).getJSONArray("PlantSpinner");
                    String[] codeValue = new String[jarray.length()];
                    String[] nameValue = new String[jarray.length()];
                    if (jarray != null) {

                        for(int i = 0; i < jarray.length() ; i++ ){
                            JSONObject jsonObject = jarray.getJSONObject(i);
                            codeValue[i] = jsonObject.getString("PLANT_CODE");
                            nameValue[i] = jsonObject.getString("PLANT_NAME");
                        }
                    } else {

                    }

                    // 공장 검색용 spinner 생성
                    Spinner spnPlantCode = (Spinner) findViewById(R.id.spnPlantCode);
                    KeyValueArrayAdapter adapter = new KeyValueArrayAdapter(OrderH_Activity.this, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    adapter.setEntries(nameValue);
                    adapter.setEntryValues(codeValue);
                    spnPlantCode.setAdapter(adapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }
        }.execute(this);
    }
    // 발주 조회
    void Request(String[] params){
        new AsyncTask<String, Void, String>(){
            Context context = OrderH_Activity.this;
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
            protected String doInBackground(String... params) {
                StringBuffer Buffer = new StringBuffer();
                String get_CustCode = params[0];
                String get_OrderNo = params[1];
                String get_PlantCode = params[2];
                String get_StartDate = params[3];
                String get_EndDate = params[4];
                String get_json = "";
                try {
                    String urlAddr = "http://123.248.155.8:9900/OrderH_Select.jsp?&CUST_CODE=" + get_CustCode + "&ORDERNO=" + get_OrderNo +
                                     "&PLANT_CODE=" + get_PlantCode + "&STARTDATE=" + get_PlantCode + "&ENDDATE=" + get_EndDate;
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
                    JSONArray jarray = new JSONObject(result).getJSONArray("OrderHSelect");
                    if (jarray != null) {

                        TableLayout table = (TableLayout) findViewById(R.id.order_H_List);
                        TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int rowCount = jarray.length();
                        int columnCount = jarray.getJSONObject(0).length();
                        TableRow row[] = new TableRow[rowCount];
                        TextView column[][] = new TextView[rowCount][columnCount];
                        for (int i = 0 ; i < rowCount ; i++){
                            row[i] = new TableRow(OrderH_Activity.this);
                            row[i].setId(i+1);
                            row[i].setClickable(true);
                            JSONObject jsonObject = jarray.getJSONObject(i);
                            String[] rowData = new String[jsonObject.length()];
                            rowData[0] = jsonObject.getString("MTRL_ORDER_NO");
                            rowData[1] =  jsonObject.getString("COMP_NAME");
                            rowData[2] =  jsonObject.getString("PLANT_NAME");
                            rowData[3] =  jsonObject.getString("ORDERDATE");
                            rowData[4] =  jsonObject.getString("COMP_CODE");
                            rowData[5] =  jsonObject.getString("PLANT_CODE");
                            for(int j = 0; j < columnCount ; j++){
                                column[i][j] = new TextView(OrderH_Activity.this);
                                column[i][j].setText(rowData[j]);
                                column[i][j].setTextColor(Color.BLACK);
                                column[i][j].setHeight(100);
                                column[i][j].setClickable(true);
                                column[i][j].setBackground(getResources().getDrawable(R.drawable.makewhitebox));
                                column[i][j].setTextSize(15);
                                column[i][j].setGravity(Gravity.CENTER);
                                row[i].addView(column[i][j]);
                                String ordno = column[i][0].getText().toString();
                                column[i][j].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        OrderD_Activity.orderNo = ordno;
                                        Intent intent = new Intent(OrderH_Activity.this,OrderD_Activity.class);
                                        OrderH_Activity.this.startActivity(intent);
                                    }
                                });
                            }
                            table.addView(row[i],rowLayout);
                        }
                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }
        }.execute(params);
    }
    //발주일자 컬럼 포멧설정
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";    // 출력형식   2021/11/01
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        EditText startDate = (EditText) findViewById(R.id.StartDate);
        startDate.setText(sdf.format(myCalendar.getTime()));
    }
    private void updateLabel2() {
        String myFormat = "yyyy-MM-dd";    // 출력형식   2021/11/01
        SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat, Locale.KOREA);

        EditText endDate = (EditText) findViewById(R.id.EndDate);
        endDate.setText(sdf2.format(myCalendar2.getTime()));
    }
}