/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {

    //by viewModels()というプロパティ委譲(ゲッター/セッターの役割を別のクラスに引き継ぐ)を使用してGameViewModelを初期化
    //viewModel オブジェクトの役割を viewModels という名前の別のクラスに委譲すると、
    //viewModel オブジェクトへのアクセスは、委譲クラス viewModels によって内部的に処理される。
    //委譲クラスは、最初にアクセスされたときに viewModel オブジェクトを作成し、構成変更(例:画面の回転)後も
    //その値を保持し、リクエストに対してその値を返します。
    private val viewModel: GameViewModel by viewModels()

    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same GameViewModel instance created by the
    // first fragment

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        //ビューバインディング
        //binding = GameFragmentBinding.inflate(inflater, container, false)
        //データバインディング
        binding = DataBindingUtil.inflate(inflater,R.layout.game_fragment,container,false)
        Log.d("GameFragment", "GameFragment created/re-created!")
        Log.d("GameFragment", "Word: ${viewModel.currentScrambledWord} " +
                "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //レイアウト変数を初期化
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS
        //LiveDataはライフサイクル対応かつ監視可能なため、ライフサイクル所有者をレイアウトに渡す必要がある
        binding.lifecycleOwner = viewLifecycleOwner
        // Setup a click listener for the Submit and Skip buttons.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }

        /*
        スコアと単語カウントのテキストビューを更新
        binding.score.text = getString(R.string.score, 0)
        binding.wordCount.text = getString(
                R.string.word_count, 0, MAX_NO_OF_WORDS)
         */

        /*
        currentScrambledWord LiveDataのオブザーバーを接続
        viewLifecycleOwner：フラグメントの View のライフサイクルを表します。
        このパラメータにより、LiveDataがGameFragmentライフサイクルを認識し、
        GameFragmentがアクティブな状態（STARTEDまたはRESUMED）の場合にのみオブザーバーに通知するようになります。
        第二引数はイベントを受け取るオブザーバー
        ここではスクランブルされた単語のテキストビューに newWord を代入する匿名関数を入れる
        viewModel.currentScrambledWord.observe(viewLifecycleOwner,
            {newWord -> binding.textViewUnscrambledWord.text = newWord})
        viewModel.score.observe(viewLifecycleOwner,
            {newScore -> binding.score.text = getString(R.string.score, newScore)})
        viewModel.currentWordCount.observe(viewLifecycleOwner,
            {newWordCount -> binding.wordCount.text =
                getString(R.string.word_count, newWordCount, MAX_NO_OF_WORDS)})
         */

    }


    /*
    //対応するアクティビティとフラグメントが破棄されたときに呼び出されるonDetach()コールバックメソッド
    override fun onDetach() {
    super.onDetach()
    Log.d("GameFragment", "GameFragment destroyed!")
    }
    */

    /*
    * Checks the user's word, and updates the score accordingly.
    * Displays the next scrambled word.
    */
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()
        if(viewModel.isUserWordCorrect(playerWord)){
            setErrorTextField(false)
            if(!viewModel.nextWord()){
                showFinalScoreDialog()
            }
        }else{
            setErrorTextField(true)
        }

    }

    /*
     * Skips the current word without changing the score.
     * Increases the word count.
     */
    private fun onSkipWord() {
        if(viewModel.nextWord()){
            setErrorTextField(false)
        }else{
            showFinalScoreDialog()
        }
    }

    /*
     * Gets a random word for the list of words and shuffles the letters in it.
     */
    private fun getNextScrambledWord(): String {
        val tempWord = allWordsList.random().toCharArray()
        tempWord.shuffle()
        return String(tempWord)
    }

    /*
     * Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.
     */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
    * Sets and resets the text field error status.
    */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    /*
     * Displays the next scrambled word on screen.
     */
    /*  オブザーバーを LiveData に接続するため、このメソッドは削除
        private fun updateNextWordOnScreen() {
        //読み取り専用のviewModelプロパティ(GameViewModel)を使用する
        binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord
    }*/

    private fun showFinalScoreDialog(){
        //ダイアログを構築
        /*
        requireContext() メソッドは null 以外の Context を返します。
        setTitle:アラートダイアログにタイトルを設定
        setMessage:メッセージを設定
        setCancelable:[戻る]キーが押されてもアラートダイアログがキャンセルされないようにfalseにする
        setNegativeButton,setPositiveButton:2 つのテキストボタン[EXIT]と[PLAY AGAIN]を追加
        setNegativeButton(getString(R.string.exit)){ _, _ -> exitGame()}
             ↓↓↓↓↓元に戻すと
        setNegativeButton(getString(R.string.exit), { _, _ -> exitGame()})
        2番めの引数はラムダとして記述できる関数 DialogInterface.OnClickListener()だが、
        setNegativeButtonにわたす引数が関数の場合は、ラムダ式を括弧の外に置くことができる（後置ラムダ構文）
        show:アラートダイアログを作成してから表示する
         */
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored,viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)){ _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)){ _, _ ->
                restartGame()
            }
            .show()
    }


}
