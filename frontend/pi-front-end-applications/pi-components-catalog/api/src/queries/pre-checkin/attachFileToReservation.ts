import { gql } from 'graphql-request';

export const ATTACH_FILE_TO_RESERVATION = gql`
  mutation attachFileToReservation(
    $fileName: String!
    $reservationId: String!
    $overwriteExistingFile: Boolean!
    $description: String!
    $hotelId: String!
    $global: Boolean!
    $fileAttachment: String!
  ) {
    attachFileToReservation(
      fileAttachmentCriteria: {
        fileName: $fileName
        reservationId: $reservationId
        overwriteExistingFile: $overwriteExistingFile
        description: $description
        hotelId: $hotelId
        global: $global
        fileAttachment: $fileAttachment
      }
    ) {
      status
      message
    }
  }
`;
