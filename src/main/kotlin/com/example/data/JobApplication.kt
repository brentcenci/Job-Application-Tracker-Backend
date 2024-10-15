package com.example.data

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class JobApplication(
    @BsonId val jobId: ObjectId? = null,
    val userId: ObjectId = ObjectId("64ff9c8e7a3b3e4f9c6a3b32"),
    val jobTitle: String,
    val jobLevel: String,
    val companyName: String,
    val industry: String,
    val applicationDate: String,
    val updateDate: String = LocalDateTime.now().toString(),
    val status: String,
    val source: String,
    val url: String = "",
)