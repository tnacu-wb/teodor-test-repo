import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import * as serverUtils from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';

import { EmployeeRequestsNotificationWrapper } from './employee-requests-notification-wrapper';

const mockGetDetailsFromToken = jest.fn();

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  getCountryLanguageByLocale: jest.fn(),
  getTranslations: jest.fn(),
  getEmployeesWithFilteringOptions: jest.fn().mockResolvedValue({ employees: [] }),
  getUserDetails: jest.fn().mockResolvedValue({ companyId: 'mockCompanyId' }),
  useTranslation: () => ({
    t: (str: string) => str,
  }),
  getPathForLocale: jest.fn(() => '/mocked-path'),
  getDetailsFromToken: () => mockGetDetailsFromToken(),
}));
jest.mock('next/headers', () => ({
  cookies: jest.fn(),
}));

describe('EmployeeRequestsNotificationWrapper', () => {
  const mockCookies = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    (cookies as jest.Mock).mockReturnValue({
      get: mockCookies,
    });
  });

  it('should render nothing if notification is not visible', async () => {
    (serverUtils.getCountryLanguageByLocale as jest.Mock).mockReturnValue({ language: 'en' });
    (serverUtils.getTranslations as jest.Mock).mockResolvedValue({
      t: jest.fn().mockReturnValue('false'),
    });

    const { container } = render(await EmployeeRequestsNotificationWrapper({ locale: LOCALES.EN }));

    expect(container.firstChild).toBeNull();
  });

  it('should render nothing if there are no employees', async () => {
    (serverUtils.getCountryLanguageByLocale as jest.Mock).mockReturnValue({ language: 'en' });
    (serverUtils.getTranslations as jest.Mock).mockResolvedValue({
      t: jest.fn().mockReturnValue('true'),
    });
    mockCookies.mockReturnValue({ value: 'mockToken' });

    const { container } = render(await EmployeeRequestsNotificationWrapper({ locale: LOCALES.EN }));

    expect(container.firstChild).toBeNull();
  });

  it('should render Notifications with employees', async () => {
    (serverUtils.getCountryLanguageByLocale as jest.Mock).mockReturnValue({ language: 'en' });
    (serverUtils.getEmployeesWithFilteringOptions as jest.Mock).mockReturnValue({
      employees: ['empOne', 'empTwo'],
    });
    (serverUtils.getTranslations as jest.Mock).mockResolvedValue({
      t: jest.fn().mockReturnValue('true'),
    });
    mockCookies.mockReturnValue({ value: 'mockToken' });

    const { container } = render(await EmployeeRequestsNotificationWrapper({ locale: LOCALES.EN }));

    expect(container).toBeInTheDocument();
  });
});
