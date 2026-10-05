import { gql } from 'graphql-request';

export const getCompanyDetailsQuery = () => gql`
  query companyDetailsV3($companyId: String!) {
    companyDetailsV3(companyId: $companyId) {
      requestedCompany {
        companyDetails {
          companyName
          alternateCompanyName
          numberOfEmployees
          companySector
          averageMonthlyBooking
          numberOfEmployee
          companyAddress {
            addressLine1
            addressLine2
            addressLine3
            addressLine4
            addressLine5
            postCode
            country
          }
          mainEmployee {
            id
            ghNumber
            emailAddress
            position
            phoneNumber
            mobileNumber
            textConfirmation
            title
            firstName
            lastName
            centralCardId
            address {
              addressLine1
              addressLine2
              addressLine3
              addressLine4
              addressLine5
              postCode
              country
            }
            accessLevel
            employeeStatus
            dialingCode
            employeeAnswers {
              customerReferenceAnswer
              purchaseOrderAnswer
              userDefinedAnswers {
                miID
                miAnswer
              }
            }
          }
        }
        companyType
        paymentDetails {
          profileLocked
          allowIndividualCards
          paymentCards {
            cardId
            cardLabel
            cardType
            nameOnCard
            cardNumber
            startDate
            expiryDate
            issueNumber
            cardToken
            billingAddress {
              addressLine1
              addressLine2
              addressLine3
              addressLine4
              addressLine5
              postCode
              country
            }
            cardNotPresentRequired
            cardNotPresent {
              businessAccountUsername
              businessAccountPassword
            }
          }
        }
        bookingAllowances {
          maxDinnerBudgets {
            uKWide {
              amount
              currency
            }
            greaterLondon {
              amount
              currency
            }
            ireland {
              amount
              currency
            }
          }
          extrasCodes
          upsellItemsAllowed
          allowAlcohol
          allowCarParking
          allowAdditionalCosts
          allowPremierSaverRates
          allowIndividualCards
          maxNumberOfNights
        }
        bookingAlerts {
          rateCaps {
            uKWide {
              amount
              currency
            }
            greaterLondon {
              amount
              currency
            }
            ireland {
              amount
              currency
            }
          }
          bookingAlertHotels
          recipientEmailAddresses
          dayOfArrival
          weekendArrival
          passThroughWeekend
          frequency
        }
        companyManagementDetails {
          purchaseOrderManagement {
            questionId
            label
            mandatory
            managementHeader
            active
            location
            managementInformationAnswer {
              answerType
              answers
            }
            type
            positionId
          }
          customerReferenceManagement {
            questionId
            label
            mandatory
            managementHeader
            active
            location
            managementInformationAnswer {
              answerType
              answers
            }
            type
            positionId
          }
          userDefinedManagement {
            questionId
            label
            mandatory
            managementHeader
            active
            location
            managementInformationAnswer {
              answerType
              answers
            }
            type
            positionId
          }
        }
        companyCellCodes {
          type
          description
        }
        restrictedRatePlans {
          code
        }
        restrictedHotelCodes {
          code
        }
        companyStatus {
          status
        }
      }
      companyCellCodes {
        type
        description
      }
      allowCentralCreditCard
      marketingAllowed
      companyLockedForEditing
      success
    }
  }
`;
