import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { AccessLevel, EmployeeStatus } from '@whitbread-eos/api';

import { UserInitials, UserInitialsProps } from './user-initials';

const mockProps: UserInitialsProps = {
  type: {
    accessLevel: AccessLevel.Super,
    emailAddress: 'john.doe@mailinator.com',
    employeeStatus: EmployeeStatus.Active,
    firstName: 'John',
    id: 'asdzxc',
    lastName: 'Doe',
    title: 'MR',
  },
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getInitials: serverUtils.getInitials,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('UserInitials Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render UserInitials component', async () => {
    const { getByTestId } = render(await UserInitials(mockProps));

    expect(getByTestId('UserInitials')).toBeInTheDocument();
  });
});
