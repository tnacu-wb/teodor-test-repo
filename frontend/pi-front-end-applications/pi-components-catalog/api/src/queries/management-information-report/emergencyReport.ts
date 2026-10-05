import { gql } from 'graphql-request';

export const emergencyReportQuery = () => gql`
  query emergencyReport($language: String!) {
    emergencyReport(language: $language) {
      downloadUrl
      fileName
      reportName
    }
  }
`;
