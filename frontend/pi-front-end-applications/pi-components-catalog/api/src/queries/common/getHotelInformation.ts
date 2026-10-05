import { gql } from 'graphql-request';

export const GET_HOTEL_INFORMATION = gql`
  query GetHotelInformation($hotelId: String!, $country: String!, $language: String!) {
    hotelInformation(hotelId: $hotelId, country: $country, language: $language) {
      address {
        addressLine1
        addressLine2
        addressLine3
        addressLine4
        postalCode
        country
      }
      hotelId
      hotelOpeningDate
      name
      brand
      parkingDescription
      directions
      cityTax {
        isCityTaxHotel
        isCityTaxBusinessHotel
      }
      county
      contactDetails {
        phone
        hotelNationalPhone
        email
      }
      coordinates {
        latitude
        longitude
      }
      links {
        detailsPage
      }
      galleryImages {
        alt
        thumbnailSrc
      }
      announcement {
        endDate
        showAnnouncement
        startDate
        text
        title
        type
      }
      importantInfo {
        title
        infoItems {
          text
          htmlText
          priority
          startDate
          endDate
          hideOnBookingFlow
        }
      }
      ancillaryCloseout {
        items {
          endDate
          serviceCode
          startDate
          text
          upsellCodes
          noMealsHeading
          noMealsMessage
        }
      }
      restaurant {
        bookingCardImage
        bookingCardBackgroundImage
      }
      bookRestaurantCta {
        bookingCardCtaText
        bookingCardCtaLink
      }
      isDataTransEnabled
    }
  }
`;
