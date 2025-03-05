# Job Application Kotlin / Ktor Backend
<hr/>

## Overview 📝📌

This is the backend server for my Job Application Tracker project that handles interacting with my MongoDB cluster and facilitates API calls via Ktor, which works in conjunction with the React JS Dashboard frontend (see https://github.com/brentcenci/Job-Application-Tracker-Frontend).

The server includes the following features:
- Connect to an existing MongoDB cluster (via a secret connection string)
- Allow users to log in or register (encrypts their password)
- Allow users to retrieve their lists of jobs
- Allow users to create jobs
- Allow users to update jobs
- Allow users to delete jobs

## Technologies 💻

It is built in the Kotlin programming language, leveraging the Ktor framework for developing asynchronous server-side and client-side applications.

For storing and retrieving data, I used MongoDB Atlas for its simple NoSQL structure.
