package com.example.fuel_cell_mes_mobile;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrderD_Activity extends AppCompatActivity {
    public static String orderNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_d);

        final HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.od_scroll);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.od_underLinear);
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.od_table);
        final TableRow tableRow = (TableRow) findViewById(R.id.od_tbrow);

        String[] param = new String[1];
        param[0] = orderNo;
        DoInquire(param);

    }
    void DoInquire(String[] param){
        new AsyncTask<String, Void, String>(){
            Context context = OrderD_Activity.this;
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
            protected String doInBackground(String... param) {
                StringBuffer Buffer = new StringBuffer();
                String get_ordNo = param[0];
                String get_json = "";
                try {
                    String urlAddr = "http://123.248.155.8:9900/OrderD_Select.jsp?&ORDERNO=" + get_ordNo;
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
                    JSONArray jarray = new JSONObject(result).getJSONArray("Order_D_Select");
                    if (jarray != null) {

                        TableLayout table = (TableLayout) findViewById(R.id.od_table);
                        TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int rowCount = jarray.length();
                        int columnCount = jarray.getJSONObject(0).length();
                        TableRow row[] = new TableRow[rowCount];
                        CheckBox chk[][] = new CheckBox[rowCount][1];
                        TextView column[][] = new TextView[rowCount][columnCount+1];
                        for (int i = 0 ; i < rowCount ; i++){
                            row[i] = new TableRow(OrderD_Activity.this);
                            row[i].setId(i+1);
                            row[i].setClickable(true);
                            chk[i][0] = new CheckBox(OrderD_Activity.this);
                            chk[i][0].setClickable(true);
                            chk[i][0].setBackground(getResources().getDrawable(R.drawable.makewhitebox));
                            chk[i][0].setGravity(Gravity.CENTER);
                            row[i].addView(chk[i][0]);
                            JSONObject jsonObject = jarray.getJSONObject(i);
                            String[] rowData = new String[jsonObject.length()+1];
                            rowData[0] = "";
                            rowData[1] = jsonObject.getString("MTRL_ORDER_NO");
                            rowData[2] =  jsonObject.getString("REQUESTNO");
                            rowData[3] =  jsonObject.getString("ITEM_CODE");
                            rowData[4] =  jsonObject.getString("ITEM_NAME");
                            rowData[5] = String.valueOf(jsonObject.getInt("ORDERQTY"));
                            rowData[6] = String.valueOf(jsonObject.getInt("LEFT_INPUT_QTY"));
                            rowData[7] =  jsonObject.getString("ORDERDATE");
                            for(int j = 1; j < columnCount+1 ; j++){
                                column[i][j] = new TextView(OrderD_Activity.this);
                                column[i][j].setText(rowData[j]);
                                column[i][j].setTextColor(Color.BLACK);
                                column[i][j].setHeight(100);
                                column[i][j].setClickable(true);
                                column[i][j].setBackground(getResources().getDrawable(R.drawable.makewhitebox));
                                column[i][j].setTextSize(15);
                                column[i][j].setGravity(Gravity.CENTER);
                                row[i].addView(column[i][j]);
                               // Button btn_addCart = (Button) findViewById(R.id.od_addcart);
                                //btn_addCart.setOnClickListener(new View.OnClickListener() {
                                //    @Override
                                 //   public void onClick(View v) {
                                  //      for (int k = 0; k < rowCount ; k++){
                                   //         String[] InsertParams = new String[9];
                                    //    }
                                   // }
                                //});
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
        }.execute(param);
    }
}