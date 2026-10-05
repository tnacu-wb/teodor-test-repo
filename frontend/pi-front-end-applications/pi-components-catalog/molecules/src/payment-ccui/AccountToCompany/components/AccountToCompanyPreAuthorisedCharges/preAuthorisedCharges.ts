import { PreAuthorisedChargesData, ACCOUNT_TO_COMPANY_ALLOWANCES } from '@whitbread-eos/api';

const PRE_AUTHORISED_CHARGES: PreAuthorisedChargesData[] = [
  {
    id: 1,
    label: ACCOUNT_TO_COMPANY_ALLOWANCES.BREAKFAST,
    key: 'ccui.accountToCompanyCharges.option1',
  },
  {
    id: 2,
    label: ACCOUNT_TO_COMPANY_ALLOWANCES.CAR_PARKING,
    key: 'ccui.accountToCompanyCharges.option2',
  },
];

export default PRE_AUTHORISED_CHARGES;
