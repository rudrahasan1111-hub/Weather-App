package com.example.weatherapp100c;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText editTextCity;
    Button buttonGetWeather;
    TextView textViewResult;

    OkHttpClient client = new OkHttpClient();

    // TODO: Replace with your OpenWeatherMap API Key
    String API_KEY = "17613027a611f2904999680cb2e8c6f6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        buttonGetWeather = findViewById(R.id.buttonGetWeather);
        textViewResult = findViewById(R.id.textViewResult);

        buttonGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    fetchWeatherData(city);
                }
            }
        });
    }

    private void fetchWeatherData(String city) {
        // Example: Using Forecast API (you can replace with Air Pollution API too)
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + API_KEY + "&units=metric";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> textViewResult.setText("Failed to connect"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    parseJSONData(jsonData);
                } else {
                    runOnUiThread(() -> textViewResult.setText("Failed to get data"));
                }
            }
        });
    }

    private void parseJSONData(String jsonData) {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        JsonArray listArray = jsonObject.getAsJsonArray("list");

        if (listArray.size() > 0) {
            JsonObject firstItem = listArray.get(0).getAsJsonObject();
            JsonObject mainObject = firstItem.getAsJsonObject("main");

            double temp = mainObject.get("temp").getAsDouble();
            double feelsLike = mainObject.get("feels_like").getAsDouble();

            runOnUiThread(() -> {
                String result = "Temp: " + temp + "°C\nFeels Like: " + feelsLike + "°C";
                textViewResult.setText(result);
            });
        } else {
            runOnUiThread(() -> textViewResult.setText("No weather data found"));
        }
    }
}
