package com.phatlee.food_app.Helper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class GeminiAPI {
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    public static final String API_KEY = "AIzaSyABJ0Tkjd4GHyCGTaCiWZDy6rWHnUAB2Go"; // <== Dán key ở đây

    public interface GeminiService {
        @POST("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent")
        Call<GeminiResponse> generateContent(@Query("key") String apiKey, @Body GeminiRequest body);
    }

    public static GeminiService createService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GeminiService.class);
    }

    // Request
    public static class GeminiRequest {
        public List<Content> contents = new ArrayList<>();
        public GeminiRequest(String text) {
            contents.add(new Content(new Part(text)));
        }
    }

    public static class Content {
        public String role = "user"; // 👈 Bổ sung role
        public List<Part> parts;

        public Content(Part part) {
            this.parts = new ArrayList<>();
            this.parts.add(part);
        }
    }

    public static class Part {
        public String text;
        public Part(String text) {
            this.text = text;
        }
    }

    // Response
    public static class GeminiResponse {
        public List<Candidate> candidates;
    }

    public static class Candidate {
        public Content content;
    }
}

