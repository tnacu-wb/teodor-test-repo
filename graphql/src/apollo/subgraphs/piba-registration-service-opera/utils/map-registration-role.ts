import { PibaRegistrationRole } from '../models/PibaRegistrationRole';

export const mapRegistrationRole = (role: string): PibaRegistrationRole | null => {
  switch (role) {
    case 'ReportsAndInvoices':
    case 'MMAReportsAndInvoices':
      return PibaRegistrationRole.FinanceUser;
    case 'AccountHolder':
      return PibaRegistrationRole.AccountHolder;
    case 'Cardholder':
      return PibaRegistrationRole.Cardholder;
    case 'CostCenterUser':
      return PibaRegistrationRole.CostCenterUser;
    case 'CostCentreHolder':
      return PibaRegistrationRole.CostCentreHolder;
    default:
      return null;
  }
};
