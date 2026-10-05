import { gql } from 'graphql-request';

export const GET_STATIC_CONTENT = gql`
  query GetStaticContent(
    $language: String!
    $country: String!
    $businessBooker: Boolean
    $site: String!
  ) {
    headerInformation(country: $country, language: $language, businessBooker: $businessBooker) {
      announcement {
        browserCompatibilityMessage
        text
        type
      }
      config {
        api {
          bookingChannel {
            business
            leisure
          }
        }
        amazonChat {
          amazonChatUrl
          amazonAuthUrl
          amazonChatId
          amazonChatSnippetId
          chatBotStatus
          webChatEnabledPages
          chatIcon
        }
        authentication {
          accountLinks {
            icon
            title
            url
          }
          business {
            businessAccountLinks {
              subMenuLinks {
                url
                title
              }
              title
            }
          }
          businessAccountCardRedirectPath
        }
        bookingSearch {
          show
          dashboardRedirect {
            bookingReference
            cookie {
              domain
              minutesTillExpiry
              name
            }
            url
          }
        }
        roomCodes {
          accessible
          double
          family
          single
          twin
        }
        promotionBanner {
          enabled
          icon
          title
          description
          terms
          srpNotificationTitle
          srpNotificationText
        }
      }
      content {
        hero {
          backgroundColor
          bottomCaptionShow
          bottomCaptionShadow
          subHeadingText
          subHeadingFontColor
          backgroundImage
          headingFontColor
          subHeadingFontShadow
          headingTextShort
          subHeadingFontWeight
          bottomCaptionArrowShow
          bottomCaptionText
          subHeadingShow
          headingFontShadow
          footnotePosRight
          headingTextLong
          headingFontWeight
        }
        favicon {
          faviconUrl
          msIcons {
            name
            content
          }
          icons {
            sizes
            rel
            href
          }
        }
        authentication {
          forgottenPassword {
            backToLogin
            backToYourDetails
            genericError
            business {
              emailPlaceholder
              formLabel
              formTitle
              submitButton
            }
            cancel
            emailSentHeader
            emailSentMessage
            leisure {
              emailPlaceholder
              formLabel
              formTitle
              submitButton
            }
          }
          goBackButton
          signUpButton
          login {
            business {
              bookingsInvalidEmailMsg
              bookingLoginRequiredText
              bookingEmailMaxLengthMsg
              companyActivateFailTitle
              signupButton
              companyActivateFailBody
              tab
              doubleOptInSuccessMessage
              companyActivateSuccessBody
              companyActivateSuccessTitle
              employeeActivationSuccessTitle
              loginInfoNotification
              tabMobile
              doubleOptInFailMessage
              formLabel
              emailPlaceholder
              loginButton
              travelForBusiness
              businessDomain
              businessLogin
            }
            businessAccountCard {
              banner {
                imagePath
              }
              buttonLabel
              tab
              tabMobile
              textBody
              title
            }
            forgotPassword
            leisure {
              emailPlaceholder
              formLabel
              loginButton
            }
            badCredentialsError
            invalidEmail
            passwordPlaceholder
            signupLink
            signupMessage
          }
          logoutButton
          resetPassword {
            criteria
            criteriaDescription
            email
            invalidPassword
            invalidPasswordConfirmation
            logInButton
            password
            passwordConfirmation
            passwordMin
            passwordRequired
            passwordRequirementsAllowed
            passwordRequirementsIdentical
            passwordRequirementsMin
            resetPasswordTitle
            submitButton
            successMessage
          }
        }
        countries {
          flagUrl
          language
        }
        global {
          accessible
          addRoom
          adult
          adultsLabel
          adults
          brand {
            hub
            hubBadge
            hubLogo
            piLogo
            pid
            pi
            pidLogo
            zip
            zipBadge
            zipLogo
            hubHdpLogo
          }
          child
          children
          childrenLabel
          done
          double
          family
          night
          room
          roomLabel
          rooms
          tomorrow
          today
          single
          twin
          offers {
            cellCode
            maxRooms
            numberOfNights
            page
            corpId: corporateId
            ratePlanCode
          }
          promotions {
            maxRooms
            numberOfNights
            page
            promoCode
            enabled
          }
          thirdParties
        }
        header {
          image
        }
        menu {
          agentMemo
          bookHotel
          business
          changeLogs
          discoverPI
          findBooking
          guestAccount
          language
          languageButton
          logIn
          mobileMenuButton
          tick
          promoCode
        }
        subNav {
          navOptions {
            title
            url
          }
          title
        }
      }
      datePicker {
        checkOut
        invalidDate
        reset
      }
      form {
        adultsHelperText
        arrivalDateLabel
        bookingInvalid
        bookingReferenceLabel
        bookingSurnameLabel
        checkout
        childrenHelperText
        cotLimit
        findBookingDescription
        findBookingTitle
        includeCot
        invalidFutureDate
        invalidDate
        invalidLocation
        invalidNights
        invalidPastDate
        invalidReference
        invalidRooms
        invalidSurname
        removeRoom
        roomType
        searchBookingError
        snowdropError
        snowdropErrorRetry
        where
      }
      results {
        notifications {
          availabilitiesErrorMessage
          ccuiGroupBookingMessage
          errorTitle
          groupBookingHeader
          groupBookingMessage
          noResults
          emp01groupBookingMessage
          groupBookingFormPageMessage
        }
      }
      contactBanner {
        date
        enabledPages
        text
        type
      }
    }

    footer(site: $site, country: $country, language: $language) {
      copyrightInfo
      newsletterSignup {
        introViewText
        introViewTitle
        signUpButtonText
      }
      socialMediaIcons {
        iconSrc
        label
        linkSrc
      }
      tabs {
        columns {
          linkItems {
            linkSrc
            name
            openInNewTab
          }
          name
        }
        intro {
          description
          name
        }
        name
      }
    }
  }
`;

export const getStaticContent = (isBarrierFreeLabelEnabled = false) => gql`
  query GetStaticContent(
    $language: String!
    $country: String!
    $businessBooker: Boolean
    $site: String!
  ) {
    headerInformation(country: $country, language: $language, businessBooker: $businessBooker) {
      announcement {
        browserCompatibilityMessage
        text
        type
      }
      config {
        api {
          bookingChannel {
            business
            leisure
          }
        }
        amazonChat {
          amazonChatUrl
          amazonAuthUrl
          amazonChatId
          amazonChatSnippetId
          chatBotStatus
          webChatEnabledPages
          chatIcon
        }
        authentication {
          accountLinks {
            icon
            title
            url
          }
          business {
            businessAccountLinks {
              subMenuLinks {
                url
                title
              }
              title
            }
          }
          businessAccountCardRedirectPath
        }
        bookingSearch {
          show
          dashboardRedirect {
            bookingReference
            cookie {
              domain
              minutesTillExpiry
              name
            }
            url
          }
        }
        roomCodes {
          accessible
          double
          family
          single
          twin
        }
        promotionBanner {
          enabled
          icon
          title
          description
          terms
          srpNotificationTitle
          srpNotificationText
        }
      }
      content {
        hero {
          backgroundColor
          bottomCaptionShow
          bottomCaptionShadow
          subHeadingText
          subHeadingFontColor
          backgroundImage
          headingFontColor
          subHeadingFontShadow
          headingTextShort
          subHeadingFontWeight
          bottomCaptionArrowShow
          bottomCaptionText
          subHeadingShow
          headingFontShadow
          footnotePosRight
          headingTextLong
          headingFontWeight
        }
        favicon {
          faviconUrl
          msIcons {
            name
            content
          }
          icons {
            sizes
            rel
            href
          }
        }
        authentication {
          forgottenPassword {
            backToLogin
            backToYourDetails
            genericError
            business {
              emailPlaceholder
              formLabel
              formTitle
              submitButton
            }
            cancel
            emailSentHeader
            emailSentMessage
            leisure {
              emailPlaceholder
              formLabel
              formTitle
              submitButton
            }
          }
          goBackButton
          signUpButton
          login {
            business {
              bookingsInvalidEmailMsg
              bookingLoginRequiredText
              bookingEmailMaxLengthMsg
              companyActivateFailTitle
              signupButton
              companyActivateFailBody
              tab
              doubleOptInSuccessMessage
              companyActivateSuccessBody
              companyActivateSuccessTitle
              employeeActivationSuccessTitle
              loginInfoNotification
              tabMobile
              doubleOptInFailMessage
              formLabel
              emailPlaceholder
              loginButton
              travelForBusiness
              businessDomain
              businessLogin
            }
            businessAccountCard {
              banner {
                imagePath
              }
              buttonLabel
              tab
              tabMobile
              textBody
              title
            }
            forgotPassword
            leisure {
              emailPlaceholder
              formLabel
              loginButton
            }
            badCredentialsError
            invalidEmail
            passwordPlaceholder
            signupLink
            signupMessage
          }
          logoutButton
          resetPassword {
            criteria
            criteriaDescription
            email
            invalidPassword
            invalidPasswordConfirmation
            logInButton
            password
            passwordConfirmation
            passwordMin
            passwordRequired
            passwordRequirementsAllowed
            passwordRequirementsIdentical
            passwordRequirementsMin
            resetPasswordTitle
            submitButton
            successMessage
          }
        }
        countries {
          flagUrl
          language
        }
        global {
          accessible
          ${isBarrierFreeLabelEnabled ? 'accessibleOrBarrierFree' : ''}
          addRoom
          adult
          adultsLabel
          adults
          brand {
            hub
            hubBadge
            hubLogo
            piLogo
            pid
            pi
            pidLogo
            zip
            zipBadge
            zipLogo
            hubHdpLogo
          }
          child
          children
          childrenLabel
          done
          double
          family
          night
          room
          roomLabel
          rooms
          tomorrow
          today
          single
          twin
          offers {
            cellCode
            maxRooms
            numberOfNights
            page
            corpId: corporateId
            ratePlanCode
          }
          promotions {
            maxRooms
            numberOfNights
            page
            promoCode
            enabled
          }
          thirdParties
        }
        header {
          image
        }
        menu {
          agentMemo
          bookHotel
          business
          changeLogs
          discoverPI
          findBooking
          guestAccount
          language
          languageButton
          logIn
          mobileMenuButton
          tick
          promoCode
        }
        subNav {
          navOptions {
            title
            url
          }
          title
        }
      }
      datePicker {
        checkOut
        invalidDate
        reset
      }
      form {
        adultsHelperText
        arrivalDateLabel
        bookingInvalid
        bookingReferenceLabel
        bookingSurnameLabel
        checkout
        childrenHelperText
        cotLimit
        findBookingDescription
        findBookingTitle
        includeCot
        invalidFutureDate
        invalidDate
        invalidLocation
        invalidNights
        invalidPastDate
        invalidReference
        invalidRooms
        invalidSurname
        removeRoom
        roomType
        searchBookingError
        snowdropError
        snowdropErrorRetry
        where
      }
      results {
        notifications {
          availabilitiesErrorMessage
          ccuiGroupBookingMessage
          errorTitle
          groupBookingHeader
          groupBookingMessage
          noResults
          emp01groupBookingMessage
          groupBookingFormPageMessage
        }
      }
      contactBanner {
        date
        enabledPages
        text
        type
      }
    }

    footer(site: $site, country: $country, language: $language) {
      copyrightInfo
      newsletterSignup {
        introViewText
        introViewTitle
        signUpButtonText
      }
      socialMediaIcons {
        iconSrc
        label
        linkSrc
      }
      tabs {
        columns {
          linkItems {
            linkSrc
            name
            openInNewTab
          }
          name
        }
        intro {
          description
          name
        }
        name
      }
    }
  }
`;
