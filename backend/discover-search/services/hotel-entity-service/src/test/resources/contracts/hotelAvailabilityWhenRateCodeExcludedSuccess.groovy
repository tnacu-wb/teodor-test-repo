package contracts

import org.springframework.cloud.contract.spec.Contract

import java.time.LocalDate
import java.time.format.DateTimeFormatter

Contract.make {
    def DATE_PATTERN = "yyyy-MM-dd"
    def startDate = LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN))
    def endDate = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern(DATE_PATTERN))

    description("hotel-availability-controller")
    request {
        method 'GET'
        urlPath('v1/hotels/BIRPLI/availabilities') {
            queryParameters {
                parameter 'arrivalDate': $(consumer(anyDate()), producer(startDate))
                parameter 'departureDate': $(consumer(anyDate()), producer(endDate))
                parameter 'roomTypes': "DB,DB"
                parameter 'adultsNumber': "1,2"
                parameter 'childrenNumber': "0,0"
                parameter 'cotsRequired': "false,false"
                parameter 'channel': "PI"
                parameter 'subchannel': "MOBILE"
                parameter 'country': "GB"
            }
        }
    }

    response {
        status 200
        body(file("response/hotelAvailabilityWhenRateCodeExcluded.json"))
        bodyMatchers {
            jsonPath('$.startDate', byRegex(startDate))
            jsonPath('$.endDate', byRegex(endDate))
        }
        headers {
            header('Content-Type', 'application/json')
        }
    }
}