package com.project.models

import java.io.Serializable

data class Hospital constructor(
    val id: String,
    val name: String,
    val description: String,
    val location: Position
) : Serializable {
    constructor() : this("", "", "", Position())
}

data class HospitalListResponse(
    val hospitals: List<Hospital>
) : Serializable {
    constructor() : this(emptyList())
}

data class HospitalFullData constructor(
    val scores: List<Score>,
    val id : String,
    val name: String,
    val description: String,
    val location: Position
): Serializable {
    constructor(): this( emptyList(), "", "", "", Position())
}

data class Position constructor(
    val lat: Double,
    val lng: Double
) : Serializable {
    constructor() : this(0.0, 0.0)
}
