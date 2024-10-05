package com.example.data

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class JobApplication(
    @BsonId val jobId: ObjectId? = null,
    val userId: ObjectId = ObjectId("64ff9c8e7a3b3e4f9c6a3b32"),
    val jobTitle: String,
    val companyName: String,
    val applicationDate: String,
    val status: String,
)