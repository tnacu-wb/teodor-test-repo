import { gql } from 'graphql-request';

export const GET_HOTELS_INFORMATION = gql`
  query getHotelsInformation(
    $hotelIds: [String!]!
    $country: String!
    $language: String!
    $latitudeRef: Float
    $longitudeRef: Float
    $tripAdvisorDataRequired: Boolean
  ) {
    getHotelsInformation(
      hotelIds: $hotelIds
      country: $country
      language: $language
      latitudeRef: $latitudeRef
      longitudeRef: $longitudeRef
      tripAdvisorDataRequired: $tripAdvisorDataRequired
    ) {
      name
      brand
      countryCodeISO
      hotelId
      distanceFromReference
      hotelDescription
      currencyCode
      links {
        detailsPage
      }
      satNavDirections
      transportInformation
      contactDetails {
        hotelNationalPhone
        phone
      }
      coordinates {
        latitude
        longitude
      }
      address {
        addressLine1
        addressLine2
        addressLine3
        addressLine4
        postalCode
        cityName
        country
      }
      links {
        detailsPage
      }
      galleryImages {
        alt
        caption
        iconSrc
        imageSrc
        thumbnailSrc
      }
      hotelFacilities {
        code
        description
        icon
        isVisible
        name
        weight
      }
      messagingFlag {
        color
        description
        text
      }
      topSectionImages {
        thumbnailSrc
        tags
        imageSrc
        iconSrc
        caption
        alt
      }
      tripAdvisorReviews {
        rating
        address
        numberOfReviews
        reviews {
          rating
          user {
            username
          }
        }
      }
    }
  }
`;
