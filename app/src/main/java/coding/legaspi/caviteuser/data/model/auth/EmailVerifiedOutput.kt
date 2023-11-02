package coding.legaspi.caviteuser.data.model.auth

data class EmailVerifiedOutput(
    val email: String,
    val emailVerified: Boolean,
    val id: String,
    val realm: Any,
    val username: Any,
    val verificationToken: Any
)