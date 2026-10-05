import { gql } from 'graphql-request';

export const InnBRegistrationStepOneQuery = () => gql`
  mutation InnBRegistrationStepOne(
    $innBRegistrationStepOneRequest: InnBRegistrationStepOneRequest!
  ) {
    innBRegistrationStepOne(innBRegistrationStepOneRequest: $innBRegistrationStepOneRequest) {
      existingCompany
      existingEmployee
    }
  }
`;
