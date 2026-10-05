import '@testing-library/jest-dom';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import {
  AccessLevel,
  EmployeeStatus,
  Language,
  LOCALES,
  SS_ALTERNATE_PATH,
  URLParams,
  requestErrors,
  requestStatus,
} from '@whitbread-eos/api';
import React from 'react';

import { AddEditEmployee } from './add-edit-employee';

const toastMock = jest.fn();
const updateEmployeeDetailsMock = jest.fn();
const addNewEmployeeMock = jest.fn();
const mockPush = jest.fn();
const setAddEmployeeAnalyticsData = jest.fn();
const setPageName = jest.fn();
const revalidateCacheOnLinkMock = jest.fn();
let alternateSourceValue: string | null = null;
let submittedEmployeeStatus: EmployeeStatus = EmployeeStatus.Purged;
const getDetailsFromTokenMock = jest.fn(() => ({ isBusinessPayManager: false }));

jest.mock('@whitbread-eos/atoms/ui', () => {
  return {
    useToast: () => ({ toast: toastMock }),
    Button: ({ children, ...props }: any) => (
      <button {...props} type="button">
        {children}
      </button>
    ),
    FormPage: ({ baseDataTestId, children }: any) => (
      <div data-testid={`${baseDataTestId}-page`}>{children}</div>
    ),
    Notification: ({ type, message, title }: any) => (
      <div data-testid={`Notification-${type}`}>
        {title ?? null}
        {message ?? null}
      </div>
    ),
    SanitizedContent: ({ children }: any) => <>{children}</>,
  };
});

jest.mock('@whitbread-eos/utils', () => ({
  GLOBALS: { language: { EN: 'en' } },
  analytics: { update: jest.fn() },
  getAuthCookie: jest.fn(() => 'auth-cookie'),
  useTranslation: jest.fn(() => ({ t: (key: string) => key })),
  getPathForLocale: jest.fn((_, path: string) => `/${path}`),
  getLocaleByPathname: jest.fn(() => LOCALES.EN),
  formatIBAssetsUrl: jest.fn(() => '/asset'),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  getCountriesList: jest.fn(),
  getCountryName: jest.fn(() => 'United Kingdom'),
  getLabelType: jest.fn(),
  updateEmployeeDetails: (...args: unknown[]) => updateEmployeeDetailsMock(...args),
  addNewEmployee: (...args: unknown[]) => addNewEmployeeMock(...args),
  getDetailsFromToken: () => getDetailsFromTokenMock(),
}));

jest.mock('next/navigation', () => ({
  useRouter: () => ({ push: mockPush }),
  usePathname: () => '/en/manage/employees',
  useSearchParams: () => ({
    get: (param: string) => (param === URLParams.alternateSource ? alternateSourceValue : null),
  }),
}));

jest.mock('~components/innBusiness/forms/PaymentCardForm', () => ({
  PaymentCardForm: ({ onSubmit, formRef }: any) => {
    React.useEffect(() => {
      if (formRef) {
        formRef.current = {
          requestSubmit: () => onSubmit({ cardId: 'central-card' }),
        };
      }
    }, [formRef, onSubmit]);
    return null;
  },
}));

jest.mock('next/cache', () => ({
  revalidatePath: (path: string) => {
    return path;
  },
}));

jest.mock('../../../cards/components/revalidate-link', () => ({
  revalidateCacheOnLink: (...args: unknown[]) => revalidateCacheOnLinkMock(...args),
}));

jest.mock('~components/innBusiness/forms/PersonalDetailsForm', () => ({
  PersonalDetailsForm: ({ onSubmit, formRef }: any) => {
    React.useEffect(() => {
      if (formRef) {
        formRef.current = {
          requestSubmit: () =>
            onSubmit({
              firstName: 'Test',
              lastName: 'User',
              emailAddress: 'test@example.com',
            }),
        };
      }
    }, [formRef, onSubmit]);
    return null;
  },
}));

jest.mock('~components/innBusiness/forms/UserRoleForm', () => ({
  UserRoleForm: ({ onSubmit, formRef, showLastTMError }: any) => {
    React.useEffect(() => {
      if (formRef) {
        formRef.current = {
          requestSubmit: () =>
            onSubmit({
              accessLevel: AccessLevel.Super,
              employeeStatus: submittedEmployeeStatus,
            }),
        };
      }
    }, [formRef, onSubmit]);
    return <div data-testid="UserRoleForm" data-last-tm-error={showLastTMError} />;
  },
}));

jest.mock('~components/innBusiness/forms/RegistrationQuestionsForm', () => ({
  RegistrationQuestionsForm: ({ onSubmit, formRef }: any) => {
    React.useEffect(() => {
      if (formRef) {
        formRef.current = {
          requestSubmit: () => onSubmit({ question: 'answer' }),
        };
      }
    }, [formRef, onSubmit]);
    return null;
  },
}));

jest.mock('~components/innBusiness/forms/CompanyAddressForm', () => ({
  CompanyAddress: ({ onSubmit, formRef }: any) => {
    React.useEffect(() => {
      if (formRef) {
        formRef.current = {
          requestSubmit: () =>
            onSubmit({
              addressLine1: 'Line 1',
              postCode: 'AB12',
              country: 'GB',
            }),
        };
      }
    }, [formRef, onSubmit]);
    return null;
  },
}));

jest.mock('~components/innBusiness/ReviewChanges', () => ({
  ReviewChanges: () => <div data-testid="Review-Changes" />,
}));

jest.mock('~components/innBusiness/ManageEmployeesAnalytics', () => {
  const Analytics = React.forwardRef(function AnalyticsMock(_, ref) {
    React.useImperativeHandle(ref, () => ({
      setAddEmployeeAnalyticsData,
      setPageName,
      setValidation: jest.fn(),
    }));
    return <div data-testid="Analytics" />;
  });
  return {
    Analytics,
    roleToAnalyticsCaption: {
      [AccessLevel.Super]: 'Travel manager',
      [AccessLevel.Booker]: 'Booker',
      [AccessLevel.Self]: 'Self-booker',
      [AccessLevel.Stayer]: 'Guest',
      [AccessLevel.BusinessPayManager]: 'Business Pay Manager',
      [AccessLevel.BusinessPayUser]: 'Business Pay User',
    },
    PageNames: {
      MANAGE_EMPLOYEES_SUCCESSFUL_ACCOUNT_CREATION: 'creation',
      MANAGE_EMPLOYEES_SUCCESSFUL_ACCOUNT_DELETION: 'deletion',
    },
  };
});

type AddEditEmployeeProps = React.ComponentProps<typeof AddEditEmployee>;

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
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
  clear: jest.fn(() => {
    Object.keys(sessionStore).forEach((key) => delete sessionStore[key]);
  }),
};

Object.defineProperty(window, 'sessionStorage', {
  value: sessionStorageMock,
});

const resetSessionStorage = () => {
  sessionStorageMock.clear();
  sessionStorageMock.getItem.mockClear();
  sessionStorageMock.setItem.mockClear();
  sessionStorageMock.removeItem.mockClear();
  sessionStorageMock.clear.mockClear();
};

const baseProps: AddEditEmployeeProps = {
  icons: {
    'icon.arrow.left.purple': '/arrow',
    'icon.notification.error': '/error',
    'icon.notification.info': '/info',
    'icon.chevron.down': '/chevron',
  },
  language: 'en' as Language,
  paymentCards: [],
  employeeStatus: EmployeeStatus.Active,
  userDetails: {
    title: 'Mr',
    firstName: 'Stefan',
    lastName: 'Ciora',
    emailAddress: 'stefan.ciora@mailinator.com',
    phoneNumber: '+4411111111',
    mobileNumber: '+441111111',
    address: { addressLine1: 'test', postCode: '11111', country: 'GB' },
    centralCardId: '1',
    employeeId: '1',
    accessLevel: AccessLevel.Super,
  },
  companyId: '1',
  registrationQuestions: [],
  companyAddress: {
    addressLine1: 'Line 1',
    postCode: 'AB12',
    country: 'GB',
  },
};

describe('AddEditEmployee Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    updateEmployeeDetailsMock.mockReset();
    addNewEmployeeMock.mockReset();
    revalidateCacheOnLinkMock.mockReset();
    resetSessionStorage();
    (window as any).analyticsData = { innBusiness: {} };
    (window as any)._satellite = { track: jest.fn() };
    (window as any).scrollTo = jest.fn();
    alternateSourceValue = null;
    submittedEmployeeStatus = EmployeeStatus.Purged;
  });

  const renderComponent = (override: Partial<AddEditEmployeeProps> = {}) =>
    render(<AddEditEmployee {...baseProps} {...override} />);

  it('renders AddEditEmployee component as Add', async () => {
    renderComponent();

    await waitFor(() => {
      expect(screen.getByTestId('AddEditEmployee-page')).toBeInTheDocument();
    });
  });

  it('renders AddEditEmployee component as Edit', async () => {
    renderComponent({ id: 'employee-123' });

    expect(screen.getByTestId('AddEditEmployee-page')).toBeInTheDocument();
  });

  it('submits edit flow and tracks deletion analytics', async () => {
    updateEmployeeDetailsMock.mockResolvedValueOnce({ status: requestStatus.success });

    renderComponent({ id: 'employee-123' });

    fireEvent.click(screen.getByTestId('Submit-Employee-Details'));

    await waitFor(() => {
      expect(updateEmployeeDetailsMock).toHaveBeenCalled();
    });

    expect(revalidateCacheOnLinkMock).toHaveBeenCalledWith('/manage/employees/employee-123');
    expect(revalidateCacheOnLinkMock).toHaveBeenCalledWith('/manage/employees');
    expect(mockPush).toHaveBeenCalledWith('/manage/employees');
    expect((window as any)._satellite.track).toHaveBeenCalledWith('saveUpdates');
    expect(toastMock).toHaveBeenCalledWith(
      expect.objectContaining({ content: 'userMgmt.employee.edit.success' })
    );
    expect(setPageName).toHaveBeenCalledWith('deletion');
  });

  it('does revalidate list cache non-delete edit flow', async () => {
    submittedEmployeeStatus = EmployeeStatus.Active;
    updateEmployeeDetailsMock.mockResolvedValueOnce({ status: requestStatus.success });

    renderComponent({ id: 'employee-123' });

    fireEvent.click(screen.getByTestId('Submit-Employee-Details'));

    await waitFor(() => {
      expect(updateEmployeeDetailsMock).toHaveBeenCalled();
    });

    expect(revalidateCacheOnLinkMock).toHaveBeenCalledWith('/manage/employees/employee-123');
    expect(revalidateCacheOnLinkMock).toHaveBeenCalledWith('/manage/employees');
    expect(mockPush).toHaveBeenCalledWith('/manage/employees');
  });

  it('shows last travel manager error when update returns specific error', async () => {
    updateEmployeeDetailsMock.mockResolvedValueOnce({
      status: requestStatus.fail,
      error: requestErrors.lastTravelManager,
    });

    renderComponent({ id: 'employee-123' });

    fireEvent.click(screen.getByTestId('Submit-Employee-Details'));

    await waitFor(() => {
      expect(updateEmployeeDetailsMock).toHaveBeenCalled();
    });

    await waitFor(() => {
      expect(screen.getByTestId('UserRoleForm').dataset.lastTmError).toBe('true');
    });
  });

  it('submits add flow, uses alternate redirect and removes stored path', async () => {
    addNewEmployeeMock.mockResolvedValueOnce({
      status: requestStatus.success,
      employeeId: 'new-employee',
    });
    alternateSourceValue = 'true';
    sessionStore[SS_ALTERNATE_PATH] = '/alternate-route?foo=bar';

    renderComponent();

    fireEvent.click(screen.getByTestId('Submit-Employee-Details'));

    await waitFor(() => {
      expect(addNewEmployeeMock).toHaveBeenCalled();
    });

    expect(revalidateCacheOnLinkMock).toHaveBeenCalledWith('/manage/employees');
    expect(mockPush).toHaveBeenCalledWith('/alternate-route?foo=bar&employeeId=new-employee');
    expect(sessionStorageMock.removeItem).toHaveBeenCalledWith(SS_ALTERNATE_PATH);
    expect((window as any)._satellite.track).toHaveBeenCalledWith('saveUpdates');
    expect(setAddEmployeeAnalyticsData).toHaveBeenCalledWith(
      expect.objectContaining({
        addEmployee: true,
        employee: 'individual',
      })
    );
  });

  it('shows failure notification when add flow fails', async () => {
    addNewEmployeeMock.mockResolvedValueOnce({
      status: requestStatus.fail,
    });

    renderComponent();

    fireEvent.click(screen.getByTestId('Submit-Employee-Details'));

    await waitFor(() => {
      expect(addNewEmployeeMock).toHaveBeenCalled();
    });

    await waitFor(() => {
      expect(screen.getByTestId('Notification-error')).toBeInTheDocument();
    });
  });

  it('submits add flow for BP', async () => {
    addNewEmployeeMock.mockResolvedValueOnce({
      status: requestStatus.success,
      employeeId: 'new-employee',
    });
    getDetailsFromTokenMock.mockReturnValue({ isBusinessPayManager: true });

    renderComponent();

    fireEvent.click(screen.getByTestId('Submit-Employee-Details'));

    await waitFor(() => {
      expect(addNewEmployeeMock).toHaveBeenCalled();
    });

    expect(setAddEmployeeAnalyticsData).toHaveBeenCalledWith(
      expect.objectContaining({
        addEmployee: true,
        employee: 'individual',
      })
    );
  });

  it('shows "add multiple" link when companyType is not BUSINESS_PAY', async () => {
    renderComponent({ companyType: 'BB' });
    expect(screen.getByText('userMgmt.employee.add.multiple.label')).toBeInTheDocument();
  });

  it('does not show "add multiple" link when companyType is BUSINESS_PAY', async () => {
    renderComponent({ companyType: 'BP' });
    expect(screen.queryByText('userMgmt.employee.add.multiple.label')).not.toBeInTheDocument();
  });
});
