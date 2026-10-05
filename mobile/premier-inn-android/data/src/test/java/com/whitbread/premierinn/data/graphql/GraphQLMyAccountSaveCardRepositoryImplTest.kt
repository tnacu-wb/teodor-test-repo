package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToSaveCardDomainGQL
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.MyAccountSaveCardGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.myAccount.repository.GraphQLMyAccountSaveCardRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.InitiateSaveCardRequest
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveCardBillingAddress
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveCardDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveCardRequestBody
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class GraphQLMyAccountSaveCardRepositoryImplTest {
    private val graphQlApi: WBGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphQLMyAccountSaveCardRepository: GraphQLMyAccountSaveCardRepository = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesSaveCard: JSONObject = mockk(relaxed = true)

    val SAVE_CARD_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/save_card_success_gql.json"),
        MyAccountSaveCardGraphQLContract.SaveCardGraphQLContractData::class.java
    )


    val SAVE_PAYMENT_QUERY_STRING = "Initiate payment query"

    @Before
    fun setUp() {
        graphQLMyAccountSaveCardRepository = GraphQLMyAccountSaveCardRepositoryImpl(
            graphQlApi, fileDataProvider,
            jsonObject, jsonObjectForVariablesSaveCard
        )

        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns SAVE_PAYMENT_QUERY_STRING
        every { jsonObject.toString() } returns SAVE_PAYMENT_QUERY_STRING
    }

    @Test
    fun `Given SaveCard is successful then no error is thrown and mapping works`() {
        every { graphQlApi.saveCardGraphQL(any(), any()) } returns Single.just(SAVE_CARD_SUCCESS)

        val expectedPaymentResponse = SAVE_CARD_SUCCESS.mapToSaveCardDomainGQL()
        graphQLMyAccountSaveCardRepository.saveCard("token", mockSaveCardRequestData()).test()
            .assertNoErrors()
            .assertValue { it == expectedPaymentResponse }
    }

    @Test
    fun `Given Initiate payment contains Error then return response as GraphQl Error`() {
        every { graphQlApi.saveCardGraphQL(any(), any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        graphQLMyAccountSaveCardRepository.saveCard("token", mockSaveCardRequestData()).test()
            .assertError { it is GraphQLServerError }
    }

    private fun mockSaveCardRequestData(): SaveCardRequestBody {
        return SaveCardRequestBody(
            initiateSaveCardRequest = InitiateSaveCardRequest(
                requestId = "45673",
                billingAddress = SaveCardBillingAddress(
                    line1 = "First Line of address",
                    line2 = "Second line of address",
                    postCode = "EC1 N2TD",
                    countryCode = "GB",
                    type = "Home"

                ),
                cardDetails = SaveCardDetails(
                    cardType = "CARD",
                    cnpRequired = false
                ),
                environment = "https://www.uat.premierinn.digital",
                country = "UK",
                language = "en",
                )
        )

    }
}