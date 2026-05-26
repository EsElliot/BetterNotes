package com.example.sync

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

data class DriveFileList(val files: List<DriveFile>)
data class DriveFile(val id: String, val name: String)

interface DriveApi {
    @GET("drive/v3/files")
    suspend fun listFiles(
        @Header("Authorization") auth: String,
        @Query("spaces") spaces: String = "appDataFolder",
        @Query("q") query: String = "name='notes_backup.json'"
    ): DriveFileList

    @GET("drive/v3/files/{fileId}?alt=media")
    suspend fun downloadFile(
        @Header("Authorization") auth: String,
        @Path("fileId") fileId: String
    ): ResponseBody

    @POST("drive/v3/files")
    suspend fun createFileMetadata(
        @Header("Authorization") auth: String,
        @Body metadata: Map<String, Any>
    ): DriveFile

    @PATCH("upload/drive/v3/files/{fileId}?uploadType=media")
    suspend fun uploadFileContent(
        @Header("Authorization") auth: String,
        @Path("fileId") fileId: String,
        @Body content: RequestBody
    ): DriveFile
}
