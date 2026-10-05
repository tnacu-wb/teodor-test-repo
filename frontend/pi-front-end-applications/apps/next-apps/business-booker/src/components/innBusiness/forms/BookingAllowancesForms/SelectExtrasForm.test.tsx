import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';

import { SelectExtrasForm } from '~components/innBusiness/forms/BookingAllowancesForms/SelectExtrasForm';
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

describe('SelectExtrasForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.bookingAllowances = bookingAllowancesMocks;
  });

  it('should render SelectExtrasForm component', async () => {
    const { getByTestId } = render(<SelectExtrasForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`SelectExtrasForm-container`)).toBeInTheDocument();
    });
  });
  it('should render SelectExtrasForm component and change switch', async () => {
    const { getByTestId } = render(<SelectExtrasForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`SelectExtrasForm-container`)).toBeInTheDocument();
    });

    const switchButton = getByTestId(`SelectExtrasForm-additionalCosts-switcher`);

    await waitFor(async () => {
      fireEvent.click(switchButton);
    });
  });
  it('should call onDirtyChange on edit and revert', async () => {
    const { getByTestId } = render(<SelectExtrasForm {...mockProps} />);
    const switchButton = getByTestId(`SelectExtrasForm-additionalCosts-switcher`);

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
  it('should render SelectExtrasForm component with advance set to false', async () => {
    mockProps.bookingAllowances = [] as any;
    const { getByTestId } = render(<SelectExtrasForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`SelectExtrasForm-container`)).toBeInTheDocument();
    });
  });
});
