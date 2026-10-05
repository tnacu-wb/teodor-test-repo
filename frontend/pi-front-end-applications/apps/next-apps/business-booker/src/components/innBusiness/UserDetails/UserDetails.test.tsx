import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';

import { UserDetails } from '~components/innBusiness/UserDetails/UserDetails';

const mockProps = {
  userInformation: {
    title: 'Mr',
    firstName: 'John',
    lastName: 'Doe',
    emailAddress: 'john.doe@mailnator.com',
    phoneNumber: '3333333333',
    mobileNumber: '',
    position: '',
  },
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('CompanyMainContact Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render UserDetails component', async () => {
    const { getByTestId } = render(<UserDetails {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId('UserDetails-container')).toBeInTheDocument();
    });
  });
});
