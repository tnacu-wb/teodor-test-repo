import { gql } from 'graphql-request';

export const updateAppCompanyDetailsQuery = () => gql`
  mutation UpdateAppCompanyDetails(
    $updateAppCompanyDetailsCriteria: UpdateAppCompanyDetailsCriteria!
  ) {
    updateAppCompanyDetails(updateAppCompanyDetailsCriteria: $updateAppCompanyDetailsCriteria) {
      message
      status
    }
  }
`;
