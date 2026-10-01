
package com.example.netpulse.data.remote

import retrofit2.http.GET

data class ServerDto(
    val id: String,
    val name: String,
    val region: String,
    val baseUrl: String,
    val weight: Int = 1,
)

data class ServerListDto(
    val version: Int,
    val servers: List<ServerDto>,
)

interface SpeedApi {
    @GET("api/v1/servers")
    suspend fun servers(): ServerListDto
}
