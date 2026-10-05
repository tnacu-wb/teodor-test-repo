export enum RegisterPersonalInformationStep {
  PERSONAL_INFO_LANDING = 'PERSONAL_INFO_LANDING',
  PERSONAL_INFO_PASSWORD = 'PERSONAL_INFO_PASSWORD',
}

export type RegisterPersonalInformationState = {
  title: string;
  firstName: string;
  lastName: string;
  phone: {
    prefix: string;
    phoneNumber: string;
  };
  emailAddress: string;
  activationKey: string | string[];
  countryCode: string;
};
