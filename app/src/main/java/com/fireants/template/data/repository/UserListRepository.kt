package com.fireants.template.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.fireants.template.data.database.UsersDatabase
import com.fireants.template.data.database.asDomainModel
import com.fireants.template.data.domain.UserListItem
import com.fireants.template.data.network.UserListService
import com.fireants.template.data.network.model.asDatabaseModel
import timber.log.Timber
import javax.inject.Inject

class UserListRepository @Inject constructor(
    private val userListService: UserListService,
    private val database: UsersDatabase,
) {

    val users: LiveData<List<UserListItem>> =
        database.usersDao.getDatabaseUsers().map {
            it.asDomainModel()
        }

    suspend fun refreshUserList() {
        try {
            val users = userListService.getUserList()
            database.usersDao.insertAll(users.asDatabaseModel())
        } catch (e: Exception) {
            Timber.w(e)
        }
    }
}