export enum ResetStep {
  RESET_FORM = 'RESET_FORM',
}

export type ResetState = {
  email: string;
  passwordToken: string | string[];
  isInvalidKey: boolean;
};
