import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import { CompanyBookingAllowances } from '@whitbread-eos/api';

import { BookingOptionsForm } from '~components/innBusiness/forms/BookingAllowancesForms/BookingOptionsForm';
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
    getDefaultSwitchState: serverUtils.getDefaultSwitchState,
  };
});

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('BookingOptionsForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.bookingAllowances = bookingAllowancesMocks;
  });

  it('should render BookingOptionsForm component', async () => {
    const { getByTestId } = render(<BookingOptionsForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`BookingOptionsForm-container`)).toBeInTheDocument();
    });
  });
  it('should render BookingOptionsForm component and change switch', async () => {
    const { getByTestId } = render(<BookingOptionsForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`BookingOptionsForm-container`)).toBeInTheDocument();
    });

    const switchButton = getByTestId(`BookingOptionsForm-${CompanyBookingAllowances.PIB}-switcher`);

    await waitFor(async () => {
      fireEvent.click(switchButton);
    });
  });
  it('should call onDirtyChange on edit and revert', async () => {
    const { getByTestId } = render(<BookingOptionsForm {...mockProps} />);
    const switchButton = getByTestId(`BookingOptionsForm-${CompanyBookingAllowances.PIB}-switcher`);

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
  it('should render BookingOptionsForm component without bookingAllowances', async () => {
    mockProps.bookingAllowances = [] as any;
    const { getByTestId } = render(<BookingOptionsForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`BookingOptionsForm-container`)).toBeInTheDocument();
    });
  });
});
