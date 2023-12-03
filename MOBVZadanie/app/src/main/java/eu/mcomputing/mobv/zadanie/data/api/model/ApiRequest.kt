package eu.mcomputing.mobv.zadanie.data.api.model

data class UserRegistrationRequest(val name: String, val email: String, val password: String)

data class UserLoginRequest(val name: String, val password: String)
data class UserResetPasswordRequest(val email: String)

data class UserChangePasswordRequest(val old_password: String, val new_password: String)
data class RefreshTokenRequest(val refresh: String)

data class GeofenceUpdateRequest(val lat: Double, val lon: Double, val radius: Double)