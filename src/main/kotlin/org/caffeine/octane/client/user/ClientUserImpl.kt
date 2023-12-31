package org.caffeine.octane.client.user

import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.client.connection.payloads.client.AttachmentRequestData
import org.caffeine.octane.client.connection.payloads.client.AttachmentSlotRequest
import org.caffeine.octane.client.connection.payloads.gateway.AttachmentSlotResponse
import org.caffeine.octane.client.connection.payloads.gateway.SerialMessage
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.channels.BaseChannel
import org.caffeine.octane.entities.channels.DMChannel
import org.caffeine.octane.entities.channels.GuildChannel
import org.caffeine.octane.entities.channels.TextCapableChannel
import org.caffeine.octane.entities.guild.Guild
import org.caffeine.octane.entities.message.Message
import org.caffeine.octane.entities.message.PartialAttachment
import org.caffeine.octane.entities.nitro.RedeemedCode
import org.caffeine.octane.entities.users.User
import org.caffeine.octane.typedefs.*
import org.caffeine.octane.utils.BASE_URL
import org.caffeine.octane.utils.MessageSendData
import org.caffeine.octane.utils.json
import org.caffeine.octane.utils.log
import kotlin.math.absoluteValue
import kotlin.random.Random

data class ClientUserImpl(
    override var verified : Boolean,
    override var username : String,
    override var id : Snowflake,
    override var email : String?,
    override var bio : String?,
    override var settings : ClientUserSettings,
    override var avatar : String?,
    override var premium : Boolean?,
    override var token : String,
    override var relation : RelationshipType,
    override var bot : Boolean,
    override var client : ClientImpl,
) : ClientUser, BaseClientUserImpl(bio, token, client, username, id, avatar, bot) {

    override var asMention : String = "<@${id}>"

    override var relationships : HashMap<Snowflake, User> = HashMap()
    override val friends : Map<Snowflake, User> get() = relationships.filterValues { it.relation == RelationshipType.FRIEND }
    override val blocked : Map<Snowflake, User> get() = relationships.filterValues { it.relation == RelationshipType.BLOCKED }

    override val dmChannels : Map<Snowflake, DMChannel>
        get() = channels.values.filterIsInstance<DMChannel>().associateBy { it.id }

    override val textChannels : Map<Snowflake, TextCapableChannel>
        get() = channels.values.filterIsInstance<TextCapableChannel>().associateBy { it.id }

    override val guildChannels : Map<Snowflake, GuildChannel>
        get() = channels.values.filterIsInstance<GuildChannel>().associateBy { it.id }


    override suspend fun sendMessage(
        channel : TextCapableChannel,
        message : MessageSendData,
    ) : CompletableDeferred<Message> {
        if (message.byteAttachments.size != 0) finalizeAttachments(channel, message)
        val data = json.encodeToString(message)
        val response = client.httpClient.post("$BASE_URL/channels/${channel.id}/messages", data).await()
        val serial = json.decodeFromString<SerialMessage>(response)
        return CompletableDeferred(client.utils.createMessage(serial))
    }


    override suspend fun removeRelationship(user : User) {
        client.httpClient.delete("$BASE_URL/users/@me/relationships/${user.id}")
    }

    override suspend fun setHouse(house : HypeSquadHouseType) {
        if (house.ordinal !in 1..3) {
            client.httpClient.delete("$BASE_URL/hypesquad/online")
            return
        }
        val data = "{\"house_id\":${house.ordinal}}"
        client.httpClient.post("$BASE_URL/hypesquad/online", data)
    }

    override suspend fun setCustomStatus(status : CustomStatus) {
        val text = json.encodeToString(status)
        val data = "{ \"custom_status\" : $text }"
        client.httpClient.patch("$BASE_URL/users/@me/settings", data)
    }

    override suspend fun setTheme(theme : ThemeType) {
        val data = json.parseToJsonElement("{\"theme\":\"${theme.value}\"}").toString()
        client.httpClient.patch("$BASE_URL/users/@me/settings", data)
    }

    override suspend fun setStatus(status : StatusType) {
        val data = json.parseToJsonElement("{\"status\":\"${status.value}\"}").toString()
        client.httpClient.patch("$BASE_URL/users/@me/settings", data)
    }

    private suspend fun finalizeAttachments(channel : BaseChannel, message : MessageSendData) : MessageSendData {
        val attachmentSlotRequests = mutableListOf<AttachmentSlotRequest>()
        message.byteAttachments.mapTo(attachmentSlotRequests) {
            AttachmentSlotRequest(
                it.key,
                it.value.size,
                Random.nextInt(0, client.user.id.toLong().toInt().absoluteValue).toString()
            )
        }
        val attachmentRequestData = json.encodeToString(AttachmentRequestData(attachmentSlotRequests))
        if (attachmentSlotRequests.isEmpty()) return message
        val attachmentSlotsResponse =
            client.httpClient.post("$BASE_URL/channels/${channel.id}/attachments", attachmentRequestData)
                .await()
        val attachmentSlots = json.decodeFromString<AttachmentSlotResponse>(attachmentSlotsResponse).attachments

        client.utils.prepareGoogleCloudBucketForAttachments(attachmentSlots).await()
        client.utils.uploadAttachmentsToGoogleCloudBucket(attachmentSlots, message.byteAttachments).await()
            .map {
                message.attachments +=
                    PartialAttachment(
                        it.second.id.toString(),
                        it.first,
                        it.second.uploadFilename
                    )
            }
        return message
    }

    override suspend fun redeemCode(code : String) : CompletableDeferred<RedeemedCode> {
        var rc = RedeemedCode()
        var la : Long
        val start = System.currentTimeMillis()
        try {
            client.httpClient.post("$BASE_URL/entitlements/gift-codes/$code/redeem")
            val end = System.currentTimeMillis()
            la = (start - end)
            rc = RedeemedCode(code, la.absoluteValue, RedeemedCodeStatusType.SUCCESS)
        } catch (ex : Exception) {
            val end = System.currentTimeMillis()
            la = (start - end)
            if (ex.toString().contains("Unknown Gift Code")) {
                rc = RedeemedCode(
                    code,
                    la.absoluteValue,
                    RedeemedCodeStatusType.INVALID,
                    RedeemedCodeErrorType.UNKNOWN_CODE
                )
            }
        }
        return CompletableDeferred(rc)
    }

    override suspend fun block(user : User) {
        val data = json.parseToJsonElement("{\"type\":2}").toString()
        client.httpClient.post("$BASE_URL/users/@me/relationships/${user.id}", data)
    }

    override fun muteGuild(guild : Guild, i : Int) {
        log("NOT IMPLEMENTED // TODO")
    }

}