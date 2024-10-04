package com.example.repo

import com.example.JobApplication
import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.types.ObjectId

class JobApplicationRepo(private val collection: MongoCollection<JobApplication>) {
    suspend fun createJobApplication(jobApplication: JobApplication) {
        val jobWithUserId = jobApplication.copy(userId = jobApplication.userId ?: ObjectId("64ff9c8e7a3b3e4f9c6a3b32"), jobId = ObjectId.get())
        collection.insertOne(jobWithUserId)
    }

    suspend fun getAllJobApplications(): List<JobApplication> {
        return collection.find().toList()
    }

    suspend fun getJobApplicationById(jobId: ObjectId): JobApplication? {
        return collection.find(Filters.eq("_id", jobId)).firstOrNull()
    }

    suspend fun getJobApplicationsByUserId(userId: ObjectId): List<JobApplication> {
        return collection.find(Filters.eq("userId", userId)).toList()
    }

    suspend fun updateJobApplication(id: ObjectId, updatedJobApplication: JobApplication): Boolean {
        val current = collection.find(Filters.eq("_id", id)).firstOrNull()
        val result = current?.let { updatedJobApplication.copy(userId = it.userId, jobId = it.jobId) }?.let {
            collection.replaceOne(Document("_id", id),
                it
            )
        }
        return result != null
    }

    suspend fun deleteJobApplication(id: ObjectId): Boolean {
        val result = collection.deleteOne(Filters.eq("_id", id))
        return result.deletedCount > 0
    }
}