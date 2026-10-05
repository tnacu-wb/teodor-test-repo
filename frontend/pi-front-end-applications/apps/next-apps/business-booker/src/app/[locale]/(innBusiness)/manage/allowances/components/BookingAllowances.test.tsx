import '@testing-library/jest-dom';
import { fireEvent, render, waitFor, act } from '@testing-library/react';
import { CompanyBookingAllowances } from '@whitbread-eos/api';

import { BookingAllowances } from './BookingAllowances';

const mockProps = {
  icons: {},
  companyId: 'COMP_adasdad13131',
  bookingAllowances: {
    maxDinnerBudgets: {
      uKWide: {
        amount: 15,
        currency: 'GBP',
      },
      greaterLondon: {
        amount: 10,
        currency: 'GBP',
      },
      ireland: {
        amount: 20,
        currency: 'GBP',
      },
    },
    extrasCodes: ['1', '2', '3', '4', '5'],
    upsellItemsAllowed: ['11', '15', '12', '17', '5', '18', '135', '136', '137'],
    allowAlcohol: true,
    allowCarParking: true,
    allowAdditionalCosts: true,
    allowPremierSaverRates: true,
    allowIndividualCards: true,
    maxNumberOfNights: 14,
  },
};

let mockUpdateResponse = {
  status: 'success',
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
    getDefaultSwitchState: serverUtils.getDefaultSwitchState,
    mapSwitchState: serverUtils.mapSwitchState,
    updateBookingAllowances: () => mockUpdateResponse,
    findError: serverUtils.findError,
  };
});

window.scrollTo = jest.fn();

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    refresh: jest.fn(),
  })),
}));

jest.mock('~components/innBusiness/ReviewChanges', () => ({
  ReviewChanges: () => <div data-testid="ReviewChanges" />,
}));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('BookingAllowances Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUpdateResponse = { status: 'success' };
    mockProps.companyId = 'COMP_adasdad13131';
  });

  it('should render BookingAllowances component', async () => {
    const { getByTestId, queryByTestId } = render(<BookingAllowances {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId(`BookingAllowances-container`)).toBeInTheDocument();
    });

    expect(queryByTestId('ReviewChanges')).not.toBeInTheDocument();
  });

  it('should show ReviewChanges only when allowances are dirty and hide when reverted', async () => {
    const { getByTestId, queryByTestId } = render(<BookingAllowances {...(mockProps as any)} />);

    const switchButton = getByTestId(`BookingOptionsForm-${CompanyBookingAllowances.PIB}-switcher`);
    expect(queryByTestId('ReviewChanges')).not.toBeInTheDocument();

    await act(async () => {
      fireEvent.click(switchButton);
    });

    await waitFor(() => {
      expect(queryByTestId('ReviewChanges')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(switchButton);
    });

    await waitFor(() => {
      expect(queryByTestId('ReviewChanges')).not.toBeInTheDocument();
    });
  });
  it('should render BookingAllowances component and click on save changes', async () => {
    const { getByTestId, queryByTestId } = render(<BookingAllowances {...(mockProps as any)} />);

    const saveChangesButton = getByTestId(`BookingAllowances-save-changes-button`);

    const switchButton = getByTestId(`BookingOptionsForm-${CompanyBookingAllowances.PIB}-switcher`);

    await act(async () => {
      fireEvent.click(switchButton);
    });

    await act(async () => {
      expect(saveChangesButton).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(queryByTestId('ReviewChanges')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(saveChangesButton);
    });

    await waitFor(() => {
      expect(queryByTestId('ReviewChanges')).not.toBeInTheDocument();
    });
  });

  it('should call updateBookingAllowances with no companyId and status fail', async () => {
    mockUpdateResponse = { status: 'fail' };
    mockProps.companyId = null as any;

    const { getByTestId } = render(<BookingAllowances {...(mockProps as any)} />);

    const saveChangesButton = getByTestId(`BookingAllowances-save-changes-button`);

    await waitFor(async () => {
      expect(saveChangesButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(saveChangesButton);
    });

    await waitFor(() => {
      expect(window.scrollTo).toBeCalledWith({ top: 0, behavior: 'smooth' });
    });
  });
});
