package com.example.plugins

import com.example.JobApplication
import com.example.repo.JobApplicationRepo
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

fun Application.configureRouting(jobApplicationRepo: JobApplicationRepo) {
    routing {
        get("/jobs") {
            val jobs = jobApplicationRepo.getAllJobApplications()
            call.respond(jobs)
        }
        post("/jobs") {
            val jobApp = call.receive<JobApplication>()
            jobApplicationRepo.createJobApplication(jobApp)
            call.respond(HttpStatusCode.Created, jobApp)
        }
        get("/jobs/{id}") {
            val jobIdParam = call.parameters["id"]
            if (jobIdParam != null) {
                try {
                    val jobId = ObjectId(jobIdParam)
                    val job = jobApplicationRepo.getJobApplicationById(jobId)

                    if (job != null) {
                        call.respond(job)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Job Application Not Found: $jobIdParam")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid Job ID")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest,  "Job ID Missing")
            }
        }





        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
