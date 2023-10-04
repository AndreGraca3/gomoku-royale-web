package pt.isel.gomoku.domain

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val role: Role = Role.USER,
    val avatarUrl: String?,
    val rank: String
)

enum class Role(val value: String) {
    USER("user"), DEV("dev")
}