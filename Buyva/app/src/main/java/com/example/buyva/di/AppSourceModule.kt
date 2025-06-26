package com.example.buyva.di

import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.currency.CurrencyRemoteDataSource
import com.example.buyva.data.repository.Authentication.AuthRepository
import com.example.buyva.data.repository.Authentication.IAuthRepository
import com.example.buyva.data.repository.adresses.AddressRepoImpl
import com.example.buyva.data.repository.adresses.IAddressRepo
import com.example.buyva.data.repository.cart.CartRepo
import com.example.buyva.data.repository.cart.CartRepoImpl
import com.example.buyva.data.repository.categories.CategoryRepositoryImpl
import com.example.buyva.data.repository.categories.ICategoryRepository
import com.example.buyva.data.repository.currency.CurrencyRepo
import com.example.buyva.data.repository.currency.ICurrencyRepo
import com.example.buyva.data.repository.favourite.FavouriteRepository
import com.example.buyva.data.repository.favourite.FavouriteRepositoryImpl
import com.example.buyva.data.repository.home.HomeRepositoryImpl
import com.example.buyva.data.repository.home.IHomeRepository
import com.example.buyva.data.repository.order.IOrderRepository
import com.example.buyva.data.repository.order.OrderRepositoryImpl
import com.example.buyva.data.repository.paymentRepo.PaymentRepo
import com.example.buyva.data.repository.paymentRepo.PaymentRepoImpl
import com.example.buyva.data.repository.search.ISearchRepository
import com.example.buyva.data.repository.search.SearchRepositoryImpl
import com.omarinc.shopify.network.currency.CurrencyRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppSourceModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: AuthRepository
    ): IAuthRepository

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(
        remoteDataSource: RemoteDataSourceImpl
    ): RemoteDataSource

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepository: HomeRepositoryImpl
    ): IHomeRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepository: CategoryRepositoryImpl
    ): ICategoryRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepository: OrderRepositoryImpl
    ): IOrderRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(
        addressRepository: AddressRepoImpl
    ): IAddressRepo


    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepository: CartRepoImpl
    ): CartRepo

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepository: PaymentRepoImpl
    ): PaymentRepo

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        currencyRepository: CurrencyRepo
    ): ICurrencyRepo

    @Binds
    @Singleton
    abstract fun bindFavouriteRepository(
        favouriteRepository: FavouriteRepositoryImpl
    ): FavouriteRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepository: SearchRepositoryImpl
    ): ISearchRepository

    @Binds
    @Singleton
    abstract fun bindCurrencyRemoteDataSource(
        impl: CurrencyRemoteDataSourceImpl
    ): CurrencyRemoteDataSource

}