package pt.isel.gomoku.server.repository.interfaces

import pt.isel.gomoku.domain.Token
import pt.isel.gomoku.domain.User
import pt.isel.gomoku.server.http.model.user.UserIdAndName
import pt.isel.gomoku.server.http.model.user.UserNameAndAvatar
import java.time.LocalDateTime

interface UserRepository {

    fun createUser(name: String, email: String, password: String, avatarUrl: String?): Int

    fun getUserById(id: Int): UserNameAndAvatar?

    fun getUserByName(name: String): UserNameAndAvatar?

    fun getUserByEmail(email: String): User?

    fun getUsers(role: String? = null): List<UserIdAndName>

    fun updateUser(id: Int, name: String?, avatarUrl: String?)

    fun deleteUser(id: Int)

    fun createToken(tokenValue: String, userId: Int): Token

    fun getUserAndTokenByTokenValue(token: String): Pair<User, Token>?

    fun getTokenByUserId(userId: Int): Token?

    fun updateTokenLastUsed(token: Token, now: LocalDateTime)

    fun deleteToken(token: String)
}