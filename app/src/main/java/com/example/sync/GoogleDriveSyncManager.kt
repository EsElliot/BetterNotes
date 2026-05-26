package com.example.sync

import android.content.Context
import android.util.Log
import com.example.data.Note
import com.example.data.NoteRepository
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class GoogleDriveSyncManager(
    private val context: Context,
    private val repository: NoteRepository
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, Note::class.java)
    private val adapter: JsonAdapter<List<Note>> = moshi.adapter(listType)

    private val driveApi = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(DriveApi::class.java)

    suspend fun syncNotes(account: GoogleSignInAccount) = withContext(Dispatchers.IO) {
        try {
            val token = GoogleAuthUtil.getToken(
                context, account.account!!, "oauth2:https://www.googleapis.com/auth/drive.appdata"
            )
            val authHeader = "Bearer $token"

            val filesResponse = driveApi.listFiles(authHeader)
            val backupFileId = filesResponse.files.firstOrNull()?.id

            val currentNotes = repository.allNotes.first()

            if (currentNotes.isEmpty() && backupFileId != null) {
                // Restore from drive
                val downloadResponse = driveApi.downloadFile(authHeader, backupFileId)
                val json = downloadResponse.string()
                val driveNotes = adapter.fromJson(json) ?: emptyList()

                for (note in driveNotes) {
                    repository.insert(note)
                }
                Log.d("Sync", "Restored ${driveNotes.size} notes from Drive")
            } else {
                // Backup to drive
                val json = adapter.toJson(currentNotes)
                val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

                var fileId = backupFileId
                if (fileId == null) {
                    val metadata = mapOf(
                        "name" to "notes_backup.json",
                        "parents" to listOf("appDataFolder")
                    )
                    val newFile = driveApi.createFileMetadata(authHeader, metadata)
                    fileId = newFile.id
                }

                driveApi.uploadFileContent(authHeader, fileId, requestBody)
                Log.d("Sync", "Backed up ${currentNotes.size} notes to Drive")
            }
        } catch (e: Exception) {
            Log.e("Sync", "Sync error", e)
            throw e
        }
    }
}
