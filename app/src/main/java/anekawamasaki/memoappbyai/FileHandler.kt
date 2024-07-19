package anekawamasaki.memoappbyai

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class FileHandler(private val context: Context) : AppCompatActivity() {
    //private val fileHandler = FileHandler(this)
    //var filePath: String = ""
    var content: String = ""
    var filePath: String = Environment.DIRECTORY_DOWNLOADS
    var uri : Uri? = null
    var action : String = ""

    // readFile(), writeFile(), saveAsNewFile() の実装
    fun readFile(): String {
        var data :String = ""
        uri?.let { context.contentResolver.openInputStream(it).use{ fileInputStream ->
            val size: Int? = fileInputStream?.available()
            val buffer = size?.let { it1 -> ByteArray(it1)}
            fileInputStream?.read(buffer)
            data = buffer?.toString(Charsets.UTF_8).toString()
        } }
        return data
    }

    fun writeFile(textData: String) {
        val state = Environment.getExternalStorageState()

        if( Environment.MEDIA_MOUNTED != state ) {
            Toast.makeText(this, "External Storage is not available", Toast.LENGTH_SHORT).show()
        }

        //getPath(Intent.ACTION_CREATE_DOCUMENT)
        Log.i("saveAsNewFile", "Content URI is:$uri")

        //var context = requireContext()

        uri?.let {
            context.contentResolver.openOutputStream(it, "rw").use { fileOutputStream ->
                fileOutputStream?.write(textData.toByteArray())
                fileOutputStream?.flush()
                fileOutputStream?.close()
            }
        }

    }

    fun saveAsNewFile(textData: String) {
        writeFile(textData)
    }

}