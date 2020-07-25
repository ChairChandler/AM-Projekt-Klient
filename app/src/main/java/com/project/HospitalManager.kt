package com.project

import com.project.models.Hospital
import com.project.models.HospitalFullData
import com.project.tasks.AddCommentTask
import com.project.tasks.AddScoreTask
import com.project.tasks.GetHospitalByIDTask
import com.project.tasks.GetHospitalsTask

object HospitalManager {

    const val SAMPLE_HOSPITAL_ID = "0f08affb-214b-42b9-98aa-f8d070af156b"

    val hospitals = ArrayList<Hospital>()

    val hospitalsWithFullInfo = ArrayList<HospitalFullData>()

    fun downloadHospitalData(): List<Hospital> {
       return GetHospitalsTask().execute().get()
    }

    fun downloadHospitalFullData(id: String): HospitalFullData{
        return GetHospitalByIDTask().execute(id).get()
    }

    fun addScore(score: Int, userName: String, hospitalId: String) : Boolean{
        return AddScoreTask().execute(score.toString(), userName, hospitalId).get()
    }

    fun addComment(comment: String, userName: String, hospitalId: String) : Boolean {
        return AddCommentTask().execute(comment, userName, hospitalId).get()
    }
}