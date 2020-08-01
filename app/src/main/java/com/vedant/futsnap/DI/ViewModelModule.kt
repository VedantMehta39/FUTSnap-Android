package com.vedant.futsnap.DI

import androidx.lifecycle.ViewModel
import com.vedant.futsnap.UI.ViewModels.MainActivityViewModel
import com.vedant.futsnap.Network.ApiClient
import com.vedant.futsnap.UI.ViewModels.SearchPlayerViewModel
import com.vedant.futsnap.UI.ViewModels.SinglePlayerDialogFragmentViewModel
import com.vedant.futsnap.UI.ViewModels.CustomViewModelFactory
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

import kotlin.reflect.KClass

@Module
class ViewModelModule {

    companion object{
        @MustBeDocumented
        @Target(
            AnnotationTarget.FUNCTION,
            AnnotationTarget.PROPERTY_GETTER,
            AnnotationTarget.PROPERTY_SETTER
        )
        @Retention(AnnotationRetention.RUNTIME)
        @MapKey
        annotation class ViewModelKey(val value: KClass<out ViewModel>)

        @Singleton
        @Provides
        fun provideViewModelFactory(viewModelMap:Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>)
                =
            CustomViewModelFactory(
                viewModelMap
            )

        @Provides
        @IntoMap
        @ViewModelKey(MainActivityViewModel::class)
        fun getMainActivityViewModel(@Named("SERVICE")apiClient: ApiClient):ViewModel
                =
            MainActivityViewModel(
                apiClient
            )

        @Provides
        @IntoMap
        @ViewModelKey(SearchPlayerViewModel::class)
        fun getSearchPlayerViewModel(@Named("SEARCH")apiClient: ApiClient):ViewModel
                =
            SearchPlayerViewModel(
                apiClient
            )

        @Provides
        @IntoMap
        @ViewModelKey(SinglePlayerDialogFragmentViewModel::class)
        fun getSinglePlayerDialogFragmentViewModel(@Named("PRICE")apiClient: ApiClient)
                :ViewModel =
            SinglePlayerDialogFragmentViewModel(
                apiClient
            )



    }


}