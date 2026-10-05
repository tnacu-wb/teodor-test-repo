import { createAxios } from '../../../client/axios';

const baseUrl = process.env.COMPANY_SERVICE_OPERA ?? 'http://company-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  DELETE_CUSTOM_QUESTION: {
    endpoint: '/companies/{companyId}/admin/employee-questions/{questionId}',
    flowCode: 'DIGITAL_CMP_009',
    axiosClient: axiosClient
  },
  CREATE_COMPANY_USER_QUESTION: {
    endpoint: '/companies/{companyId}/admin/employee-questions',
    flowCode: 'DIGITAL_CMP_011',
    axiosClient: axiosClient
  },
  COMPANY_DETAILS: {
    endpoint: '/company/{companyId}',
    flowCode: 'DIGITAL_CMP_004',
    axiosClient: axiosClient
  },
  GET_COMPANY_REGISTRATION_QUESTIONS_AND_ANSWERS: {
    endpoint: '/companies/{companyId}/registration-questions',
    flowCode: 'DIGITAL_CMP_016',
    axiosClient: axiosClient
  },
  GET_EMPLOYEE_REGISTRATION_QUESTIONS_AND_ANSWERS: {
    endpoint: '/companies/{companyId}/employees/{employeeId}/registration-questions',
    flowCode: 'DIGITAL_CMP_017',
    axiosClient: axiosClient
  },
  UPDATE_BUSINESS_QUESTIONS: {
    endpoint: '/companies/{companyId}/admin/business-questions/{questionId}',
    flowCode: 'DIGITAL_CMP_008',
    axiosClient: axiosClient
  },
  UPDATE_BOOKING_ALLOWANCES: {
    endpoint: '/companies/admin/{companyId}/booking-allowances',
    flowCode: 'DIGITAL_CMP_012',
    axiosClient: axiosClient
  },
  UPDATE_COMPANY_DETAILS: {
    endpoint: '/company/admin/{companyId}',
    flowCode: 'DIGITAL_CMP_013',
    axiosClient: axiosClient
  },
  UPDATE_COMPANY_USER_QUESTION: {
    endpoint: '/companies/{companyId}/admin/employee-questions/{questionId}',
    flowCode: 'DIGITAL_CMP_014',
    axiosClient: axiosClient
  },
  UPDATE_BOOKING_ALERTS: {
    endpoint: '/companies/admin/{companyId}/booking-alerts',
    flowCode: 'DIGITAL_CMP_015',
    axiosClient: axiosClient
  }
};
