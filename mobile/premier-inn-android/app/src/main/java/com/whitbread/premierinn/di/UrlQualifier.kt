package com.whitbread.premierinn.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MicroServiceRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GraphQLRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NonRxGraphQLRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SnowdropRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleMapsRetrofit