package com.whitbread.premierinn.data.graphql.mapper

import com.google.gson.Gson
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.RoomKeyInstructionsGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.RoomKeyInstructionsGraphQLContract.LabelsContent
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.RoomKeyInstructionsDomain

fun RoomKeyInstructionsGraphQLContract.CategoryLabelsData.mapToRoomKeyInstructionsGQL(): RoomKeyInstructionsDomain {
    val labelsContent = this.data.categoryLabels.labels
    val labelsFromStringToJson = toJson(labelsContent)

    val description = labelsFromStringToJson.descriptionPid ?: labelsFromStringToJson.description.orEmpty()

    return RoomKeyInstructionsDomain(
        title = labelsFromStringToJson.title,
        description = handleOrderedList(description)
    )
}

fun toJson(labelsContent: String): LabelsContent {
    val gson = Gson()
    val labelsDecodedJson = gson.fromJson(labelsContent, String::class.java)

    return gson.fromJson(labelsDecodedJson, LabelsContent::class.java)
}

private fun handleOrderedList(html: String): String {
    var result = html
    var listCounter = 0  // Global counter to keep track across lists

    // Process <ol> blocks and handle each list separately
    result = result.replace(Regex("<ol>(.*?)</ol>", RegexOption.DOT_MATCHES_ALL)) { match ->
        val listContent = match.groups[1]?.value ?: EMPTY_STRING
        listCounter = 1  // Reset counter for each ordered list

        // Process list items within this list
        val processedList = listContent.replace(Regex("<li>(.*?)</li>", RegexOption.DOT_MATCHES_ALL)) { itemMatch ->
            val itemContent = itemMatch.groups[1]?.value ?: EMPTY_STRING
            val numberedItem = "<br>&nbsp;&nbsp;&nbsp;&nbsp;$listCounter. $itemContent"  // Add indentation and numbering
            listCounter++
            numberedItem
        }
        processedList.trimStart() + "<br>"  // Ensure a line break after the list
    }

    return result
}
