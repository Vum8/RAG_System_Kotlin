package com.example.rag_system.data.api.model

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("role") val role: String = "STUDENT",
    @SerializedName("studentCode") val studentCode: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String
)

data class AuthResponseDto(
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: UserDto? = null,
    @SerializedName("requireOtp") val requireOtp: Boolean = false
)

data class UserDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("fullName") val fullName: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("role") val role: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("authVersion") val authVersion: Int = 1
)

data class UserProfileResponseDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("role") val role: String = "STUDENT",
    @SerializedName("full_name") val fullName: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("status") val status: String = "",
    @SerializedName("student_code") val studentCode: String? = null,
    @SerializedName("date_of_birth") val dateOfBirth: String? = null
)

data class UpdateProfileRequestDto(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("phone") val phone: String?
)

data class ChangePasswordRequestDto(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String
)

data class ForgotPasswordRequestDto(
    @SerializedName("email") val email: String
)

data class ResetPasswordRequestDto(
    @SerializedName("token") val token: String,
    @SerializedName("newPassword") val newPassword: String
)
