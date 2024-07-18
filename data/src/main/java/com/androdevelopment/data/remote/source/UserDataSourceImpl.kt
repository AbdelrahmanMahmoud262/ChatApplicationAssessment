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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
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
                        isActive = it.getBoolean("isActive") == true
                    )
                } ?:  User(
                id = "",
                firstName = "Unknown",
                lastName = "Unknown",
                isActive = false
            )
        }

        send(deferredResult.await())

        awaitClose {
            deferredResult.cancel()
        }
    }

    override fun checkUser(user: User): Result {
        TODO("Not yet implemented")
    }
}