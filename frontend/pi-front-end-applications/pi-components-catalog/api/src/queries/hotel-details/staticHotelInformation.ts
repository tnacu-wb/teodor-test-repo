import { gql } from 'graphql-request';

export const STATIC_HOTEL_INFORMATION_QUERY = gql`
  query hotelInformationBySlug(
    $slug: String!
    $language: String!
    $country: String!
    $stayStartDate: String
    $stayEndDate: String
  ) {
    hotelInformationBySlug(
      slug: $slug
      language: $language
      country: $country
      stayStartDate: $stayStartDate
      stayEndDate: $stayEndDate
    ) {
      bookingFlow {
        bookingFlowItems {
          bookingId
          rateCode
          rateCategory
        }
      }
      seo {
        geoJsonLd
      }
      cityTax {
        isCityTaxHotel
        isCityTaxBusinessHotel
        percentage
        effectiveFrom
        bookingDateFrom
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
      # badges
      name
      title
      brand
      countryCodeISO
      headline
      address {
        addressLine1
        addressLine2
        addressLine3
        postalCode
        country
      }
      satNavDirections
      directions
      transportInformation
      parkingDescription
      links {
        detailsPage
      }
      roomConfiguration {
        tabGroups {
          groupId
          groupName
        }
        tabItems {
          roomTypeCode
          roomDescription
          roomName
          roomType
          images {
            alt
            caption
            iconSrc
            imageSrc
            thumbnailSrc
          }
          facilities {
            weight
            name
            isVisible
            icon
            description
            code
          }
        }
      }
      restaurant {
        name
        description
        logoSrc
        menus {
          name
          description
          imageSrc
          menuSrc
          menuLabel
          disclaimer
        }
      }
      coordinates {
        latitude
        longitude
      }
      whatThreeWords
      hotelId
      hotelOpeningDate
      messagingFlag {
        color
        description
        text
      }
      announcement {
        endDate
        showAnnouncement
        startDate
        text
        bbText
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
          hideOnHdp
        }
      }
      accessibilityInfo {
        header
        linkText
        phoneNumber
        text
      }
      contactDetails {
        phone
        hotelNationalPhone
        email
      }
      hotelDescription
      faq {
        faqItems {
          answer
          question
        }
        title
      }
      facts {
        factItems {
          title
          description
        }
      }
      breadcrumb {
        title
        link
      }
      tripAdvisorReviews {
        rating
        name
        address
        numberOfReviews
        writeReview
        webUrl
        locationId
        hotelCode
        awards {
          awardType
          year
          image
        }
        reviews {
          publishedDate
          rating
          tripType
          title
          text
          user {
            username
            location
          }
        }
        subRatings {
          ratingImageUrl
          value
          localisedName
        }
      }
      roomClassConfiguration {
        code
        title
      }
      hotelFlags {
        isEnabled
        flagOverlay {
          text
          textColour
          backgroundColour
          backgroundImage
        }
        flagBanner {
          text
          textColour
          backgroundColour
          backgroundImage
        }
      }
    }
  }
`;
