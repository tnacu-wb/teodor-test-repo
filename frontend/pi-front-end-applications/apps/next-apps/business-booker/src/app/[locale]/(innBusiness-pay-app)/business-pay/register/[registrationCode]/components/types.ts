import { AuthenticationAnswer, RegistrationPrePopulatedItems } from '@whitbread-eos/api';

export enum RegisterIbStep {
  QUESTIONS_FORM = 'QUESTIONS_FORM',
  DETAILS_FORM = 'DETAILS_FORM',
}

export type RegistrationState = {
  prepopulatedItems: RegistrationPrePopulatedItems | null;
  authenticationAnswers: AuthenticationAnswer[];
};
