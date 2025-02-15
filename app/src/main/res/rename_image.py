import os

# Đường dẫn tới thư mục chứa ảnh
folder_path = os.path.join(os.getcwd(), "drawable/food")

# Kiểm tra xem thư mục có tồn tại không
if not os.path.exists(folder_path):
    print(f"❌ Thư mục '{folder_path}' không tồn tại! Hãy kiểm tra lại đường dẫn.")
    exit()

# Đổi tên tất cả file trong thư mục
for filename in os.listdir(folder_path):
    # Kiểm tra nếu file là ảnh (.jpg hoặc .png)
    if filename.lower().endswith(('.jpg', '.png')):
        # Tạo tên mới: thêm "food_" vào đầu, thay khoảng trắng bằng "_", chuyển thành chữ thường
        name, ext = os.path.splitext(filename)  # Tách tên và phần mở rộng
        new_filename = "food_" + name.replace(" ", "_").lower() + ext

        # Đường dẫn cũ và mới
        old_path = os.path.join(folder_path, filename)
        new_path = os.path.join(folder_path, new_filename)

        # Nếu tên file mới khác tên file cũ thì đổi tên
        if old_path != new_path:
            os.rename(old_path, new_path)
            print(f"✅ Đã đổi: {filename} → {new_filename}")

print("🎉 Hoàn tất! Tất cả file ảnh trong 'res/drawable/food' đã được đổi tên đúng chuẩn.")