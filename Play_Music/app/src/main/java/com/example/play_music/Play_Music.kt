package com.example.play_music

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView

class Play_Music  : Activity(), MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private var btnPlay: ImageButton? = null
    private var btnForward: ImageButton? = null
    private var btnBackward: ImageButton? = null

    private var btnRepeat: ImageButton? = null
    private var btnShuffle: ImageButton? = null
    private var share: ImageButton? = null
    private var songProgressBar: SeekBar? = null
    private var songTitleLabel: TextView? = null
    private var artis: TextView? = null
    private var songCurrentDurationLabel: TextView? = null
    private var songTotalDurationLabel: TextView? = null
    internal lateinit var imageView: ImageView
    // Media Player
    private var mp: MediaPlayer? = null
    // Handler to update UI timer, progress bar etc,.
    private val mHandler = Handler()
    private var utils: Utilities? = null
    private val seekForwardTime = 5000 // 5000 milliseconds
    private val seekBackwardTime = 5000 // 5000 milliseconds
    private val currentSongIndex = 0
    private var isShuffle = false
    private var isRepeat = false

    internal lateinit var desc: String
    internal lateinit var link: String



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
        val totalDuration = mp!!.duration
        val currentPosition = utils!!.progressToTimer(seekBar!!.progress, totalDuration)

        // forward or backward to certain seconds
        mp!!.seekTo(currentPosition)

        // update timer progress again
        updateProgressBar()    }

    override fun onCompletion(mp: MediaPlayer?) {
        btnPlay!!.setImageResource(R.drawable.ic_play)    }

    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            val totalDuration = mp!!.duration.toLong()
            val currentDuration = mp!!.currentPosition.toLong()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player)


        val intent = intent
        desc =intent.getStringExtra("song")
        link=intent.getStringExtra("url")


        btnPlay = findViewById<View>(R.id.btnPlay) as ImageButton
        btnForward = findViewById<View>(R.id.btnForward) as ImageButton
        btnBackward = findViewById<View>(R.id.btnBackward) as ImageButton
        share = findViewById<View>(R.id.share) as ImageButton
        share!!.setOnClickListener {
            try {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"

                i.putExtra(Intent.EXTRA_TEXT, link)
                startActivity(Intent.createChooser(i, "Choose one"))
            } catch (e: Exception) {
                //e.toString();
            }
        }

        btnRepeat = findViewById<View>(R.id.btnRepeat) as ImageButton
        btnShuffle = findViewById<View>(R.id.btnShuffle) as ImageButton
        songProgressBar = findViewById<View>(R.id.songProgressBar) as SeekBar
        songTitleLabel = findViewById<View>(R.id.songTitle) as TextView
        songTitleLabel!!.text = desc
        songCurrentDurationLabel = findViewById<View>(R.id.songCurrentDurationLabel) as TextView
        songTotalDurationLabel = findViewById<View>(R.id.songTotalDurationLabel) as TextView



        btnPlay!!.setOnClickListener {
            var position = 0
            // check for already playing
            val mDialog = ProgressDialog(this@Play_Music)
            position = mp!!.getCurrentPosition ()


            val mp3Play = object : AsyncTask<String, String, String>() {

                override fun onPreExecute() {
                    mDialog.setMessage("Please wait")
                    mDialog.show()
                }

                @SuppressLint("WrongThread")
                override fun doInBackground(vararg params: String): String {
                    try {
                        mp!!.setDataSource(params[0])
                        mp!!.prepare()
                    } catch (ex: Exception) {

                    }

                    return ""
                }

                override fun onPostExecute(s: String) {
                    if (mp!!.isPlaying()) {
                       // mp!!.seekTo (position)
                        mp!!.pause()
                        btnPlay!!.setImageResource(R.drawable.ic_play)



                    } else {

                        mp!!.seekTo (position)
                        mp!!.start()
                        btnPlay!!.setImageResource(R.drawable.ic_pause)




                    }

                    updateProgressBar()
                    mDialog.dismiss()
                }
            }

            mp3Play.execute(link) // direct link mp3 file

            mp!!.start()

        }


        mp = MediaPlayer()
        utils = Utilities()


        songProgressBar!!.setOnSeekBarChangeListener(this) // Important
        mp!!.setOnCompletionListener(this)




        btnForward!!.setOnClickListener {
            // get current song position
            val currentPosition = mp!!.currentPosition
            // check if seekForward time is lesser than song duration
            if (currentPosition + seekForwardTime <= mp!!.duration) {
                // forward song
                mp!!.seekTo(currentPosition + seekForwardTime)
            } else {
                // forward to end position
                mp!!.seekTo(mp!!.duration)
            }
        }

        /**
         * Backward button click event
         * Backward song to specified seconds
         */
        btnBackward!!.setOnClickListener {
            // get current song position
            val currentPosition = mp!!.currentPosition
            // check if seekBackward time is greater than 0 sec
            if (currentPosition - seekBackwardTime >= 0) {
                // forward song
                mp!!.seekTo(currentPosition - seekBackwardTime)
            } else {
                // backward to starting position
                mp!!.seekTo(0)
            }
        }
    }

    override fun onDestroy () {
        super.onDestroy ()
        mp!!.release ()
    }


}
