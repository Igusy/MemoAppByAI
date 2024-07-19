package anekawamasaki.memoappbyai

import anekawamasaki.memoappbyai.ui.theme.AppTheme
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    var uri: Uri? = null
    lateinit var fileActionLauncher : ActivityResultLauncher<Intent>
    val fileHandler = FileHandler(this)
    val textEditorState = TextEditor()
    var title = mutableStateOf("Memo App")
    //var darkTheme = mutableStateOf(false)
    val themeWatcher = ThemeWatcher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fileActionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
            Log.i("FileHandler", result.toString())
            if(result.resultCode == RESULT_OK){
                val data: Intent? = result.data
                uri = data?.data
                if(uri != null){
                    //Log.i("FileHandler", "File URI: $uri")
                    setResult(Activity.RESULT_OK, data)

                    fileHandler.uri = uri

                    if("readFile" == fileHandler.action){
                        textEditorState.input =  TextFieldValue(fileHandler.readFile())
                    }

                    if("saveAsNewFile" == fileHandler.action) {
                        fileHandler.saveAsNewFile(textData = textEditorState.text)
                    }

                }else{
                    uri = null
                }
            } else{
                uri = null
            }
            titleUpdate(uri,title,this)
        }


        setContent {
            AppTheme(darkTheme=themeWatcher.darkTheme) {
                MemoApp(
                    fileRunner = { fileAction, method -> fileRunner(fileAction, method) },
                    fileHandler = fileHandler,
                    textEditorState = textEditorState,
                    title = title,
                    themeWatcher = themeWatcher
                )
            }
        }
    }

    fun fileRunner(fileAction : String, method: String){
        val intent = Intent(this, FileAction::class.java)
        intent.putExtra("action", fileAction)
        fileHandler.action = method
        fileActionLauncher.launch(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoApp(fileRunner: (fileAction : String, method: String) -> Unit ,
            fileHandler : FileHandler,
            textEditorState: TextEditor,
            title: MutableState<String>,
            themeWatcher: ThemeWatcher
) {
    //val uriAvailable = remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(DrawerValue.Closed) // Drawer State
    val scope = rememberCoroutineScope()
    //val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(){
                // Modal Navigation Drawer content
                Text(
                    "Menu",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Read File") },
                    selected = false,
                    onClick = {
                        fileRunner(Intent.ACTION_OPEN_DOCUMENT, "readFile")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Write File") },
                    selected = false,
                    onClick = {
                        if(fileHandler.uri == null) {
                            fileRunner(Intent.ACTION_CREATE_DOCUMENT, "saveAsNewFile")
                        }else{
                            fileHandler.writeFile(textData = textEditorState.text)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Save As New File") },
                    selected = false,
                    onClick = {
                        fileRunner(Intent.ACTION_CREATE_DOCUMENT, "saveAsNewFile")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Toggle Theme") },
                    selected = false,
                    onClick = {
                        themeWatcher.DarkThemeSwitch()
                        Log.i("Memo_APP", "darkTheme: ${themeWatcher.darkTheme}")
                    }
                )
            }
        }
    ){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title.value) },
                    navigationIcon = {
                        IconButton(onClick = {
                            focusManager.clearFocus()
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }){
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    modifier = Modifier.clickable(interactionSource = NoRippleInteractionSource(),indication = null){ focusManager.clearFocus() }
                )
            },
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.height(48.dp).clickable(interactionSource = NoRippleInteractionSource(),indication = null){ focusManager.clearFocus() },
                    actions = {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { textEditorState.undo() }) {
                            Icon(painter = painterResource(R.drawable.undo), contentDescription = "Undo")
                        }
                        IconButton(onClick = { textEditorState.redo() }) {
                            Icon(painter = painterResource(R.drawable.redo), contentDescription = "Redo")
                        }
                    }
                )
            }
            // その他のレイアウト要素をここに記述する
        ) { innerPadding ->
            // テキスト編集フィールド
            TextField(
                value = textEditorState.input,
                onValueChange = { textEditorState.onTextChanged(it) },
                modifier = Modifier.padding(innerPadding).fillMaxWidth(),
            )
        }
    }
}

class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()

    override suspend fun emit(interaction: Interaction) {}

    override fun tryEmit(interaction: Interaction) = true
}

fun titleUpdate(uri: Uri?, title: MutableState<String>, context: Context){
    if(uri != null){
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            title.value = cursor.getString(nameIndex)
        }
    }
    if(uri == null){
        title.value = "Memo App"
    }
}


class ThemeWatcher{
    var darkTheme : Boolean by mutableStateOf(true)

    fun DarkThemeSwitch() {
        darkTheme = !darkTheme
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    //MemoApp( context = "" , getPath = { })
}