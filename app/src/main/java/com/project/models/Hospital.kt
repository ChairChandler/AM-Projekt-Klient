package com.project.models

data class Hospital constructor (
    val id : String,
    val name: String,
    val description: String,
    val location: Position
){
    constructor(): this("", "", "", Position())
}

data class HospitalListResponse (
    val hospitals: List<Hospital>
){
    constructor(): this(emptyList())
}

data class HospitalFullData constructor (
    val scores: List<Score>,
    val id : String,
    val name: String,
    val description: String,
    val location: Position
){
    constructor(): this( emptyList(), "", "", "", Position())
}

data class Position constructor (
    val lat: Double,
    val lng: Double
){
    constructor(): this(0.0, 0.0)
}
