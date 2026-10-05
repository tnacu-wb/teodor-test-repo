import { gql } from 'graphql-request';

export const GET_BOOK_PAGE_DATA = gql`
  query bookPage($location: String!, $restaurant: String!, $subLocation: String!) {
    bookPage(location: $location, restaurant: $restaurant, subLocation: $subLocation) {
      heroBackgroundImageSrc
      subtitleName
      name
      heroImageSrc
    }
  }
`;
