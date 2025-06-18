package com.example.philatelia.helpers;

import android.util.Log;

import com.example.philatelia.BuildConfig;
import com.example.philatelia.models.deepseek.ChatRequest;
import com.example.philatelia.models.deepseek.ChatResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.concurrent.TimeUnit;

public class AIHelper {
    private static final String TAG = "AIHelper";
    // Ключ будет считываться из BuildConfig
    private static final String API_KEY = BuildConfig.MISTRAL_API_KEY; 
    private static final String BASE_URL = "https://api.deepinfra.com/v1/openai/";

    private static MistralService mistralService;

    public AIHelper() {
        if (mistralService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Bearer " + API_KEY)
                                .header("Content-Type", "application/json");
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    })
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mistralService = retrofit.create(MistralService.class);
        }
    }

    public String getResponse(String userMessage) {
        try {
            ChatRequest.Message systemMessage = new ChatRequest.Message("system", "Ты — дружелюбный эксперт-филателист. Твоя роль — предоставлять подробную и точную информацию о почтовых марках, их истории, дизайне и ценности. Всегда отвечай на русском языке. Будь готов помочь и предложить интересные темы для разговора, если пользователь не знает, о чем спросить.");
            ChatRequest.Message userMsg = new ChatRequest.Message("user", userMessage);
            
            ChatRequest requestBody = new ChatRequest("mistralai/Mistral-7B-Instruct-v0.2", java.util.Arrays.asList(systemMessage, userMsg), false);

            Call<ChatResponse> call = mistralService.getChatResponse(requestBody);
            Response<ChatResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                String content = response.body().getFirstChoiceContent();
                if (content != null) {
                    return content;
                }
                return "Ошибка: Модель не вернула контент.";
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "нет тела ошибки";
                Log.e(TAG, "Ошибка от сервера: " + errorBody);
                return "Ошибка: Сервер вернул HTTP " + response.code();
            }

        } catch (Exception e) {
            Log.e(TAG, "Ошибка в getResponse: " + e.getMessage(), e);
            return "Ошибка при подключении к API: " + e.getMessage();
        }
    }

    public interface MistralService {
        @POST("chat/completions")
        Call<ChatResponse> getChatResponse(@Body ChatRequest requestBody);
    }
}

