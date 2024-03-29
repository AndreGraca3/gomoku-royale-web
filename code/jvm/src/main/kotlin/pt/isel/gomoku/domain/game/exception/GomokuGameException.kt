package pt.isel.gomoku.domain.game.exception

import pt.isel.gomoku.domain.game.Player
import pt.isel.gomoku.domain.game.cell.Dot

sealed class GomokuGameException(override val message: String) : Exception(message) {

    class InvalidPlay(val dst: Dot, lazyMessage: () -> String) : GomokuGameException(lazyMessage())

    class InvalidTurn(val turn: Player, lazyMessage: () -> String) : GomokuGameException(lazyMessage())

    class AlreadyFinished(lazyMessage: () -> String) : GomokuGameException(lazyMessage())

    class InvalidBoardSize(val variant: String, val size: Int, val sizes: List<Int>) :
        GomokuGameException("Invalid size for $variant variant. Valid sizes are $sizes.")

    class InvalidVariant(val variant: String) : GomokuGameException("Invalid variant: $variant.")

    class NotEnoughPlayers(val matchId: String) : GomokuGameException("Match with $matchId didn't started yet..." )
}

fun requireOrThrow(condition: Boolean, exception: GomokuGameException) {
    if (!condition) throw exception
}