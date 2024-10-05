package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.Credentials
import com.example.data.JobApplication
import com.example.repo.JobApplicationRepo
import com.example.repo.UserRepo
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

fun Application.configureRouting(jobApplicationRepo: JobApplicationRepo, userRepo: UserRepo) {
    routing {
        //JOBS
        authenticate {
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
            put("/jobs/{id}") {

                //Get the Job Id from the call parameters, if not found return with BadRequest
                val jobIdParam = call.parameters["id"]
                if (jobIdParam == null) {
                    call.respond(HttpStatusCode.BadRequest, "Job ID Missing")
                    return@put
                }

                // Convert the jobId to ObjectId format, otherwise if not valid return with BadRequest
                val jobId = try {
                    ObjectId(jobIdParam)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid Job ID")
                    return@put
                }

                // Get the updated job details from the request body, otherwise return with BadRequest
                val updatedJob = try{
                    call.receive<JobApplication>()
                } catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid Request Body")
                    return@put
                }

                // If Job Application does not exist, return with NotFound
                if (jobApplicationRepo.getJobApplicationById(jobId) == null) {
                    call.respond(HttpStatusCode.NotFound, "Job Application Not Found")
                    return@put
                }

                // Attempt to update the Job Application, return with OK if success or InternalServerError if failed
                val result = jobApplicationRepo.updateJobApplication(jobId, updatedJob)
                if (result) {
                    call.respond(HttpStatusCode.OK, "Job Application Updated")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Unsuccessful Job Application Update")
                }
            }
            delete("/jobs/{id}") {
                //Get the Job Id from the call parameters, if not found return with BadRequest
                val jobIdParam = call.parameters["id"]
                if (jobIdParam == null) {
                    call.respond(HttpStatusCode.BadRequest, "Job ID Missing")
                    return@delete
                }

                // Convert the jobId to ObjectId format, otherwise if not valid return with BadRequest
                val jobId = try {
                    ObjectId(jobIdParam)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Job ID")
                    return@delete
                }

                // If Job Application does not exist, return with NotFound
                if (jobApplicationRepo.getJobApplicationById(jobId) == null) {
                    call.respond(HttpStatusCode.NotFound, "Job Application Not Found")
                    return@delete
                }

                val result = jobApplicationRepo.deleteJobApplication(jobId)
                if (result) {
                    call.respond(HttpStatusCode.OK, "Job Application Deleted")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to delete Job Application")
                }
            }
        }


        //LOGIN
        post("/login") {
            val credentials = call.receive<Credentials>()
            if (credentials.username == "user" && credentials.password == "pass") {
                val token = JWT.create()
                    .withAudience("jwt-audience")
                    .withIssuer("https://jwt-provider-domain/")
                    .withClaim("username", credentials.username)
                    .sign(Algorithm.HMAC256("secret"))
                call.respond(hashMapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid Credentials")
            }
        }




        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
