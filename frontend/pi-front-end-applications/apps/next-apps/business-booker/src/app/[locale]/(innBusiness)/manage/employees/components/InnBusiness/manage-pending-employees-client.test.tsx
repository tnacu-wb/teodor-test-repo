import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import ManagePendingEmployeesClient from './manage-pending-employees-client';

const mockProps = {
  employees: [
    {
      accessLevel: undefined,
      emailAddress: 'john.doe@example.com',
      employeeId: '1',
      employeeStatus: undefined,
      firstName: 'John',
      id: '1',
      lastName: 'Doe',
      title: 'Developer',
    },
    {
      accessLevel: undefined,
      emailAddress: 'jane.doe@example.com',
      employeeId: '2',
      employeeStatus: undefined,
      firstName: 'Jane',
      id: '2',
      lastName: 'Doe',
      title: 'Designer',
    },
  ],
  locale: LOCALES.EN,
  token: 'mocked_token',
  icons: {},
  baseDataTestId: 'ManagePendingEmployees',
};

const mockGetEmployees = jest.fn().mockReturnValue({});

jest.mock('next/navigation', () => ({
  useRouter: jest.fn().mockReturnValue({
    push: jest.fn(),
    replace: jest.fn(),
    pathname: '/mocked-path',
    query: {},
    asPath: '/mocked-path',
    back: jest.fn(),
    refresh: jest.fn(),
  }),
}));

jest.mock('react', () => ({
  ...jest.requireActual('react'),
  useOptimistic: (data: any) => [data, jest.fn()],
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  useTranslation: () => ({
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

jest.mock('./manage-pending-employee-item', () => ({
  ManagePendingEmployeeItem: jest.fn(({ dataTestId }) => (
    <div data-testId={dataTestId}>Mocked ManagePendingEmployeeItem</div>
  )),
}));

describe('ManagePendingEmployees', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetEmployees.mockReturnValue({});
  });

  it('should render component', async () => {
    const { getByTestId } = render(<ManagePendingEmployeesClient {...mockProps} />);

    expect(getByTestId('ManagePendingEmployees-container')).toBeInTheDocument();
  });

  it('should render pending requests', async () => {
    const { getByTestId, queryByTestId } = render(<ManagePendingEmployeesClient {...mockProps} />);

    expect(getByTestId('ManagePendingEmployees-title')).toHaveTextContent(
      'userMgmt.manageEmployees.innBusiness.pendingRequestsTitle (2)'
    );

    expect(getByTestId('ManagePendingEmployees-list')).toBeInTheDocument();
    expect(getByTestId('ManagePendingEmployees-item-0')).toBeInTheDocument();
    expect(getByTestId('ManagePendingEmployees-item-1')).toBeInTheDocument();

    expect(queryByTestId('ManagePendingEmployees-noPendingRequests')).not.toBeInTheDocument();
  });

  it('should not render the title when there are no employees', async () => {
    mockProps.employees = [];
    const { queryByTestId } = render(<ManagePendingEmployeesClient {...mockProps} />);

    expect(queryByTestId('ManagePendingEmployees-title')).not.toBeInTheDocument();
  });
});
