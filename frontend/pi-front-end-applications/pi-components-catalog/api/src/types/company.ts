import { Company } from './graphql';

export type CompanyResults = {
  companies: Company[];
  tooManyResults: boolean;
};

export type CompanyData = {
  companyProfile: CompanyProfile | undefined;
};

export type CompanyProfile = {
  address: CompanyAddress | undefined;
  name: string | undefined;
};

export type CompanyAddress = {
  addressLine1: string;
  addressLine2: string;
  addressLine3: string;
  addressLine4?: string;
  cityName?: string;
  country: string;
  postalCode: string;
};
