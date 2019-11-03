package com.example.play_music

import android.Manifest
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_offline.*
import android.R.attr.data
import java.nio.file.Files.isDirectory
import android.webkit.PermissionRequest
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.widget.ListView
import java.io.File


private val TAG = "PermissionDemo"

class Offline_Activity : AppCompatActivity() {
    public lateinit var mp: MediaPlayer



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline)


        display()

    }



    fun findSong(file: File): ArrayList<File> {
        val arrayList = ArrayList<File>()
        val files = file.listFiles()
        for (singleFile in files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {

                arrayList.addAll(findSong(singleFile))
            } else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {

                    arrayList.add(singleFile)


                }
            }


        }

        return arrayList

    }

    fun display() {
        val  mysonglistview =findViewById<ListView>(R.id.list_view);

        val mySongs = findSong(Environment.getExternalStorageDirectory())
        val items = arrayOfNulls<String>(mySongs.size)
        for (i in 0 until mySongs.size) {

            items[i] = mySongs[i].getName().toString().replace(".mp3", "").replace(".wav", "")

        }

        val myadapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)
        mysonglistview.setAdapter(myadapter)

        mysonglistview.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, i, l ->
            val SongName = mysonglistview.getItemAtPosition(i).toString()

            startActivity(
                Intent(this, Play_music_ofline::class.java)

                    .putExtra("songs", mySongs).putExtra("songname", SongName)
                    .putExtra("pos", i)

            )

        })
    }

}