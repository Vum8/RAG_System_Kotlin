# NHẬT KÝ TIẾN ĐỘ & BÁO CÁO THỰC TẬP - EDURAG MOBILE FE

Tài liệu này dùng để ghi nhận chi tiết quá trình phát triển phần Frontend Mobile (Kotlin Android Native - Jetpack Compose) của dự án **EduRAG**. Tài liệu này sẽ giúp bạn dễ dàng tổng hợp dữ liệu làm báo cáo thực tập cuối kỳ mà không bị bỏ sót thông tin.

---

## 📅 LỊCH SỬ TIẾN ĐỘ ĐÃ THỰC HIỆN

### 🔹 TUẦN 1: Thiết kế UI/UX & Thống nhất luồng hệ thống
* **Thời gian:** [Điền ngày bắt đầu - ngày kết thúc]
* **Nội dung công việc:**
  * **Thiết kế Figma:** Xây dựng bản wireframe và luồng UI hoàn chỉnh (Login/Register, Main Tab, Chat Screen, Library, Document Reader, Profile Screen).
  * **Họp nhóm với Backend (BE):** Phân tích luồng nghiệp vụ RAG. Thống nhất danh sách API, các trường dữ liệu truyền nhận (Request/Response DTOs) và thiết kế API RESTful.
* **Kết quả:** Hoàn thiện bản thiết kế UI/UX và đặc tả API để hai bên song song triển khai.

### 🔹 TUẦN 2: Khởi tạo dự án & Phát triển giao diện (UI Code)
* **Thời gian:** [Điền ngày bắt đầu - ngày kết thúc]
* **Nội dung công việc:**
  * **Setup Base code:** Khởi tạo project Android Native với Kotlin + Jetpack Compose, chia cấu trúc Clean Architecture (`data`, `ui`, `navigation`).
  * **Phát triển giao diện:** Viết code UI tĩnh cho các màn hình theo bản vẽ Figma (Login, ForgotPassword, ChatScreen, LibraryScreen, DocumentReaderScreen, ProfileScreen).
  * **Tối ưu UX:** Cài đặt cơ chế giữ trạng thái tab chat (`MainTabScreen`) để không bị reload lại khi chuyển tab.
* **Kết quả:** Có bộ khung ứng dụng hoàn chỉnh hoạt động mượt mà trên môi trường giả lập/thiết bị thật.

### 🔹 TUẦN 3: Tích hợp API cơ bản & Chuẩn hóa cấu trúc MVVM
* **Thời gian:** [Điền ngày bắt đầu - ngày kết thúc]
* **Nội dung công việc:**
  * **Hạ tầng Network:** Tích hợp Retrofit, xây dựng `TokenManager` và OkHttpClient Interceptor tự động gán Bearer Token.
  * **Xử lý Session:** Viết `BaseRepository` hỗ trợ xử lý ngoại lệ mạng (`safeApiCall`) và bắt lỗi `401 Unauthorized` để phát tín hiệu tự động Logout thông qua `SessionEventBus`.
  * **Tích hợp API:** Kết nối API Đăng nhập, API lấy thông tin người dùng (`Profile`), cập nhật thông tin và đổi mật khẩu; API lấy danh sách tài liệu từ Thư viện và đọc chi tiết văn bản.
  * **Tái cấu trúc code:** Áp dụng mô hình **ViewModel Delegate** (`AuthRouteDelegate`, `DocumentRouteDelegate`) và chuyển đổi các Screen thành **Stateless Compose** để tối ưu hóa hiệu năng render.
* **Kết quả:** Hoàn thiện luồng kết nối API thực tế cho hai phân hệ Xác thực và Thư viện tài liệu.

---

## 📈 KẾ HOẠCH & TIẾN TRÌNH CÁC TUẦN TIẾP THEO

*Gợi ý khung kế hoạch thực tế từ Tuần 4 trở đi. Bạn chỉ cần cập nhật chi tiết những gì đã làm vào đây sau mỗi tuần.*

### 🔹 TUẦN 4: Tích hợp API Chat RAG & Xử lý Upload tài liệu
* **Mục tiêu:** Hoàn thiện luồng cốt lõi của ứng dụng (Hỏi đáp tài liệu và Tải tài liệu lên).
* **Nội dung công việc:**
  * [ ] Tích hợp API gửi câu hỏi lên hệ thống RAG (`ChatRepository`).
  * [ ] Xử lý phản hồi dạng Stream (nếu Backend trả về dữ liệu dạng Server-Sent Events / Text Streaming) giúp hiển thị câu trả lời AI chạy từng từ như ChatGPT.
  * [ ] Tích hợp API Upload file tài liệu (PDF, DOCX) từ thiết bị lên server Backend.
  * [ ] Hiển thị thanh tiến trình (Progress Bar) khi upload file.
* **Kết quả dự kiến:** Hoàn thành luồng kết nối RAG hoàn chỉnh (Upload -> Embedding -> Chat hỏi đáp).

### 🔹 TUẦN 5: Xử lý ngoại lệ, Lưu trữ offline & Đánh giá/Phản hồi
* **Mục tiêu:** Tăng tính ổn định của app khi mất mạng và tích hợp các tính năng phụ trợ.
* **Nội dung công việc:**
  * [ ] Triển khai caching dữ liệu thư viện bằng Database cục bộ (Room DB / DataStore) để người dùng xem được danh sách tài liệu đã tải khi offline.
  * [ ] Tích hợp tính năng Đánh giá ứng dụng (Feedback) và màn hình Giới thiệu.
  * [ ] Xử lý các trường hợp mất kết nối mạng đột ngột khi đang Chat hoặc Upload file (hiển thị UI báo lỗi thân thiện thay vì crash app).
* **Kết quả dự kiến:** App hoạt động ổn định trong mọi điều kiện kết nối và đầy đủ tính năng phụ.

### 🔹 TUẦN 6: Kiểm thử (Testing), Sửa lỗi (Fix bugs) & Đóng gói sản phẩm
* **Mục tiêu:** Đảm bảo ứng dụng chạy mượt mà, không giật lag và xuất file cài đặt APK.
* **Nội dung công việc:**
  * [ ] Chạy kiểm thử thủ công (Manual Testing) trên nhiều thiết bị thật có cấu hình/kích thước màn hình khác nhau để tinh chỉnh UI Responsive.
  * [ ] Profile bộ nhớ (Memory Profiling) để phát hiện và xử lý các vấn đề rò rỉ bộ nhớ (Memory leak) nếu có.
  * [ ] Sửa các lỗi UI phát sinh (lỗi tràn chữ, bàn phím che mất input chat, v.v.).
  * [ ] Build file APK/AAB hoàn chỉnh, viết tài liệu hướng dẫn cài đặt và cấu hình ứng dụng.
* **Kết quả dự kiến:** Đóng gói sản phẩm EduRAG Mobile chạy ổn định, sẵn sàng bàn giao và báo cáo hội đồng thực tập.

---

## 📝 NHẬT KÝ LỖI & GIẢI PHÁP KỸ THUẬT (BUG LOG)
*Nơi ghi lại các lỗi lớn gặp phải và cách giải quyết để làm điểm nhấn trong báo cáo thực tập (Mentor/Cô giáo rất thích phần này).*

| Tuần | Tên lỗi gặp phải | Nguyên nhân | Cách khắc phục / Giải pháp |
| :--- | :--- | :--- | :--- |
| **Tuần 2** | Mất dữ liệu chat khi đổi tab | Compose Screen bị hủy và dựng lại khiến State bị reset về mặc định | Sử dụng `rememberSaveable` kết hợp giữ Instance của ViewModel ở cấp độ MainTab để lưu trữ tin nhắn. |
| **Tuần 3** | Form đăng nhập bị reset khi nhập sai mật khẩu | `safeApiCall` mặc định tự phát sự kiện `Unauthorized` xóa toàn bộ session | Thêm cờ `emitUnauthorizedEvent: Boolean` vào `safeApiCall`, đặt bằng `false` khi gọi API Login để chỉ xử lý báo lỗi tại màn hình Login. |
| **Tuần 3** | Màn hình Screen bị phình to code (>150 lines) | Cả UI Scaffold và các component nhỏ được viết chung một file | Tách biệt các view nhỏ (như Card tài liệu, Bottom Sheet) ra thư mục `ui/components/` riêng. |
| **Tuần ...**| [Tên lỗi tiếp theo] | [Nguyên nhân] | [Giải pháp khắc phục] |
