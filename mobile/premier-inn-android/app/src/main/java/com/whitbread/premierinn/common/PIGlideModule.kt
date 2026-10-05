package com.whitbread.premierinn.common

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.caverock.androidsvg.SVG
import com.whitbread.premierinn.common.view.svg.SvgDecoder
import com.whitbread.premierinn.common.view.svg.SvgDrawableTranscoder
import com.whitbread.premierinn.di.OkHttpClientEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.io.InputStream

@GlideModule
class PIGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            OkHttpClientEntryPoint::class.java
        )
        val okHttpClient = entryPoint.okHttpClient()
        registry
            .register(SVG::class.java, Drawable::class.java, SvgDrawableTranscoder((context.applicationContext as BaseApplication)))
            .append(InputStream::class.java, SVG::class.java, SvgDecoder())
            .replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient))
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultRequestOptions(
                RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.NONE)
                        .skipMemoryCache(false))
    }
}