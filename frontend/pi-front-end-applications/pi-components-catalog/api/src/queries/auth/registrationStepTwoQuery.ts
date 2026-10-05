import { gql } from 'graphql-request';

export const registrationStepTwoQuery = () => gql`
  mutation InnBRegistrationStepTwo(
    $innBRegistrationStepTwoRequest: InnBRegistrationStepTwoRequest!
  ) {
    innBRegistrationStepTwo(innBRegistrationStepTwoRequest: $innBRegistrationStepTwoRequest) {
      email
      companyId
    }
  }
`;
