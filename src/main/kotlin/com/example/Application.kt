package com.example

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
import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDate

fun main() {

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun initMongoClient(): MongoDatabase {
    val connectionString = "mongodb+srv://nigeltheaustralian:2IBZBl3Inf3NJuZt@cluster0.ahxpy.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
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

data class JobApplication(
    @BsonId val jobId: ObjectId? = null,
    val userId: ObjectId = ObjectId("64ff9c8e7a3b3e4f9c6a3b32"),
    val jobTitle: String,
    val companyName: String,
    val applicationDate: String,
    val status: String,
)

data class User(
    @BsonId val userId: ObjectId? = null,
    val username: String,
    val passwordHash: String
)

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

    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureRouting(jobApplicationRepo, userRepo)
}
