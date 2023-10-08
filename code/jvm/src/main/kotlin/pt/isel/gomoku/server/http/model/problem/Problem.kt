package pt.isel.gomoku.server.http.model.problem

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

open class Problem(
    subType: String,
    val status: Int,
    val title: String,
    val detail: String? = null,
    val data: Any? = null
) {

    val type = "https://gomokyroyale.pt/probs/$subType"

    companion object {
        private val MEDIA_TYPE = MediaType.parseMediaType("application/problem+json")
    }

    fun response() = ResponseEntity
        .status(status)
        .contentType(MEDIA_TYPE)
        .body<Any>(this)
}