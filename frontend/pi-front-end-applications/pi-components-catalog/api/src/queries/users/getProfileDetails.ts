import { gql } from 'graphql-request';

export const getProfileDetailsQuery = () => gql`
  query getProfileDetails($customerId: String!, $business: Boolean, $innBusiness: Boolean) {
    getProfileDetailsV3(customerId: $customerId, business: $business, innBusiness: $innBusiness) {
      customerAccountId
      contactDetail {
        title
        firstName
        lastName
        email
        telephone
        mobile
        nationality
        passport {
          number
          countryOfIssue
        }
        carRegistration
        address {
          line1
          line2
          line3
          line4
          line5
          postCode
          countryCode
          countryCodeISO
          type
          companyName
        }
      }
      paymentPreference {
        electronicInvoiceRequired
        paymentCard {
          cardId
          cardLabel
          cardType
          cardNumber
          startDate
          expiryDate
          issueNumber
          cardHolderName
          cardToken
          billingAddress {
            line1
            line2
            line3
            line4
            line5
            postCode
            countryCode
            countryCodeISO
            type
            companyName
          }
          cnpRequired
          cnpBusinessAccountUsername
          cnpBusinessAccountPassword
        }
      }
      additionalGuests {
        title
        firstName
        lastName
        email
        telephone
        nationality
        passport {
          number
          countryOfIssue
        }
        carRegistration
      }
      bookingPreference {
        roomRequirements {
          type
          lettingType
          adults
          children
          cotRequired
          hotelBrand
        }
        reason
        foodPreference
        wantSmsConfirmations
        preselectWifi
      }
      companyId
      businessUse
      business {
        accessLevel
        purchaseOrderAnswer
        customerReferenceAnswer
        centralCard
        myPILink
        dismissMPILink
        miSetupRequired
        awaitingApproval
        employeeId
        tethered
      }
      guestHistoryCreation
      totalStays
    }
  }
`;
