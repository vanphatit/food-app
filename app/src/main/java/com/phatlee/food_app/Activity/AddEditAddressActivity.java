package com.phatlee.food_app.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.phatlee.food_app.Entity.Address;
import com.phatlee.food_app.R;
import com.phatlee.food_app.Repository.AddressRepository;

public class AddEditAddressActivity extends AppCompatActivity {
    EditText nameEdt, phoneEdt, detailEdt;
    SwitchCompat defaultSwitch;
    Button saveBtn;
    Address editing = null;
    AddressRepository repo = new AddressRepository();
    String userId = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);

        nameEdt = findViewById(R.id.nameEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        detailEdt = findViewById(R.id.detailEdt);
        defaultSwitch = findViewById(R.id.defaultSwitch);
        saveBtn = findViewById(R.id.saveBtn);

        if (getIntent().hasExtra("editAddress")) {
            editing = (Address) getIntent().getSerializableExtra("editAddress");
            nameEdt.setText(editing.getName());
            phoneEdt.setText(editing.getPhone());
            detailEdt.setText(editing.getDetail());
            defaultSwitch.setChecked(editing.isDefault());
        }

        saveBtn.setOnClickListener(v -> {
            Address a = editing != null ? editing : new Address();
            a.setUserId(userId);
            a.setName(nameEdt.getText().toString());
            a.setPhone(phoneEdt.getText().toString());
            a.setDetail(detailEdt.getText().toString());
            a.setDefault(defaultSwitch.isChecked());

            repo.saveAddress(a, new AddressRepository.OnSaveCallback() {
                @Override public void onSuccess() {
                    finish();
                }

                @Override public void onFail() {
                    Toast.makeText(getApplicationContext(), "Lưu thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
