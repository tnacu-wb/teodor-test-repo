import { gql } from 'graphql-request';

export const GET_ROOM_TYPE_INFORMATION_QUERY = gql`
  query getRoomTypeInformation(
    $brand: String!
    $language: String!
    $country: String!
    $hotelId: String!
  ) {
    roomTypeInformation(brand: $brand, language: $language, country: $country, hotelId: $hotelId) {
      roomTypes {
        roomTypeCode
        roomCategory
        roomLabel
        roomDescription
        roomImage
        groupId
      }
    }
  }
`;
