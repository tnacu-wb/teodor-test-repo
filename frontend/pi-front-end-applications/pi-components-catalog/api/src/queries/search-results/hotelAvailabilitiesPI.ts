import { gql } from 'graphql-request';

export const MULTI_HOTEL_AVAILABILITIES_QUERY = gql`
  query hotelAvailabilities(
    $startDate: String!
    $endDate: String!
    $rooms: [Room!]!
    $place: Place
    $oldWorldChannel: String!
    $channel: String!
    $subChannel: String!
    $companyId: String
    $page: Int!
    $initialPageSize: Int!
    $lazyLoadPageSize: Int!
    $country: String!
    $language: String!
    $sort: String!
    $sortOption: SortOption
    $filters: [String!]
    $ratePlanCodes: [String]
    $promotionCode: String
  ) {
    hotelAvailabilities(
      availabilitiesSearchCriteria: {
        startDate: $startDate
        endDate: $endDate
        rooms: $rooms
        place: $place
        oldWorldChannel: $oldWorldChannel
        channel: $channel
        subChannel: $subChannel
        companyId: $companyId
        page: $page
        initialPageSize: $initialPageSize
        lazyLoadPageSize: $lazyLoadPageSize
        country: $country
        language: $language
        sort: $sort
        sortOption: $sortOption
        filters: $filters
        ratePlanCodes: $ratePlanCodes
        promotionCode: $promotionCode
      }
    ) {
      promotionsInformation {
        showPromo
        isWithinPromoWindow
        promotionCode
        landingPage
        promoBannerColour
        promoBannerIcon
        promoBannerTitle
        promoBannerSubtitle
        promoInvalidMessage
        promoExpiredMessage
        promoBannerVisibility
      }
      multiHotelAvailabilities {
        hotelAvailability {
          lowestRoomRate {
            currencyCode
            netTotal
          }
          distance
          unit
          available
          hasMlosRestriction
          limitedAvailability
          pmsSource
          cellCode
          numberOfRoomsAvailable
        }
        name
        hotelId
        hotelInformation {
          hotelOpeningDate
          brand
          coordinates {
            latitude
            longitude
          }
          messagingFlag {
            color
            text
          }
          hotelFlags {
            isEnabled
            flagBanner {
              text
              textColour
              backgroundImage
              backgroundColour
            }
          }
          thumbnailImages {
            imageSrc
            tags
          }
          hotelFacilities {
            code
            description
            icon
            isVisible
            name
            weight
          }
          links {
            detailsPage
          }
        }
      }
      page
      pageSize
      total
    }
  }
`;

// For use in PI to utilise caching in SRP
export const MULTI_HOTEL_AVAILABILITIES_QUERY_PI_V2 = gql`
  query hotelAvailabilitiesV2(
    $startDate: String
    $endDate: String
    $rooms: [Room!]!
    $place: Place
    $oldWorldChannel: String!
    $channel: String!
    $subChannel: String!
    $companyId: String
    $page: Int!
    $initialPageSize: Int!
    $lazyLoadPageSize: Int!
    $country: String!
    $language: String!
    $sort: String!
    $sortOption: SortOption
    $filters: [String!]
    $ratePlanCodes: [String]
    $promotionCode: String
  ) {
    hotelAvailabilitiesV2(
      availabilitiesSearchCriteria: {
        startDate: $startDate
        endDate: $endDate
        rooms: $rooms
        place: $place
        oldWorldChannel: $oldWorldChannel
        channel: $channel
        subChannel: $subChannel
        companyId: $companyId
        page: $page
        initialPageSize: $initialPageSize
        lazyLoadPageSize: $lazyLoadPageSize
        country: $country
        language: $language
        sort: $sort
        sortOption: $sortOption
        filters: $filters
        ratePlanCodes: $ratePlanCodes
        promotionCode: $promotionCode
      }
    ) {
      promotionsInformation {
        showPromo
        isWithinPromoWindow
        promotionCode
        landingPage
        promoBannerColour
        promoBannerIcon
        promoBannerTitle
        promoBannerSubtitle
        promoInvalidMessage
        promoExpiredMessage
      }
      multiHotelAvailabilities {
        hotelAvailability {
          lowestRoomRate {
            currencyCode
            netTotal
          }
          distance
          unit
          available
          limitedAvailability
          pmsSource
          cellCode
          numberOfRoomsAvailable
        }
        name
        hotelId
        hotelInformation {
          hotelOpeningDate
          brand
          coordinates {
            latitude
            longitude
          }
          messagingFlag {
            color
            text
          }
          hotelFlags {
            isEnabled
            flagBanner {
              text
              textColour
              backgroundImage
              backgroundColour
            }
          }
          thumbnailImages {
            imageSrc
            tags
          }
          hotelFacilities {
            code
            description
            icon
            isVisible
            name
            weight
          }
          links {
            detailsPage
          }
        }
      }
      page
      pageSize
      total
    }
  }
`;
