import jwtDecode from 'jwt-decode';

export const OPERA_COMPANY_ID = 'https://premierinn.com/operaCompanyId';
export const CDH_COMPANY_ID = 'https://premierinn.com/companyAccountId';
export const CDH_EMPLOYEE_ID = 'https://premierinn.com/employeeAccountId';
export const CDH_CUSTOMER_ID = 'https://premierinn.com/customerAccountId';

export default function getLoggedInUserInfo(token: string) {
  let decodedToken = {
    name: '',
    profile: {
      accessLevel: '',
      companyId: '',
      employeeId: '',
      isBusiness: '',
      sessionId: '',
    },
    [CDH_COMPANY_ID]: '',
    [CDH_EMPLOYEE_ID]: '',
    [OPERA_COMPANY_ID]: '',
    [CDH_CUSTOMER_ID]: '',
  };
  let cdhCompanyId = '';
  let cdhEmployeeId = '';
  let operaCompanyId = '';
  let cdhCustomerId = '';

  try {
    if (token?.length) {
      decodedToken = jwtDecode(token);
      cdhCompanyId = decodedToken[CDH_COMPANY_ID];
      cdhEmployeeId = decodedToken[CDH_EMPLOYEE_ID];
      operaCompanyId = decodedToken[OPERA_COMPANY_ID];
      cdhCustomerId = decodedToken[CDH_CUSTOMER_ID];
    }
  } catch (e) {
    // eslint-disable-next-line no-console
    console.log(e);
  }

  return {
    ...decodedToken,
    ...decodedToken.profile,
    cdhCompanyId,
    cdhEmployeeId,
    operaCompanyId,
    cdhCustomerId,
  };
}
