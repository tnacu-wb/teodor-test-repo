import '@testing-library/jest-dom';
import { act, render, fireEvent, waitFor } from '@testing-library/react';

import { ActivateCardButton } from '~components/innBusiness/CardStatus/activate-card-button';

const mockProps: any = {
  cardDetails: {
    myCard: true,
    cardId: '35845',
    cardHolderName: 'test',
    cardRegistration:
      'CardCancel,CardChangeOwnerShipOf,CardEditUpdate,CardEditUpdateCardLimit,CardEditUpdateDisplayName,CardEditUpdateRestrictCardUsage,CardReplaceNewNumber',
    cardNumber: '308950*********0841',
    cardStatus: 'CURRENT',
    cardRegistrationCount: 1,
    isActivated: false,
  },
  accountHolder: {
    tetheredGuid: 'abc',
    scheme: 'GB',
  },
  icons: {
    icon: '/',
  },
  onActivation: jest.fn(),
  afterActivation: jest.fn(),
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
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    getPathForLocale: () => {
      return '/';
    },
    activatePibaCard: () => {
      return {
        status: 'success',
      };
    },
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    refresh: jest.fn(),
  })),
}));

describe('ActivateCardButton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render ActivateCardButton component', async () => {
    const { getByTestId } = render(<ActivateCardButton {...mockProps} />);

    const button = getByTestId('Activate-Card-Popup-Button');
    expect(button).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(button);
    });

    const activateCardButton = getByTestId('Activate-Card-Confirm');
    const checkbox = getByTestId('Card-Received-Checkbox');

    await waitFor(() => {
      expect(getByTestId('Activate-Card-Modal')).toBeInTheDocument();
      expect(activateCardButton).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(checkbox);
    });

    await waitFor(() => {
      expect(activateCardButton).not.toHaveAttribute('disabled');
    });

    await act(async () => {
      fireEvent.click(activateCardButton);
    });
  });

  it('should render ActivateCardButton component without activation props', async () => {
    mockProps.afterActivation = undefined;
    mockProps.onActivation = undefined;

    const { getByTestId } = render(<ActivateCardButton {...mockProps} />);

    const button = getByTestId('Activate-Card-Popup-Button');
    expect(button).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(button);
    });

    const activateCardButton = getByTestId('Activate-Card-Confirm');
    const checkbox = getByTestId('Card-Received-Checkbox');

    await waitFor(() => {
      expect(getByTestId('Activate-Card-Modal')).toBeInTheDocument();
      expect(activateCardButton).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(checkbox);
    });

    await waitFor(() => {
      expect(activateCardButton).not.toHaveAttribute('disabled');
    });

    await act(async () => {
      fireEvent.click(activateCardButton);
    });
  });
});
