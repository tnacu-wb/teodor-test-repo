import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { EmployeeStatus } from '@whitbread-eos/api';

import { UserStatus } from './user-status';

const mockProps = {
  type: EmployeeStatus.Active,
  userDetails: {
    title: 'MR',
    firstName: 'John',
    lastName: 'Doe',
    email: 'john@mailnator.com',
    employeeId: '1',
  },
  companyId: 'abc',
  language: 'en',
  token: 'test',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getStyling: jest.fn(),
    getLabelType: jest.fn(),
  };
});

describe('UserStatus Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render UserStatus component', async () => {
    const { getByTestId } = render(await UserStatus(mockProps));

    expect(getByTestId('UserStatus')).toBeInTheDocument();
  });

  it('should render UserStatus component with type null', async () => {
    (mockProps.type as any) = null;
    const { getByTestId } = render(await UserStatus(mockProps));

    expect(getByTestId('UserStatus')).toBeInTheDocument();
  });
});
