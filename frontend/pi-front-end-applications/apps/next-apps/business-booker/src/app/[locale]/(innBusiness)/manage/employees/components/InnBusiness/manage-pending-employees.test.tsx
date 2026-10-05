import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { ManagePendingEmployees } from './manage-pending-employees';

const mockGetEmployees = jest.fn().mockReturnValue({});

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  getTranslations: () => ({
    t: (str: string) => str,
  }),
  getCountryLanguageByLocale: () => ({
    language: 'en',
  }),
  getCommonIcons: jest.fn(),
  ID_TOKEN_COOKIE: 'mocked_id_token_cookie',
  getEmployeesWithFilteringOptions: () => mockGetEmployees(),
}));

const mockCookies = {
  get: jest.fn().mockReturnValue({
    value: 'mocked_token',
  }),
};

jest.mock('next/headers', () => ({
  cookies: () => mockCookies,
}));

jest.mock('./manage-pending-employees-client', () => ({
  __esModule: true,
  default: () => <div data-testid="ManagePendingEmployeesClient">Mocked</div>,
}));

describe('ManagePendingEmployees', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetEmployees.mockReturnValue({});
  });

  it('should render', async () => {
    const { getByTestId } = render(
      await ManagePendingEmployees({ companyId: 'test-company-id', locale: LOCALES.EN })
    );

    expect(getByTestId('ManagePendingEmployeesClient')).toBeInTheDocument();
  });

  it('should render pending requests', async () => {
    mockGetEmployees.mockResolvedValue({
      employees: [
        { employeeId: '1', name: 'John Doe' },
        { employeeId: '2', name: 'Jane Doe' },
      ],
    });

    render(await ManagePendingEmployees({ companyId: 'test-company-id', locale: LOCALES.EN }));

    expect(mockGetEmployees).toHaveBeenCalled();
  });
});
