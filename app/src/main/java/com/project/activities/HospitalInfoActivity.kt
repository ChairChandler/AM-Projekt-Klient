package com.project.activities

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.project.HospitalManager
import com.project.R
import com.project.models.Comment
import com.project.models.Hospital
import com.project.models.HospitalFullData
import com.project.views.CommentsArrayAdapter

class HospitalInfoActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var hospital: HospitalFullData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_info)

        val baseInfo = intent.getSerializableExtra("info") as Hospital
        title = baseInfo.name
        hospital = HospitalManager.downloadHospitalFullData(baseInfo.id)
        this.showComments()
    }

    private fun showComments() {
        listView = findViewById(R.id.comments)
        val adapter = CommentsArrayAdapter(this, hospital.scores.map { Comment(it.id.hospitalId, it.id.user, it.score, it.comment, it.dateUTC) })
        listView.adapter = adapter
    }
}