package anekawamasaki.memoappbyai

class Settings {
    private var _encoding: String = "UTF-8"
    fun setEncoding(encoding: String) { _encoding = encoding }
    fun getEncoding(): String = _encoding
}