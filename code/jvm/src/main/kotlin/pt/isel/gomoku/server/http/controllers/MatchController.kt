package pt.isel.gomoku.server.http.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.gomoku.domain.game.cell.Dot
import pt.isel.gomoku.server.http.Uris
import pt.isel.gomoku.server.http.model.match.MatchCreationInput
import pt.isel.gomoku.server.http.model.problem.MatchProblem
import pt.isel.gomoku.server.http.model.user.AuthenticatedUser
import pt.isel.gomoku.server.service.MatchService
import pt.isel.gomoku.server.service.error.match.MatchCreationError
import pt.isel.gomoku.server.service.error.match.MatchFetchingError
import pt.isel.gomoku.server.service.error.match.MatchJoiningError
import pt.isel.gomoku.server.utils.Failure
import pt.isel.gomoku.server.utils.Success

@RestController
@RequestMapping(Uris.Matches.BASE)
class MatchController(private val service: MatchService) {

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun createMatch(@RequestBody input: MatchCreationInput, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val res =
            service.createMatch(authenticatedUser.user.id, input.isPrivate, input.size, input.variant)
        ) {
            is Success -> ResponseEntity.status(201).body(res.value)
            is Failure -> when (res.value) {
                is MatchCreationError.InvalidVariant -> MatchProblem.InvalidVariant(res.value).response()
                is MatchCreationError.InvalidBoardSize -> MatchProblem.InvalidBoardSize(res.value).response()
                is MatchCreationError.AlreadyInMatch -> MatchProblem.AlreadyInMatch(res.value).response()
                is MatchCreationError.InvalidPrivateMatch -> MatchProblem.InvalidPrivateMatch(res.value).response()
            }
        }
    }

    @PostMapping("${Uris.ID}${Uris.Matches.JOIN}")
    fun joinPrivateMatch(@PathVariable id: String, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val res = service.joinPrivateMatch(id, authenticatedUser.user.id)) {
            is Success -> ResponseEntity.status(201).body(res.value)
            is Failure -> when (res.value) {
                is MatchFetchingError.MatchByIdNotFound -> MatchProblem.MatchNotFound(res.value).response()
                is MatchJoiningError.MatchIsNotPrivate -> MatchProblem.MatchIsNotPrivate(res.value).response()
                is MatchCreationError.AlreadyInMatch -> MatchProblem.AlreadyInMatch(res.value).response()
                else -> throw Exception()
            }
        }
    }

    @GetMapping(Uris.ID)
    fun getMatchById(@PathVariable id: String, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val res = service.getMatchById(id, authenticatedUser.user.id)) {
            is Success -> ResponseEntity.status(200).body(res.value)
            is Failure -> when (res.value) {
                is MatchFetchingError.MatchByIdNotFound -> MatchProblem.MatchNotFound(res.value).response()
                is MatchFetchingError.UserNotInMatch -> MatchProblem.UserNotInMatch(res.value).response()
            }
        }
    }

    @GetMapping()
    fun getMatchesFromUser(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return ResponseEntity.status(200).body(service.getMatchesFromUser(authenticatedUser.user.id))
    }

    @PostMapping(Uris.ID)
    fun play(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: String,
        @RequestBody move: Dot
    ): ResponseEntity<*> {
        return when (val res = service.play(authenticatedUser.user.id, id, move)) {
            is Success -> ResponseEntity.status(200).build<Unit>()
            is Failure -> when (res.value) {
                is MatchFetchingError.MatchByIdNotFound -> MatchProblem.MatchNotFound(res.value).response()
                is MatchFetchingError.UserNotInMatch -> MatchProblem.UserNotInMatch(res.value).response()
            }
        }
    }
}