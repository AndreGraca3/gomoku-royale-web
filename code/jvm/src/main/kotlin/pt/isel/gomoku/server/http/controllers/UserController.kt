package pt.isel.gomoku.server.http.controllers

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.gomoku.server.http.Uris
import pt.isel.gomoku.server.http.model.*
import pt.isel.gomoku.server.http.response.problem.UserProblem
import pt.isel.gomoku.server.http.response.siren.Siren
import pt.isel.gomoku.server.http.response.siren.actions.UserActions.getDeleteUserAction
import pt.isel.gomoku.server.http.response.siren.actions.UserActions.getUpdateUserAction
import pt.isel.gomoku.server.http.response.siren.SirenLink
import pt.isel.gomoku.server.pipeline.authorization.AuthenticationDetails
import pt.isel.gomoku.server.repository.dto.AuthenticatedUser
import pt.isel.gomoku.server.service.UserService
import pt.isel.gomoku.server.service.errors.user.UserCreationError
import pt.isel.gomoku.server.utils.Failure
import pt.isel.gomoku.server.utils.Success
import java.net.URI

@RestController
class UserController(private val service: UserService) {

    @PostMapping(Uris.Users.BASE)
    fun createUser(@RequestBody input: UserCreationInputModel): ResponseEntity<*> {
        return when (val res = service.createUser(input.name, input.email, input.password, input.avatarUrl)) {
            is Success -> UserIdOutputModel(id = res.value.id)
                .toSirenObject(
                    links = listOf(
                        SirenLink.self(href = Uris.Users.buildUuserByIdUri(res.value.id))
                    )
                ).toResponseEntity(201)

            is Failure -> when (res.value) {
                is UserCreationError.InsecurePassword -> UserProblem.InsecurePassword(res.value).toResponseEntity()
                is UserCreationError.EmailAlreadyInUse -> UserProblem.UserAlreadyExists(res.value).toResponseEntity()
                is UserCreationError.InvalidEmail -> UserProblem.InvalidEmail(res.value).toResponseEntity()
            }
        }
    }

    @GetMapping(Uris.Users.BASE)
    fun getUsers(@RequestParam role: String?, @RequestParam page: Int?, @RequestParam limit: Int?): ResponseEntity<*> {
        val usersInfo = service.getUsers(role, page, limit)
        return usersInfo.toSirenObject(
            links = Uris.Pagination.getPaginationSirenLinks(
                uri = URI(Uris.Users.BASE),
                page = usersInfo.page,
                limit = usersInfo.limit,
                pageSize = usersInfo.users.size,
                collectionSize = usersInfo.collectionSize,
            )
        ).toResponseEntity(200)
    }

    @GetMapping(Uris.Users.AUTHENTICATED_USER)
    fun getAuthenticatedUser(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val res = service.getUserByTokenValue(authenticatedUser.token.tokenValue)
        return if (res != null) UserInfoOutputModel(
            id = res.user.id,
            name = res.user.name,
            avatarUrl = res.user.avatarUrl,
            role = res.user.role,
            rank = res.user.rank,
            createdAt = res.user.createdAt,
        ).toSirenObject(
            links = listOf(
                SirenLink.self(href = Uris.Users.buildUuserByIdUri(res.user.id)),
                SirenLink(href = Uris.Stats.buildStatsByUserIdUri(res.user.id), rel = listOf("stats"))
            ),
            actions = listOf(
                getUpdateUserAction(res.user.id),
                getDeleteUserAction(),
            )
        ).toResponseEntity(200)
        else UserProblem.InvalidToken.toResponseEntity()
    }

    @GetMapping(Uris.Users.USER_BY_ID)
    fun getUserById(@PathVariable id: Int): ResponseEntity<*> {
        return when (val res = service.getUserById(id)) {
            is Success -> res.value.toOutputModel().toSirenObject(
                links = listOf(
                    SirenLink.self(href = Uris.Users.buildUuserByIdUri(res.value.id)),
                    SirenLink(href = Uris.Stats.buildStatsByUserIdUri(res.value.id), rel = listOf("stats"))
                )
            ).toResponseEntity(200)

            is Failure -> UserProblem.UserByIdNotFound(res.value).toResponseEntity()
        }
    }

    @PatchMapping(Uris.Users.BASE)
    fun updateUser(
        authenticatedUser: AuthenticatedUser,
        @RequestBody userInput: UserUpdateInputModel,
    ): ResponseEntity<*> {
        return when (val res =
            service.updateUser(authenticatedUser.user.id, userInput.name, userInput.avatarUrl)) {
            is Success -> Siren(
                properties = null,
                links = listOf(
                    SirenLink.self(href = Uris.Users.buildUuserByIdUri(authenticatedUser.user.id)),
                    SirenLink(href = Uris.Stats.buildStatsByUserIdUri(authenticatedUser.user.id), rel = listOf("stats"))
                ),
                actions = listOf(
                    getDeleteUserAction(),
                )
            ).toResponseEntity(200)

            is Failure -> UserProblem.InvalidValues(res.value).toResponseEntity()
        }
    }

    @DeleteMapping(Uris.Users.BASE)
    fun deleteUser(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        service.deleteUser(authenticatedUser.user.id)
        return ResponseEntity.status(200).body(null)
    }

    @PutMapping(Uris.Users.TOKEN)
    fun createToken(@RequestBody input: UserCredentialsInput, response: HttpServletResponse): ResponseEntity<*> {
        return when (val res = service.createToken(input.email, input.password)) {
            is Success -> {
                response.addCookie(
                    Cookie(
                        AuthenticationDetails.NAME_AUTHORIZATION_COOKIE,
                        res.value.tokenValue
                    ).apply {
                        path = "/"
                        isHttpOnly = true
                        maxAge = 3600
                    }
                )
                ResponseEntity.status(200).body(null)
            }

            is Failure -> UserProblem.InvalidCredentials(res.value).toResponseEntity()
        }
    }
}