package com.project.tasks

import android.os.AsyncTask
import com.project.HospitalManager
import com.project.api.HospitalRestMapper
import com.project.models.Hospital
import com.project.models.HospitalFullInfo

class AddScoreTask() : AsyncTask<String, Void, Void>() {

    private val hospitalService = HospitalRestMapper()


    override fun doInBackground(vararg p0: String?): Void? {
        hospitalService.addScore(p0[0]!!.toInt(), p0[1]!!, p0[2]!!)
        return null
    }
}