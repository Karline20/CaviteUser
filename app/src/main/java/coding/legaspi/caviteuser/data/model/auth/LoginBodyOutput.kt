package coding.legaspi.caviteuser.data.model.auth

data class LoginBodyOutput(
    val id: String,
    val token: String,
    val username: String,
    val emailVerified: Boolean
)