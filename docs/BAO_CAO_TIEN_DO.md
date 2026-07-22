# NHẬT KÝ TIẾN ĐỘ & BÁO CÁO THỰC TẬP - EDURAG MOBILE FE

Tài liệu này ghi nhận chi tiết và trung thực quá trình phát triển ứng dụng di động phía Sinh viên (Client) bằng ngôn ngữ **Kotlin Android Native (Jetpack Compose)** của dự án **EduRAG**. Các mốc thời gian được tính thực tế bắt đầu từ ngày **29/06/2026**.

---

## 📅 NHẬT KÝ CHI TIẾT HÀNG NGÀY (ĐÃ THỰC HIỆN)

### 🔹 TUẦN 1: Nghiên cứu nghiệp vụ RAG & Thảo luận Định hướng dự án
*(Thời gian: 29/06/2026 - 03/07/2026)*

*   **Thứ 2 (29/06/2026):** 
    *   Khảo sát tổng quan về nghiệp vụ RAG (Retrieval-Augmented Generation) và mô hình hoạt động của hệ thống hỏi đáp thông minh dựa trên ngữ cảnh tài liệu học tập.
    *   Phân tích các yêu cầu kỹ thuật cần có cho client di động chuyên biệt dành cho sinh viên.
*   **Thứ 3 (30/06/2026):** 
    *   **Họp nhóm cả đội dự án:** Thảo luận chi tiết về bài toán RAG và một số bài toán/định hướng công nghệ khác để đưa ra quyết định cuối cùng về phạm vi của dự án. Đồng thời phân chia rõ ràng vai trò công việc cho từng thành viên (Backend, Web Frontend, Mobile Frontend).
*   **Thứ 4 (01/07/2026) - Thứ 6 (03/07/2026):** 
    *   Nghiên cứu các giải pháp công nghệ trên nền tảng di động Android (Jetpack Compose, Clean Architecture, Kotlin Coroutines).
    *   Phân tích cấu trúc dữ liệu lưu trữ và định hình trước các sơ đồ màn hình của Client sinh viên để chuẩn bị bước vào giai đoạn thiết kế UI/UX.

---

### 🔹 TUẦN 2: Thiết kế UI/UX Figma, Khởi tạo codebase & Dựng giao diện thô
*(Thời gian: 06/07/2026 - 10/07/2026)*

*   **Chủ Nhật (05/07/2026):**
    *   Bắt đầu quá trình thiết kế giao diện chi tiết (UI/UX) cho ứng dụng di động trên Figma (luồng Xác thực, Chatbot RAG, Thư viện).
*   **Thứ 2 (06/07/2026):**
    *   Hoàn thiện toàn bộ các bản thiết kế Figma (bao gồm lựa chọn hệ màu Brand Blue, thiết kế các nút bấm, ô nhập liệu, bong bóng chat) trước buổi họp nhóm tiếp theo.
*   **Thứ 3 (07/07/2026):**
    *   **Họp nhóm cả đội dự án:** Bàn thảo về kế hoạch triển khai chi tiết sau khi đã chốt quyết định cuối cùng, phân định rõ những bước khởi đầu để bắt tay vào lập trình.
*   **Thứ 4 (08/07/2026) - Thứ 6 (10/07/2026):**
    *   Khởi tạo dự án Android Native sử dụng Kotlin và Jetpack Compose.
    *   Thiết lập cấu trúc thư mục Clean Architecture chuẩn của dự án di động: `.data` (api, repository, preferences), `.ui` (theme, components, screens, viewmodels), và `.navigation`.
    *   Xây dựng giao diện thô và điều hướng cơ bản giữa các màn hình Đăng nhập (`LoginScreen`), Màn hình Chat (`ChatScreen`), và Thư viện (`LibraryScreen`).

---

### 🔹 TUẦN 3: Thiết lập Network Layer, Tích hợp API Xác thực, Profile & Thư viện
*(Thời gian: 13/07/2026 - 17/07/2026)*

*   **Chủ Nhật (12/07/2026):**
    *   **Họp tiến độ với anh Quyền:** Các phân hệ (Backend, Web, Mobile) báo cáo tiến độ hiện tại. Phía Mobile đặt câu hỏi làm rõ các phần liên quan đến kết nối API backend và cấu trúc database. Do phía Mobile mới trong giai đoạn code base app và mockdata nên chưa có nhiều vấn đề để hỏi chuyên sâu.
*   **Thứ 2 (13/07/2026):**
    *   Cài đặt Retrofit Client, cấu hình `TokenManager` sử dụng EncryptedSharedPreferences để lưu trữ JWT Token an toàn dưới local. 
    *   Viết Interceptor tự động đính kèm Token xác thực vào các API được bảo mật.
*   **Thứ 3 (14/07/2026):**
    *   **Họp nhóm cả đội dự án:** Thống nhất chốt những tính năng không làm trong hệ thống (như loại bỏ tính năng upload tài liệu phía client sinh viên trên mobile). Tiến hành nghiệm thu một số phần công việc của các thành viên để kiểm tra hiệu suất và trao đổi thắc mắc còn tồn đọng liên quan đến API contract.
*   **Thứ 4 (15/07/2026) - Thứ 6 (17/07/2026):**
    *   Tích hợp nhóm API Đăng nhập và xem thông tin cá nhân (`ProfileScreen`).
    *   Tích hợp API lấy danh sách tài liệu trong Thư viện (`LibraryScreen`) và màn hình đọc trang giáo trình (`DocumentReaderScreen`).
    *   Áp dụng mô hình **ViewModel Route Delegate** giúp quản lý State sạch sẽ và tối ưu hóa hiệu năng render.

---

### 🔹 TUẦN 4: Tích hợp API Chat RAG, Xử lý Citation & Hoàn thiện luồng Đăng nhập/Đăng ký
*(Thời gian: 20/07/2026 - 26/07/2026)*

*   **Thứ 2 (20/07/2026):**
    *   Tích hợp API gửi câu hỏi lên hệ thống RAG (`ChatRepository` kết nối `ChatApiService`).
    *   Đồng bộ cơ chế Idempotency bằng cách tự sinh UUID `clientRequestId` ở phía Client để truyền lên body gửi tin nhắn, tránh việc gửi trùng lặp câu hỏi khi kết nối mạng không ổn định.
*   **Thứ 3 (21/07/2026):**
    *   **Họp nhóm cả đội dự án:** Phía Backend hướng dẫn các thông tin liên quan đến API Key, cấu hình chạy thử, và các chức năng backend đã hoàn thiện. Phía Frontend Web và Mobile tiến hành **pull dự án backend (`RAG_Be`)** về máy cục bộ để rà soát các cập nhật mới liên quan đến giao diện client sinh viên và chuẩn bị kết nối trực tiếp.
*   **Thứ 4 (22/07/2026):**
    *   Nghiên cứu cấu trúc code của backend `RAG_Be` vừa pull về để cập nhật các API.
    *   Lọc bỏ hoàn toàn các chức năng gửi/đính kèm tệp trong ô nhập liệu Chat di động để đảm bảo đồng bộ với nghiệp vụ của NodeJS backend (tính năng tải tài liệu chỉ dành cho Admin/Giảng viên trên giao diện Web).
*   **Thứ 5 (23/07/2026):**
    *   Phát triển thuật toán bóc tách thẻ trích dẫn (Citations) dạng số `[1]`, `[2]` trong nội dung văn bản trả về của AI bằng regex động (`InlineTextContent`).
    *   Thiết kế giao diện hiển thị danh sách nguồn trích dẫn đính kèm phía dưới tin nhắn của AI gồm: Tên tài liệu trích xuất, Số trang, và Nội dung trích dẫn.
*   **Thứ 6 (24/07/2026):**
    *   Liên kết sự kiện nhấp chọn nguồn trích dẫn để mở trực tiếp trang tài liệu tương ứng thông qua Bottom Sheet / Reader Overlay.
    *   Triển khai xử lý luồng Chat Đồng bộ (Synchronous HTTP): hiển thị trạng thái chờ phản hồi (`BotLoadingBubble`).
*   **Thứ 7 (25/07/2026):**
    *   Hoàn thiện toàn bộ các trường hợp nghiệp vụ đăng nhập đặc biệt:
        *   Tự động bắt lỗi và hiển thị nguyên nhân chi tiết nếu tài khoản của sinh viên đang ở trạng thái bị Khóa (`LOCKED`) hoặc Chờ phê duyệt (`PENDING`).
        *   Chặn tài khoản có quyền Quản trị viên (`ADMIN` / `TEACHER`) đăng nhập vào ứng dụng mobile, báo lỗi chỉ cho phép sinh viên đăng nhập.
*   **Chủ Nhật (26/07/2026):**
    *   Phát triển mới màn hình Đăng ký tài khoản (`RegisterScreen` và `RegisterForm`) với đầy đủ các trường thông tin đồng bộ với schema của Node.js, kết nối API đăng ký.
    *   Móc nối logic loading/error cho màn hình Quên mật khẩu (`ForgotPasswordScreen`) vào `AuthViewModel`.
    *   Tiến hành kiểm thử biên dịch toàn cục thông qua `.\gradlew.bat compileDebugKotlin` bảo đảm codebase sạch sẽ, không có bất kỳ lỗi cú pháp hay linter nào.

---

## 📝 NHẬT KÝ LỖI & GIẢI PHÁP KỸ THUẬT (BUG LOG)

| Tuần | Tên lỗi gặp phải | Nguyên nhân | Cách khắc phục / Giải pháp |
| :--- | :--- | :--- | :--- |
| **Tuần 2** | Mất dữ liệu chat khi đổi tab | Compose Screen bị hủy và dựng lại khiến State bị reset về mặc định | Sử dụng `rememberSaveable` kết hợp giữ Instance của ViewModel ở cấp độ MainTab để lưu trữ tin nhắn. |
| **Tuần 3** | Form đăng nhập bị reset khi nhập sai mật khẩu | `safeApiCall` mặc định tự phát sự kiện `Unauthorized` xóa toàn bộ session | Thêm cờ `emitUnauthorizedEvent: Boolean` vào `safeApiCall`, đặt bằng `false` khi gọi API Login để chỉ xử lý báo lỗi tại màn hình Login. |
| **Tuần 3** | Màn hình Screen bị phình to code (>150 lines) | Cả UI Scaffold và các component nhỏ được viết chung một file | Tách biệt các view nhỏ (như Card tài liệu, Bottom Sheet) ra thư mục `ui/components/` riêng. |
| **Tuần 4** | Lỗi import DTO và Deprecated OkHttp | API Upload tài liệu sử dụng cú pháp cũ không tương thích với OkHttp mới trong Gradle | Cập nhật hàm parse `MediaType` sử dụng `.toMediaTypeOrNull()` và `RequestBody` sử dụng `.asRequestBody()`. |
| **Tuần 4** | Lỗi format dữ liệu Chat từ phía NodeJS | Server NodeJS trả về `senderType` thay vì `role` làm app bị crash khi parse dữ liệu | Cập nhật lại data class DTO khớp định dạng của NodeJS trả về. |
