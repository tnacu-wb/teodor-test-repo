import { gql } from 'graphql-request';

export const GET_PACKAGES = gql`
  query GetPackages(
    $hotelId: String!
    $startDate: String!
    $endDate: String!
    $adultsNumber: Int!
    $childrenNumber: Int!
    $nightsNumber: Int!
    $bookingFlowId: String!
    $basketReferenceId: String!
    $language: String!
    $country: String!
    $channel: Channel
    $isManageBookingPage: Boolean
  ) {
    packages(
      packagesCriteria: {
        hotelId: $hotelId
        startDate: $startDate
        endDate: $endDate
        adultsNumber: $adultsNumber
        childrenNumber: $childrenNumber
        nightsNumber: $nightsNumber
        language: $language
        country: $country
        bookingFlowId: $bookingFlowId
        basketReferenceId: $basketReferenceId
        channel: $channel
        isManageBookingPage: $isManageBookingPage
      }
    ) {
      packages {
        meals {
          allergyInfoLabel
          allergyInfoSrc
          bartId
          currency
          description
          freeBreakfastCode
          freeBreakfastMaxPerMeal
          freeBreakfastOption
          upsellType
          id
          imageSrc
          name
          menu {
            imageSrc
            description
            disclaimer
            menuLabel
            menuSrc
            name
          }
          order
          price
          basePrice
          isFree
        }
        mealsKids {
          allergyInfoLabel
          allergyInfoSrc
          currency
          description
          id
          imageSrc
          menu {
            description
            disclaimer
            menuLabel
            imageSrc
            menuSrc
            name
          }
          name
          order
          price
          totalPrice
        }
        extrasItems {
          currency
          description
          id
          imageSrc
          name
          order
          price
          available
          basePrice
          isFree
          promoText
        }
        roomSelection {
          reservationId
          packagesSelection {
            id
            noOfSelections
          }
        }
        roomSelectionAmendExtras {
          reservationId
          packagesSelection {
            id
            noOfSelections
          }
        }
      }
      privacyPolicy {
        description
        linkLabel
        linkSrc
        moreInfo {
          description
          image
        }
        moreInfoLabel
        name
      }
      restaurant {
        logoSrc
        menus {
          description
          disclaimer
          imageSrc
          menuLabel
          menuSrc
          name
        }
        messageDescription
        messageHeader
        noMealsFound
        restaurantNotFound
      }
      hotelHasCityTaxForBusiness
      hotelHasCityTaxForLeisure
    }
  }
`;
