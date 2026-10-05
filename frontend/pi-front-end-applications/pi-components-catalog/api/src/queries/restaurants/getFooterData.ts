import { gql } from 'graphql-request';

export const GET_FOOTER_DATA = gql`
  query ($restaurant: String!) {
    footer(restaurant: $restaurant) {
      copyrightInfo
      legalCopyRightLabel
      socialMediaIcons {
        label
        linkSrc
        visible
      }
      tabs {
        columns {
          linkItems {
            linkSrc
            name
            openInNewTab
          }
          name
        }
        name
      }
    }
  }
`;
