package com.project.models

data class Score constructor (
    val id: Id,
    val score: Int,
    val comment: String,
    val dateUTC: String
){
    constructor(): this(Id(), 0, "", "")
}

data class Id constructor (
    val hospitalId: String,
    val user: String
){
    constructor(): this("", "")
}

data class ScoreRequest(
    val user: String,
    val score: Int,
    val comment: String
)

data class Comment constructor (
    val hospitalId: String,
    val user : String,
    val comment: String,
    val dateUTC: String
) {
    constructor() : this( "", "", "", "")
}