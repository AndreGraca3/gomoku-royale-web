package pt.isel.gomoku.server.http.model.match

import java.util.*

data class MatchUpdateInput(
    val id : UUID,
    val winner: Int?
)


