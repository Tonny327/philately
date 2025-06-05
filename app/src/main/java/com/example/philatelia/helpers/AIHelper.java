package com.example.philatelia.helpers;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class AIHelper {
    private static final String TAG = "AIHelper";
    private static final String API_KEY = "AIzaSyDaaamRpeOQsI_VzyU1g_WmdpXz_AQc_CI"; // Замените на свой ключ
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1/";

    // 🔹 ДАННЫЕ ДЛЯ ПРОКСИ
    private static final String PROXY_HOST = "157.245.95.247"; // IP-адрес прокси
    private static final int PROXY_PORT = 443;
    private static GeminiService geminiService;

    public AIHelper() {
        if (geminiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 🔹 Настройка прокси (без аутентификации)
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));

            OkHttpClient client = new OkHttpClient.Builder()
                    .proxy(proxy) // Указываем прокси
                    .addInterceptor(logging) // Логирование запросов
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            geminiService = retrofit.create(GeminiService.class);
            Log.d("AIHelper", "Подключение через прокси: " + PROXY_HOST + ":" + PROXY_PORT);

        }
    }


    public String getResponse(String userMessage) {
        try {
            JsonObject requestBody = new JsonObject();
            JsonObject contents = new JsonObject();
            JsonArray partsArray = new JsonArray();
            JsonObject textObj = new JsonObject();

            textObj.addProperty("text", userMessage);
            partsArray.add(textObj);

            contents.add("parts", partsArray);
            JsonArray contentsArray = new JsonArray();
            contentsArray.add(contents);

            requestBody.add("contents", contentsArray);

            // Логируем JSON перед отправкой
            Log.d("AIHelper", "Отправляем JSON в Gemini: " + requestBody.toString());

            Call<JsonObject> call = geminiService.getChatResponse(API_KEY, requestBody);
            Response<JsonObject> response = call.execute();

            // Логируем HTTP-код ответа
            Log.d("AIHelper", "HTTP-код ответа: " + response.code());

            if (response.isSuccessful() && response.body() != null) {
                JsonObject responseBody = response.body();
                Log.d("AIHelper", "Ответ от Gemini API: " + responseBody.toString());

                if (responseBody.has("candidates")) {
                    JsonArray candidates = responseBody.getAsJsonArray("candidates");
                    if (candidates.size() > 0) {
                        JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
                        JsonArray parts = content.getAsJsonArray("parts");
                        if (parts.size() > 0) {
                            return parts.get(0).getAsJsonObject().get("text").getAsString();
                        }
                    }
                }
                return "Ошибка: Gemini не вернул ожидаемый ответ. Ответ: " + responseBody;
            } else {
                if (response.errorBody() != null) {
                    Log.e(TAG, "Ошибка от сервера: " + response.errorBody().string());
                }
                return "Ошибка: Сервер вернул HTTP " + response.code();
            }

        } catch (Exception e) {
            Log.e("AIHelper", "Ошибка в getResponse: " + e.getMessage(), e);
            return "Ошибка при подключении к Gemini: " + e.getMessage();
        }
    }



    // Интерфейс для работы с API
    public interface GeminiService {
        @Headers("Content-Type: application/json")
        @POST("models/gemini-pro:generateContent")
        Call<JsonObject> getChatResponse(
                @Query("key") String apiKey,
                @Body JsonObject requestBody
        );
    }
}

