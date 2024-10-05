package com.example.data

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId val userId: ObjectId? = null,
    val username: String,
    val passwordHash: String
)