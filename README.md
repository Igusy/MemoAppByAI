# MemoAppByAI: 生成AIを使って作られたテキストエディタアプリ

このアプリは、授業の課題で作成した。ファイルの読み込み、編集、保存をするテキストエディタ。生成AIには Chat GPT (3と4o) と Gemma 2 9B が使われ、コードの出力をさせた。

## Features

* **ファイル操作:**
    * ファイルを読み込み、編集する。
    * ファイルを新しい名前で保存する。
    * ストレージアクセスフレームワーク (SAF) を使用してファイルの場所を取得する。
    * 操作の取り消しとやり直し。
* **テキスト編集:**
    * 上部にファイル名を表示。
    * キーボードの出現に応じて、UI の配置が自動的に調整。
* **UI:**
    * Jetpack ComposeによるUI構築。
    * Material Theme Builder を使用。
    * テーマを手動で切り替え。 (ライトテーマとダークテーマ)
    * 左上にメニューボタンを実装。
* **最小API/SDKレベル:**
    * Android 7.0 (API Level 24) 
* **動作確認済み:**
    * 仮想デバイス: Pixel 8 (Android 15 API 35)
    * 実機: Sony SOV42 (Android 10.0 API 29)

## スクリーンショット

* 仮想デバイス (Pixel8) の動作画面
<img src="/screenshots/Screenshot_1.png" width="50%" height="50%" />

* Sony SOV42 の動作画面
<img src="/screenshots/Screenshot_2.png" width="50%" height="50%" />

##  既知の問題点

* 画面を回転させると情報が全て失われる。 (ViewModel を使った実装ができていないため)



## 開発環境

* Android Studio
* Kotlin
* Material Theme Builder
