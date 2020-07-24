package com.project.models

data class Score constructor (
    val id: Id,
    val score: Int,
    val dateUTC: String
){
    constructor(): this(Id(), 0, "")
}

data class Comment constructor (
    val id: String,
    val hospitalId: String,
    val user : String,
    val comment: String,
    val dateUTC: String
){
    constructor(): this("", "", "", "", "")
}

data class Id constructor (
    val hospitalId: String,
    val user: String
){
    constructor(): this("", "")
}

data class ScoreRequest(
    val id: String,
    val score: Int
)

data class CommentRequest(
    val id: String,
    val comment: String
)