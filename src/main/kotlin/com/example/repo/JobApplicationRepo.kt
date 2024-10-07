package com.example.repo

import com.example.data.JobApplication
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.types.ObjectId

class JobApplicationRepo(private val collection: MongoCollection<JobApplication>) {
    suspend fun createJobApplication(jobApplication: JobApplication): Boolean {
        val jobWithUserId = jobApplication.copy(jobId = ObjectId.get())
        return collection.insertOne(jobWithUserId).wasAcknowledged()
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

        /*val result = collection.updateOne(
            Filters.eq("_id", id),
            Updates.combine(
                Updates.set("jobTitle", updatedJobApplication.jobTitle),
                Updates.set("companyName", updatedJobApplication.companyName),
                Updates.set("applicationDate", updatedJobApplication.applicationDate),
                Updates.set("status", updatedJobApplication.status),
            )
        )

        return result.modifiedCount > 0*/
    }

    suspend fun deleteJobApplication(id: ObjectId): Boolean {
        val result = collection.deleteOne(Filters.eq("_id", id))
        return result.deletedCount > 0
    }
}