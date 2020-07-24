package com.project.models

data class Hospital constructor (
    val id : String,
    val name: String,
    val description: String,
    val location: Position
)

data class HospitalListResponse (
    val hospitals: List<Hospital>
)

data class HospitalFullInfo constructor (
    val scores: List<Score>,
    val comments: List<Comment>,
    val id : String,
    val name: String,
    val description: String,
    val location: Position
){
    constructor(): this(emptyList(), emptyList(), "", "", "", Position())
}

data class Position constructor (
    val lat: Double,
    val lng: Double
){
    constructor(): this(0.0, 0.0)
}
