import { gql } from 'graphql-request';

export const managementInformationReportQuery = () => gql`
  query managementInformationReport(
    $fromDate: String!
    $toDate: String!
    $showQnAcolumns: Boolean!
    $language: String!
  ) {
    managementInformationReport(
      fromDate: $fromDate
      toDate: $toDate
      showQnAcolumns: $showQnAcolumns
      language: $language
    ) {
      downloadUrl
      fileName
      reportName
    }
  }
`;
