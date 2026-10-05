package com.whitbread.premierinn.common.view.svg

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.caverock.androidsvg.SVG
import com.whitbread.premierinn.common.BaseApplication

/**
 * Convert the [SVG]'s internal representation to an Android-compatible one ([Picture]).
 */
class SvgDrawableTranscoder(baseApplication: BaseApplication) : ResourceTranscoder<SVG, Drawable> {
    private val resources = baseApplication.resources

    override fun transcode(toTranscode: Resource<SVG>, options: Options): Resource<Drawable> {
        val svg = toTranscode.get()
        val picture = svg.renderToPicture()
        val drawable = PictureDrawable(picture)

        val returnedBitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawPicture(drawable.picture)
        return SimpleResource(BitmapDrawable(resources, returnedBitmap))
    }
}
