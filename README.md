# EduRAG System - Android Mobile App

EduRAG là một ứng dụng di động Android Native được thiết kế dựa trên kiến trúc **RAG (Retrieval-Augmented Generation)** giúp người dùng tra cứu, hỏi đáp thông minh và đọc các tài liệu giáo dục trực quan. Dự án được phát triển bằng **Jetpack Compose** kết hợp với kiến trúc **MVVM (Model-View-ViewModel)** chuẩn hóa.

---

## 🚀 Tính Năng Chính

- **Hệ thống Xác thực (Auth Flow)**: Hỗ trợ Đăng nhập bằng Email, Đăng nhập qua Google và khôi phục mật khẩu.
- **Trò chuyện cùng AI (Chat System)**: Giao diện Chatbot thông minh hỗ trợ trả lời dựa trên ngữ cảnh tài liệu từ hệ thống RAG.
- **Thư viện Tài liệu (Document Library)**: Nơi hiển thị và quản lý danh sách tài liệu học tập của người dùng.
- **Trình đọc Tài liệu Thông minh (Smart Document Reader)**:
  - Cho phép đọc tài liệu chi tiết chia theo từng trang.
  - Tích hợp **Document Reader BottomSheet** hỗ trợ xem nhanh thông tin, tóm tắt và ghi chú tài liệu ngay trong màn hình Chat.
- **Lịch sử Hội thoại (Chat History)**: Lưu trữ các phiên hỏi đáp trước đó để người dùng dễ dàng xem lại.
- **Quản lý Tài khoản (User Profile)**: Quản lý thông tin cá nhân và xử lý đăng xuất an toàn.

---

## 🏛️ Kiến Trúc Hệ Thống (Clean MVVM)

Hệ thống tuân thủ nghiêm ngặt mô hình thiết kế **MVVM + Delegates** và các quy tắc phát triển di động chất lượng cao:

### 1. Phân Lớp Kiến Trúc Cốt Lõi
- **Stateless UI**: Các file màn hình tại thư mục `ui/screens/` chỉ chứa bố cục khung (`Scaffold`/`Layout`), không lưu giữ trạng thái hoặc xử lý logic nghiệp vụ. Trạng thái giao diện được truyền từ ViewModel dưới dạng thô hoặc UI Model, các tương tác được truyền đi qua các hàm callback Lambda (`onAction`).
- **ViewModel Coordinator / Route Delegates**: Tránh tình trạng ViewModel quá lớn (**Anti-Monolith**). Các logic nghiệp vụ lớn hoặc các tuyến API được phân chia và xử lý trong các lớp phụ trợ `*RouteDelegate`. ViewModel chính đóng vai trò điều phối tổng thể.
- **Component Modularization (Tách biệt thành phần)**: Tất cả các thành phần nhỏ có tính độc lập và tái sử dụng cao (như thanh nhập liệu, Form đăng nhập, danh sách chat, bottom sheet đọc tài liệu) đều được tách thành các tệp tin riêng biệt đặt trong thư mục `ui/components/`.
- **Xử lý Dữ liệu & Lỗi Tập Trung**:
  - Toàn bộ lời gọi API được bọc trong hàm `safeApiCall` thuộc `BaseRepository` để đảm bảo ứng dụng không bị crash khi mất mạng hoặc lỗi server.
  - Trạng thái bất đồng bộ được quản lý rõ ràng bằng `UiLoadState` (Idle, Loading, Success, Error, Empty).
  - Tích hợp `SessionEventBus` (sử dụng Kotlin Flow) để phát tín hiệu đăng xuất tự động chuyển về màn hình `Login` ngay khi API phản hồi lỗi Token hết hạn (401 - Unauthorized).

---

## 📂 Cấu Trúc Thư Mục Dự Án

```text
[rag_system]
 ├── data
 │    ├── api          # Cấu hình Retrofit, API Services, DTOs & DI Helpers
 │    ├── repository   # Các Repositories xử lý nghiệp vụ (Mock/Remote) thừa kế BaseRepository
 │    └── session      # SessionEventBus quản lý vòng đời phiên đăng nhập (401 Unauthorized)
 ├── navigation
 │    ├── Screen.kt    # Định nghĩa các tuyến đường (Routes) và tham số truyền nhận
 │    └── AppNav.kt    # Hệ thống điều hướng gốc NavHost của ứng dụng
 └── ui
      ├── theme        # Cấu hình màu sắc, Typography, Shape cho Jetpack Compose
      ├── components   # Các UI Components nhỏ có thể tái sử dụng (LoginForm, ChatHistoryList, BottomSheet...)
      ├── models       # Các UI Model sạch chuyên biệt cho tầng hiển thị
      ├── screens      # Chứa bố cục UI chính (auth: Login, Register / user: Chat, Library, Reader...)
      ├── state        # Định nghĩa các trạng thái tải bất đồng bộ (UiLoadState)
      └── viewmodels   # Các ViewModel điều phối và giữ trạng thái màn hình
```

---

## 🛠️ Tech Stack & Thư Viện Sử Dụng

- **Ngôn ngữ**: Kotlin (1.9+)
- **Bộ công cụ UI**: Jetpack Compose (Declarative UI)
- **Kiến trúc**: MVVM + Coroutines & Flow (StateFlow, SharedFlow)
- **Điều hướng**: Navigation Compose
- **Network**: Retrofit & OkHttp (cho các kết nối API)
- **Dependency Injection**: Hilt / Dagger (hoặc Constructor Injection trực tiếp thông qua Composable)

---

## ⚙️ Hướng Dẫn Cài Đặt & Biên Dịch

### Yêu cầu hệ thống:
- Android Studio Iguana / Jellyfish trở lên.
- JDK 17.
- Android SDK API 26 (Android 8.0) trở lên.

### Lệnh biên dịch nhanh qua Terminal:

Để kiểm tra xem code có sạch và không bị lỗi biên dịch Kotlin hay không:
```powershell
# Chạy lệnh gradle wrapper để compile code Kotlin Debug
.\gradlew.bat compileDebugKotlin
```

Để dọn dẹp và rebuild dự án hoàn toàn:
```powershell
.\gradlew.bat clean build
```

---

## 📝 Quy Tắc Phát Triển Cho Developer
1. **Tuyệt đối cấm viết gộp (Anti-Monolith)**: Bất kỳ thành phần giao diện nhỏ nào có khả năng tái sử dụng hoặc hiển thị độc lập phải được tách ra thư mục `ui/components/`. File Screen chính chỉ thiết lập khung chứa.
2. **Giữ UI Stateless**: Màn hình Compose chỉ được nhận trạng thái và phát sự kiện ra ngoài. Logic và API call phải nằm trong ViewModel hoặc Delegate.
3. **Luôn chạy biên dịch kiểm thử**: Trước khi push code lên Git, bắt buộc phải chạy lệnh biên dịch `.\gradlew.bat compileDebugKotlin` để đảm bảo code sạch lỗi.
