package com.example

import com.example.plugins.*
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.Document

fun main() {

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    // Replace the placeholders with your credentials and hostname
    val connectionString = "mongodb+srv://nigeltheaustralian:<password>@cluster0.ahxpy.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
    val serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build()
    val mongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(connectionString))
        .serverApi(serverApi)
        .build()
    // Create a new client and connect to the server
    val mongoClient = MongoClient.create(mongoClientSettings)
    val database = mongoClient.getDatabase("sample_mflix")

    runBlocking {
        log.info("List of Collection Names: ${database.listCollectionNames().toList()}")
    }


    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureRouting(database)
}
