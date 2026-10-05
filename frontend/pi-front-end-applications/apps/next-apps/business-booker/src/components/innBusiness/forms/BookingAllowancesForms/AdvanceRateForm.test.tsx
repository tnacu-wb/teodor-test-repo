import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';

import { AdvanceRateForm } from '~components/innBusiness/forms/BookingAllowancesForms/AdvanceRateForm';
import { bookingAllowancesMocks } from '~components/innBusiness/forms/BookingAllowancesForms/helpers/BookingAllowancesMocks';

const mockProps = {
  onSubmit: jest.fn(),
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

describe('AdvanceRateForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render AdvanceRateForm component', async () => {
    const { getByTestId } = render(<AdvanceRateForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`AdvanceRateForm-container`)).toBeInTheDocument();
    });
  });
  it('should render AdvanceRateForm component and change switch', async () => {
    const { getByTestId } = render(<AdvanceRateForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`AdvanceRateForm-container`)).toBeInTheDocument();
    });

    const switchButton = getByTestId(
      `AdvanceRateForm-${mockProps.bookingAllowances.allowPremierSaverRates}-switcher`
    );

    await waitFor(async () => {
      fireEvent.click(switchButton);
    });
  });
  it('should render AdvanceRateForm component with advance set to false', async () => {
    mockProps.bookingAllowances = [] as any;
    const { getByTestId } = render(<AdvanceRateForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`AdvanceRateForm-container`)).toBeInTheDocument();
    });
  });
});
