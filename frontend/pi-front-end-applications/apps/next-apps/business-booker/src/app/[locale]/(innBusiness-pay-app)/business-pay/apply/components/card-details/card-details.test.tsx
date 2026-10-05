import '@testing-library/jest-dom';
import { act, fireEvent, render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';
import { useRouter } from 'next/navigation';

import { PayApplicationStep } from '../types';
import { CardDetails } from './card-details';

const mockUpdateResponse = {
  status: 'success',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getPathForLocale: () => {
      return 'en-gb/business-pay/apply?applicationId=75312&applicationGuid=ab271ba6-dfe4-4836-a16b-5c2ae2b7f212';
    },
    getCommonIcons: () => ({}),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    TranslationProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
    addPayAppCard: () => mockUpdateResponse,
    updateResumeUrl: () => true,
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useSearchParams: jest.fn(() => ({
    get: jest.fn(() => null),
  })),
  useRouter: jest.fn(() => ({
    push: jest.fn(),
  })),
}));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

const mockCardDetailsProps = {
  locale: LOCALES.EN,
  isTravelManager: true,
  isBooker: false,
  isBusinessPayManager: false,
  appInitiatorDetails: {},
  isCurrentUserInitiator: true,
};

const mockProps = {
  icons: {},
  estimatedMonthlySpend: '',
  locale: LOCALES.EN,
  header: null,
  initialState: {
    companyDetails: {
      companyName: '',
      companyBusinessType: '',
    },
    cardDetails: [],
  },
  initialStepId: PayApplicationStep.CARD_DETAILS,
  steps: [
    {
      id: PayApplicationStep.CARD_DETAILS,
      component: <CardDetails {...mockCardDetailsProps} />,
    },
    {
      id: PayApplicationStep.PAYMENT_DETAILS,
      component: <div></div>,
    },
  ],
};

describe('CardDetails component', () => {
  const mockRouter = {
    push: jest.fn(),
    replace: jest.fn(),
    refresh: jest.fn(),
    back: jest.fn(),
    forward: jest.fn(),
    prefetch: jest.fn(),
  };
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CardDetails component', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });

  it('should render CardDetails component with some cards', () => {
    mockProps.initialState.cardDetails = [
      {
        myCard: true,
        cardName: 'name',
        cardOwnerName: 'name',
        emailAddress: 'mail@mail.com',
        cardGuid: '123',
      },
    ] as any;
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });

  it('should render CardDetails component and click on save and close', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);
    const wizardPage = getByTestId('wizard-page');
    const saveAndClose = getByTestId('footer-link');

    expect(wizardPage).toBeInTheDocument();
    expect(saveAndClose).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(saveAndClose);
    });
  });

  it('should render CardDetails component and click on continue', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);
    const wizardPage = getByTestId('wizard-page');
    const continueButton = getByTestId('footer-button');

    expect(wizardPage).toBeInTheDocument();
    expect(continueButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(continueButton);
    });
  });

  it('should not render CardDetails component when employeeId exists in url', async () => {
    const mockUseSearchParams = jest.requireMock('next/navigation').useSearchParams;
    mockUseSearchParams.mockReturnValueOnce({
      get: jest.fn((param) => (param === 'employeeId' ? '456' : null)),
    });
    jest.mocked(useRouter).mockReturnValue(mockRouter);
    const { queryByTestId } = render(<Wizard {...mockProps} />);
    const wizardPage = queryByTestId('wizard-page');
    expect(wizardPage).not.toBeInTheDocument();
  });

  describe('DeletePayAppCard visibility', () => {
    const cardDetailsWithCards = [
      {
        myCard: true,
        cardName: 'Test Name',
        cardOwnerName: 'Mr Test Name',
        emailAddress: 'test@mail.com',
        cardGuid: '123-test-guid',
      },
    ];

    it('should show DeletePayAppCard when user is a Business Pay Manager', () => {
      const propsWithCards = {
        ...mockProps,
        initialState: {
          ...mockProps.initialState,
          cardDetails: cardDetailsWithCards,
        },
        steps: [
          {
            id: PayApplicationStep.CARD_DETAILS,
            component: (
              <CardDetails
                {...mockCardDetailsProps}
                isTravelManager={false}
                isBooker={false}
                isBusinessPayManager={true}
              />
            ),
          },
          {
            id: PayApplicationStep.PAYMENT_DETAILS,
            component: <div></div>,
          },
        ],
      };

      const { container } = render(<Wizard {...propsWithCards} />);
      const deleteButton = container.querySelector('[data-testid*="Delete-PayAppCard-Button"]');
      expect(deleteButton).toBeInTheDocument();
    });

    it('should NOT show DeletePayAppCard when user has no permissions', () => {
      const propsWithCards = {
        ...mockProps,
        initialState: {
          ...mockProps.initialState,
          cardDetails: cardDetailsWithCards,
        },
        steps: [
          {
            id: PayApplicationStep.CARD_DETAILS,
            component: (
              <CardDetails
                {...mockCardDetailsProps}
                isTravelManager={false}
                isBooker={false}
                isBusinessPayManager={false}
              />
            ),
          },
          {
            id: PayApplicationStep.PAYMENT_DETAILS,
            component: <div></div>,
          },
        ],
      };

      const { container } = render(<Wizard {...propsWithCards} />);
      const deleteButton = container.querySelector('[data-testid*="Delete-PayAppCard-Button"]');
      expect(deleteButton).not.toBeInTheDocument();
    });
  });
});
