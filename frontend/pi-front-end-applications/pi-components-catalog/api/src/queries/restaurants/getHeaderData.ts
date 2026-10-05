import { gql } from 'graphql-request';

export const GET_HEADER_DATA = gql`
  query ($restaurant: String!) {
    header(restaurant: $restaurant) {
      homeAltSrc
      locationSrc
      logoAlt
      logoSrc
      moreLocationName
      navbar {
        linkItems {
          linkSrc
          name
          openInNewTab
        }
        name
      }
    }
  }
`;
