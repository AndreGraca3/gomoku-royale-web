package pt.isel.gomoku.server.service.error.match

import pt.isel.gomoku.domain.game.Player
import pt.isel.gomoku.domain.game.cell.Dot

sealed class MatchPlayError : MatchError() {
    class InvalidTurn(val turn: Player) : MatchPlayError()
    class InvalidPlay(val dot: Dot) : MatchPlayError()
}