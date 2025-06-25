package com.example.buyva.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.example.buyva.data.datasource.remote.graphql.ApolloAdmin
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.datasource.remote.stripe.StripeAPI
import com.example.buyva.data.datasource.remote.stripe.StripeClient
import com.example.buyva.utils.sharedpreference.SharedPreference
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppDataModuleProvider {
    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloService.client
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideStripeApi(): StripeAPI {
        return StripeClient.api
    }

    @Provides
    @Singleton
    fun provideApolloAdminClient(): ApolloAdmin {
        return ApolloAdmin
    }

    @Provides
    @Singleton
    fun provideSharedPreferenceImpl(): SharedPreference {
        return SharedPreferenceImpl
    }


}