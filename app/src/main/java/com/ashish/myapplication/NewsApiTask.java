package com.ashish.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class NewsApiTask extends AsyncTask<Integer, Void, String> {

    private final String API_URL = "http://api.nytimes.com/svc/mostpopular/v2/mostemailed/all-sections/1.json?api-key=fa5723452d7d2454cf24a2a3d920012c:10:66680873";
    private final String OFFSET_PARAM = "&offset=";

    protected void onPreExecute() {
    }

    protected String doInBackground(Integer... params) {
        // Do some validation here
        int offset = params[0];

        try {
            URL url = new URL(API_URL + OFFSET_PARAM + offset);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }
}