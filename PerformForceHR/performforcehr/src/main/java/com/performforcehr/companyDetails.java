package com.performforcehr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class companyDetails extends Activity {

    Intent intent;
    String pdf;
    JSONObject jObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_company_details);

    }

    /** Called when the user clicks the Send button */
    public void sendCode(View view) {
        pdf = "";
        String url = "https://fierce-earth-5818.herokuapp.com/handbook/";
        intent = new Intent(this, displayHandbook.class);
        // Do something in response to button
        EditText editText = (EditText) findViewById(R.id.code);
        String message = editText.getText().toString();
        url += message;

        Log.i("Starting Downloading Json from", url);
        if(isNetworkAvailable()){
            getJSONFromUrl task = new getJSONFromUrl();
            task.execute( url );
            Log.i("Task Exectued on ",url);
        }else{
            Toast networkwarning = Toast.makeText(this.getApplicationContext(),"Internet Not Available",3);
            networkwarning.show();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class getJSONFromUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                Log.i("HTTP REQUEST SENT AND RECEIVED",url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                    Log.i("JSON IS ",response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                jObj = new JSONObject(result);
                pdf = ("http://docs.google.com/gview?embedded=true&url="+jObj.getString("link"));
                Log.i("PDF URL",pdf);
            }catch (JSONException e){e.printStackTrace();}
            intent.putExtra("url", pdf);
            startActivity(intent);
        }
    }
}