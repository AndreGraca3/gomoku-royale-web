package pt.isel.gomoku.server.repository.interfaces

import pt.isel.gomoku.server.http.model.match.MatchCreateInputModel
import pt.isel.gomoku.server.http.model.match.MatchCreationOut
import pt.isel.gomoku.server.http.model.match.MatchOut
import pt.isel.gomoku.server.http.model.match.MatchOutDev
import java.util.*

interface MatchRepository {
    fun createMatch(visibility: String, board: String, variant: String, player1_id: Int): MatchCreationOut
    fun getMatchById(id: UUID): MatchOut?
    fun getMatchDev(id : UUID) : MatchOutDev?
    fun updateMatch(id: UUID, newVisibility: String?, newWinner: Int?) : Unit
}