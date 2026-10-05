package com.whitbread.premierinn.data.upsellavailable

import androidx.room.*
import com.whitbread.premierinn.data.booking.entity.PriceEntity

@Entity(tableName = "upsell_item_available")
data class UpsellItemAvailableEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "foodUpsell") val foodUpsell: Boolean,
    @ColumnInfo(name = "freeBreakfastTrigger") val freeBreakfastTrigger: Boolean,
    @ColumnInfo(name = "availableForChildren") val availableForChildren: Boolean,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "freeBreakfastCode") val freeBreakfastCode: String,
    @ColumnInfo(name = "legend") val legend: String,
    @Embedded(prefix = "unit_") val unitCost: PriceEntity,
    @ColumnInfo(name = "freeBreakfastOption") val freeBreakfastOption: Boolean,
    @ColumnInfo(name = "attachments") val attachments: List<UpsellAttachmentEntity>
)

data class UpsellAttachmentEntity(
    @ColumnInfo(name = "path") val path: String?,
    @ColumnInfo(name = "label") val label: String?
)
