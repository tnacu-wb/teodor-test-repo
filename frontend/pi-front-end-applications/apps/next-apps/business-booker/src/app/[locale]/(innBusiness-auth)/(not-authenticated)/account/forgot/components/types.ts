export enum ForgotStep {
  FORGOT_FORM = 'FORGOT_FORM',
  FORGOT_CONFIRMATION = 'FORGOT_CONFIRMATION',
}

export enum ForgotConfirmationState {
  DEFAULT = 'DEFAULT',
  SUCCESS = 'SUCCESS',
  ERROR = 'ERROR',
}

export type ForgotState = {
  email: string;
  confirmationState: ForgotConfirmationState;
};
