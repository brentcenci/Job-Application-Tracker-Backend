package com.example.repo

import com.example.User
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

class UserRepo(private val collection: MongoCollection<User>) {

    suspend fun addUser(user: User) : ObjectId {
        val newUser = user.copy(userId = ObjectId())
        collection.insertOne(newUser)
        return newUser.userId!!
    }

    suspend fun getUserById(userId: String): User? {
        return collection.find(Filters.eq("_id", userId)).firstOrNull()
    }

    suspend fun getUserByUsername(username: String): User? {
        return collection.find(Filters.eq("username", username)).firstOrNull()
    }

    suspend fun updateUser(userId: ObjectId, updatedUser: User): Boolean {
        val result = collection.replaceOne(Filters.eq("_id", userId), updatedUser)
        return result.modifiedCount > 0
    }

    suspend fun deleteUser(userId: ObjectId): Boolean {
        val result = collection.deleteOne(Filters.eq("_id", userId))
        return result.deletedCount > 0
    }
}