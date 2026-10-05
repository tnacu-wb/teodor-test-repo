import { get, post, put } from '../../../../../apollo/client/rest-client';

import {
  addEmployee,
  getActivationDetails,
  getEmployeeDetails,
  getEmployees,
  sendActivationEmail,
  updateEmployee,
  getEmployeesWithFilteringOptions
} from '../../../../../apollo/subgraphs/company-employee-service-opera/services/employee-service';
import { endpoints } from '../../../../../apollo/subgraphs/company-employee-service-opera/services/base-service';
jest.mock('../../../../../apollo/client/rest-client');

describe('getEmployees', () => {
  const headers = { 'Content-Type': 'application/json' };
  const companyId = 'company-123';
  const size = 5;
  const context = {};

  it('should call get function with correct parameters when getEmployees is called', async () => {
    const getEmployeesEndPoint = {
      endpoint: '/companies/company-123/employees',
      flowCode: 'DIGITAL_CMP_003',
      axiosClient: expect.any(Function)
    };

    await getEmployees({ companyId, size }, context);

    expect(get).toHaveBeenCalledWith(
      getEmployeesEndPoint,
      getEmployees,
      expect.objectContaining({
        size: 5
      }),
      context
    );
  });

  it('should handle errors gracefully when getEmployees throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getEmployees({ companyId, size }, headers)).rejects.toThrow('Test error');
  });
});

describe('getEmployeeDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const companyId = 'company-123';
  const employeeId = 'employee-123';
  const size = 5;
  const context = {};

  it('should call get function with correct parameters when getEmployeeDetails is called', async () => {
    const serviceEndpoint = {
      ...endpoints.GET_EMPLOYEE_DETAILS,
      endpoint: '/companies/company-123/employees/employee-123'
    };

    await getEmployeeDetails({ companyId, employeeId }, context);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getEmployeeDetails, {}, context);
  });

  it('should handle errors gracefully when getEmployeeDetails throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getEmployees({ companyId, size }, headers)).rejects.toThrow('Test error');
  });
});

describe('sendActivationEmail', () => {
  const companyId = 'company-123';
  const sendActivationEmailCriteria = {
    emailAddress: 'emailAddress@mail.com'
  };
  const context = {};

  it('should call get function with correct parameters when sendActivationEmail is called', async () => {
    const serviceEndpoint = {
      ...endpoints.SEND_ACTIVATION_EMAIL,
      endpoint: '/companies/admin/company-123/employees/invite'
    };

    await sendActivationEmail({ companyId, sendActivationEmailCriteria }, context);

    expect(post).toHaveBeenCalledWith(
      serviceEndpoint,
      sendActivationEmail,
      sendActivationEmailCriteria,
      context
    );
  });

  it('should handle errors gracefully when sendActivationEmail throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      sendActivationEmail({ companyId, sendActivationEmailCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('addEmployee', () => {
  const companyId = 'company-123';
  const employee = {
    emailAddress: 'emailAddress@mail.com',
    firstName: 'John',
    lastName: 'Doe'
  };
  const context = {};

  it('should call get function with correct parameters when addEmployee is called', async () => {
    const serviceEndpoint = {
      ...endpoints.ADD_EMPLOYEE,
      endpoint: '/companies/admin/company-123/employees'
    };

    await addEmployee({ companyId, employee }, context);

    expect(post).toHaveBeenCalledWith(serviceEndpoint, addEmployee, employee, context, true);
  });

  it('should handle errors gracefully when addEmployee throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(addEmployee({ companyId, employee }, context)).rejects.toThrow('Test error');
  });
});

describe('updateEmployee', () => {
  const companyId = 'company-123';
  const employeeId = 'employee-123';
  const updateEmployeeCriteria = {
    emailAddress: 'emailAddress@mail.com',
    firstName: 'John',
    lastName: 'Doe'
  };
  const context = {};

  (put as jest.Mock).mockResolvedValue({
    data: {}
  });

  it('should call get function with correct parameters when updateEmployee is called', async () => {
    const serviceEndpoint = {
      ...endpoints.UPDATE_EMPLOYEE,
      endpoint: '/companies/company-123/employees/employee-123'
    };

    await updateEmployee({ companyId, employeeId, updateEmployeeCriteria }, context);

    expect(put).toHaveBeenCalledWith(
      serviceEndpoint,
      updateEmployee,
      updateEmployeeCriteria,
      context
    );
  });

  it('should call put with activationKey when provided', async () => {
    const activationKey = 'activation-123';
    const resolvedEndpoint = {
      endpoint: '/companies/company-123/employees/employee-123?activation-key=activation-123',
      flowCode: 'DIGITAL_CMP_007',
      axiosClient: expect.any(Function)
    };

    await updateEmployee({ companyId, employeeId, activationKey, updateEmployeeCriteria }, context);

    expect(put).toHaveBeenCalledWith(
      resolvedEndpoint,
      updateEmployee,
      updateEmployeeCriteria,
      context
    );
  });

  it('should call put without activationKey when not provided', async () => {
    const serviceEndpoint = {
      ...endpoints.UPDATE_EMPLOYEE,
      endpoint: '/companies/company-123/employees/employee-123'
    };

    await updateEmployee({ companyId, employeeId, updateEmployeeCriteria }, context);

    expect(put).toHaveBeenCalledWith(
      serviceEndpoint,
      updateEmployee,
      updateEmployeeCriteria,
      context
    );
  });

  it('should handle errors gracefully when updateEmployee throws an error', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      updateEmployee({ companyId, employeeId, updateEmployeeCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('getActivationDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const activationKey = 'activation-123';
  const context = {};

  it('should call get function with correct parameters when getActivationDetails is called', async () => {
    const serviceEndpoint = {
      ...endpoints.GET_ACTIVATION_DETAILS,
      endpoint: '/companies/employees/activation-details'
    };

    await getActivationDetails({ activationKey }, context);

    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      getActivationDetails,
      expect.objectContaining({
        'activation-key': 'activation-123'
      }),
      context
    );
  });

  it('should handle errors gracefully when getActivationDetails throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getActivationDetails({ activationKey }, context)).rejects.toThrow('Test error');
  });
});

describe('getEmployeesWithFilteringOptions', () => {
  const headers = { 'Content-Type': 'application/json' };
  const companyId = 'company-123';
  const size = 5;
  const context = {};

  it('should call get function with correct parameters when getEmployeesWithFilteringOptions is called', async () => {
    const getEmployeesEndPoint = {
      endpoint: '/companies/company-123/employees',
      flowCode: 'DIGITAL_CMP_003',
      axiosClient: expect.any(Function)
    };

    await getEmployeesWithFilteringOptions({ companyId, size }, context);

    expect(get).toHaveBeenCalledWith(
      getEmployeesEndPoint,
      getEmployeesWithFilteringOptions,
      expect.objectContaining({
        size: 5
      }),
      context
    );
  });

  it('should handle errors gracefully when getEmployeesWithFilteringOptions throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getEmployeesWithFilteringOptions({ companyId, size }, headers)).rejects.toThrow(
      'Test error'
    );
  });
});
