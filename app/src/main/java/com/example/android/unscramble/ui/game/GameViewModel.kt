package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*
警告:ViewModelから変更可能なデータフィールドを公開しないでください。
このデータは他のクラスから変更できないようにしてください。
ViewModel内の変更可能なデータは常にprivateである必要があります。
 */

//ViewModelをサブクラス化する(ViewModel は抽象クラスである)
class GameViewModel:ViewModel() {



    //アプリ内で使用するデータ変数


    //private var _currentScrambledWord = "test"
    private var wordsList:MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    /*
    バッキングプロパティを追加する
    private lateinit var _currentScrambledWord: String
    バッキングプロパティを追加する
    Kotlinでは Getter/Setterが自動で作られるが、getterをカスタムし_currentScrambledWordを返すようにする
    デフォルトの可視性修飾子はpublicなので外部のクラスがこのプロパティにアクセスすると、
    _currentScrambledWordの値が返されますが、その値は変更できません(valのためsetterは自動で作られない！！)
    val currentScrambledWord: String
        get() = _currentScrambledWord
     */

    /*
    LiveDataを追加する
    LiveDataの値を更新したい時、MutableLiveDataを使うのが一般的
    MutableLiveData型：外のクラスから値を更新出来る
    ※MutableListと混同しないように！！Listじゃないです！！！
    LiveData型：外のクラスから値を更新出来ない
    LiveDataオブジェクトに格納されている値を編集する必要がある場合は、MutableLiveDataクラスで
    公開されている setValue(T)メソッドとpostValue(T)メソッドを使用する必要があります。
     */

    //この書き方(MutableLiveData型)だと外のクラスから値を更新することが可能
    private val _currentScrambledWord = MutableLiveData<String>()
    //バッキングフィールドのcurrentScrambledWord型は不変であるためLiveData型
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private var _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount


    //オブジェクトインスタンスが最初に作成され初期化されたときに実行されます
    //データ変数の宣言、バッキングプロパティを追加した後に呼び出す！！(コードの位置関係に注意)
    init {
        Log.d("GameFragment", "GameViewModel created!")
        //初期化時に呼び出すので「test」ではなくスクランブルされた単語になる
        getNextWord()
    }

    /*
    //ViewModelが破棄される時にonCleared()コールバックが呼び出される
    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }
     */

    private fun getNextWord(){
        currentWord = allWordsList.random()
        //currentWord文字列を文字の配列に変換
        //Arrayは初期化時にサイズが固定されます。
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()
        while(String(tempWord).equals(currentWord, false)){
            tempWord.shuffle()
        }
        if(wordsList.contains(currentWord)){
            getNextWord()
        }else {
            //LiveDataオブジェクトのデータにアクセスするには.valueプロパティを使用する
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
    }

    fun nextWord(): Boolean{
        return if(_currentWordCount.value!! < MAX_NO_OF_WORDS){
            getNextWord()
            true
        }else false
    }

    private fun increaseScore(){
        //セーフコール演算子を使うことで安全に加算を実行する
        //セーフコール演算子:呼び出し元がnullでない場合にのみメソッドやプロパティを呼びだす
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord: String): Boolean{
        if(playerWord.equals(currentWord,true)){
            increaseScore()
            return true
        }
        return false
    }

    fun reinitializeData(){
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }
}