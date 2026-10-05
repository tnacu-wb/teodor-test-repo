import { gql } from 'graphql-request';

export const GET_LOWEST_RATES_BY_LOCATION_ID = gql`
  query getLowestRatesByLocationId($criteria: PriceFinderLocationSearchInput!) {
    getLowestRatesByLocationId(criteria: $criteria) {
      priceFinderOperaHotelAvailabilitiesDtoList {
        hotelCode
        hotelName
        links {
          detailsPage
        }
        availabilities {
          availableDate
          currency
          minimumRate
          rateCode
          hasMlosRestriction
          rateClassification
          roomType
          roomCategory
          minimumNights
          quantity
        }
      }
      total
      page
      pageSize
      lowestMonthlyRate {
        price
        currency
      }
    }
  }
`;
