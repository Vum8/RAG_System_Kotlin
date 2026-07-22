package com.example.rag_system.data.api.model

import com.google.gson.annotations.SerializedName

data class DocumentDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("uploadedBy") val uploadedBy: Long = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("originalFilename") val originalFilename: String = "",
    @SerializedName("fileType") val fileType: String = "",
    @SerializedName("fileSizeBytes") val fileSizeBytes: Long = 0,
    @SerializedName("processingStatus") val processingStatus: String = "",
    @SerializedName("visibilityStatus") val visibilityStatus: String = "",
    @SerializedName("processedAt") val processedAt: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

data class DocumentListResponseDto(
    @SerializedName("offset") val offset: Int = 0,
    @SerializedName("limit") val limit: Int = 20,
    @SerializedName("total") val total: Long = 0,
    @SerializedName("documents") val documents: List<DocumentDto> = emptyList()
)

data class DocumentDetailResponseDto(
    @SerializedName("document") val document: DocumentDto? = null,
    @SerializedName("latestJob") val latestJob: ProcessingJobDto? = null
)

data class ProcessingJobDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("documentId") val documentId: Long = 0,
    @SerializedName("jobType") val jobType: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("currentStage") val currentStage: String? = null,
    @SerializedName("totalChunks") val totalChunks: Int = 0,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("finishedAt") val finishedAt: String? = null
)
