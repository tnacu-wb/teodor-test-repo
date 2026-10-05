package com.whitbread.premierinn.data.countries

import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.graphql.mapper.mapToCountryListDomain
import com.whitbread.premierinn.data.remote.graphql.contracts.CountriesGraphQLContract
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.countries.repository.CountriesRepository
import javax.inject.Inject

class CountriesRepositoryImpl @Inject constructor(private val deviceLocaleProvider: DeviceLocaleProvider,
                                                  private val simplePersistenceManager: SimplePersistenceManager,
                                                  private val fileDataProvider: FileDataProvider, private val gson: Gson): CountriesRepository {

    override fun getCountriesFromAsset(): List<CountryDomain> {
        val deviceLanguage = deviceLocaleProvider.getDeviceLanguage()
        val json = fileDataProvider.loadFileFromAsset("countriesResponse/countriesResponse_$deviceLanguage.json")
        val countries = gson.fromJson<CountriesGraphQLContract.CountriesData>(json, CountriesGraphQLContract.CountriesData::class.java)
        return countries.mapToCountryListDomain().sortedBy { it.countryName }
    }

    override fun getCountriesFromSharedPref(): List<CountryDomain> {
        return simplePersistenceManager.getListOfCountries()?.sortedBy { it.countryName } ?: getCountriesFromAsset()
    }
}