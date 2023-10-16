package pt.isel.gomoku.server.repository.transaction

import org.jdbi.v3.core.Handle
import pt.isel.gomoku.server.repository.jdbi.repositories.JdbiMatchRepository
import pt.isel.gomoku.server.repository.jdbi.repositories.JdbiLobbyRepository
import pt.isel.gomoku.server.repository.jdbi.repositories.JdbiUserRepository

class JdbiTransaction(private val handle: Handle) : Transaction {

    override val userRepository = JdbiUserRepository(handle)
    override val matchRepository = JdbiMatchRepository(handle)
    override val lobbyRepository = JdbiLobbyRepository(handle)

    override fun rollback() {
        handle.rollback()
    }
}