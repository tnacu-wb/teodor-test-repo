@file:JvmName("EmojiUtils")
package com.whitbread.premierinn.common.utils

import androidx.emoji2.text.EmojiCompat

fun isoCodeToEmoji(isoCode: String): CharSequence? {
    val flagOffset = 0x1F1E6
    val asciiOffset = 0x41

    val firstChar = Character.codePointAt(isoCode, 0) - asciiOffset + flagOffset
    val secondChar = Character.codePointAt(isoCode, 1) - asciiOffset + flagOffset
    val characters = String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
    return if (EmojiCompat.get().loadState == EmojiCompat.LOAD_STATE_SUCCEEDED && EmojiCompat.get().hasEmojiGlyph(characters)) {
        EmojiCompat.get().process(characters)
    } else {
        null
    }
}