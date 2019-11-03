package com.example.play_music

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Parcelable
import android.speech.RecognizerIntent
import android.support.annotation.RequiresApi
import android.view.View
import java.io.File


class Play_music_ofline : Activity(), MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    var mymediaplayer: MediaPlayer? = null
    var position: Int = 0
    var sname: String? = null
    var mySongs: ArrayList<File>? = null
    var updateseekbar: Thread? = null
    private val seekForwardTime = 5000 // 5000 milliseconds
    private val seekBackwardTime = 5000 // 5000 milliseconds
    private val mHandler = Handler()
    private var utils: Utilities? = null
    val btn_pause = findViewById<ImageButton>(R.id.btnPlay);
    private var songTotalDurationLabel = findViewById<View>(R.id.songTotalDurationLabel) as TextView
    private var songCurrentDurationLabel: TextView? = null
    private var songProgressBar: SeekBar? = null


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }
    fun updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100)
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
// remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask)    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        mHandler.removeCallbacks(mUpdateTimeTask)
        val totalDuration = mymediaplayer!!.duration
        val currentPosition = utils!!.progressToTimer(seekBar!!.progress, totalDuration)

        // forward or backward to certain seconds
        mymediaplayer!!.seekTo(currentPosition)

        // update timer progress again
        updateProgressBar()    }

    override fun onCompletion(mp: MediaPlayer?) {
        btn_pause!!.setImageResource(R.drawable.ic_play)    }

    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            val totalDuration = mymediaplayer!!.duration.toLong()
            val currentDuration = mymediaplayer!!.currentPosition.toLong()

            // Displaying Total Duration time
            songTotalDurationLabel!!.text = "" + utils!!.milliSecondsToTimer(totalDuration)
            // Displaying time completed playing
            songCurrentDurationLabel!!.text = "" + utils!!.milliSecondsToTimer(currentDuration)

            // Updating progress bar
            val progress = utils!!.getProgressPercentage(currentDuration, totalDuration)
            //Log.d("Progress", ""+progress);
            songProgressBar!!.progress = progress

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_music_ofline)

        var position = 0
        val btnForward = findViewById<ImageButton>(R.id.btnForward);
        val btnBackward = findViewById<ImageButton>(R.id.btnBackward);

        val songTextlabel = findViewById<TextView>(R.id.songTitle);
        val songseekbar = findViewById<SeekBar>(R.id.songProgressBar);


        btn_pause!!.setImageResource(R.drawable.ic_pause)
        btn_pause.setOnClickListener {
            position = mymediaplayer!!.getCurrentPosition ()
            if (mymediaplayer!!.isPlaying()) {
                mymediaplayer!!.seekTo (position)
                mymediaplayer!!.pause()
                btn_pause!!.setImageResource(R.drawable.ic_play)


            } else {
                mymediaplayer!!.seekTo (position)
                mymediaplayer!!.start()
                btn_pause!!.setImageResource(R.drawable.ic_pause)
            }

        }

        btnForward!!.setOnClickListener {
            // get current song position
            val currentPosition = mymediaplayer!!.currentPosition
            // check if seekForward time is lesser than song duration
            if (currentPosition + seekForwardTime <= mymediaplayer!!.duration) {
                // forward song
                mymediaplayer!!.seekTo(currentPosition + seekForwardTime)
            } else {
                // forward to end position
                mymediaplayer!!.seekTo(mymediaplayer!!.duration)
            }
        }
        btnBackward!!.setOnClickListener {
            // get current song position
            val currentPosition = mymediaplayer!!.currentPosition
            // check if seekBackward time is greater than 0 sec
            if (currentPosition - seekBackwardTime >= 0) {
                // forward song
                mymediaplayer!!.seekTo(currentPosition - seekBackwardTime)
            } else {
                // backward to starting position
                mymediaplayer!!.seekTo(0)
            }
        }

        if (mymediaplayer != null) {
                mymediaplayer!!.stop();
                mymediaplayer!!.release();
            }

            val i: Intent = getIntent();
            val bundle: Bundle = i.getExtras();
            var passedIntent = intent.extras

            var mySongs: ArrayList<Parcelable> = bundle.getParcelableArrayList("songs")
            val sname = mySongs!!.get(position).toString();

            val songName: String = i.getStringExtra("songname");

            songTextlabel.setText(songName);
            songTextlabel.setSelected(true);

            position = bundle.getInt("pos", 0);

            val u: Uri = Uri.parse(mySongs!!.get(position).toString());

            mymediaplayer = MediaPlayer.create(getApplicationContext(), u);

            mymediaplayer!!.start();
            songseekbar.setMax(mymediaplayer!!.getDuration());


        }
        override fun onDestroy() {
            super.onDestroy()
            mymediaplayer!!.release()
        }

    }

