package typedefs

enum class PermissionType(val value : Long, val channels : PermissionCategory) {
    // Text Channels
    MANAGE_MESSAGES(0x0000000000002000L, PermissionCategory.TEXT_CHANNEL),
    SEND_MESSAGES(0x0000000000000800L, PermissionCategory.TEXT_CHANNEL),
    READ_MESSAGES(0x0000000000010000L, PermissionCategory.TEXT_CHANNEL),
    ADD_REACTIONS(0x0000000000000040L, PermissionCategory.TEXT_CHANNEL),
    EMBED_LINKS(0x0000000000004000L, PermissionCategory.TEXT_CHANNEL),
    ATTACH_FILES(0x0000000000008000L, PermissionCategory.TEXT_CHANNEL),
    USE_EXTERNAL_EMOJIS(0x0000000000040000L, PermissionCategory.TEXT_CHANNEL),

    // Voice Channels
    CONNECT(0x0000000000100000L, PermissionCategory.VOICE_CHANNEL),
    SPEAK(0x0000000000200000L, PermissionCategory.VOICE_CHANNEL),
    START_ACTIVITY(0x0000008000000000L, PermissionCategory.VOICE_CHANNEL),
    USE_VOICE_ACTIVITY(0x0000000002000000L, PermissionCategory.VOICE_CHANNEL),
    PRIORITY_SPEAKER(0x0000000000000100L, PermissionCategory.VOICE_CHANNEL),
    MUTE_MEMBERS(0x0000000000400000L, PermissionCategory.VOICE_CHANNEL),
    DEAFEN_MEMBERS(0x0000000000800000L, PermissionCategory.VOICE_CHANNEL),
    MOVE_MEMBERS(0x0000000001000000L, PermissionCategory.VOICE_CHANNEL),
    REQUEST_TO_SPEAK(0x0000000100000000L, PermissionCategory.VOICE_CHANNEL),

    // General Permissions
    CHANGE_NICKNAME(0x0000000004000000L, PermissionCategory.GENERAL),
    VIEW_CHANNELS(0x0000000000008000L, PermissionCategory.GENERAL),
    MANAGE_CHANNELS(0x0000000000000010L, PermissionCategory.GENERAL),
    MANAGE_ROLES(0x0000000000002000L, PermissionCategory.GENERAL),
    MANAGE_EMOJIS_AND_STICKERS(0x0000000000001000L, PermissionCategory.GENERAL),
    VIEW_AUDIT_LOG(0x0000000000000800L, PermissionCategory.GENERAL),
    VIEW_SERVER_INSIGHTS(0x0000000000080000L, PermissionCategory.GENERAL),
    MANAGE_WEBHOOKS(0x0000000000000200L, PermissionCategory.GENERAL),
    MANAGE_SERVER(0x0000000000001000L, PermissionCategory.GENERAL),
    CREATE_INVITE(0x0000000000000800L, PermissionCategory.GENERAL),
    KICK_MEMBERS(0x0000000000000002L, PermissionCategory.GENERAL),
    BAN_MEMBERS(0x0000000000000004L, PermissionCategory.GENERAL),
    MANAGE_EVENTS(0x0000000000000100L, PermissionCategory.GENERAL),
    ADMINISTRATOR(0x0000000000000008, PermissionCategory.GENERAL)
}