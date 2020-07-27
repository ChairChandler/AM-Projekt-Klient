package com.project

import com.project.models.Hospital
import com.project.models.HospitalFullData
import com.project.tasks.AddScoreTask
import com.project.tasks.GetHospitalByIDTask
import com.project.tasks.GetHospitalsTask

object HospitalManager {

    const val SAMPLE_HOSPITAL_ID = "76aec26f-ed1b-4c12-858f-bcab30d2fcb3"

    val hospitals = ArrayList<Hospital>()

    fun downloadHospitalData(): List<Hospital> {
       return GetHospitalsTask().execute().get()
    }

    fun downloadHospitalFullData(id: String): HospitalFullData{
        return GetHospitalByIDTask().execute(id).get()
    }

    fun addScore(score: Int, comment: String, userName: String, hospitalId: String) : Boolean{
        return AddScoreTask().execute(score.toString(), comment, userName, hospitalId).get()
    }
}