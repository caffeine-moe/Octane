package org.caffeine.octane.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import org.caffeine.octane.client.config.ClientConfig
import org.caffeine.octane.client.user.BaseClientUser

interface Client : CoroutineScope {

    val user : BaseClientUser
    val token : String
    val configuration : ClientConfig
    val events : SharedFlow<ClientEvent>
    suspend fun login()
    suspend fun logout()
    suspend fun restart()
    suspend fun destroy()

    companion object Builder {

        fun config(config : ClientConfig.() -> Unit) : ClientConfig = ClientConfig().also(config)

        fun build(config : Builder.() -> ClientConfig) : Client {
            return ClientImpl(config.invoke(this))
        }
    }
}