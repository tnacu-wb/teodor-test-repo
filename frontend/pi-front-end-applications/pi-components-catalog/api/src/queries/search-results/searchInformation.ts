import { gql } from 'graphql-request';

export const SEARCH_INFORMATION_RESULTS = gql`
  query searchInformation($country: String!, $language: String!) {
    searchInformation(country: $country, language: $language) {
      config {
        api {
          initialPageSize
          lazyLoadPageSize
          radius
        }
      }
      content {
        global {
          brand {
            zipBadge
            hubBadge
          }
        }
        map {
          controlText
          list
        }
        dynamicFilters {
          groupTitle
          groupOperator
          groupItems {
            label
            info
            codes
            queryParam
          }
        }
        filter {
          label {
            header
            parking
            freeParking
            restaurant
            airCon
            chargeableOffsiteParking
            chargeableOnsiteParking
            lift
            meet
            apply
            reset
            facilities
          }
          info {
            lift
          }
          code {
            restaurant
            airCon
            chargeableOffsiteParking
            freeParking
            chargeableOnsiteParking
            lift
            meet
          }
        }
        results {
          menu {
            listLong
            mapLong
            distance
            price
            filtersLong
            recommended
            map
            list
            filter
          }
          notifications {
            fullyBooked
            openingSoon
            noFilteredHotels
            availabilitiesErrorMessage
            errorTitle
          }
          result {
            availabilityWarning
            distanceUnitPlural
            facilities {
              businessRoom
              freeParking
              noParking
              parking
              premierPlusRoom
              standardExtraRoom
            }
            fromLocation
            fullyBooked
            openingOn
            openingSoon
            priceFrom
            viewDetails
            promotions {
              discountApplied
              discountUnavailable
            }
          }
        }
        totalHotels
      }
    }
  }
`;

export const SEARCH_INFORMATION_PAGE_SIZE_RESULTS = gql`
  query searchInformation($country: String!, $language: String!) {
    searchInformation(country: $country, language: $language) {
      config {
        api {
          initialPageSize
          lazyLoadPageSize
        }
      }
    }
  }
`;
