package anekawamasaki.memoappbyai

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class FileAction : ComponentActivity() {
    var uri: Uri? = null
    lateinit var getPathLauncher2 : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getPathLauncher2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
            Log.i("Memo_APP", result.toString())
            if(result.resultCode == RESULT_OK){
                val data: Intent? = result.data
                uri = data?.data
                if(uri != null){
                    Toast.makeText(this, "File URI: $uri", Toast.LENGTH_SHORT).show()
                    //Log.i("Memo_APP", "File URI: $uri")
                    setResult(Activity.RESULT_OK, data);
                }else{
                    Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(this, "File selection cancelled or failed", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        setContent{
            val data:String = intent.getStringExtra("action").toString()
            getPath(data)
        }
    }

    fun getPath(fileAction : String) {
        val intent = Intent().setType("*/*").setAction(fileAction)
        getPathLauncher2.launch(intent)
    }
}
