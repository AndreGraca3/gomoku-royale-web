package pt.isel.gomoku.server.service

import com.cloudinary.Cloudinary
import org.springframework.stereotype.Component
import pt.isel.gomoku.domain.MatchState
import pt.isel.gomoku.domain.Token
import pt.isel.gomoku.domain.User
import pt.isel.gomoku.server.http.model.UserIdOutputModel
import pt.isel.gomoku.server.http.model.UsersOutputModel
import pt.isel.gomoku.server.http.model.toOutputModel
import pt.isel.gomoku.server.repository.dto.AuthenticatedUser
import pt.isel.gomoku.server.repository.dto.UserInfo
import pt.isel.gomoku.server.repository.dto.UserDetails
import pt.isel.gomoku.server.repository.transaction.managers.TransactionManager
import pt.isel.gomoku.server.service.core.SecurityManager
import pt.isel.gomoku.server.service.errors.user.*
import pt.isel.gomoku.server.utils.Either
import pt.isel.gomoku.server.utils.failure
import pt.isel.gomoku.server.utils.success
import java.time.LocalDateTime

@Component
class UserService(
    private val trManager: TransactionManager,
    private val securityManager: SecurityManager,
    private val cloudinary: Cloudinary,
) {

    fun createUser(
        name: String,
        email: String,
        password: String,
        avatar: String?,
    ): Either<UserCreationError, UserIdOutputModel> {
        if (!securityManager.isSafePassword(password))
            return failure(UserCreationError.InsecurePassword(password))

        if (verifyEmail(email))
            return failure(UserCreationError.InvalidEmail(email))

        return trManager.run { transaction ->
            if (transaction.userRepository.getUserByEmail(email) != null)
                return@run failure(UserCreationError.EmailAlreadyInUse(email))

            val id = transaction.userRepository.createUser(
                name,
                email,
                password,
                avatar?.let {
                    cloudinary.uploader().upload(it, mapOf("folder" to "gomoku/avatars"))
                        .get("url") as String
                })
            transaction.statsRepository.createStatsEntry(id)
            success(UserIdOutputModel(id))
        }
    }

    fun getUsers(role: String? = null, skip: Int, limit: Int): UsersOutputModel {
        return trManager.run { transaction ->
            val usersCollection = transaction.userRepository.getUsers(role, skip, limit)
            UsersOutputModel(
                users = usersCollection.results.map { it.toOutputModel() },
                total = usersCollection.total,
            )
        }
    }

    fun getUserById(id: Int): Either<UserFetchingError.UserByIdNotFound, UserInfo> {
        return trManager.run {
            val user = it.userRepository.getUserById(id)
            if (user != null) success(user)
            else failure(UserFetchingError.UserByIdNotFound(id))
        }
    }

    fun updateUser(id: Int, newName: String?, newAvatar: String?): Either<UserUpdateError.InvalidValues, UserDetails> {
        if (newName?.isBlank() == true || newAvatar?.isBlank() == true)
            return failure(UserUpdateError.InvalidValues)

        val cloudinaryAvatarUrl = newAvatar?.let {
            try {
                cloudinary.uploader().upload(it, mapOf("folder" to "gomoku/avatars"))["url"] as String
            } catch (e: Exception) {
                return failure(UserUpdateError.InvalidValues)
            }
        }

        return trManager.run {
            success(it.userRepository.updateUser(id, newName, cloudinaryAvatarUrl))
        }
    }

    fun deleteUser(id: Int): Either<UserDeleteError, Unit> {
        return trManager.run {
            val matchStatus = it.matchRepository.getMatchStatusFromUser(id)
            if (matchStatus?.state == MatchState.ONGOING) {
                return@run failure(UserDeleteError.UserInAnOngoingMatch)
            }
            success(it.userRepository.deleteUser(id))
        }
    }

    fun createToken(email: String, password: String): Either<TokenCreationError.InvalidCredentials, Token> {
        if (email.isBlank() || password.isBlank())
            failure(TokenCreationError.InvalidCredentials(email, password))

        return trManager.run {
            val user: User = it.userRepository.getUserByEmail(email)
                ?: return@run failure(TokenCreationError.InvalidCredentials(email, password))

            if (password != user.password)
                return@run failure(TokenCreationError.InvalidCredentials(email, password))

            // if token already exists: if is valid, return it else delete it
            val token = it.userRepository.getTokenByUserId(user.id)
            if (token != null) {
                when (securityManager.isTokenTimeValid(token)) {
                    true -> {
                        it.userRepository.updateTokenLastUsed(token, LocalDateTime.now())
                        return@run success(token)
                    }

                    false -> it.userRepository.deleteToken(token.tokenValue)
                }
            }

            // No Token found, create new one
            val tokenValue = securityManager.generateTokenValue()
            success(it.userRepository.createToken(tokenValue, user.id))
        }
    }

    fun deleteToken(token: String) {
        return trManager.run {
            it.userRepository.deleteToken(token)
        }
    }


    // Helper function, does not return Either
    fun getUserByTokenValue(token: String): AuthenticatedUser? {
        return trManager.run {
            val userAndToken =
                it.userRepository.getUserAndTokenByTokenValue(token)
            if (userAndToken != null && securityManager.isTokenTimeValid(userAndToken.token)) {
                it.userRepository.updateTokenLastUsed(userAndToken.token, LocalDateTime.now())
                userAndToken
            } else null
        }
    }

    private fun verifyEmail(email: String) = !Regex("@").containsMatchIn(email)
}
