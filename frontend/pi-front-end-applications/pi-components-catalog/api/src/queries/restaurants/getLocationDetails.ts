import { gql } from 'graphql-request';

export const GET_LOCATIONS_DETAILS = gql`
  query MyQuery($restaurant: String!, $location: String!, $subLocation: String!) {
    locations(restaurant: $restaurant, location: $location, subLocation: $subLocation) {
      id
      path
      title
      contactInfo
      googleMapURL
      bookingHeroImage
      bookingHeroBackgroundImage
    }
  }
`;
