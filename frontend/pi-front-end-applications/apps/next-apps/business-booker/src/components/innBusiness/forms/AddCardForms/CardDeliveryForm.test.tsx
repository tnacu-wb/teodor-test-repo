import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { LOCALES } from '@whitbread-eos/api';

import { CardDeliveryForm } from '~components/innBusiness/forms/AddCardForms/CardDeliveryForm';

jest.mock('~components/innBusiness/ReviewChanges', () => ({
  ReviewChanges: () => <div />,
}));

const mockProps = {
  onSubmit: (data: any) => {
    return data;
  },
  icons: { 'icon.arrow.left': '/' },
  formRef: { current: document.createElement('form') },
  companyAddress: { country: 'EN', addressLine1: '', postCode: '' },
  addressComponent: <div></div>,
  selectedEmployee: null,
  isMyCard: true,
} as any;

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getSearchParams: () => new URLSearchParams(),
    getCardManagementLabels: () => null,
    getCommonIcons: () => ({}),
    formatIBAssetsUrl: () => {
      return '/';
    },
    getAccountList: () => [
      {
        accountName: 'test',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
    ],
    getSelectedAccountHolder: () => {
      return {
        accountName: 'test four',
        accountNumber: '6356290001000112',
        schemeCustomerId: 19000302,
        tetheredGuid: '4abb9835-8996-42bd-8199-5ba0919f106e',
        registrationRoles: ['COST_CENTRE_USER'],
        errorCode: null,
        scheme: 'DE',
      };
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

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    push: jest.fn(),
  })),
  usePathname: () => {
    return '/';
  },
}));

const mockWatch = {
  function: (value: string) => {
    switch (value) {
      case 'creditLimitNumber':
        return '-1';
      case 'startDate':
        return '';
      case 'endDate':
        return '';
      default:
        return '1';
    }
  },
} as any;

jest.mock('react-hook-form', () => ({
  useFormContext: () => ({
    control: {},
    formState: { errors: {} },
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    watch: mockWatch.function,
    setValue: jest.fn(),
  }),
  Controller: jest.fn(({ render }) => render({ field: {} })),
}));

describe('CardDeliveryForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.companyAddress.country = 'EN';
  });

  it('should render CardDeliveryForm component EN', async () => {
    const { getByTestId } = render(<CardDeliveryForm {...mockProps} />);

    expect(getByTestId('Delivery-Title')).toBeInTheDocument();
  });

  it('should render CardDeliveryForm component with selected user', async () => {
    const user = userEvent.setup();
    mockProps.selectedEmployee = {
      employeeData: {
        title: 'Mr',
        firstName: 'Test',
        lastName: 'Test',
      },
    };
    const { getByTestId } = render(<CardDeliveryForm {...mockProps} />);
    const titleInput = getByTestId('Title-IB-Form-Select-Button');

    expect(getByTestId('Delivery-Title')).toBeInTheDocument();

    await user.click(titleInput);
    titleInput.blur();
    await user.tab();
  });

  it('should render CardDeliveryForm component DE', async () => {
    mockProps.companyAddress.country = 'DE';
    const { getByTestId } = render(<CardDeliveryForm {...mockProps} />);

    expect(getByTestId('Delivery-Title')).toBeInTheDocument();
  });

  it('should render CardDeliveryForm component DE with classes', async () => {
    mockProps.companyAddress.country = 'DE';
    const { getByTestId } = render(
      <CardDeliveryForm
        {...mockProps}
        isMyCard={true}
        sectionTitle={'title'}
        sectionTitleClassName={'test'}
        formContainerClassName={'test'}
      />
    );

    expect(getByTestId('Delivery-Title')).toBeInTheDocument();
  });

  it('should render CardDeliveryForm when is my card', () => {
    const { getByText, getByRole } = render(<CardDeliveryForm {...mockProps} />);
    expect(getByText('cards.cardMgmt.delivery.where.title')).toBeInTheDocument();
    const companyCorrespondenceAddressRadio = getByRole('radio', {
      name: 'cards.cardMgmt.delivery.where.options.companyCorrespondence',
    }) as HTMLInputElement;
    expect(companyCorrespondenceAddressRadio.value).toBe('COMPANY_CORRESPONDENCE_ADDRESS');
    expect(companyCorrespondenceAddressRadio.disabled).toBe(false);
    const cardholderAddressRadio = getByRole('radio', {
      name: /cards.cardMgmt.delivery.where.options.cardHolderAddress/i,
    }) as HTMLInputElement;
    expect(cardholderAddressRadio).toBeInTheDocument();
    expect(cardholderAddressRadio.value).toBe('CARDHOLDER_ALTERNATIVE_ADDRESS');
    expect(cardholderAddressRadio.disabled).toBe(true);
  });

  it('should render CardDeliveryForm when is not my card and the user is allowed to send card to an alternative address', () => {
    const updatedProps = {
      ...mockProps,
      isMyCard: false,
      sendCardsToCardholder: true,
    };
    const { getByText, getByRole } = render(<CardDeliveryForm {...updatedProps} />);
    expect(getByText('cards.cardMgmt.delivery.where.title')).toBeInTheDocument();
    const companyCorrespondenceAddressRadio = getByRole('radio', {
      name: 'cards.cardMgmt.delivery.where.options.companyCorrespondence',
    }) as HTMLInputElement;
    expect(companyCorrespondenceAddressRadio.disabled).toBe(false);
    const cardholderAddressRadio = getByRole('radio', {
      name: /cards.cardMgmt.delivery.where.options.cardHolderAddress/i,
    }) as HTMLInputElement;
    expect(cardholderAddressRadio).toBeInTheDocument();
    expect(cardholderAddressRadio.disabled).toBe(false);
  });

  it('should render CardDeliveryForm when is not my card and the user is not allowed to send card to an alternative address', () => {
    const updatedProps = {
      ...mockProps,
      isMyCard: false,
      sendCardsToCardholder: false,
    };
    const { getByText, getByRole } = render(<CardDeliveryForm {...updatedProps} />);
    expect(getByText('cards.cardMgmt.delivery.where.title')).toBeInTheDocument();
    const companyCorrespondenceAddressRadio = getByRole('radio', {
      name: 'cards.cardMgmt.delivery.where.options.companyCorrespondence',
    }) as HTMLInputElement;
    expect(companyCorrespondenceAddressRadio.disabled).toBe(false);
    const cardholderAddressRadio = getByRole('radio', {
      name: /cards.cardMgmt.delivery.where.options.cardHolderAddress/i,
    }) as HTMLInputElement;
    expect(cardholderAddressRadio).toBeInTheDocument();
    expect(cardholderAddressRadio.disabled).toBe(true);
  });
});
