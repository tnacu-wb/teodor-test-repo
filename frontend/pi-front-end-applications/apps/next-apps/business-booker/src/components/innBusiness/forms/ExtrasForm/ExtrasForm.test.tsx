import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';

import { ExtrasForm } from './ExtrasForm';

const mockProfileDetails = {
  contactDetail: {
    title: 'Mr',
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@yupmail.com',
    phoneNumber: '+4411111111',
    mobileNumber: '+4411111112',
    address: {
      line1: 'Test',
      line2: '',
      line3: '',
      line4: '',
      line5: '',
      countryCode: 'GB',
      postCode: 'SW1A 2AA',
    },
  },
  bookingPreference: {
    roomRequirements: {
      type: 'FAM',
      lettingType: null,
      adults: 1,
      children: 1,
      cotRequired: false,
      hotelBrand: null,
    },
    reason: null,
    foodPreference: 11,
    wantSmsConfirmations: null,
    preselectWifi: true,
  },
  paymentPreference: {
    electronicInvoiceRequired: true,
    paymentCard: null,
  },
} as any;

const mockProps = {
  formRef: { current: document.createElement('form') },
  icons: { icon: 'test' },
  onSubmit: jest.fn(),
  profileDetails: mockProfileDetails,
};

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
  };
});

describe('ExtrasForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render ExtrasForm component', async () => {
    const { getByTestId } = render(<ExtrasForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId('Meal-Options-Container-List')).toBeInTheDocument();
      expect(getByTestId('Wifi-Options-Container')).toBeInTheDocument();
      expect(getByTestId('Invoicing-Options-Container-List')).toBeInTheDocument();
    });
  });

  it('should change radio buttons options', async () => {
    const { getByLabelText } = render(<ExtrasForm {...mockProps} />);
    const mealOptionOne = getByLabelText('extraspreferences.mealoptions.pibreakfast');
    const mealOptionTwo = getByLabelText('extraspreferences.mealoptions.continentalbreakfast');
    const wifiOptionOne = getByLabelText('extraspreferences.wifi.always');
    const wifiOptionTwo = getByLabelText('extraspreferences.wifi.never');
    const invoicingOptionOne = getByLabelText('extraspreferences.edit.invoice.email');
    const invoicingOptionTwo = getByLabelText('extraspreferences.edit.invoice.checkin');

    await waitFor(async () => {
      fireEvent.click(mealOptionOne);
    });
    await waitFor(async () => {
      fireEvent.click(mealOptionTwo);
    });
    await waitFor(async () => {
      expect(mealOptionTwo).toBeChecked();
    });

    await waitFor(async () => {
      fireEvent.click(wifiOptionOne);
    });
    await waitFor(async () => {
      fireEvent.click(wifiOptionTwo);
    });
    await waitFor(async () => {
      expect(wifiOptionTwo).toBeChecked();
    });

    await waitFor(async () => {
      fireEvent.click(invoicingOptionOne);
    });
    await waitFor(async () => {
      fireEvent.click(invoicingOptionTwo);
    });
    await waitFor(async () => {
      expect(invoicingOptionTwo).toBeChecked();
    });
  });
});
