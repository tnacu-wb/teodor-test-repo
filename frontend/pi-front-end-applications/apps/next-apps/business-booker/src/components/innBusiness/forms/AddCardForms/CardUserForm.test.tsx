import '@testing-library/jest-dom';
import { fireEvent, act, render } from '@testing-library/react';
import { Language, LOCALES, UserAccessLevels, SS_ALTERNATE_PATH } from '@whitbread-eos/api';
import { useRouter } from 'next/navigation';

import {
  CardUserForm,
  parseDisplayName,
} from '~components/innBusiness/forms/AddCardForms/CardUserForm';

jest.mock('~components/innBusiness/ReviewChanges', () => ({
  ReviewChanges: () => <div />,
}));

const mockProps = {
  onSubmit: (data: any) => {
    return data;
  },
  icons: {
    'icon.arrow.left': '/',
    'icon.notification.info': '/',
    'icon.notification.error': '/',
    'icon.notification.alert': '/',
    'icon.chevron.down': '/',
  },
  cardHolderName: 'test',
  formRef: { current: document.createElement('form') },
  companyId: '123',
  defaultEmployeeId: '123',
  onContinue: () => {
    return;
  },
  calendarLabels: {},
  disableReview: () => {
    return;
  },
  language: 'en' as Language,
  token: '123',
  updateSelectedEmployee: jest.fn(),
  selectedEmployee: null,
  accessLevel: 'SUPER' as UserAccessLevels,
  className: '',
  loggedEmployeeDetails: {},
  costCenterOptions: [],
  applicationId: '793192',
  applicationGuid: 'ad2478f5-89eb-4ee1-b192-906b55082df3',
  hideAddEmployeeButton: false,
  isPayApp: true,
};

jest.mock('@whitbread-eos/utils', () => ({
  cn: jest.fn(),
  getLocaleByPathname: () => {
    return LOCALES.EN;
  },
  getPathForLocale: (locale: string, path: string) => {
    return `/${locale}/${path}`;
  },
  useTranslation: () => {
    return {
      t: (str: string) => str,
    };
  },
  formatIBAssetsUrl: () => {
    return '/';
  },
  getAuthCookie: () => {
    return '123';
  },
  findError: () => undefined,
  GLOBALS: {
    language: {
      DE: 'de',
      EN: 'en',
    },
  },
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    IBPayCardUserSchema: () => ({
      validation: jest.fn(),
    }),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getSearchParams: () => new URLSearchParams(),
    getCardManagementLabels: () => null,
    getCommonIcons: () => ({}),
    findError: serverUtils.findError,
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
  useSearchParams: jest.fn(() => ({
    get: jest.fn(() => null),
  })),
}));

const mockWatchResponses = {
  user: 'existing',
};

const mockWatch = {
  function: (value: string) => {
    switch (value) {
      case 'creditLimitNumber':
        return '-1';
      case 'startDate':
        return '';
      case 'endDate':
        return '';
      case 'user':
        return mockWatchResponses.user;
      default:
        return '1';
    }
  },
} as any;

const setValueMock = jest.fn((name, value) => {
  if (name === 'user') {
    mockWatchResponses.user = value;
  }
});

jest.mock('react-hook-form', () => ({
  useFormContext: () => ({
    control: {},
    formState: { errors: {} },
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    watch: mockWatch.function,
    setValue: setValueMock,
    getValues: jest.fn(),
  }),
  Controller: jest.fn(({ render }) => render({ field: {} })),
}));

const sessionStore: Record<string, string> = {};

const sessionStorageMock = {
  getItem: jest.fn((key: string) => sessionStore[key] ?? null),
  setItem: jest.fn((key: string, value: string) => {
    sessionStore[key] = value;
  }),
  removeItem: jest.fn((key: string) => {
    delete sessionStore[key];
  }),
};

Object.defineProperty(window, 'sessionStorage', {
  value: sessionStorageMock,
});

const originalLocation = window.location;

describe('CardUserForm Component', () => {
  const mockRouter = {
    push: jest.fn(),
    replace: jest.fn(),
    refresh: jest.fn(),
    back: jest.fn(),
    forward: jest.fn(),
    prefetch: jest.fn(),
  };
  beforeEach(() => {
    Object.defineProperty(window, 'location', {
      configurable: true,
      value: {
        ...originalLocation,
        origin: 'http://localhost:3000',
        pathname: '/en-gb/business-pay/apply',
      },
    });
    jest.mocked(useRouter).mockReturnValue(mockRouter);
    jest.clearAllMocks();
  });

  it('should render CardUserForm component', async () => {
    const { getByTestId } = render(<CardUserForm {...mockProps} />);

    expect(getByTestId('Who-Container-Title')).toBeInTheDocument();
  });

  it('should render CardUserForm component with card for me', async () => {
    mockWatchResponses.user = 'me';
    const { getByTestId, queryByTestId } = render(<CardUserForm {...mockProps} />);

    const continueButton = getByTestId('Continue-Button-Add-Card');
    expect(continueButton).toBeInTheDocument();
    expect(getByTestId('Who-Container-Title')).toBeInTheDocument();
    expect(queryByTestId('Select-Cost-Center-Section')).not.toBeInTheDocument();
    await act(async () => {
      fireEvent.click(continueButton);
    });
  });

  it('should render CardUserForm component and click add employee button', async () => {
    const { getByTestId } = render(<CardUserForm {...mockProps} />);

    const addEmployeeButton = getByTestId('Add-Card-New-Employee-Button');
    expect(addEmployeeButton).toBeInTheDocument();
    expect(getByTestId('Who-Container-Title')).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(addEmployeeButton);
      expect(sessionStorageMock.setItem).toHaveBeenCalledWith(
        SS_ALTERNATE_PATH,
        'http://localhost:3000/en-gb/business-pay/apply?applicationId=793192&applicationGuid=ad2478f5-89eb-4ee1-b192-906b55082df3'
      );
      expect(mockRouter.push).toHaveBeenCalledWith(
        '/en-gb/manage/employees/add?alt=1&backUrl=business-pay%2Fapply%3FapplicationId%3D793192%26applicationGuid%3Dad2478f5-89eb-4ee1-b192-906b55082df3'
      );
    });
  });

  it('should render CardUserForm component and click continue button WITH INVALID OPTIONS', async () => {
    const { getByTestId } = render(<CardUserForm {...mockProps} />);

    const continueButton = getByTestId('Continue-Button-Add-Card');
    expect(continueButton).toBeInTheDocument();
    expect(getByTestId('Who-Container-Title')).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(continueButton);
    });
  });

  it('should render CardUserForm component with cost center options', async () => {
    const updatedProps = {
      ...mockProps,
      isCostCentreManagementEnabled: true,
      costCenterOptions: [
        { displayValue: 'Cost Center 1', value: 'cc1' },
        { displayValue: 'Cost Center 2', value: 'cc2' },
      ],
    };
    const { getByTestId } = render(<CardUserForm {...updatedProps} />);

    const continueButton = getByTestId('Continue-Button-Add-Card');
    expect(continueButton).toBeInTheDocument();
    expect(getByTestId('Select-Cost-Center-Section')).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(continueButton);
    });
  });

  it('should render CardUserForm component and click continue button WITH VALID OPTIONS', async () => {
    mockWatch.function = (value: string) => {
      switch (value) {
        case 'creditLimitNumber':
          return '1';
        case 'usageRestriction':
          return true;
        case 'startDate':
          return '2';
        case 'endDate':
          return '2';
        default:
          return '1';
      }
    };
    const { getByTestId } = render(<CardUserForm {...mockProps} />);

    const continueButton = getByTestId('Continue-Button-Add-Card');
    expect(continueButton).toBeInTheDocument();
    expect(getByTestId('Who-Container-Title')).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(continueButton);
    });
  });

  it('should call parseDisplayName', async () => {
    let name = parseDisplayName('fullName', 'Mr', 'firstName', 'lastName');
    expect(name).toBe('firstName lastName');

    name = parseDisplayName('titleFullName', 'Mr', 'firstName', 'lastName');
    expect(name).toBe('Mr firstName lastName');

    name = parseDisplayName('initialLastName', 'Mr', 'firstName', 'lastName');
    expect(name).toBe('f lastName');

    name = parseDisplayName('titleInitialLastName', 'Mr', 'firstName', 'lastName');
    expect(name).toBe('Mr f lastName');
  });

  it('selects existing employee option when employeeId is present in URL', () => {
    const mockUseSearchParams = jest.requireMock('next/navigation').useSearchParams;
    mockUseSearchParams.mockReturnValueOnce({
      get: jest.fn((param) => (param === 'employeeId' ? '456' : null)),
    });
    const { getByRole } = render(<CardUserForm {...mockProps} />);
    const existingEmployeeRadio = getByRole('radio', {
      name: 'cards.cardMgmt.addCard.who.options.existing',
    }) as HTMLInputElement;
    expect(existingEmployeeRadio).toBeInTheDocument();
    expect(existingEmployeeRadio.disabled).toBe(false);
    expect(setValueMock).toHaveBeenCalledWith('user', 'existing');
    expect(mockRouter.replace).toHaveBeenCalledWith(
      '/en-gb/business-pay/apply?applicationId=793192&applicationGuid=ad2478f5-89eb-4ee1-b192-906b55082df3'
    );
  });
});

describe('CardUserForm costCenterSelect', () => {
  const baseProps = {
    ...mockProps,
    isCostCentreManagementEnabled: true,
    costCenterOptions: [
      { value: 'cost1', displayValue: 'Cost Center 1' },
      { value: 'cost2', displayValue: 'Cost Center 2' },
    ],
  };

  it('renders cost center select when enabled and multiple options', () => {
    const { getByText, getByTestId } = render(<CardUserForm {...baseProps} />);
    expect(getByText('cards.cardMgmt.costCentre.assign.label')).toBeInTheDocument();
    expect(getByTestId('Who-Container-Title')).toBeInTheDocument();
  });

  it('renders single cost center display value when only one option and not "none"', () => {
    const props = {
      ...baseProps,
      costCenterOptions: [{ value: 'cost1', displayValue: 'Cost Center 1' }],
    };
    const { getByText } = render(<CardUserForm {...props} />);
    expect(getByText('Cost Center 1')).toBeInTheDocument();
  });

  it('does not render cost center select if only option is "none"', () => {
    const props = {
      ...baseProps,
      costCenterOptions: [{ value: 'none', displayValue: 'None' }],
    };
    const { queryByText } = render(<CardUserForm {...props} />);
    expect(queryByText('cards.cardMgmt.costCentre.assign.label')).not.toBeInTheDocument();
  });

  it('does not render cost center select if isCostCentreManagementEnabled is false', () => {
    const props = {
      ...baseProps,
      isCostCentreManagementEnabled: false,
    };
    const { queryByText } = render(<CardUserForm {...props} />);
    expect(queryByText('cards.cardMgmt.costCentre.assign.label')).not.toBeInTheDocument();
  });
});

describe('CardUserForm - Business Pay Company Type', () => {
  describe('Business Pay Manager role (behaves like Travel Manager)', () => {
    const businessPayManagerProps = {
      ...mockProps,
      accessLevel: UserAccessLevels.BUSINESS_PAY_MANAGER,
    };

    beforeEach(() => {
      jest.clearAllMocks();
    });

    it('should render "Who is this card for?" section for Business Pay Manager', () => {
      const { getByTestId } = render(<CardUserForm {...businessPayManagerProps} />);
      expect(getByTestId('Who-Container-Title')).toBeInTheDocument();
    });

    it('should show existing employee option for Business Pay Manager', () => {
      const { getByRole } = render(<CardUserForm {...businessPayManagerProps} />);
      const existingEmployeeRadio = getByRole('radio', {
        name: 'cards.cardMgmt.addCard.who.options.existing',
      });
      expect(existingEmployeeRadio).toBeInTheDocument();
    });

    it('should show add employee button for Business Pay Manager', () => {
      const { getByTestId } = render(<CardUserForm {...businessPayManagerProps} />);
      const addEmployeeButton = getByTestId('Add-Card-New-Employee-Button');
      expect(addEmployeeButton).toBeInTheDocument();
    });

    it('should NOT show restricted access notification for Business Pay Manager', () => {
      const { queryByText } = render(<CardUserForm {...businessPayManagerProps} />);
      const notification = queryByText('cards.cardMgmt.restrictedAccess.title');
      expect(notification).not.toBeInTheDocument();
    });

    it('should allow Business Pay Manager to click add employee button', async () => {
      const { getByTestId } = render(<CardUserForm {...businessPayManagerProps} />);
      const addEmployeeButton = getByTestId('Add-Card-New-Employee-Button');

      await act(async () => {
        fireEvent.click(addEmployeeButton);
        expect(sessionStorageMock.setItem).toHaveBeenCalledWith(
          SS_ALTERNATE_PATH,
          'http://localhost:3000/en-gb/business-pay/apply?applicationId=793192&applicationGuid=ad2478f5-89eb-4ee1-b192-906b55082df3'
        );
      });
    });

    it('should hide add employee button if hideAddEmployeeButton is true for Business Pay Manager', () => {
      const props = {
        ...businessPayManagerProps,
        hideAddEmployeeButton: true,
      };
      const { queryByTestId } = render(<CardUserForm {...props} />);
      const addEmployeeButton = queryByTestId('Add-Card-New-Employee-Button');
      expect(addEmployeeButton).not.toBeInTheDocument();
    });
  });

  describe('Business Pay User role (behaves like Self Booker)', () => {
    const businessPayUserProps = {
      ...mockProps,
      accessLevel: UserAccessLevels.BUSINESS_PAY_USER,
    };

    beforeEach(() => {
      jest.clearAllMocks();
      mockWatchResponses.user = 'me';
    });

    it('should auto-select "Me" for Business Pay User', () => {
      render(<CardUserForm {...businessPayUserProps} />);
      expect(setValueMock).toHaveBeenCalledWith('user', 'me');
    });

    it('should NOT show add employee button for Business Pay User', () => {
      const { queryByTestId } = render(<CardUserForm {...businessPayUserProps} />);
      const addEmployeeButton = queryByTestId('Add-Card-New-Employee-Button');
      expect(addEmployeeButton).not.toBeInTheDocument();
    });

    it('should show restricted access notification for Business Pay User', () => {
      const { getByText } = render(<CardUserForm {...businessPayUserProps} />);
      const notification = getByText('cards.cardMgmt.restrictedAccess.title');
      expect(notification).toBeInTheDocument();
    });

    it('should NOT show existing employee option for Business Pay User', () => {
      const { queryByRole } = render(<CardUserForm {...businessPayUserProps} />);
      const existingEmployeeRadio = queryByRole('radio', {
        name: 'cards.cardMgmt.addCard.who.options.existing',
      });
      expect(existingEmployeeRadio).not.toBeInTheDocument();
    });

    it('should render continue button for Business Pay User', () => {
      const { getByTestId } = render(<CardUserForm {...businessPayUserProps} />);
      const continueButton = getByTestId('Continue-Button-Add-Card');
      expect(continueButton).toBeInTheDocument();
    });
  });

  describe('Self Booker role (existing tests validation)', () => {
    const selfBookerProps = {
      ...mockProps,
      accessLevel: UserAccessLevels.SELF,
    };

    beforeEach(() => {
      jest.clearAllMocks();
      mockWatchResponses.user = 'me';
    });

    it('should auto-select "Me" for Self Booker', () => {
      render(<CardUserForm {...selfBookerProps} />);
      expect(setValueMock).toHaveBeenCalledWith('user', 'me');
    });

    it('should show restricted access notification for Self Booker', () => {
      const { getByText } = render(<CardUserForm {...selfBookerProps} />);
      const notification = getByText('cards.cardMgmt.restrictedAccess.title');
      expect(notification).toBeInTheDocument();
    });
  });

  describe('Guest role (existing tests validation)', () => {
    const guestProps = {
      ...mockProps,
      accessLevel: UserAccessLevels.STAYER,
    };

    beforeEach(() => {
      jest.clearAllMocks();
      mockWatchResponses.user = 'me';
    });

    it('should auto-select "Me" for Guest', () => {
      render(<CardUserForm {...guestProps} />);
      expect(setValueMock).toHaveBeenCalledWith('user', 'me');
    });

    it('should show restricted access notification for Guest', () => {
      const { getByText } = render(<CardUserForm {...guestProps} />);
      const notification = getByText('cards.cardMgmt.restrictedAccess.title');
      expect(notification).toBeInTheDocument();
    });
  });
});
