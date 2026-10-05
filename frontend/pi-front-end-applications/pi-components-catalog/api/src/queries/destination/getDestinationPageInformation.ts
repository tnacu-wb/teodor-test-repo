import { gql } from 'graphql-request';

export const GET_DESTINATION_PAGE_INFORMATION = gql`
  query dlpInformation($country: String!, $dlpPath: String!, $language: String!) {
    dlpInformation(country: $country, dlpPath: $dlpPath, language: $language) {
      seo {
        pageDescription
        pageTitle
        geoJsonLd
        hreflangs {
          hreflang
          href
        }
      }
      breadcrumbs {
        title
        link
      }
      title
      description
      picture
      coordinates {
        longitude
        latitude
        hideHotelDistance
      }
      hotels {
        code
        order
      }
      why {
        title
        description
        picture
        whyItems {
          itemIcon
          itemTitle
          itemDescription
        }
      }
      faq {
        title
        faqItems {
          question
          answer
        }
      }
      dlps {
        title
        dlpItems {
          picture
          title
          link
          order
        }
      }
      thingsToDo {
        title
        items {
          picture
          title
          link
          order
        }
      }
      promos {
        picture
        title
        description
        linkText
        linkUrl
        linkTarget
        order
      }
    }
  }
`;
