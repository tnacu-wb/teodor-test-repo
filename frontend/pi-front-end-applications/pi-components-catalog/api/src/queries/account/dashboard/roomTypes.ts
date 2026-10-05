import { gql } from 'graphql-request';

export const GET_ROOM_TYPE_BOOKING_HISTORY_INFORMATION_QUERY = gql`
  query getRoomTypeInformation($brand: String!, $language: String!, $country: String!) {
    roomTypeInformation(brand: $brand, language: $language, country: $country) {
      roomTypes {
        roomTypeCode
        roomLabel
      }
    }
  }
`;
