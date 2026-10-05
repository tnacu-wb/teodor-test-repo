import { gql } from 'graphql-request';

export const getInnBusinessHeader = () => gql`
  query headerInformation($country: String!, $language: String!, $businessBooker: Boolean!) {
    headerInformation(country: $country, language: $language, businessBooker: $businessBooker) {
      innBusinessHeader(country: $country, language: $language) {
        content {
          header {
            image
            imageAlt
          }
          global {
            today
            tomorrow
            adult
            adults
            child
            children
            room
            rooms
            night
            nights
            single
            double
            accessible
            twin
            family
            adultsLabel
            childrenLabel
            addRoom
            addRoomIcon
            done
            brand {
              piLogo
              pidLogo
              hubLogo
              zipLogo
            }
          }
          countries {
            code
            language
            flagUrl
            url
          }
          form {
            where
            whereIcon
            calendarIcon
            guestIcon
            whereDismissIcon
            hotelsLabel
            search
            searchIcon
            searchEdit
            datePicker {
              months
              weekdaysShort
              reset
              done
              checkOut
            }
            room
            removeRoom
            adultsHelperText
            childrenHelperText
            includeCot
            cotLimit
            roomType
            invalidLocation
            invalidNights
            invalidRooms
          }
          results {
            notifications {
              groupBookingHeader
              groupBookingFormPageMessage
            }
          }
          authentication {
            myProfile
            logoutButton
          }
          contactBanner {
            type
            text
            date
            enabledPages
          }
        }

        layout {
          manageAccount {
            cards {
              title
              link
              icon
            }
            profile {
              text
              link
              title
            }
            title
            employees {
              title
              link
              icon
            }
          }
          menu {
            bookings {
              label
              icon
              iconActive
            }
            spending {
              label
              icon
              iconActive
            }
            home {
              label
              icon
              iconActive
            }
            manage {
              label
              icon
              iconActive
              options {
                employees
                allowances
                alerts
                cards
                questions
                company
              }
            }
            contact {
              label
              icon
              iconActive
            }
          }
          help {
            faq {
              label
              icon
            }
            needHelp
            tour {
              label
              icon
            }
            contact {
              label
              icon
            }
          }
          sidebar {
            collapse
            expand
          }
        }
      }
    }
  }
`;
