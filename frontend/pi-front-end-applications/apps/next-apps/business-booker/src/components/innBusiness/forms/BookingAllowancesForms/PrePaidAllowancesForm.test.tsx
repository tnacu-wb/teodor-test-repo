import '@testing-library/jest-dom';
import { render, waitFor, fireEvent, act } from '@testing-library/react';

import { PrePaidAllowancesForm } from '~components/innBusiness/forms/BookingAllowancesForms/PrePaidAllowancesForm';
import { bookingAllowancesMocks } from '~components/innBusiness/forms/BookingAllowancesForms/helpers/BookingAllowancesMocks';
import { userEvent } from '~utils/test-utils';

const mockProps = {
  onSubmit: jest.fn(),
  onDirtyChange: jest.fn(),
  formRef: { current: null },
  bookingAllowances: bookingAllowancesMocks,
  icons: {},
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
    findError: serverUtils.findError,
  };
});

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('PrePaidAllowancesForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.bookingAllowances = JSON.parse(JSON.stringify(bookingAllowancesMocks));
  });

  it('should render PrePaidAllowancesForm component', async () => {
    const { getByTestId } = render(<PrePaidAllowancesForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`PrePaidAllowancesForm-container`)).toBeInTheDocument();
    });
  });
  it('should render PrePaidAllowancesForm component and change switch', async () => {
    const { getByTestId } = render(<PrePaidAllowancesForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`PrePaidAllowancesForm-container`)).toBeInTheDocument();
    });

    const switchButton = getByTestId(
      `PrePaidAllowancesForm-${mockProps.bookingAllowances.allowAlcohol}-switcher`
    );

    await waitFor(async () => {
      fireEvent.click(switchButton);
    });
  });
  it('should call onDirtyChange on edit and revert', async () => {
    const { getByTestId } = render(<PrePaidAllowancesForm {...mockProps} />);
    const unitedKingdomInput = getByTestId('PrePaidAllowancesForm-unitedKingdom-Form-Input');

    await waitFor(() => {
      expect(mockProps.onDirtyChange).toHaveBeenLastCalledWith(false);
    });

    fireEvent.change(unitedKingdomInput, { target: { value: '20' } });

    await waitFor(() => {
      expect(mockProps.onDirtyChange).toHaveBeenLastCalledWith(true);
    });

    fireEvent.change(unitedKingdomInput, { target: { value: '15' } });

    await waitFor(() => {
      expect(mockProps.onDirtyChange).toHaveBeenLastCalledWith(false);
    });
  });
  it('should render PrePaidAllowancesForm component with correct default values', async () => {
    mockProps.bookingAllowances.maxDinnerBudgets.uKWide.amount = undefined as any;
    mockProps.bookingAllowances.maxDinnerBudgets.greaterLondon.amount = undefined as any;
    mockProps.bookingAllowances.maxDinnerBudgets.ireland.amount = undefined as any;
    const { getByTestId } = render(<PrePaidAllowancesForm {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('PrePaidAllowancesForm-unitedKingdom-Form-Input')).toHaveValue('');
      expect(getByTestId('PrePaidAllowancesForm-greaterLondon-Form-Input')).toHaveValue('');
      expect(getByTestId('PrePaidAllowancesForm-germanyIreland-Form-Input')).toHaveValue('');
    });
  });
  it('should render PrePaidAllowancesForm component and click on input', async () => {
    const { getByTestId } = render(<PrePaidAllowancesForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`PrePaidAllowancesForm-container`)).toBeInTheDocument();
    });
  });
  it('should render PrePaidAllowancesForm component without bookingAllowances', async () => {
    mockProps.bookingAllowances = [] as any;
    const { getByTestId } = render(<PrePaidAllowancesForm {...mockProps} />);
    const prePaidContainer = getByTestId(`PrePaidAllowancesForm-container`);
    const unitedKingdomInput = getByTestId('PrePaidAllowancesForm-unitedKingdom-Form-Input');
    const greaterLondonInput = getByTestId('PrePaidAllowancesForm-greaterLondon-Form-Input');
    const germanyIrelandInput = getByTestId('PrePaidAllowancesForm-germanyIreland-Form-Input');

    await waitFor(async () => {
      expect(prePaidContainer).toBeInTheDocument();
      expect(unitedKingdomInput).toBeInTheDocument();
      expect(greaterLondonInput).toBeInTheDocument();
      expect(germanyIrelandInput).toBeInTheDocument();
    });
    await act(async () => {
      unitedKingdomInput.focus();
      fireEvent.change(unitedKingdomInput, { target: { value: '20' } });
      await userEvent.tab();
    });
    await act(async () => {
      greaterLondonInput.focus();
      fireEvent.change(greaterLondonInput, { target: { value: '20' } });

      await userEvent.tab();
    });
    await act(async () => {
      germanyIrelandInput.focus();
      fireEvent.change(germanyIrelandInput, { target: { value: '20' } });

      await userEvent.tab();
    });
  });
});
