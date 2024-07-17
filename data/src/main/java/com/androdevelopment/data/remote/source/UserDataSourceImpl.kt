package com.androdevelopment.data.remote.source

import com.androdevelopment.domain.entity.Result
import com.androdevelopment.data.repository.source.UserDataSource
import com.androdevelopment.data.utlis.Constants
import com.androdevelopment.domain.entity.User
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(

) : UserDataSource {

    private val db = FirebaseFirestore.getInstance()

    override fun insertUser(user: User): Result {
        db.collection(Constants.USER_COLLECTION)
            .add(user)

        return Result.Success(Unit)
    }
}