package com.project.tasks

import android.os.AsyncTask
import com.project.HospitalManager
import com.project.api.HospitalRestMapper
import com.project.models.HospitalFullData

class GetHospitalByIDTask() : AsyncTask<String, Void, HospitalFullData>() {

    private val hospitalService = HospitalRestMapper()


    override fun doInBackground(vararg p0: String?) : HospitalFullData {
        return hospitalService.getHospital(p0.first()!!)
    }
}