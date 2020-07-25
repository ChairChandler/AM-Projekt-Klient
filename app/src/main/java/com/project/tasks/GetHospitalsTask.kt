package com.project.tasks

import android.os.AsyncTask
import com.project.HospitalManager
import com.project.api.HospitalRestMapper
import com.project.models.Hospital

class GetHospitalsTask() : AsyncTask<String, Void, List<Hospital>>() {

    private val hospitalService = HospitalRestMapper()

    override fun doInBackground(vararg p0: String?): List<Hospital> {
        return hospitalService.getHospitals().hospitals
    }
}