package com.project.tasks

import android.os.AsyncTask
import com.project.HospitalManager
import com.project.api.HospitalRestMapper
import com.project.models.Hospital
import com.project.models.HospitalFullInfo

class GetHospitalByIDTask() : AsyncTask<String, Void, HospitalFullInfo>() {

    private val hospitalService = HospitalRestMapper()


    override fun doInBackground(vararg p0: String?): HospitalFullInfo {
        HospitalManager.hospitalsWithFullInfo.add(hospitalService.getHospital(p0.first()!!))
        return HospitalManager.hospitalsWithFullInfo.last()
    }
}