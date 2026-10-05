import { gql } from 'graphql-request';

export const getStaticLabels = (fetchPreCheckInLabel = false) => gql`
  query GetStaticLabels($language: String!, $country: String!) {
    labels(country: $country, language: $language) {
      booking
      main
      piBookings
      ${fetchPreCheckInLabel ? 'piPreCheckIn' : ''}
      piGroupBooking
        extras
      promotions
    }
  }
`;
