# 🍽️ Canteen Food Ordering System (User + Admin Android Apps)

This project is a dual-app Android-based food ordering system built with Kotlin. It includes two separate apps:

- 📱 **User App**: For students/teachers to browse menu items and place food orders.
- 🛠️ **Admin App**: For canteen staff to manage the menu and view incoming orders.

Both apps are built using **Kotlin** and **XML**, and integrate **Firebase services** for real-time data handling and user authentication.

---

## 🔧 Tech Stack

- **Language**: Kotlin
- **UI**: XML
- **Backend**: Firebase
  - Firebase Authentication
  - Realtime Database
  - Firestore
- **Libraries Used**:
  - Glide (for image loading)
 
- **Some other functionalities**:   
  - RecyclerView (for list displays)
  - Custom Adapters
  - Bottom Navigation View

---

## ✨ Features

### 👤 User App:
- Sign up / Log in with Firebase Authentication
- Browse menu items (images, titles, prices)
- Place orders via Firebase Realtime Database
- View order history and status

### 🧑‍🍳 Admin App:
- Admin login system
- View live incoming orders
- Add/update/remove menu items (Firestore)
- View order history
