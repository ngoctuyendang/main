package com.example.play_music

public class Model{
    lateinit var id:String
    lateinit var title:String
    lateinit var link:String

    constructor(id: String,title:String,link:String) {
        this.id = id
        this.title = title
        this.link = link
    }

    constructor()
}