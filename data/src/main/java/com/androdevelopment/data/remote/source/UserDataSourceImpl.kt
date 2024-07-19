package com.androdevelopment.data.remote.source

import com.androdevelopment.data.repository.source.UserDataSource
import com.androdevelopment.data.utlis.Constants
import com.androdevelopment.domain.entity.Result
import com.androdevelopment.domain.entity.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(

) : UserDataSource {

    private val db = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun insertUser(user: User): Result {
        db.collection(Constants.USER_COLLECTION)
            .add(user)

        return Result.Success(Unit)
    }

    override fun getUser(id: String): Flow<User> = channelFlow {
        val deferredResult = async {
            db.collection(Constants.USER_COLLECTION)
                .whereEqualTo(Constants.ID, id)
                .limit(1)
                .get()
                .await()
                .documents.firstOrNull()?.let {
                    User(
                        id = it.getString(Constants.ID) ?: "",
                        firstName = it.getString("firstName") ?: "",
                        lastName = it.getString("lastName") ?: "",
                        email = it.getString("email") ?: "",
                        password = it.getString("password") ?: "",
                        isActive = it.getBoolean("isActive") == true
                    )
                } ?:  User(
                id = "",
                firstName = "Unknown",
                lastName = "Unknown",
                email = "Unknown",
                password = "Unknown",
                isActive = false
            )
        }

        send(deferredResult.await())

        awaitClose {
            deferredResult.cancel()
        }
    }

    override fun validateUser(email: String, password: String): Flow<User?> = channelFlow{
        db.collection(Constants.USER_COLLECTION)
            .whereEqualTo(Constants.EMAIL,email)
            .whereEqualTo(Constants.PASSWORD,password)
            .limit(1)
            .get()
            .await()
            .documents.firstOrNull()?.let {
                send(
                    User(
                        id = it.getString(Constants.ID) ?: "",
                        firstName = it.getString("firstName") ?: "",
                        lastName = it.getString("lastName") ?: "",
                        email = it.getString("email") ?: "",
                        password = it.getString("password") ?: "",
                        isActive = it.getBoolean("isActive") == true
                    )
                )
            }
    }

    override fun getUsers(): Flow<List<User>> = callbackFlow {
        val deferredListener = db.collection(Constants.USER_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val users = value?.documents?.map {
                    User(
                        id = it.getString(Constants.ID) ?: "",
                        firstName = it.getString("firstName") ?: "",
                        lastName = it.getString("lastName") ?: "",
                        email = it.getString("email") ?: "",
                        password = it.getString("password") ?: "",
                        isActive = it.getBoolean("active") == true
                    )
                } ?: emptyList()

                scope.launch {
                    send(users)
                }
            }

        awaitClose{
            deferredListener.remove()
            scope.cancel()

        }
    }
}