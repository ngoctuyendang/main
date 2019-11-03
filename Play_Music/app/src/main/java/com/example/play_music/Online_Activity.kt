package com.example.play_music

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.ListView
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class Online_Activity : AppCompatActivity() {
    lateinit var listView_details: ListView
    var arrayList_details:ArrayList<Model> = ArrayList();
    //OkHttpClient creates connection pool between client and server
    val client = OkHttpClient()
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online)

        listView_details = findViewById<ListView>(R.id.listView) as ListView

        val urljson= "http://starlord.hackerearth.com/studio"
        ReadJSON().execute(urljson)
    }
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    @SuppressLint("StaticFieldLeak")
    inner class ReadJSON : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            val content : StringBuilder = StringBuilder();
            val url: URL = URL(params[0])
            val urlConnection: HttpURLConnection =url.openConnection()as HttpURLConnection
            val inputStreamReader: InputStreamReader = InputStreamReader(urlConnection.inputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)

            var line :String =""


            try {
                do {
                    line= bufferedReader.readLine()
                    if(line!=null) {
                        content.append(line)
                    }

                }while (line!=null)
                bufferedReader.close()
            }catch (e: Exception){
                Log.d("AAA",e.toString())
            }

            return content.toString()
        }

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // Toast.makeText(applicationContext,result,Toast.LENGTH_LONG).show()
            val  jsonObject: JSONArray = JSONArray(result)

            var song:String=""
            arrayList_details= ArrayList();
            for(music in 0 ..jsonObject.length()-1){

                var objMusic: JSONObject =jsonObject.getJSONObject(music)
                // song= song + " " + objMusic.getString("song")

                var model:Model= Model();
                model.id=objMusic.getString("song")
                model.title=objMusic.getString("artists")
                model.link=objMusic.getString("url")
                arrayList_details.add(model)
                // Toast.makeText(applicationContext,song,Toast.LENGTH_LONG).show()


            }
            runOnUiThread {
                //stuff that updates ui
                val obj_adapter : CustomAdapter
                obj_adapter = CustomAdapter(applicationContext,arrayList_details)
                listView_details.adapter=obj_adapter
            }

        }

    }
}
