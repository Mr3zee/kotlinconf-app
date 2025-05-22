package org.jetbrains.kotlinconf.backend.routes

import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcp
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.backend.services.SessionizeService

import org.koin.ktor.ext.inject

/*
GET http://localhost:8080/conference
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
fun Route.scheduleRoutes() {
    val sessionize: SessionizeService by inject()
    get("conference") {
        call.respond(sessionize.getConferenceData())
    }
}

fun Application.mcpRouting() {
    mcp {
        val sessionize: SessionizeService by inject()

        Server(
            serverInfo = Implementation(
                name = "example-sse-server",
                version = "1.0.0"
            ),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = false),
                )
            )
        ).apply {
            addTool(
                name = "list_events",
                description = "Return list of all events on KotlinConf"
            ) { _ ->
                val conference = sessionize.getConferenceData()
                val briefSummary = conference.sessions.map { session ->
                    BriefSession(
                        id = session.id,
                        title = session.title,
                        speakers = session.speakerIds.associateWith { speakerId ->
                            conference.speakers.first { it.id == speakerId }.name
                        },
                        startsAt = session.startsAt,
                        endsAt = session.endsAt,
                    )
                }

                val encoded = Json.encodeToString(briefSummary)

                CallToolResult(listOf(TextContent(encoded)))
            }

            addTool(
                "get_event_by_id",
                "Return all event info by its id",
                inputSchema = Tool.Input(
                    properties = buildJsonObject {
                        putJsonObject("id") {
                            put("type", "string")
                            put("description", "Id of the event")
                        }
                    },
                    required = listOf("id"),
                ),
            ) { request ->
                val eventId = (request.arguments["id"] as? JsonPrimitive)?.content ?: error("id argument is required")
                val event = sessionize.getConferenceData().sessions
                    .find { it.id.id == eventId }
                    ?: error("Event with id $eventId not found")

                val encoded = Json.encodeToString(event)

                CallToolResult(listOf(TextContent(encoded)))
            }
        }
    }
}

@Serializable
data class BriefSession(
    val id: SessionId,
    val title: String,
    val speakers: Map<SpeakerId, String>,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
)
