package com.example

import com.example.data.JobApplication
import com.example.data.User
import com.example.plugins.*
import com.example.repo.JobApplicationRepo
import com.example.repo.UserRepo
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*

fun main() {

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun initMongoClient(): MongoDatabase {
    val connectionString = System.getenv("CONNECTION_STRING") ?: throw IllegalArgumentException("Missing CONNECTION_STRING env variable")
    val serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build()
    val mongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(connectionString))
        .serverApi(serverApi)
        .build()
    val mongoClient = MongoClient.create(mongoClientSettings)
    return mongoClient.getDatabase("job_application_tracker")
}

fun getJobApplicationsCollection(database: MongoDatabase): MongoCollection<JobApplication> {
    return database.getCollection<JobApplication>("job_applications")
}

fun getUsersCollection(database: MongoDatabase): MongoCollection<User> {
    return database.getCollection<User>("users")
}

fun Application.module() {

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }

    val database = initMongoClient()
    val jobApplicationRepo = JobApplicationRepo(getJobApplicationsCollection(database))
    val userRepo = UserRepo(getUsersCollection(database))

    configureCors()
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureRouting(jobApplicationRepo, userRepo)

}
