package pt.isel.gomoku.server.service.error.user

sealed class UserFetchingError {
    class UserByIdNotFound(val id: Int) : UserFetchingError()
    class UserByTokenNotFound(val token: String): UserFetchingError()
}