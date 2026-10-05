package com.whitbread.premierinn.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RxSchedulersModule {

    @Provides
    @Singleton
    @IoScheduler
    fun provideIoScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Singleton
    @ComputationScheduler
    fun provideComputationScheduler(): Scheduler = Schedulers.computation()

    @Provides
    @Singleton
    @MainScheduler
    fun provideMainScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()
}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoScheduler

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainScheduler

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComputationScheduler
