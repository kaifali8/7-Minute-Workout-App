package com.example.a7minutesworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding
import com.example.a7minutesworkout.databinding.DialogCustomBackConfirmationBinding
import java.util.Locale

class ExerciseActivity:AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding:ActivityExerciseBinding?=null
    private var restTimer:CountDownTimer?=null
    private var restProgress=0
    private var restTimerDuration:Long=1
    private var exerciseTimer:CountDownTimer?=null
    private var exerciseProgress=0
    private var exerciseTimerDuration:Long=1
    private var exerciseList:ArrayList<ExerciseModel>?=null
    private var currentExercisePosition=-1

    private var tts:TextToSpeech?=null
    private var player:MediaPlayer?=null

    private var exerciseAdapter:ExerciseStatusAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarExercise)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        exerciseList=Constants.defaultExerciseList()

        tts=TextToSpeech(this,this)
        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }
        setRestView()
        setupExerciseStatusRecyclerView()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
    }
    private fun customDialogForBackButton() {
        val customDialog=Dialog(this)
        val dialogBinding=DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun setupExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager=
            LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        exerciseAdapter= ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter=exerciseAdapter
    }
    private fun setRestView(){

        try {
            val soundURI= Uri.parse("android.resource://com.example.a7minutesworkout/"+R.raw.press_start)
            player=MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping=false
            player?.start()
        }catch (e:Exception){
            e.printStackTrace()
        }

        binding?.flRestView?.visibility= View.VISIBLE
        binding?.tvTitle?.visibility=View.VISIBLE
        binding?.tvExerciseName?.visibility=View.INVISIBLE
        binding?.ivImage?.visibility=View.INVISIBLE
        binding?.flExerciseProgress?.visibility=View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility=View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility=View.VISIBLE
        if (restTimer!=null){
            restTimer?.cancel()
            restProgress=0
        }
        speakOut("Please rest and get ready for next exercise")
        //binding?.tvUpcomingExerciseName?.text=exerciseList!![currentExercisePosition+1].getName()
        if (currentExercisePosition + 1 < exerciseList!!.size) {
            binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition + 1].getName()
        } else {
            binding?.tvUpcomingExerciseName?.text = "No upcoming exercise"
        }
        setRestProgressBar()
    }

    private fun setExerciseView(){
        binding?.flRestView?.visibility= View.INVISIBLE
        binding?.tvTitle?.visibility=View.INVISIBLE
        binding?.tvExerciseName?.visibility=View.VISIBLE
        binding?.ivImage?.visibility=View.VISIBLE
        binding?.flExerciseProgress?.visibility=View.VISIBLE
        binding?.tvUpcomingLabel?.visibility=View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility=View.INVISIBLE
        if (exerciseTimer!=null){
            exerciseTimer?.cancel()
            exerciseProgress=0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text=exerciseList!![currentExercisePosition].getName()
        setExerciseProgressBar()
    }
    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer=object :CountDownTimer(restTimerDuration*1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress=10-restProgress
                binding?.tvTimer?.text=(10-restProgress).toString()
            }
            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar(){
        binding?.exerciseProgressBar?.progress=exerciseProgress
        exerciseTimer=object :CountDownTimer(exerciseTimerDuration*3000,1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.exerciseProgressBar?.progress=30-exerciseProgress
                binding?.tvExerciseTimer?.text=(30-exerciseProgress).toString()
            }
            override fun onFinish() {

                if (currentExercisePosition < exerciseList?.size!! -1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setRestView()
                }else{
                    val intent=Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        //stopping the rest timer
        if (restTimer!=null){
            restTimer?.cancel()
            restProgress=0
        }
        //stopping the exercise timer
        if (exerciseTimer!=null){
            exerciseTimer?.cancel()
            exerciseProgress=0
        }
        //shutting down text to speech feature
        if (tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player!=null){
            player!!.stop()
            player=null
        }

        binding=null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result=tts?.setLanguage(Locale.ENGLISH)
            if (result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","The language specified is not supported")
            }
        }else{
            Log.e("TTS","Initialisation Failed!")
        }
    }

    private fun speakOut(text:String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
}