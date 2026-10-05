@file:JvmName("HotelImageProcessor")

package com.whitbread.premierinn.hoteldetails

import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.view.gallery.Image
import com.whitbread.premierinn.domain.common.hoteldetails.entity.GalleryImageDomain


fun convertToDisplayableImages(galleryImageList: List<GalleryImageDomain>): List<Image> {
   return   galleryImageList.mapNotNull { it.imageSrc }
            .map { Image(fullImageUrl(it), null) }
}

private fun fullImageUrl(fileReference: String): String {
    return Urls.CONTENT_BASE_URL + fileReference
}

//TODO: extract strings here and in hasHubBiggerRooms
//private fun imageLabel(imageTags: List<String>, hotelBrand: Hotel.Brand): Badge? {
//    return when (hotelBrand) {
//        Hotel.Brand.PI -> when {
//            imageTags.contains("standard") -> Badge("STANDARD\nROOM", R.color.premier_inn_purple)
//            imageTags.contains("ultimate") -> Badge("PREMIER\nPLUS ROOM", R.color.teal)
//            imageTags.contains("extra") -> Badge("BUSINESS\nROOM", R.color.teal)
//            else -> null
//        }
//
//        Hotel.Brand.HUB -> when {
//            imageTags.contains("hub-standard-room") -> Badge("hub\nstandard\nrooms", R.color.grey_dark)
//            imageTags.contains("hub-bigger-room") -> Badge("hub\nbigger\nrooms", R.color.grey_dark)
//            else -> null
//        }
//
//        else -> null
//    }
//}