package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static Button button1, button2, button3, button4;
    static ImageView celebImageView;
    static ArrayList<String> celebImageUrls = new ArrayList<>();
    static ArrayList<String> celebName = new ArrayList<>();
    static int selectedCeleb = 0;
    //String[] answers = new String[4];
    static String[] answers = new String[4];
    static int locationOfCorrectAnswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        celebImageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.btn1);
        button2 = findViewById(R.id.btn2);
        button3 = findViewById(R.id.btn3);
        button4 = findViewById(R.id.btn4);

        DownloadTask task = new DownloadTask();
        String result = null;
        try {
            result = task.execute("https://www.imdb.com/list/ls052283250/").get();
            String[] splitString = result.split("<div class=\"lister-list\">");

            Pattern pattern = Pattern.compile("src=\"(.*?)\"");
            Matcher matcher = pattern.matcher(splitString[1]);

            while (matcher.find()) {
                celebImageUrls.add(matcher.group(1));
            }
            pattern = Pattern.compile("alt=\"(.*?)\"");
            matcher = pattern.matcher(splitString[1]);

            while (matcher.find()) {
                celebName.add(matcher.group(1));
            }


        }
        catch (Exception e) {
            e.printStackTrace();
        }
        newQuestion();


/*
        Log.i("ImageURLS", Arrays.toString(new ArrayList[]{celebImageUrls}));
        Log.i("ImageURLSNames", Arrays.toString(new ArrayList[]{celebName}));
*/
    }

    public void chooseCeleb(View view) {
        try {
            if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer + 1))) {
                Toast.makeText(MainActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Wrong! It was " + answers[locationOfCorrectAnswer], Toast.LENGTH_SHORT).show();
            }
            newQuestion();
        }
        catch (Exception e) {
            try {
                newQuestion();
            }
            catch (Exception ee) {
                newQuestion();
            }

        }
    }

    static void newQuestion() {
        Random random = new Random();
        selectedCeleb = random.nextInt(celebImageUrls.size());

        ImageDownloader imageDownloader = new ImageDownloader();
        try {
            Bitmap selectedCelebImage = imageDownloader.execute(celebImageUrls.get(selectedCeleb)).get();
            celebImageView.setImageBitmap(selectedCelebImage);
        }
        catch (Exception e) {

        }
        int incorrectLocation;
        locationOfCorrectAnswer = random.nextInt(4);
        for (int i = 0; i < 4; i++) {
            if (i == locationOfCorrectAnswer) {
                answers[i] = celebName.get(selectedCeleb);

            }
            else {
                incorrectLocation = random.nextInt(celebName.size());
                while (incorrectLocation == locationOfCorrectAnswer) {
                    incorrectLocation = random.nextInt(celebName.size());
                }
                answers[i] = celebName.get(incorrectLocation);
            }
        }
        button1.setText(answers[0]);
        button2.setText(answers[1]);
        button3.setText(answers[2]);
        button4.setText(answers[3]);
    }
}

class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            urlConnection.connect();
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}


class DownloadTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder result = new StringBuilder();
        URL url;
        HttpsURLConnection urlConnection;

        try {
            url = new URL(strings[0]);
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            int data = reader.read();
            while (data != -1) {
                char current = (char) data;
                result.append(current);
                data = reader.read();
            }
            return result.toString();
        } catch (Exception e) {
            //Toast.makeText(DownloadTask.this, "Exception in Download Task Class!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return "Tejkiran";
        }
    }
}