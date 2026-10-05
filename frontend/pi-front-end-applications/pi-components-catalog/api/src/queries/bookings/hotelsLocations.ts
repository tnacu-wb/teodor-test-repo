import { gql } from 'graphql-request';

export const HOTELS_BY_LOCATION = gql`
  query getHotelsByLocation(
    $location: String!
    $locationFormat: String!
    $radius: String!
    $radiusUnit: String!
  ) {
    hotelsLocations(
      hotelsSearchCriteria: {
        location: $location
        locationFormat: $locationFormat
        radius: $radius
        radiusUnit: $radiusUnit
      }
    ) {
      brand
      distance
      hotelId
      name
    }
  }
`;
