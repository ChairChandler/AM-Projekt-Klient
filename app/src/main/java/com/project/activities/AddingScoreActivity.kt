package com.project.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.project.HospitalManager
import com.project.R
import kotlinx.android.synthetic.main.activity_adding_score.*

class AddingScoreActivity : AppCompatActivity() {
    @SuppressLint("ShowToast", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_score)

        val bundle = intent.extras
        val hospitalName = bundle!!.getString("name")
        val hospitalId = bundle.getString("id")
        hospitalDescripton.text = hospitalName


        send.setOnClickListener {
            if (ratingBar.rating.toInt() != 0 && userNameInput.text.isNotEmpty()) {
                if (commentInput.text.isNotEmpty()) {
                    HospitalManager.addComment(
                        commentInput.text.toString(),
                        userNameInput.text.toString(),
                        hospitalId!!
                    )
                }
                HospitalManager.addScore(
                    ratingBar.rating.toInt(),
                    userNameInput.text.toString(),
                    hospitalId!!
                )
                val i = Intent(this, MapsActivity::class.java)
                startActivity(i)
            } else {
                Toast.makeText(
                    this.baseContext,
                    "Proszę dodać ocenę oraz wpisać imię",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}