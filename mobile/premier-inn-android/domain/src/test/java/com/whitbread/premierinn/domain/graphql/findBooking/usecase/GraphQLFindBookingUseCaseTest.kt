package com.whitbread.premierinn.domain.graphql.findBooking.usecase

import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain
import com.whitbread.premierinn.domain.graphql.findBooking.repository.GraphQLFindBookingRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.junit.Test
import java.lang.Exception


class GraphQLFindBookingUseCaseTest {
    private val graphQLFindBookingUseCase: GraphQLFindBookingUseCase = mockk()
    private val graphQLFindBookingRepository: GraphQLFindBookingRepository = mockk()

   @Test
   fun `WHEN find booking returns opera check is opera returns true`() {
       every { graphQLFindBookingRepository.findBooking(findBookingRequestBody)} returns Single.just(
               FindBookingDomain("Opera", "AHV3737", "AHV3737-7bfbd1bb-1b98-4be8-acc2-7a14f8ee9787",
                       "CfiYrAs4Ks787uRmO+9A0HWnkl7/i", "LONEUS", false))
       every { graphQLFindBookingUseCase.isOperaBooking(findBookingRequestBody) } returns Single.just(true)
       graphQLFindBookingUseCase.isOperaBooking(findBookingRequestBody)
               .test()
               .assertValue { it }

   }

    @Test
    fun `WHEN find booking returns Bart check is opera returns false`() {
        every { graphQLFindBookingRepository.findBooking(findBookingRequestBody)} returns Single.just(
                FindBookingDomain("Bart", "AHV3737", "", null,
                    "LONEUS", false))
        every { graphQLFindBookingUseCase.isOperaBooking(findBookingRequestBody) } returns Single.just(false)
        graphQLFindBookingUseCase.isOperaBooking(findBookingRequestBody)
                .test()
                .assertValue { !it }

    }

    @Test
    fun `WHEN find booking returns error check is opera returns false`() {
        every { graphQLFindBookingRepository.findBooking(findBookingRequestBody)} returns Single.error(
                Exception())
        every { graphQLFindBookingUseCase.isOperaBooking(findBookingRequestBody) } returns Single.just(false)
        graphQLFindBookingUseCase.isOperaBooking(findBookingRequestBody)
                .test()
                .assertValue { !it }

    }
    companion object {
        val findBookingRequestBody = FindBookingRequestBody(
                "AHV3737", "Tester",
                "2023-11-10", "en", "gb", BookingChannelDetails("PI", "MOBILE", "en")
        )
    }
}