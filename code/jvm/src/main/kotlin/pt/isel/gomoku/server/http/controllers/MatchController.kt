package pt.isel.gomoku.server.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.gomoku.domain.MatchState
import pt.isel.gomoku.domain.game.cell.Dot
import pt.isel.gomoku.server.http.Uris
import pt.isel.gomoku.server.http.Uris.Pagination.getPaginationSirenLinks
import pt.isel.gomoku.server.http.model.MatchCreationInputModel
import pt.isel.gomoku.server.http.model.MatchOutputModel
import pt.isel.gomoku.server.http.model.PaginationInputs
import pt.isel.gomoku.server.http.response.problem.MatchProblem
import pt.isel.gomoku.server.http.response.problem.ServerProblem
import pt.isel.gomoku.server.http.response.siren.*
import pt.isel.gomoku.server.http.response.siren.actions.MatchActions.getDeleteMatchAction
import pt.isel.gomoku.server.http.response.siren.actions.MatchActions.getForfeitMatchAction
import pt.isel.gomoku.server.http.response.siren.actions.MatchActions.getJoinPrivateMatchAction
import pt.isel.gomoku.server.http.response.siren.actions.MatchActions.getPlayMatchAction
import pt.isel.gomoku.server.repository.dto.AuthenticatedUser
import pt.isel.gomoku.server.service.MatchService
import pt.isel.gomoku.server.service.errors.match.MatchCreationError
import pt.isel.gomoku.server.service.errors.match.MatchFetchingError
import pt.isel.gomoku.server.service.errors.match.MatchStateError
import pt.isel.gomoku.server.utils.Failure
import pt.isel.gomoku.server.utils.Success
import java.net.URI

@RestController
class MatchController(private val service: MatchService) {

    @PostMapping(Uris.Matches.BASE)
    fun createMatch(
        @RequestBody input: MatchCreationInputModel,
        authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<*> {
        return when (val res =
            service.createMatch(authenticatedUser.user.id, input.isPrivate, input.size, input.variant)
        ) {
            is Success -> {
                res.value.toSirenObject(
                    links = listOf(
                        SirenLink.self(href = Uris.Matches.buildMatchByIdUri(res.value.id))
                    ),
                    actions = when (res.value.state) {
                        MatchState.SETUP -> listOf(
                            getJoinPrivateMatchAction(res.value.id),
                            getDeleteMatchAction()
                        )

                        MatchState.ONGOING -> listOf(getPlayMatchAction(res.value.id))
                        else -> null
                    },
                ).toResponseEntity(201)
            }

            is Failure -> when (res.value) {
                is MatchCreationError.InvalidVariant -> MatchProblem.InvalidVariant(res.value).toResponseEntity()
                is MatchCreationError.InvalidBoardSize -> MatchProblem.InvalidBoardSize(res.value).toResponseEntity()
                is MatchCreationError.UserAlreadyPlaying -> MatchProblem.AlreadyInMatch(res.value).toResponseEntity()
                is MatchCreationError.InvalidPrivateMatch -> MatchProblem.InvalidPrivateMatch(res.value)
                    .toResponseEntity()
            }
        }
    }

    @PutMapping(Uris.Matches.JOIN_PRIVATE_MATCH)
    fun joinPrivateMatch(@PathVariable id: String, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val res = service.joinPrivateMatch(id, authenticatedUser.user.id)) {
            is Success -> res.value.toSirenObject(
                links = listOf(
                    SirenLink.self(href = Uris.Matches.buildMatchByIdUri(res.value.id))
                ),
                actions = listOf(
                    getPlayMatchAction(res.value.id)
                ),
            ).toResponseEntity(200)

            is Failure -> when (res.value) {
                is MatchFetchingError.MatchByIdNotFound -> MatchProblem.MatchNotFound(res.value).toResponseEntity()
                is MatchStateError.AlreadyStarted -> MatchProblem.AlreadyStarted(res.value).toResponseEntity()
                is MatchStateError.MatchIsNotPrivate -> MatchProblem.IsNotPrivate(res.value).toResponseEntity()
                else -> ServerProblem.InternalServerError().toResponseEntity()
            }
        }
    }

    @GetMapping(Uris.Matches.MATCH_BY_ID)
    fun getMatchById(@PathVariable id: String, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val res = service.getMatchById(id, authenticatedUser.user.id)) {
            is Success -> {
                val actions = mutableListOf<SirenAction>()
                when (res.value.state) {
                    MatchState.SETUP -> {
                        actions.add(getDeleteMatchAction())
                        if (res.value.isPrivate) actions.add(getJoinPrivateMatchAction(res.value.id))
                    }

                    MatchState.ONGOING -> {
                        actions.add(getPlayMatchAction(res.value.id))
                        actions.add(getForfeitMatchAction(res.value.id))
                    }

                    else -> Unit
                }

                val entities = listOfNotNull(
                    EmbeddedLink(
                        clazz = listOf(SirenClass.match),
                        rel = listOf("playerBlack"),
                        href = Uris.Users.buildUserByIdUri(res.value.blackId),
                    ),
                    if (res.value.whiteId != null)
                        EmbeddedLink(
                            clazz = listOf(SirenClass.match),
                            rel = listOf("playerWhite"),
                            href = Uris.Users.buildUserByIdUri(res.value.whiteId),
                        )
                    else null
                )

                MatchOutputModel(
                    res.value.id,
                    res.value.blackId,
                    res.value.whiteId,
                    res.value.isPrivate,
                    res.value.variant.toString(),
                    res.value.state,
                    res.value.board,
                ).toSirenObject(
                    links = listOf(
                        SirenLink.self(href = Uris.Matches.buildMatchByIdUri(res.value.id))
                    ),
                    entities = entities,
                    actions = actions,
                ).toResponseEntity(200)
            }

            is Failure -> when (res.value) {
                is MatchFetchingError.MatchByIdNotFound -> MatchProblem.MatchNotFound(res.value).toResponseEntity()
                is MatchFetchingError.UserNotInMatch -> MatchProblem.UserNotInMatch(res.value).toResponseEntity()
            }
        }
    }

    @GetMapping(Uris.Matches.BASE)
    fun getMatchesFromUser(
        authenticatedUser: AuthenticatedUser,
        paginationInputs: PaginationInputs,
    ): ResponseEntity<*> {
        val matchesCollection =
            service.getMatchesFromUser(authenticatedUser.user.id, paginationInputs.skip, paginationInputs.limit)

        return matchesCollection.toSirenObject(
            links = getPaginationSirenLinks(
                URI(Uris.Matches.BASE),
                paginationInputs.skip,
                paginationInputs.limit,
                matchesCollection.total
            ),
        ).toResponseEntity(200)
    }

    @PostMapping(Uris.Matches.PLAY)
    fun play(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: String,
        @RequestBody move: Dot,
    ): ResponseEntity<*> {
        return when (val res = service.play(authenticatedUser.user.id, id, move)) {
            is Success ->
                res.value.toSirenObject(
                    links = listOf(
                        SirenLink.self(href = Uris.Matches.buildMatchByIdUri(id))
                    ),
                ).toResponseEntity(200)

            is Failure -> when (res.value) {
                is MatchFetchingError.MatchByIdNotFound -> MatchProblem.MatchNotFound(res.value).toResponseEntity()
                is MatchFetchingError.UserNotInMatch -> MatchProblem.UserNotInMatch(res.value).toResponseEntity()
                is MatchStateError.MatchIsNotOngoing -> MatchProblem.NotOngoing(res.value).toResponseEntity()
                else -> ServerProblem.InternalServerError().toResponseEntity()
            }
        }
    }

    @PutMapping(Uris.Matches.FORFEIT)
    fun forfeit(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: String,
    ): ResponseEntity<*> {
        return when (val res = service.forfeit(authenticatedUser.user.id, id)) {
            is Success -> res.value.toSirenObject(
                links = listOf(
                    SirenLink.self(href = Uris.Matches.buildMatchByIdUri(id))
                ),
            ).toResponseEntity(200)

            is Failure -> when (res.value) {
                is MatchFetchingError.MatchByIdNotFound -> MatchProblem.MatchNotFound(res.value).toResponseEntity()
                is MatchFetchingError.UserNotInMatch -> MatchProblem.UserNotInMatch(res.value).toResponseEntity()
                is MatchStateError.MatchIsNotOngoing -> MatchProblem.NotOngoing(res.value).toResponseEntity()
                else -> ServerProblem.InternalServerError().toResponseEntity()
            }
        }
    }

    @DeleteMapping(Uris.Matches.BASE)
    fun deleteSetupMatch(
        authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<*> {
        service.deleteSetupMatch(authenticatedUser.user.id)
        return ResponseEntity.status(200).body(
            Siren<Nothing>()
        )
    }
}