package com.example.sandboxalpha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openCalculator(view: View){
        val intent = Intent(this,AlphaCalculator::class.java)
        startActivity(intent)
    }
}