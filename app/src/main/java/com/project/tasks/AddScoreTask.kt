package com.project.tasks

import android.os.AsyncTask
import com.project.api.HospitalRestMapper

class AddScoreTask() : AsyncTask<String, Void, Boolean>() {

    private val hospitalService = HospitalRestMapper()


    override fun doInBackground(vararg p0: String?): Boolean {
        return hospitalService.addScore(p0[0]!!.toInt(), p0[1]!!, p0[2]!!)
    }
}