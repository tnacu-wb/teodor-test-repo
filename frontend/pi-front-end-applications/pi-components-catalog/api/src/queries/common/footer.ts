import { gql } from 'graphql-request';

export const getFooterDetails = () => gql`
  query footer($country: String!, $site: String!, $language: String!) {
    footer(site: $site, country: $country, language: $language) {
      newsletterSignup {
        introViewTitle
        introViewText
        signUpButtonText
      }
      copyrightInfo
      socialMediaIcons {
        linkSrc
        iconSrc
        label
      }
      tabs {
        intro {
          name
          description
        }
        columns {
          name
          linkItems {
            linkSrc
            openInNewTab
            name
          }
        }
        name
      }
      bottomLinks {
        linkSrc
        openInNewTab
        name
      }
    }
  }
`;
