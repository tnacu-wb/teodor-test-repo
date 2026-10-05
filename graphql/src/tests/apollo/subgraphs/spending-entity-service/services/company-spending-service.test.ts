import { endpoints } from '../../../../../apollo/subgraphs/spending-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import {
  getAccountSpending,
  getAccountUpcomingSpending,
  getEmployeeSpend,
  getPaymentInfo,
  retrieveCompanySpending
} from '../../../../../apollo/subgraphs/spending-entity-service/services/company-spending-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveCompanySpending', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const searchCompanySpending = {
    fromMonthYear: '01-2020',
    toMonthYear: '12-2025'
  };

  it('should call the get method with the correct parameters when searchCompanySpending is provided', async () => {
    await retrieveCompanySpending({ searchCompanySpending }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.GET_COMPANY_SPENDING,
      retrieveCompanySpending,
      {
        fromMonthYear: '01-2020',
        toMonthYear: '12-2025'
      },
      context
    );
  });

  it('should handle errors gracefully when get method fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(retrieveCompanySpending({ searchCompanySpending }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getAccountUpcomingSpending', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const accountId = 'COMP_1234567';

  it('should call the get method getAccountUpcomingSpending with the correct parameters when accountOd is provided', async () => {
    await getAccountUpcomingSpending({ accountId: accountId }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.ACCOUNT_UPCOMING_SPENDING,
      getAccountUpcomingSpending,
      {
        accountId: 'COMP_1234567'
      },
      context
    );
  });

  it('should handle errors gracefully when getAccountUpcomingSpending method fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getAccountUpcomingSpending({ accountId: accountId }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getAccountSpending', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const accountSpendingCriteria = {
    fromMonthYear: '01-2020',
    toMonthYear: '12-2025',
    pibaAccountId: '1234567'
  };

  it('should call the get method with the correct parameters when accountSpendingCriteria is provided', async () => {
    await getAccountSpending({ accountSpendingCriteria }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.ACCOUNT_SPENDING,
      getAccountSpending,
      {
        fromMonthYear: '01-2020',
        toMonthYear: '12-2025',
        pibaAccountId: '1234567'
      },
      context
    );
  });

  it('should handle errors gracefully when getAccountSpending method fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getAccountSpending({ accountSpendingCriteria }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getEmployeeSpend', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const employeeSpendCriteria = {
    fromMonthYear: '01-2024',
    toMonthYear: '03-2026'
  };

  it('should call the get method with the correct parameters when employeeSpendCriteria is provided', async () => {
    await getEmployeeSpend({ employeeSpendCriteria }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.EMPLOYEE_SPEND,
      getEmployeeSpend,
      {
        fromMonthYear: '01-2024',
        toMonthYear: '03-2026'
      },
      context
    );
  });

  it('should wrap the downstream employee spend list in the GraphQL response shape', async () => {
    const employeeSpend = [
      {
        companyAccountId: 'COMP_5f8efec6-ca8f-412a-a823-71993f46f979',
        employeeAccountId: 'EMPL_6bf6eccd-49af-4fe2-91d3-1ab109e8c200',
        year: 2024,
        month: 1,
        noOfBookings: 1,
        bookingValue: 59.0,
        bookingCurrency: 'EUR'
      }
    ];
    (get as jest.Mock).mockResolvedValueOnce(employeeSpend);

    await expect(getEmployeeSpend({ employeeSpendCriteria }, context)).resolves.toEqual({
      employeeSpendDtoList: employeeSpend
    });
  });

  it('should handle errors gracefully when getEmployeeSpend method fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getEmployeeSpend({ employeeSpendCriteria }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getPaymentInfo', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const paymentInfoCriteria = {
    accountId: '123123',
    page: 1,
    size: 1,
    nonInvoiceOnly: true
  };

  it('should call get payment info with the correct query params', async () => {
    await getPaymentInfo({ paymentInfoCriteria }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.ACCOUNT_PAYMENT_INFO,
      getPaymentInfo,
      {
        accountId: '123123',
        page: 1,
        size: 1,
        nonInvoiceOnly: true
      },
      context
    );
  });

  it('should handle errors gracefully when getPaymentInfo method fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getPaymentInfo({ paymentInfoCriteria }, context)).rejects.toThrow('Test error');
  });
});
