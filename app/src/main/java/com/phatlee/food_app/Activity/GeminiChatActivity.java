package com.phatlee.food_app.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.phatlee.food_app.Adapter.BestFoodsAdapter;
import com.phatlee.food_app.Database.FoodsDaoFirestore;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Helper.GeminiAPI;
import com.phatlee.food_app.R;
import com.phatlee.food_app.Repository.FoodsRepository;
import com.phatlee.food_app.Adapter.FoodListAdapter; // hoặc BestFoodsAdapter nếu bạn có

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeminiChatActivity extends BaseActivity {
    TextView resultTxt;
    EditText inputEdt;
    Button sendBtn;
    RecyclerView suggestionList;
    FoodsRepository foodsRepository;

    List<Foods> suggestedFoods = new ArrayList<>();
    BestFoodsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gemini_chat);

        resultTxt = findViewById(R.id.resultTxt);
        inputEdt = findViewById(R.id.inputEdt);
        sendBtn = findViewById(R.id.sendBtn);
        suggestionList = findViewById(R.id.suggestionList);

        foodsRepository = new FoodsRepository();
        adapter = new BestFoodsAdapter(suggestedFoods);
        suggestionList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        suggestionList.setAdapter(adapter);

        sendBtn.setOnClickListener(v -> {
            String userPrompt = inputEdt.getText().toString().trim();
            if (!userPrompt.isEmpty()) {
                resultTxt.setText("Thinking...\n");
                getFoodsAndSendPrompt(userPrompt);
            }
        });

        // Ánh xạ thanh công cụ
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> {
            finish(); // Hoặc: startActivity(new Intent(this, MainActivity.class));
        });

    }

    private void getFoodsAndSendPrompt(String userPrompt) {
        foodsRepository.getAllFoods(new FoodsDaoFirestore.OnFoodsLoadedListener() {
            @Override
            public void onFoodsLoaded(List<Foods> foods) {
                String prompt = buildPromptWithFoods(userPrompt, foods.subList(0, Math.min(15, foods.size())));
                sendToGemini(prompt);
            }

            @Override
            public void onFailure(Exception e) {
                resultTxt.setText("Lỗi khi lấy danh sách món ăn.");
            }
        });
    }

    private String buildPromptWithFoods(String userPrompt, List<Foods> foods) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tôi có danh sách các món ăn sau:\n\n");
        for (int i = 0; i < foods.size(); i++) {
            Foods f = foods.get(i);
            sb.append((i + 1)).append(". ")
                    .append(f.getTitle()).append(" – ")
                    .append(f.getDescription()).append("\n");
        }
        sb.append("\nNgười dùng hỏi: \"").append(userPrompt).append("\"\n");
        sb.append("Dựa trên danh sách trên, hãy gợi ý 2–3 món phù hợp. ");
        sb.append("Nếu không có món nào phù hợp, hãy nói rõ: \"Không có món phù hợp trong danh sách\".");
        return sb.toString();
    }

    private void sendToGemini(String finalPrompt) {
        GeminiAPI.GeminiService service = GeminiAPI.createService();
        GeminiAPI.GeminiRequest request = new GeminiAPI.GeminiRequest(finalPrompt);

        Log.d("GeminiChat", "➡️ Prompt gửi lên:\n" + finalPrompt);

        service.generateContent(GeminiAPI.API_KEY, request).enqueue(new Callback<GeminiAPI.GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiAPI.GeminiResponse> call, Response<GeminiAPI.GeminiResponse> response) {
                Log.d("GeminiChat", "✅ Response status: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String reply = response.body().candidates.get(0).content.parts.get(0).text;
                    Log.d("GeminiChat", "✅ Reply:\n" + reply);
                    // remove the text from "Vậy câu trả lời là: " to end
                    int index = reply.indexOf("Vậy câu trả lời là: ");
                    if (index != -1) {
                        reply = reply.substring(0, index);
                    }

                    resultTxt.setText(reply);
                    filterSuggestedFoodsFromReply(reply);
                } else {
                    resultTxt.setText("Gemini không phản hồi hợp lệ.");
                }
            }

            @Override
            public void onFailure(Call<GeminiAPI.GeminiResponse> call, Throwable t) {
                resultTxt.setText("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void filterSuggestedFoodsFromReply(String reply) {
        foodsRepository.getAllFoods(new FoodsDaoFirestore.OnFoodsLoadedListener() {
            @Override
            public void onFoodsLoaded(List<Foods> allFoods) {
                suggestedFoods.clear();
                String lowerReply = reply.toLowerCase();

                for (Foods f : allFoods) {
                    if (lowerReply.contains(f.getTitle().toLowerCase())) {
                        suggestedFoods.add(f);
                    }
                }

                runOnUiThread(() -> {
                    if (suggestedFoods.isEmpty()) {
                        suggestionList.setVisibility(View.GONE);
                        resultTxt.append("\n⚠️ Không tìm thấy món phù hợp trong danh sách.");
                    } else {
                        adapter.notifyDataSetChanged();
                        suggestionList.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> resultTxt.append("\n❌ Không thể lọc món gợi ý."));
            }
        });
    }
}
