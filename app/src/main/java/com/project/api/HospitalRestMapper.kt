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

    fun addScore(score: Int, userName: String, hospitalId: String): Boolean {
        val url = URI("$SERVER/api/hospital/$hospitalId/score")
        restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter())
        val response = restTemplate.postForObject(
            url,
            ScoreRequest(userName, score),
            HospitalFullData::class.java
        )
        return response != null
    }

    fun addComment(comment: String, userName: String, hospitalId: String): Boolean {
        val url = URI("$SERVER/api/hospital/$hospitalId/comment")
        restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter())
        val response = restTemplate.postForObject(
            url,
            CommentRequest(userName, comment),
            HospitalFullData::class.java
        )
        return response != null
    }

    companion object{
        const val SERVER = "http://51.178.82.249:7100"
    }
}