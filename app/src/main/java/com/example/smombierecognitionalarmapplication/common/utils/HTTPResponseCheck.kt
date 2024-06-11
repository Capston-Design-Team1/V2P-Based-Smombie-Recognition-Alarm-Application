package com.example.smombierecognitionalarmapplication.common.utils

fun HTTPResponseCheck(code : Int) : Boolean{
    when(code){
        in 200..299 -> return true
        in 300..399 -> throw Exception("Redirection")
        in 400..499 -> throw Exception("Already Created")
        in 500..599 -> throw Exception("Server Error")
        else -> throw Exception("Unknown Error")
    }
}