package com.example.fuel_cell_mes_mobile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpMgrThread extends Thread{
    public HttpMgrThread(){
    }
    @Override
    public void run(){
        reqHttp();
    }

    public void reqHttp(){
        URL url = null;
        try{
            url = new URL("http://192.168.0.6");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
