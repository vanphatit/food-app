package com.phatlee.food_app.Helper;

import android.content.Context;
import android.widget.Toast;

import com.phatlee.food_app.Domain.Foods;

import java.util.ArrayList;

public class ManagmentCart {
    private Context context;
    private TinyDB tinyDB;

    public ManagmentCart(Context context) {
        this.context = context;
        this.tinyDB=new TinyDB(context);
    }

    public void insertFood(Foods item) {
        ArrayList<Foods> listpop = getListCart();
        boolean existAlready = false;
        int n = 0;

        for (int i = 0; i < listpop.size(); i++) {
            if (listpop.get(i).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = i;
                break;
            }
        }

        if (existAlready) {
            listpop.get(n).setNumberInCart(item.getNumberInCart());
        } else {
            // Đảm bảo ImagePath chỉ là tên ảnh, không phải URL Firebase
            String imageName = extractImageName(item.getImagePath());
            item.setImagePath(imageName);
            listpop.add(item);
        }

        tinyDB.putListObject("CartList", listpop);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    private String extractImageName(String imagePath) {
        if (imagePath.contains("/")) {
            String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            fileName = fileName.split("\\?")[0]; // Bỏ phần query (?alt=...)
            fileName = fileName.replace("%20", "_").replace(".jpg", "").replace(".png", "").toLowerCase();
            return fileName;
        }
        return imagePath;
    }

    public ArrayList<Foods> getListCart() {
        return tinyDB.getListObject("CartList");
    }

    public Double getTotalFee(){
        ArrayList<Foods> listItem=getListCart();
        double fee=0;
        for (int i = 0; i < listItem.size(); i++) {
            fee=fee+(listItem.get(i).getPrice()*listItem.get(i).getNumberInCart());
        }
        return fee;
    }
    public void minusNumberItem(ArrayList<Foods> listItem,int position,ChangeNumberItemsListener changeNumberItemsListener){
        if(listItem.get(position).getNumberInCart()==1){
            listItem.remove(position);
        }else{
            listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart()-1);
        }
        tinyDB.putListObject("CartList",listItem);
        changeNumberItemsListener.change();
    }
    public  void plusNumberItem(ArrayList<Foods> listItem,int position,ChangeNumberItemsListener changeNumberItemsListener){
        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart()+1);
        tinyDB.putListObject("CartList",listItem);
        changeNumberItemsListener.change();
    }
}