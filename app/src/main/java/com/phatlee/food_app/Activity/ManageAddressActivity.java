package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.phatlee.food_app.Adapter.AddressAdapter;
import com.phatlee.food_app.Entity.Address;
import com.phatlee.food_app.R;
import com.phatlee.food_app.Repository.AddressRepository;

import java.util.ArrayList;
import java.util.List;

public class ManageAddressActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton addBtn;
    AddressRepository repo = new AddressRepository();
    List<Address> list = new ArrayList<>();
    AddressAdapter adapter;
    String userId = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);

        recyclerView = findViewById(R.id.recyclerAddress);
        addBtn = findViewById(R.id.btnAddAddress);
        adapter = new AddressAdapter(list, this::onAddressSelected, this::onEdit, this::onDelete);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditAddressActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        repo.getAddresses(userId, addresses -> {
            list.clear();
            list.addAll(addresses);
            adapter.notifyDataSetChanged();
        });
    }

    private void onAddressSelected(Address address) {
        Intent intent = new Intent();
        intent.putExtra("addressId", address.getId());
        intent.putExtra("fullAddress", address.getFullAddress());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onEdit(Address a) {
        Intent i = new Intent(this, AddEditAddressActivity.class);
        i.putExtra("editAddress", a);
        startActivity(i);
    }

    private void onDelete(Address a) {
        repo.deleteAddress(a.getId(), new AddressRepository.OnDeleteCallback() {
            @Override public void onSuccess() { onResume(); }
            @Override public void onFail() { Toast.makeText(getApplicationContext(), "Xoá thất bại", Toast.LENGTH_SHORT).show(); }
        });
    }
}