package eu.mcomputing.mobv.zadanie.data

import android.content.Context
import com.mapbox.geojson.Point
import eu.mcomputing.mobv.zadanie.data.api.ApiService
import eu.mcomputing.mobv.zadanie.data.api.model.GeofenceUpdateRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserChangePasswordRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserLoginRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserRegistrationRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserResetPasswordRequest
import eu.mcomputing.mobv.zadanie.data.db.AppRoomDatabase
import eu.mcomputing.mobv.zadanie.data.db.LocalCache
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.data.model.User
import java.io.IOException
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.util.Random

class DataRepository private constructor(
    private val service: ApiService,
    private val cache: LocalCache
) {
    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(context: Context): DataRepository =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: DataRepository(
                        ApiService.create(context),
                        LocalCache(AppRoomDatabase.getInstance(context).appDao())
                    ).also { INSTANCE = it }
            }
    }

    suspend fun apiRegisterUser(
        username: String,
        email: String,
        password: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (email.isEmpty()) {
            return Pair("Email can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        try {
            val response = service.registerUser(UserRegistrationRequest(username, email, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.uid == "-1") {
                        return Pair("User username already exists!", null)
                    }
                    if (json_response.uid == "-2") {
                        return Pair("User email already exists!", null)
                    }
                    return Pair(
                        "",
                        User(
                            username,
                            email,
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to create user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to create user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to create user.", null)
    }

    suspend fun apiLoginUser(
        username: String,
        password: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        try {
            val response = service.loginUser(UserLoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.uid == "-1") {
                        return Pair("Wrong password or username.", null)
                    }
                    return Pair(
                        "",
                        User(
                            username,
                            "",
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to login user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to login user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to login user.", null)
    }

    suspend fun apiGetUser(
        uid: String
    ): Pair<String, User?> {
        try {
            val response = service.getUser(uid)

            if (response.isSuccessful) {
                response.body()?.let {
                    return Pair("", User(it.name, "", it.id, "", "", it.photo))
                }
            }

            return Pair("Failed to load user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to load user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load user.", null)
    }

    suspend fun apiResetUserPassword(
        email: String
    ): Pair<String, String?> {
        try {
            val response = service.resetUserPassword(UserResetPasswordRequest(email))

            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.status == "success")
                        return Pair(
                            it.status + ". Check your email!",
                            "Successful. Check your email!"
                        )
                    else
                        return Pair(it.status, it.message)
                }
            }

            return Pair("Failed to reset user password", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to reset user password.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to reset user password.", null)
    }

    suspend fun apiChangeUserPassword(
        old_password: String,
        new_password: String
    ): Pair<String, String?> {
        try {
            val response =
                service.changeUserPassword(UserChangePasswordRequest(old_password, new_password))

            if (response.isSuccessful) {
                response.body()?.let {
                    return Pair(it.status, null)
                }
            }

            return Pair("Failed to change user password", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to change user password.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to change user password.", null)
    }

    private fun generateRandomPoint(lon: Double?, lat: Double?, radius: Double?): Pair<Double, Double>  {
        // Convert Radius from meters to degrees.

        val rd = (radius!! / 111000f).toDouble()

        val random = Random()
        val u = random.nextDouble()
        val v = random.nextDouble()

        val w = rd * sqrt(u)
        val t = 2.0 * Math.PI * v
        val x = w * cos(t)
        val y = w * sin(t)

        val xp = x / cos(Math.toRadians(lat!!))

        val lonValue = lon!! + xp
        val latValue = lat + y

        return Pair(latValue, lonValue)
    }

    suspend fun apiGeofenceUsers(): String {
        try {
            val response = service.listGeofence()

            if (response.isSuccessful) {
                val myLat = response.body()?.me?.lat
                val myLon = response.body()?.me?.lon
                val myRadius = response.body()?.me?.radius
                response.body()?.list?.let {
                    val users = it.map {
                        val randPoint = generateRandomPoint(myLon, myLat, myRadius)

                        UserEntity(
                            it.uid, it.name, it.updated,
                            randPoint.first, randPoint.second, it.radius, it.photo
                        )
                    }

                    cache.insertUserItems(users)

                    return ""
                }
            }

            return "Failed to load user"
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to load user."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error apiGeofenceUsers. Failed to load user."
    }

    fun getUsers() = cache.getUsers()

    suspend fun getUsersList() = cache.getUsersList()
    suspend fun getUser(uid: String) = cache.getUserItem(uid)

    suspend fun logoutUser() = cache.logoutUser()

    suspend fun insertGeofence(item: GeofenceEntity) {
        cache.insertGeofence(item)
        try {
            val response =
                service.updateGeofence(GeofenceUpdateRequest(item.lat, item.lon, item.radius))

            apiGeofenceUsers()

            if (response.isSuccessful) {
                response.body()?.let {

                    item.uploaded = true
                    cache.insertGeofence(item)
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun removeGeofence() {
        try {
            val response = service.deleteGeofence()

            if (response.isSuccessful) {
                apiGeofenceUsers()
                response.body()?.let {
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}