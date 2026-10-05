import '@testing-library/jest-dom';
import { act, render, fireEvent } from '@testing-library/react';
import { CARD_STATUS_WL_TYPE } from '@whitbread-eos/api';

import { CardStatus, CardStatusProps } from './card-status';

const mockProps: CardStatusProps = {
  status: CARD_STATUS_WL_TYPE.CURRENT,
  isActivated: true,
  cardDetails: {
    cardId: '123',
    cardNo: {
      value: '123',
    },
    myCard: false,
  },
  icons: {
    icon: '/',
  },
  accountHolder: {
    tetheredGuid: '123',
  },
  token: '123',
  onActivation: jest.fn(),
  afterActivation: jest.fn(),
};

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: utils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getPathForLocale: () => {
      return '/';
    },
    renderSanitizedHtml: (html: string) => html,
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    getTranslations: () => {
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
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    refresh: jest.fn(),
  })),
}));

describe('CardStatus Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CardStatus component with status = CURRENT and isActivated = true', async () => {
    const { getByTestId } = render(await CardStatus(mockProps));

    expect(getByTestId('CardStatus')).toBeInTheDocument();
  });

  it('should render CardStatus component with status = CURRENT and isActivated = false', async () => {
    mockProps.isActivated = false;
    mockProps.cardDetails = { myCard: true };
    const { getByTestId } = render(await CardStatus(mockProps));

    const activateButton = getByTestId('Activate-Card-Popup-Button');
    expect(activateButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(activateButton);
    });

    const checkbox = getByTestId('Card-Received-Checkbox');
    expect(checkbox).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(checkbox);
      fireEvent.click(getByTestId('Activate-Card-Confirm'));
      fireEvent.click(getByTestId('Activate-Card-Cancel'));
    });
  });

  it('should render CardStatus component with status = PENDING', async () => {
    mockProps.status = CARD_STATUS_WL_TYPE.PENDING;
    const { getByTestId } = render(await CardStatus(mockProps));

    expect(getByTestId('CardStatus')).toBeInTheDocument();
  });

  it('should render CardStatus component with status = HOT as active', async () => {
    mockProps.status = CARD_STATUS_WL_TYPE.HOT;
    mockProps.isActivated = false;
    mockProps.cardDetails = { myCard: true };
    const { getByTestId, queryByTestId } = render(await CardStatus(mockProps));

    expect(getByTestId('CardStatus')).toBeInTheDocument();
    expect(queryByTestId('Activate-Card-Popup-Button')).not.toBeInTheDocument();
  });

  it('should render CardStatus component with status = CANCELLED', async () => {
    mockProps.afterActivation = undefined;
    mockProps.onActivation = undefined;

    mockProps.status = CARD_STATUS_WL_TYPE.CANCELLED;
    const { getByTestId } = render(await CardStatus(mockProps));

    expect(getByTestId('CardStatus')).toBeInTheDocument();
  });
});
