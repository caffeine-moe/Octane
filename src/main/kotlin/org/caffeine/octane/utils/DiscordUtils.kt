package org.caffeine.octane.utils

import SerialGuild
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.client.connection.OPCODE
import org.caffeine.octane.client.connection.payloads.client.bot.identify.IdentifyDProperties
import org.caffeine.octane.client.connection.payloads.client.resume.Resume
import org.caffeine.octane.client.connection.payloads.client.resume.ResumeD
import org.caffeine.octane.client.connection.payloads.client.user.identify.Identify
import org.caffeine.octane.client.connection.payloads.client.user.identify.IdentifyD
import org.caffeine.octane.client.connection.payloads.client.user.identify.IdentifyDClientState
import org.caffeine.octane.client.connection.payloads.client.user.identify.IdentifyDPresence
import org.caffeine.octane.client.connection.payloads.gateway.*
import org.caffeine.octane.client.connection.payloads.gateway.ready.Ready
import org.caffeine.octane.client.connection.payloads.gateway.ready.ReadyDCustomStatus
import org.caffeine.octane.client.connection.payloads.gateway.ready.ReadyDUserSettings
import org.caffeine.octane.client.user.*
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.asSnowflake
import org.caffeine.octane.entities.channels.*
import org.caffeine.octane.entities.guild.*
import org.caffeine.octane.entities.message.*
import org.caffeine.octane.entities.users.User
import org.caffeine.octane.entities.users.UserImpl
import org.caffeine.octane.typedefs.*
import java.text.SimpleDateFormat
import java.util.*
import org.caffeine.octane.client.connection.payloads.client.bot.identify.Identify as BotIdentify
import org.caffeine.octane.client.connection.payloads.client.bot.identify.IdentifyD as BotIdentifyD

class DiscordUtils(val client : ClientImpl) {

    @Serializable
    data class SuperProperties(
        var os : String = "",
        var browser : String = "",
        var device : String = "",
        var system_locale : String = "",
        var browser_user_agent : String = "",
        var browser_version : String = "",
        var os_version : String = "",
        var referrer : String = "",
        var referring_domain : String = "",
        var referrer_current : String = "",
        var referring_domain_current : String = "",
        var release_channel : String = "",
        var client_build_number : Int = 0,
        var client_event_source : String? = null,
    )

    suspend fun tokenValidator(token : String) {
        val headers = HeadersBuilder().also {
            it.append("Authorization", token)
        }
        client.httpClient.get("$BASE_URL/users/@me", headers)
    }

    val parametersFromFilters : (MessageFilters) -> String = { filters ->
        var parameters = ""
        if (!filters.beforeId.isZero()) parameters += "before=${filters.beforeId}&"
        if (!filters.afterId.isZero()) parameters += "after=${filters.afterId}&"
        if (!filters.authorId.isZero()) parameters += "author_id=${filters.authorId}&"
        if (!filters.mentioningUserId.isZero()) parameters += "mentions=${filters.mentioningUserId}&"
        parameters
    }

    suspend fun fetchMessages(channel : TextCapableChannel, filters : MessageFilters) : List<Message> {
        val collection : MutableList<Message> = arrayListOf()
        try {
            if (filters.authorId == client.user.id && filters.beforeId.toString().isBlank()) {
                fetchLastMessageInChannel(channel, client.user, MessageSearchFilters())?.let {
                    collection.add(it)
                    filters.beforeId = it.id
                }
            }
            while (true) {
                val parameters = parametersFromFilters(filters)
                val response =
                    client.httpClient.get("$BASE_URL/channels/${channel.id}/messages?$parameters").await()
                val newMessages = json.decodeFromString<MutableList<SerialMessage>>(response)
                newMessages.removeIf {
                    !filters.authorId.isZero() && it.author.id != filters.authorId.toString()
                }
                if (newMessages.size == 0) {
                    val lastmessage = fetchLastMessageInChannel(
                        channel,
                        client.user,
                        MessageSearchFilters(beforeId = filters.beforeId)
                    )
                        ?: return collection
                    collection += lastmessage
                } else {
                    collection += newMessages.map { createMessage(it) }
                }

                filters.beforeId = collection.last().id

                if (filters.needed != 0 && collection.size >= filters.needed) {
                    break
                }

                delay(500)
            }
        } catch (e : Exception) {
            log("Error: ${e.message}", "API:", LogLevel(LoggerLevel.LOW, client))
            e.printStackTrace()
        }
        return if (collection.size <= filters.needed) {
            collection
        } else {
            collection.take(filters.needed)
        }
    }

    fun createRole(role : SerialRole, guild : Guild) : Role {
        return RoleImpl(
            client,
            role.color,
            role.hoist,
            role.icon,
            role.id.asSnowflake(),
            role.managed,
            role.mentionable,
            role.name,
            extractPermissions(role.permissions),
            role.position,
            role.unicode_emoji
        ).also { it.guild = guild }
    }

    fun createGuildMember(payload : SerialGuildMember, guild : Guild) : GuildMember {
        val user = createUser(payload.user)
        return GuildMemberImpl(
            user.id,
            user,
            payload.nick,
            Snowflake(0),
            Snowflake(0),
            payload.deaf,
            payload.mute,
            client
        ).also {
            it.roleIds = payload.roles
            it.guild = guild
        }
    }

    suspend fun fetchGuildMember(id : Snowflake, guild : Guild) : GuildMember {
        return guild.members[id] ?: run {
            val data = client.httpClient.get("$BASE_URL/guilds/${guild.id}/members/$id").await()
            createGuildMember(json.decodeFromString(data), guild)
        }
    }

    fun createGuild(payload : SerialGuild) : Guild {
        return GuildImpl(
            payload.id.asSnowflake(),
            payload.name,
            payload.icon,
            payload.description,
            payload.splash,
            payload.discovery_splash,
            payload.banner,
            payload.owner_id.asSnowflake(),
            payload.application_id?.asSnowflake(),
            payload.region,
            payload.afk_channel_id.asSnowflake(),
            payload.afk_timeout,
            payload.system_channel_id?.asSnowflake(),
            payload.widget_enabled,
            Snowflake(0),
            payload.verification_level,
            payload.default_message_notifications,
            payload.mfa_level,
            payload.explicit_content_filter,
            0,
            payload.max_members,
            payload.max_video_channel_users,
            payload.vanity_url_code,
            payload.premium_tier,
            payload.premium_subscription_count,
            payload.system_channel_flags,
            payload.preferred_locale,
            payload.rules_channel_id?.asSnowflake(),
            payload.public_updates_channel_id?.asSnowflake(),
            false,
            Snowflake(0),
            client
        ).also { g ->
            (client.user as BaseClientUserImpl).guilds[g.id] = g
            payload.channels.associateByTo(
                (client.user as BaseClientUserImpl).channels,
                { it.id.asSnowflake() },
                { createGuildChannel(it, g) })
            payload.emojis.associateByTo(
                (client.user as BaseClientUserImpl).emojis,
                { it.id.asSnowflake() },
                { createEmoji(it, g) })
            payload.members.associateByTo(
                (client.user as BaseClientUserImpl).guildMembers,
                { it.user.id.asSnowflake() },
                { createGuildMember(it, g) }
            )
            payload.roles.associateByTo(
                (client.user as BaseClientUserImpl).guildRoles,
                { it.id.asSnowflake() },
                { createRole(it, g) }
            )
        }
    }

    private fun createEmoji(it : SerialEmoji, guild : Guild) : Emoji =
        EmojiImpl(
            client,
            it.animated,
            it.id.asSnowflake(),
            it.name
        ).apply {
            this.guild = guild
        }

    fun createTextChannel(channel : SerialGuildChannel, guild : Guild) =
        TextChannelImpl(
            channel.id.asSnowflake(),
            client,
            channel.id.asSnowflake(),
            channel.name,
            channel.position,
            channel.parent_id.asSnowflake(),
            channel.topic,
            false,
            ChannelType.TEXT
        ).apply { this.guild = guild }

    fun createGuildChannel(channel : SerialGuildChannel, guild : Guild) : GuildChannel {
        return when (ChannelType.enumById(channel.type)) {

            ChannelType.TEXT -> createTextChannel(channel, guild)

            ChannelType.CATEGORY -> createChannelCategory(channel, guild)

            else -> createTextChannel(channel, guild)

        }
    }

    fun createVC(channel : SerialGuildVoiceChannel, guild : Guild) : GuildChannel =
        VoiceChannelImpl(
            client,
            channel.id.asSnowflake(),
            channel.name,
            channel.position,
            channel.last_message_id?.asSnowflake() ?: Snowflake(0),
            ChannelType.VOICE
        ).apply { this.guild = guild }

    fun createChannelCategory(channel : SerialGuildChannel, guild : Guild) : GuildChannel =
        ChannelCategoryImpl(
            client,
            Snowflake(channel.id),
            channel.name,
            channel.position,
            ChannelType.CATEGORY
        ).apply { this.guild = guild }

    suspend fun createTextBasedChannel(channel : SerialTextBasedChannel, data : String) : TextCapableChannel {
        return when (ChannelType.enumById(channel.type)) {
            ChannelType.DM -> {
                val parsedData = json.decodeFromString<SerialPrivateChannel>(data)
                createDMChannel(parsedData)
            }

            else -> {
                val parsedData = json.decodeFromString<SerialGuildChannel>(data)
                val g = fetchGuild(parsedData.guild_id.asSnowflake())
                createGuildChannel(parsedData, g as GuildImpl) as TextCapableChannel
            }
        }
    }

    suspend fun fetchTextBasedChannel(channelId : Snowflake) : TextCapableChannel {
        val data = client.httpClient.get("$BASE_URL/channels/${channelId}").await()
        val parsedData = json.decodeFromString<SerialTextBasedChannel>(data)
        return createTextBasedChannel(parsedData, data)
    }

    fun timestampResolver(timestamp : String?) : Long? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return if (!timestamp.isNullOrBlank())
            dateFormat.parse(timestamp).toInstant().toEpochMilli()
        else null
    }

    suspend fun createMessage(message : SerialMessage) : Message {
        val author = createUser(message.author)

        val mentions = message.mentions.associateBy({ it.id }, { createUser(it) }) as HashMap

        val attachments = message.attachments.associateBy({ it.id.asSnowflake() }, { createAttachment(it) })

        val channel : TextCapableChannel =
            client.user.textChannels[message.channel_id.asSnowflake()]
                ?: fetchTextBasedChannel(message.channel_id.asSnowflake())

        return MessageImpl(
            client,
            Snowflake(message.id),
            channel,
            author,
            message.content,
            timestampResolver(message.edited_timestamp),
            message.tts ?: false,
            message.mention_everyone,
            mentions,
            message.pinned,
            message.id,
            MessageType.enumById(message.type),
            emptyList()
        ).also { it.attachments = attachments }
    }

    fun createReply(to : Message, with : MessageData) : MessageReply =
        MessageReply(
            with.content,
            with.tts,
            with.nonce,
            MessageReference(
                to.guild?.id.toString(),
                to.channel.id.toString(),
                to.id.toString()
            )
        )

    fun createAttachment(attachment : SerialAttachment) : MessageAttachment {
        return MessageAttachment(
            attachment.content_type,
            attachment.filename,
            attachment.height,
            attachment.id,
            attachment.proxy_url,
            attachment.size,
            attachment.url,
            attachment.width
        )
    }

    fun resolveRelationshipType(type : Int) = RelationshipType.values().elementAtOrNull(type) ?: RelationshipType.NONE
    fun createUser(user : SerialUser, relationshipType : Int = 0) : User =
        UserImpl(
            user.username,
            user.avatar,
            Snowflake(user.id),
            resolveRelationshipType(relationshipType),
            user.bot,
            client
        )

    fun createCustomStatus(cs : ReadyDCustomStatus) : CustomStatus =
        CustomStatus(
            cs.emoji_name,
            cs.emoji_id?.asSnowflake(),
            cs.expires_at?.asSnowflake(),
            cs.text
        )


    private fun createUserSettings(se : ReadyDUserSettings, client : ClientImpl) : ClientUserSettings =
        ClientUserSettings(
            se.afk_timeout,
            se.allow_accessibility_detection,
            se.animate_emoji,
            se.animate_stickers,
            se.contact_sync_enabled,
            se.convert_emoticons,
            createCustomStatus(se.custom_status),
            se.default_guilds_restricted,
            se.detect_platform_accounts,
            se.developer_mode,
            se.disable_games_tab,
            se.enable_tts_command,
            se.explicit_content_filter,
            se.friend_discovery_flags,
            se.gif_auto_play,
            se.inline_attachment_media,
            se.inline_embed_media,
            se.locale,
            se.message_display_compact,
            se.native_phone_integration_enabled,
            se.passwordless,
            se.render_embeds,
            se.render_reactions,
            // d.user_settings.restricted_guilds,
            listOf(),
            se.show_current_game,
            StatusType.enumById(se.status),
            se.stream_notifications_enabled,
            ThemeType.enumById(se.theme),
            se.timezone_offset,
            client
        )

    /*
        Super Properties Stuff
     */

    var superProperties = SuperProperties()
    var superPropertiesStr = ""
    var superPropertiesB64 = ""

    fun createSuperProperties() {
        superProperties = SuperProperties(
            "Windows",
            "Chrome",
            "",
            "en-US",
            userAgent,
            clientVersion,
            "10",
            "",
            "",
            "",
            "",
            "stable",
            clientBuildNumber
        )
        superPropertiesStr = json.encodeToString(superProperties)
        superPropertiesB64 = Base64.getEncoder().encodeToString(superPropertiesStr.toByteArray())
    }

    fun createDMChannel(channel : SerialPrivateChannel) : DMChannelImpl =
        DMChannelImpl(
            Snowflake(channel.id),
            client,
            channel.last_message_id.asSnowflake(),
            channel.name.ifBlank { channel.recipients.firstOrNull()?.username ?: client.user.username },
            if (channel.recipients.isNotEmpty()) {
                channel.recipients.associateBy(
                    { r -> r.id.asSnowflake() },
                    { r -> client.utils.createUser(r) }
                )
            } else {
                mapOf(Pair(client.user.id, client.user))
            },
            ChannelType.enumById(channel.type)
        )

    suspend fun fetchUser(id : Snowflake) : User {
        val response = client.httpClient.get("$BASE_URL/users/$id").await()
        return createUser(json.decodeFromString(response))
    }

    suspend fun fetchMessageById(id : String, channel : TextCapableChannel) : Message {
        val response = client.httpClient.get("$BASE_URL/channels/${channel.id}/messages/$id").await()
        return client.utils.createMessage(json.decodeFromString(response))
    }

    suspend fun fetchLastMessageInChannel(
        channel : TextCapableChannel,
        user : User,
        filters : MessageSearchFilters,
    ) : Message? {
        var parameters = ""
        if (!filters.beforeId.isZero()) parameters += "max_id=${filters.beforeId}&"
        if (!filters.afterId.isZero()) parameters += "after=${filters.afterId}&"
        if (!filters.mentioningUserId.isZero()) parameters += "mentions=${filters.mentioningUserId}&"
        try {
            val lastMessageResponse =
                client.httpClient.get("$BASE_URL/channels/${channel.id}/messages/search?author_id=${filters.authorId}&$parameters")
                    .await()
            json.parseToJsonElement(lastMessageResponse).jsonObject["messages"]?.jsonArray?.forEach { it ->
                val messages = json.decodeFromJsonElement<List<SerialMessage>>(it)
                if (messages.isEmpty()) {
                    if (messages.none { it.author.id == filters.authorId.toString() && it.type == 0 || it.type == 19 }) {
                        filters.beforeId = messages.last().id.asSnowflake()
                        delay(500)
                        fetchLastMessageInChannel(channel, user, filters)
                    }
                    val message =
                        messages.first { it.author.id == filters.authorId.toString() && it.type == 0 || it.type == 19 }
                    return createMessage(message)
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun createClientUser(d : Ready) : BaseClientUserImpl {
        if (client.type == ClientType.BOT) {
            return BotClientUserImpl(
                d.user.bio,
                client.configuration.token,
                client,
                d.user.username,
                d.user.id.asSnowflake(),
                d.user.avatar,
                d.user.bot,
                RelationshipType.NONE,
            )
        }
        return ClientUserImpl(
            d.user.verified,
            d.user.username,
            d.user.id.asSnowflake(),
            d.user.email,
            d.user.bio,
            createUserSettings(d.user_settings, client),
            d.user.avatar,
            d.user.premium,
            client.configuration.token,
            RelationshipType.NONE,
            d.user.bot,
            client
        )
    }

    suspend fun fetchGuild(guildId : Snowflake?) : Guild {
        return client.user.guilds[guildId] ?: run {
            val data = client.httpClient.get("$BASE_URL/guilds/$guildId").await()
            createGuild(json.decodeFromString(data))
        }
    }

    fun extractPermissions(permissions : Long) : List<PermissionType> {
        val allowedPermissions = mutableListOf<PermissionType>()
        for (permission in PermissionType.values()) {
            val permissionValue = permission.value
            if (permissions and permissionValue == permissionValue) {
                allowedPermissions.add(permission)
            }
        }
        return allowedPermissions
    }


    data class PayloadDef(
        val name : String,
        val type : PayloadType,
        val payload : String,
    )

    enum class PayloadType {
        IDENTIFY,
        RESUME;
    }

    fun generateIdentify() : PayloadDef {
        val payload =
            if (client.configuration.clientType != ClientType.BOT) json.encodeToString(generateUserIdentify()) else json.encodeToString(
                generateBotIdentify()
            )
        return PayloadDef("Identify", PayloadType.IDENTIFY, payload)
    }

    fun generateResume() : PayloadDef {
        val payload = json.encodeToString(
            Resume(
                OPCODE.RESUME.value,
                ResumeD(
                    client.socket.gatewaySequence,
                    client.socket.sessionId,
                    client.token
                )
            )
        )
        return PayloadDef("Resume", PayloadType.RESUME, payload)

    }

    private fun generateUserIdentify() : Identify = Identify(
        OPCODE.IDENTIFY.value,
        IdentifyD(
            client.configuration.token,
            2048,
            client.utils.superProperties,
            IdentifyDPresence(
                "online",
                0,
                emptyArray(),
                false
            ),
            false,
            IdentifyDClientState()
        )
    )


    private fun generateBotIdentify() : BotIdentify =
        BotIdentify(
            OPCODE.IDENTIFY.value,
            BotIdentifyD(
                client.configuration.token,
                513,
                IdentifyDProperties(
                    "Windows",
                    "OCTANE",
                    "OCTANE"
                )
            )
        )

    suspend fun prepareGoogleCloudBucketForAttachments(attachments : List<AttachmentSlot>) : CompletableDeferred<List<HttpResponse>> {
        return CompletableDeferred(attachments
            .map {
                client.httpClient.client.options(it.uploadUrl) {
                    Headers.build {
                        append(HttpHeaders.AccessControlRequestHeaders, HttpHeaders.ContentType)
                        append(HttpHeaders.AccessControlRequestMethod, HttpMethod.Put.value)
                    }
                }
            })
    }

    suspend fun uploadAttachmentsToGoogleCloudBucket(
        attachmentSlots : List<AttachmentSlot>,
        byteAttachments : HashMap<String, ByteArray>,
    ) : CompletableDeferred<List<Pair<String, AttachmentSlot>>> {
        return CompletableDeferred(attachmentSlots.map {
            val attachmentDataKey = byteAttachments.keys.first { key -> it.uploadFilename.contains(key) }
            val type = ContentType.fromFileExtension(it.uploadFilename.replaceBefore(".", "")).toString()
            client.httpClient.client.put(it.uploadUrl) {
                setBody(byteAttachments[attachmentDataKey])
                Headers.build {
                    append(HttpHeaders.ContentLength, byteAttachments[attachmentDataKey]?.size.toString())
                    append(HttpHeaders.ContentType, type)
                }
            }
            Pair(attachmentDataKey, it)
        })
    }
}