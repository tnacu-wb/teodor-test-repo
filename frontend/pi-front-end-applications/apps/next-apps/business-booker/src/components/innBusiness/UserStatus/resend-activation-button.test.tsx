import '@testing-library/jest-dom';
import { render, fireEvent, waitFor } from '@testing-library/react';
import { act } from 'react-dom/test-utils';

import ResendActivationButton from '~components/innBusiness/UserStatus/resend-activation-button';

const mockProps = {
  buttonLabel: 'Resend',
  userDetails: {
    title: 'Mr',
    firstName: 'John',
    lastName: 'Doe',
    email: 'john@mailnator.com',
    employeeId: '1',
  },
  companyId: 'abc',
  language: 'en',
  token: 'test',
};

const mockEmployeeDetails = {
  id: '123',
  ghNumber: null,
  emailAddress: 'john@mailnator.com',
  position: null,
  phoneNumber: '+11111111111',
  mobileNumber: '',
  textConfirmation: false,
  title: 'Mr',
  firstName: 'John',
  lastName: 'Doe',
  centralCardId: '1',
};

const mockActivationEmail = '';

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getEmployeeDetails: () => {
      return mockEmployeeDetails;
    },
    sendActivationEmail: () => {
      return mockActivationEmail;
    },
  };
});

describe('ResendActivationButton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render ResendActivationButton component', async () => {
    const { getByTestId } = render(<ResendActivationButton {...mockProps} />);

    const userStatusButton = getByTestId('Resend-Activation-Row-Button');
    expect(userStatusButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(userStatusButton);
    });

    await waitFor(() => {
      expect(getByTestId('Resend-Activation-Full-Name')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('Resend-Activation-Cancel-Button'));
      fireEvent.click(userStatusButton);
    });

    await waitFor(() => {
      expect(getByTestId('Resend-Activation-Full-Name')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('Resend-Activation-Submit-Button'));
    });
  });
});
