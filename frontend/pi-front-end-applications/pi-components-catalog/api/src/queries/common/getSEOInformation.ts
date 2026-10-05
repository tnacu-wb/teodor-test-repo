import { gql } from 'graphql-request';

export const GET_SEO_INFORMATION = gql`
  query getSeoInformation(
    $page: String!
    $hotelId: String
    $bookingFlowId: String
    $language: String!
    $country: String!
  ) {
    seoInformation(
      page: $page
      hotelId: $hotelId
      bookingFlowId: $bookingFlowId
      language: $language
      country: $country
    ) {
      hreflangs {
        hreflang
        href
      }
      faq {
        faqItems {
          answer
          question
        }
        title
      }
      faviconUrl
      icons {
        href
        rel
        sizes
      }
      msIcons {
        content
        name
      }
      pageDescription
      pageTitle
      cardImageUrl
    }
  }
`;
