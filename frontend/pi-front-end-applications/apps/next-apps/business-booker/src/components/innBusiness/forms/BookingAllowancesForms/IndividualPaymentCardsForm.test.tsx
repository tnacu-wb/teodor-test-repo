import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';

import { IndividualPaymentCardsForm } from '~components/innBusiness/forms/BookingAllowancesForms/IndividualPaymentCardsForm';
import { bookingAllowancesMocks } from '~components/innBusiness/forms/BookingAllowancesForms/helpers/BookingAllowancesMocks';

const mockProps = {
  onSubmit: jest.fn(),
  onDirtyChange: jest.fn(),
  formRef: { current: null },
  bookingAllowances: bookingAllowancesMocks,
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

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('IndividualPaymentCardsForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.bookingAllowances = bookingAllowancesMocks;
  });

  it('should render IndividualPaymentCardsForm component', async () => {
    const { getByTestId } = render(<IndividualPaymentCardsForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`IndividualPaymentCardsForm-container`)).toBeInTheDocument();
    });
  });
  it('should render IndividualPaymentCardsForm component and change switch', async () => {
    const { getByTestId } = render(<IndividualPaymentCardsForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`IndividualPaymentCardsForm-container`)).toBeInTheDocument();
    });

    const switchButton = getByTestId(
      `IndividualPaymentCardsForm-${mockProps.bookingAllowances.allowIndividualCards}-switcher`
    );

    await waitFor(async () => {
      fireEvent.click(switchButton);
    });
  });
  it('should call onDirtyChange on edit and revert', async () => {
    const { getByTestId } = render(<IndividualPaymentCardsForm {...mockProps} />);
    const switchButton = getByTestId(
      `IndividualPaymentCardsForm-${mockProps.bookingAllowances.allowIndividualCards}-switcher`
    );

    await waitFor(() => {
      expect(mockProps.onDirtyChange).toHaveBeenLastCalledWith(false);
    });

    fireEvent.click(switchButton);

    await waitFor(() => {
      expect(mockProps.onDirtyChange).toHaveBeenLastCalledWith(true);
    });

    fireEvent.click(switchButton);

    await waitFor(() => {
      expect(mockProps.onDirtyChange).toHaveBeenLastCalledWith(false);
    });
  });
  it('should render IndividualPaymentCardsForm component with advance set to false', async () => {
    mockProps.bookingAllowances = [] as any;
    const { getByTestId } = render(<IndividualPaymentCardsForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`IndividualPaymentCardsForm-container`)).toBeInTheDocument();
    });
  });
});
