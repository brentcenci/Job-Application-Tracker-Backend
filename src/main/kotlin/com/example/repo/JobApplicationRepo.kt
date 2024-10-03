package com.example.repo

import com.example.JobApplication
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.types.ObjectId

class JobApplicationRepo(private val collection: MongoCollection<JobApplication>) {
    suspend fun createJobApplication(jobApplication: JobApplication) {
        collection.insertOne(jobApplication)
    }

    suspend fun getAllJobApplications(): List<JobApplication> {
        return collection.find().toList()
    }

    suspend fun getJobApplicationById(jobId: ObjectId): JobApplication? {
        return collection.find(Filters.eq("_id", jobId)).firstOrNull()
    }

    suspend fun updateJobApplication(id: String, updatedJobApplication: JobApplication) {
        collection.replaceOne(Document("_id", id), updatedJobApplication)
    }

    suspend fun deleteJobApplication(id: String) {
        collection.deleteOne(Document("_id", id))
    }
}