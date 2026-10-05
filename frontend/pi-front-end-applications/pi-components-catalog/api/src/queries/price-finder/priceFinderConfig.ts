import { gql } from 'graphql-request';

export const GET_PRICE_FINDER_CONFIG = gql`
  query GetPriceFinderConfig(
    $channel: Channel!
    $brand: String
    $language: String
    $country: String
    $path: String
  ) {
    priceFinderConfig(
      channel: $channel
      brand: $brand
      language: $language
      country: $country
      path: $path
    ) {
      priceFinderConfig {
        priceFinderViews {
          path
          bannerImage
          bannerColour
          bannerHeadline
          bannerSubtext
          termsLabel
          dateRangeStart
          dateRangeEnd
          checkinDate
          highlightedPriceRangeStep
          highlightedPriceRangeMin
          highlightedPriceRangeMax
          highlightedPricePrimaryColour
          highlightedPriceSecondaryColour
          filterRoomSelected
          locationId
          locationName
          locationRadius
          hotelCodes {
            code
            order
          }
          destinations {
            locations {
              locationId
              locationName
              locationOrder
            }
          }
          infoMessages {
            messageTitle
            messageSubtitle
            messageType
            messageOrder
          }
          seoMetaTitle
          seoMetaDescription
          seoRobots
          seoHreflangs {
            hreflang
            href
          }
        }
      }
    }
  }
`;
