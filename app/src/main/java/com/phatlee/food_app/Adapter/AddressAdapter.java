package com.phatlee.food_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phatlee.food_app.Entity.Address;
import com.phatlee.food_app.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.Holder> {
    List<Address> list;
    OnSelectListener onSelect;
    OnEditListener onEdit;
    OnDeleteListener onDelete;

    public AddressAdapter(List<Address> list, OnSelectListener onSelect, OnEditListener onEdit, OnDeleteListener onDelete) {
        this.list = list;
        this.onSelect = onSelect;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
    }

    public interface OnSelectListener { void onSelect(Address a); }
    public interface OnEditListener { void onEdit(Address a); }
    public interface OnDeleteListener { void onDelete(Address a); }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int i) {
        Address a = list.get(i);
        h.addressTxt.setText(a.getFullAddress());
        h.nameTxt.setText(a.getName() + " - " + a.getPhone());
        h.itemView.setOnClickListener(v -> onSelect.onSelect(a));
        h.editBtn.setOnClickListener(v -> onEdit.onEdit(a));
        h.deleteBtn.setOnClickListener(v -> onDelete.onDelete(a));
    }

    @Override public int getItemCount() { return list.size(); }

    class Holder extends RecyclerView.ViewHolder {
        TextView addressTxt, nameTxt;
        ImageButton editBtn, deleteBtn;
        Holder(@NonNull View v) {
            super(v);
            addressTxt = v.findViewById(R.id.addressTxt);
            nameTxt = v.findViewById(R.id.nameTxt);
            editBtn = v.findViewById(R.id.editBtn);
            deleteBtn = v.findViewById(R.id.deleteBtn);
        }
    }
}
