import '@testing-library/jest-dom';
import { act, fireEvent, render, waitFor, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { sendActivationEmail } from '@whitbread-eos/utils/server';

import { InviteEmployees } from './invite-employees';

const mockProps = {
  icons: {},
  companyId: '',
  paymentCards: [],
  language: 'en',
  handleCompanyEmail: jest.fn(),
  handleSubmitClick: jest.fn(),
  isBusinessPayManager: false,
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    findError: serverUtils.findError,
    sendActivationEmail: jest.fn(() => Promise.resolve('')),
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

describe('InviteEmployees Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render InviteEmployees component', async () => {
    const { getByTestId } = render(<InviteEmployees {...mockProps} />);
    expect(getByTestId('InviteEmployees-page')).toBeInTheDocument();
  });

  it('renders PaymentCardForm', () => {
    const { getByTestId } = render(<InviteEmployees {...mockProps} />);
    expect(getByTestId('PaymentCardForm')).toBeInTheDocument();
  });

  it('should render InviteEmployees component and trigger a submit', async () => {
    const { getByTestId } = render(<InviteEmployees {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('InviteEmployees-Send-Invite-Button')).toBeInTheDocument();
    });

    await act(async () => {
      const button = getByTestId('InviteEmployees-Send-Invite-Button');
      fireEvent.click(button);
    });
  });
});

describe('InviteEmployees - submitted form payload', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('calls sendActivationEmail with selected cardId when not business pay manager', async () => {
    render(
      <InviteEmployees
        companyId="123"
        paymentCards={[
          {
            cardId: '3332',
            cardLabel: 'Visa xxxx 3332',
            cardType: 'Visa',
            cardNumber: 'xxxx3332',
          },
        ]}
        icons={{}}
        language="en"
        isBusinessPayManager={false}
      />
    );

    // Fill in the email
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'test@example.com' } });
    // Select the payment card
    fireEvent.click(screen.getByTestId('cardId-IB-Form-Select-Button'));
    fireEvent.click(screen.getByTestId('cardId-3332-Option'));

    // Submit the form
    fireEvent.click(screen.getByTestId('InviteEmployees-Send-Invite-Button'));

    // Wait for sendActivationEmail to be called with the selected cardId
    await waitFor(() => {
      expect(sendActivationEmail).toHaveBeenCalledWith(
        expect.anything(),
        '123',
        'en',
        'test@example.com',
        '3332'
      );
      expect(sendActivationEmail).not.toHaveBeenCalledWith(
        expect.anything(),
        '123',
        'en',
        'test@example.com',
        '1' // should NOT be called with '1'
      );
    });
  });

  it('sets cardId to "1" and does not render PaymentCardForm for business pay manager', async () => {
    render(
      <InviteEmployees
        companyId="123"
        paymentCards={[]}
        icons={{}}
        language="en"
        isBusinessPayManager={true}
      />
    );

    expect(screen.queryByTestId('PaymentCardForm')).not.toBeInTheDocument();

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'test@example.com' } });
    fireEvent.click(screen.getByTestId('InviteEmployees-Send-Invite-Button'));

    await waitFor(() => {
      expect(sendActivationEmail).toHaveBeenCalledWith(
        expect.anything(),
        '123',
        'en',
        'test@example.com',
        '1'
      );
    });
  });
});
