package anekawamasaki.memoappbyai

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class TextEditor {
    var input by mutableStateOf(TextFieldValue(""))
    val text: String get() = input.text
    private val undoHistory: MutableList<TextFieldValue> = mutableListOf() // 履歴を保持するリスト
    private val redoHistory: MutableList<TextFieldValue> = mutableListOf() // 履歴を保持するリスト

    fun onTextChanged(newText: TextFieldValue) {
        input = newText
        undoHistory.add(input) // 現在のテキストを履歴に追加
    }

    fun undo() {
        if (undoHistory.size > 1 ) {
            // pop the last
            Log.i("Memo_APP", "Last is "+undoHistory.last().text)
            val pop = undoHistory.removeLastOrNull()
            pop?.let {
                if (it.text.isNotEmpty()) {
                    redoHistory.add(it)
                }
            }

            // peek the last
            val peek = undoHistory.lastOrNull()
            peek?.let{
                input = it
            }
        }
    }

    fun redo() {
        val pop = redoHistory.removeLastOrNull()
        pop?.let {
            if (it.text.isNotEmpty()) {
                undoHistory.add(it)
                input = it
            }
        }
    }
}