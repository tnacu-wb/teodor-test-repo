import { SearchCompanySpendingCriteria } from '../models/search-company-spending-criteria';
import { AccountSpendingCriteria } from '../models/account-spending-criteria';
import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { PaymentInfoCriteria } from '../models/account-payment-info-criteria';
import { EmployeeSpendCriteria } from '../models/employee-spend-criteria';

/**
 * This method is used to retrieve company spending.
 *
 * @param searchCompanySpending
 * @param context contains the headers and client
 * @returns company spending response
 */
export const retrieveCompanySpending = async (
  { searchCompanySpending }: { searchCompanySpending: SearchCompanySpendingCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'fromMonthYear', value: searchCompanySpending.fromMonthYear, required: true },
      { key: 'toMonthYear', value: searchCompanySpending.toMonthYear, required: true }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(endpoints.GET_COMPANY_SPENDING, retrieveCompanySpending, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getAccountUpcomingSpending = async (
  { accountId, tetheredUserGuid }: { accountId: String; tetheredUserGuid?: String },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'accountId', value: accountId, required: true },
      { key: 'tetheredUserGuid', value: tetheredUserGuid, required: false }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(
      endpoints.ACCOUNT_UPCOMING_SPENDING,
      getAccountUpcomingSpending,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getEmployeeSpend = async (
  { employeeSpendCriteria }: { employeeSpendCriteria: EmployeeSpendCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'fromMonthYear', value: employeeSpendCriteria.fromMonthYear, required: true },
      { key: 'toMonthYear', value: employeeSpendCriteria.toMonthYear, required: true }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    const employeeSpendDtoList = await get(
      endpoints.EMPLOYEE_SPEND,
      getEmployeeSpend,
      finalMap,
      context
    );

    return {
      employeeSpendDtoList
    };
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getAccountSpending = async (
  { accountSpendingCriteria }: { accountSpendingCriteria: AccountSpendingCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'fromMonthYear', value: accountSpendingCriteria.fromMonthYear, required: true },
      { key: 'toMonthYear', value: accountSpendingCriteria.toMonthYear, required: true },
      { key: 'pibaAccountId', value: accountSpendingCriteria.pibaAccountId, required: true },
      { key: 'tetheredUserGuid', value: accountSpendingCriteria.tetheredUserGuid, required: false }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(endpoints.ACCOUNT_SPENDING, getAccountSpending, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getPaymentInfo = async (
  { paymentInfoCriteria }: { paymentInfoCriteria: PaymentInfoCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'accountId', value: paymentInfoCriteria.accountId, required: true },
      { key: 'page', value: paymentInfoCriteria.page, required: true },
      { key: 'size', value: paymentInfoCriteria.size, required: true },
      { key: 'nonInvoiceOnly', value: paymentInfoCriteria.nonInvoiceOnly, required: true },
      { key: 'tetheredUserGuid', value: paymentInfoCriteria.tetheredUserGuid, required: false }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(endpoints.ACCOUNT_PAYMENT_INFO, getPaymentInfo, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
