package com.project.api

import com.project.models.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.net.URI


class HospitalRestMapper() {
    private val restTemplate = RestTemplate()

    fun getHospitals() : HospitalListResponse {
        val url = URI("$SERVER/api/hospital")
        restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter())
        return restTemplate.getForObject(url, HospitalListResponse::class.java)
    }

    fun getHospital(id: String): HospitalFullData{
        val url = URI("$SERVER/api/hospital/$id")
        restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter())
        return restTemplate.getForObject(url, HospitalFullData::class.java)
    }

    fun addScore(score: Int, comment:String, userName: String, hospitalId: String): Boolean {
        val url = URI("$SERVER/api/hospital/$hospitalId/score")
        restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter())
        val response = restTemplate.postForObject(
            url,
            ScoreRequest(userName, score, comment),
            HospitalFullData::class.java
        )
        return response != null
    }

    companion object{
        const val SERVER = "http://51.178.82.249:7100"
    }
}