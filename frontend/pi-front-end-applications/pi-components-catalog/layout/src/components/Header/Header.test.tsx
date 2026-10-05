import { screen, waitFor, fireEvent, act } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { useRouter } from 'next/navigation';
import React from 'react';

import { mockUseTranslation, render, userEvent } from '../../utils/test-utils';
import Header from './Header.component';

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(() => ({
    push: jest.fn(),
    refresh: jest.fn(),
  })),
  usePathname: jest.fn(),
  useSearchParams: () => {
    return mockSearchParams;
  },
  useParams: () => {
    return mockUseParams;
  },
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  appPreCheck: jest.fn(),
}));

const mockValidateRoomOccupancyConditions = true;

const mockProps = {
  logoUrl: '/etc/clientlibs/pi-header/resources/images/pi-refresh-logo.svg',
  companyLabel: 'My Company',
  accountName: { firstName: 'John', lastName: 'Doe' },
  languages: [
    {
      code: 'gb',
      language: 'English',
      flagUrl: '/etc/clientlibs/pi-header/resources/images/british-round.svg',
      url: '/gb/en/inn-business/home.html',
    },
    {
      code: 'de',
      language: 'Deutsch',
      flagUrl: '/etc/clientlibs/pi-header/resources/images/germany-round.svg',
      url: '/de/de/inn-business/home.html',
    },
  ],
  userRole: 'SUPER',
  searchFormLabels: {
    where: 'Where to?',
    whereIcon: '/content/dam/global/icons/common/location.svg',
    calendarIcon: '/content/dam/global/icons/common/calendar.svg',
    guestIcon: '/content/dam/global/icons/common/person.svg',
    datePicker: {
      months: [
        'January',
        'February',
        'March',
        'April',
        'May',
        'June',
        'July',
        'August',
        'September',
        'October',
        'November',
        'December',
      ],
      weekdaysShort: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    },
  },
  calendarIcons: {},
  addRoomIcon: '',
  icons: {},
  globalLabels: {},
  userDetails: { bookingPreference: {} },
  searchRules: {
    globalConfig: {
      maxRoomsLim: {
        maxRooms: 9,
      },
    },
    maxNightsLimitation: {
      maxNights: 14,
    },
  },
  secureUrl: 'https://secure.example.com',
  onSearchButtonClick: jest.fn(),
};
const mockUseParams = { slug: ['england', 'greater-london', 'london', 'london-euston.html'] };

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

const mockSearchParams = {
  forEach: jest.fn((callback) => {
    const params = {
      ARRdd: '25',
      ARRmm: '09',
      ARRyyyy: '2024',
      NIGHTS: '1',
      ROOMS: '1',
      ADULT1: '1',
      CHILD1: '0',
      COT1: '0',
      INTTYP1: 'DB',
    };
    Object.keys(params).forEach((key) => {
      callback(params[key], key);
    });
  }),
  get: (key) => key,
};

const mockGetMultiSearchParamsIB = {
  arrival: '2024-09-25',
  departure: '2024-09-26',
  numberOfUnits: 1,
  numberOfNights: 1,
  rooms: [
    {
      adults: 1,
      children: 0,
      roomType: 'Double',
      shouldIncludeCot: false,
    },
  ],
  cellCodes: '',
};
const mockStaticHotelInformation = { name: 'London Euston' };

window.scroll = jest.fn();

// Mock function that can be overridden in tests
const mockGetLocaleByPathname = jest.fn(() => LOCALES.EN);

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    getLocaleByPathname: () => mockGetLocaleByPathname(),
    getInitials: serverUtils.getInitials,
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    RolesRequired: serverUtils.RolesRequired,
    cn: jest.fn(),
    getQueryParams: jest.fn(),
    getMultiSearchParamsIB: () => {
      return mockGetMultiSearchParamsIB;
    },
    staticHotelInformationIB: () => {
      return mockStaticHotelInformation;
    },
    useTranslation: mockUseTranslation,
    updateSearchParamsIfError: () => ({ shouldUpdate: false }),
    validateRoomOccupancyConditions: () => mockValidateRoomOccupancyConditions,
    getDaysInMonth: jest.fn(),
  };
});

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');

  return {
    ...utils,
    useCookieWatcher: jest.fn(() => false),
    useFeatureToggle: jest.fn(() => ({
      release_ib_auth: true,
    })),
  };
});

jest.mock('@whitbread-eos/atoms/ui', () => {
  // eslint-disable-next-line @typescript-eslint/no-require-imports
  const React = require('react');
  const actual = jest.requireActual('@whitbread-eos/atoms/ui');
  return {
    ...actual,
    Menu: (props: any) => <div data-testid="Menu">{props.children}</div>,
    DropdownMenuItem: (props: any) => (
      <div
        data-testid="DropdownMenuItem"
        tabIndex={0}
        role="menuitem"
        onClick={props.onSelect}
        {...props}
      >
        {props.children}
      </div>
    ),
    DropdownMenu: ({ open, onOpenChange, children }: any) => {
      const [isOpen, setIsOpen] = React.useState(false);
      const actualOpen = typeof open === 'boolean' ? open : isOpen;
      const handleOpenChange = (next: boolean) => {
        if (onOpenChange) onOpenChange(next);
        if (typeof open !== 'boolean') setIsOpen(next);
      };
      return (
        <>
          {React.Children.map(children, (child: any) => {
            if (!React.isValidElement(child)) return child;
            if (child.type.displayName === 'DropdownMenuTrigger') {
              return React.cloneElement(child, {
                onClick: (e: any) => {
                  handleOpenChange(!actualOpen);
                  if (child.props.onClick) child.props.onClick(e);
                },
                type: 'button',
                'aria-expanded': actualOpen,
                'data-state': actualOpen ? 'open' : 'closed',
              });
            }
            if (child.type.displayName === 'DropdownMenuContent') {
              return actualOpen ? child : null;
            }
            return child;
          })}
        </>
      );
    },
    DropdownMenuTrigger: Object.assign(
      ({ children, ...props }: any) => (
        <button type="button" {...props}>
          {children}
        </button>
      ),
      { displayName: 'DropdownMenuTrigger' }
    ),
    DropdownMenuContent: Object.assign(
      ({ children, ...props }: any) => (
        <div data-testid="Account-Menu-Dropdown" {...props}>
          {children}
        </div>
      ),
      { displayName: 'DropdownMenuContent' }
    ),
  };
});

jest.mock('./Search', () => {
  const MockSearch = () => <div data-testid="IB-Search-Container-Desktop" />;
  MockSearch.displayName = 'MockSearch';
  return MockSearch;
});
jest.mock('./LanguageMenu', () => {
  const MockLanguageMenu = () => <div data-testid="LanguageMenu" />;
  MockLanguageMenu.displayName = 'MockLanguageMenu';
  return MockLanguageMenu;
});
jest.mock('./Logout', () => {
  const MockLogout = () => <div data-testid="Logout" />;
  MockLogout.displayName = 'MockLogout';
  return MockLogout;
});
jest.mock('./BusinessSteps', () => {
  const MockBusinessSteps = () => <div data-testid="BusinessSteps" />;
  MockBusinessSteps.displayName = 'MockBusinessSteps';
  return MockBusinessSteps;
});
jest.mock(
  'business-booker/src/components/innBusiness/ExistingAccountModal/ExistingAccountModal',
  () => {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const React = require('react');
    type ExistingAccountModalProps = { isModalOpen: boolean; onClose?: () => void };
    const MockExistingAccountModal = ({ isModalOpen, onClose }: ExistingAccountModalProps) => {
      React.useEffect(() => {
        if (!isModalOpen) return;
        const handler = (e: KeyboardEvent) => {
          if (e.key === 'Escape' && onClose) onClose();
        };
        window.addEventListener('keydown', handler);
        return () => window.removeEventListener('keydown', handler);
      }, [isModalOpen, onClose]);
      return isModalOpen ? <div data-testid="ExistingAccountModal" /> : null;
    };
    MockExistingAccountModal.displayName = 'MockExistingAccountModal';
    return MockExistingAccountModal;
  }
);
jest.mock('business-booker/src/components/innBusiness/InnBLink/inn-b-link', () => {
  type InnBLinkProps = React.AnchorHTMLAttributes<HTMLAnchorElement> & {
    children?: React.ReactNode;
  };
  function MockInnBLink({ children, ...props }: InnBLinkProps) {
    return <a {...props}>{children}</a>;
  }
  MockInnBLink.displayName = 'MockInnBLink';
  return MockInnBLink;
});

jest.mock('business-booker/src/components/innBusiness/LinkAccountButton', () => ({
  LinkAccountButton: ({ 'data-testid': dataTestId, className }: any) => (
    <button data-testid={dataTestId} className={className}>
      cardMgmt.linkAccountBanner.linkAccountButton
    </button>
  ),
}));

describe('Header Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.userRole = 'SUPER';
    mockProps.globalLabels = {};
  });
  const mockRouter = { push: jest.fn() };

  it('should render the logo correctly', () => {
    (useRouter as jest.Mock).mockReturnValue(mockRouter);
    const { getByTestId } = render(<Header {...mockProps} />);
    const logo = getByTestId('IB-Logo');
    expect(logo).toBeInTheDocument();
  });

  it('should render the company label correctly', () => {
    (useRouter as jest.Mock).mockReturnValue(mockRouter);
    const { getByTestId } = render(<Header {...mockProps} />);
    const companyLabel = getByTestId('Company-Name');
    expect(companyLabel).toBeInTheDocument();
  });

  it('should render the account initials button', () => {
    (useRouter as jest.Mock).mockReturnValue(mockRouter);
    const { getAllByRole } = render(<Header {...mockProps} />);
    const initialsButton = getAllByRole('button', { name: 'JD' });
    expect(initialsButton[0]).toBeInTheDocument();
  });

  it('should render Header without search component', () => {
    mockProps.userRole = 'STAYER';
    const { queryByTestId } = render(<Header {...mockProps} />);
    expect(queryByTestId('IB-Search-Container-Desktop')).not.toBeInTheDocument();
  });

  it('should render Header with search component', () => {
    (useRouter as jest.Mock).mockReturnValue(mockRouter);

    mockProps.userRole = 'SUPER';
    const { queryByTestId } = render(<Header {...mockProps} />);
    expect(queryByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();
  });

  it('should render Header with search component with global labels', () => {
    (useRouter as jest.Mock).mockReturnValue(mockRouter);

    mockProps.globalLabels = { addRoomIcon: '/' };
    const { queryByTestId } = render(<Header {...mockProps} />);
    expect(queryByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();
  });

  it('should render Header with searchFormLabels undefined', () => {
    (useRouter as jest.Mock).mockReturnValue(mockRouter);

    mockProps.searchFormLabels = undefined;
    const { queryByTestId } = render(<Header {...mockProps} />);
    expect(queryByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();
  });

  it('should render Header with languages undefined', () => {
    (useRouter as jest.Mock).mockReturnValue(mockRouter);

    mockProps.languages = undefined;
    const { queryByTestId } = render(<Header {...mockProps} />);
    expect(queryByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();
  });

  it('opens Account-Menu-Dropdown and handles LinkAccount-Button and profile link actions', async () => {
    (useRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getByTestId, queryByTestId } = render(<Header {...mockProps} isTethered={false} />);
    await waitFor(() => expect(getByTestId('Account-Menu-Button')).toBeInTheDocument());
    await userEvent.click(getByTestId('Account-Menu-Button'));

    await waitFor(() => {
      expect(getByTestId('Account-Menu-Dropdown')).toBeInTheDocument();
    });

    // Click the DropdownMenuItem to open the modal
    const dropdownItems = screen.getAllByTestId('DropdownMenuItem');
    await userEvent.click(dropdownItems[0]);

    // Wait for modal to appear
    await waitFor(() => {
      const modal = queryByTestId('ExistingAccountModal');
      expect(modal).toBeInTheDocument();
    });

    // Simulate Escape key to close modal
    // Fire the event on the window to match the mock's event listener
    await act(async () => {
      fireEvent.keyDown(window, { key: 'Escape', code: 'Escape', keyCode: 27, charCode: 27 });
    });
    await waitFor(() => {
      expect(screen.queryByTestId('ExistingAccountModal')).not.toBeInTheDocument();
    });

    // Open dropdown again
    await userEvent.click(getByTestId('Account-Menu-Button'));
    await waitFor(() => {
      expect(getByTestId('Account-Menu-Dropdown')).toBeVisible();
    });

    // Click profile link
    await userEvent.click(getByTestId('Account-Profile-Link'));
    expect(mockRouter.push).toHaveBeenCalledWith(expect.stringContaining('profile'));
  });

  it('closes the profile dropdown when Logout is clicked', async () => {
    (useRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getByTestId } = render(<Header {...mockProps} />);
    // Open the dropdown
    await waitFor(() => expect(getByTestId('Account-Menu-Button')).toBeInTheDocument());
    await userEvent.click(getByTestId('Account-Menu-Button'));
    await waitFor(() => expect(getByTestId('Account-Menu-Dropdown')).toBeInTheDocument());
  });

  describe('Link Account Button Visibility', () => {
    beforeEach(() => {
      jest.clearAllMocks();
      mockGetLocaleByPathname.mockReturnValue(LOCALES.EN);
      (useRouter as jest.Mock).mockReturnValue(mockRouter);
    });

    it('should SHOW Link Account button when locale is English and isTethered is false', async () => {
      mockGetLocaleByPathname.mockReturnValue(LOCALES.EN);

      const { getByTestId, queryByTestId } = render(<Header {...mockProps} isTethered={false} />);

      // Open the dropdown menu
      await waitFor(() => expect(getByTestId('Account-Menu-Button')).toBeInTheDocument());
      await userEvent.click(getByTestId('Account-Menu-Button'));

      await waitFor(() => {
        expect(getByTestId('Account-Menu-Dropdown')).toBeInTheDocument();
      });

      // Verify Link Account button is present
      const linkAccountButton = queryByTestId('LinkAccount-Button');
      expect(linkAccountButton).toBeInTheDocument();
    });

    it('should HIDE Link Account button when locale is German (even if isTethered is false)', async () => {
      mockGetLocaleByPathname.mockReturnValue(LOCALES.DE);

      const { getByTestId, queryByTestId } = render(<Header {...mockProps} isTethered={false} />);

      // Open the dropdown menu
      await waitFor(() => expect(getByTestId('Account-Menu-Button')).toBeInTheDocument());
      await userEvent.click(getByTestId('Account-Menu-Button'));

      await waitFor(() => {
        expect(getByTestId('Account-Menu-Dropdown')).toBeInTheDocument();
      });

      // Verify Link Account button is NOT present
      const linkAccountButton = queryByTestId('LinkAccount-Button');
      expect(linkAccountButton).not.toBeInTheDocument();
    });

    it('should HIDE Link Account button when isTethered is true (even if locale is English)', async () => {
      mockGetLocaleByPathname.mockReturnValue(LOCALES.EN);

      const { getByTestId, queryByTestId } = render(<Header {...mockProps} isTethered={true} />);

      // Open the dropdown menu
      await waitFor(() => expect(getByTestId('Account-Menu-Button')).toBeInTheDocument());
      await userEvent.click(getByTestId('Account-Menu-Button'));

      await waitFor(() => {
        expect(getByTestId('Account-Menu-Dropdown')).toBeInTheDocument();
      });

      // Verify Link Account button is NOT present
      const linkAccountButton = queryByTestId('LinkAccount-Button');
      expect(linkAccountButton).not.toBeInTheDocument();
    });

    it('should HIDE Link Account button when locale is German AND isTethered is true', async () => {
      mockGetLocaleByPathname.mockReturnValue(LOCALES.DE);

      const { getByTestId, queryByTestId } = render(<Header {...mockProps} isTethered={true} />);

      // Open the dropdown menu
      await waitFor(() => expect(getByTestId('Account-Menu-Button')).toBeInTheDocument());
      await userEvent.click(getByTestId('Account-Menu-Button'));

      await waitFor(() => {
        expect(getByTestId('Account-Menu-Dropdown')).toBeInTheDocument();
      });

      // Verify Link Account button is NOT present
      const linkAccountButton = queryByTestId('LinkAccount-Button');
      expect(linkAccountButton).not.toBeInTheDocument();
    });

    it('should open ExistingAccountModal when Link Account button is clicked', async () => {
      mockGetLocaleByPathname.mockReturnValue(LOCALES.EN);

      const { getByTestId, queryByTestId } = render(<Header {...mockProps} isTethered={false} />);

      // Open the dropdown menu
      await waitFor(() => expect(getByTestId('Account-Menu-Button')).toBeInTheDocument());
      await userEvent.click(getByTestId('Account-Menu-Button'));

      await waitFor(() => {
        expect(getByTestId('Account-Menu-Dropdown')).toBeInTheDocument();
      });

      // Click the first DropdownMenuItem (Link Account button)
      const dropdownItems = screen.getAllByTestId('DropdownMenuItem');
      await userEvent.click(dropdownItems[0]);

      // Verify modal opens
      await waitFor(() => {
        expect(queryByTestId('ExistingAccountModal')).toBeInTheDocument();
      });
    });

    it('should display correct translation text for Link Account button', async () => {
      mockGetLocaleByPathname.mockReturnValue(LOCALES.EN);

      const { getByTestId } = render(<Header {...mockProps} isTethered={false} />);

      // Open the dropdown menu
      await waitFor(() => expect(getByTestId('Account-Menu-Button')).toBeInTheDocument());
      await userEvent.click(getByTestId('Account-Menu-Button'));

      await waitFor(() => {
        expect(getByTestId('Account-Menu-Dropdown')).toBeInTheDocument();
      });

      // Verify the button has the correct text
      const linkAccountButton = getByTestId('LinkAccount-Button');
      expect(linkAccountButton).toHaveTextContent('cardMgmt.linkAccountBanner.linkAccountButton');
    });

    it('should still show Profile and Logout options when Link Account button is hidden (German locale)', async () => {
      mockGetLocaleByPathname.mockReturnValue(LOCALES.DE);

      const { getByTestId, queryByTestId } = render(<Header {...mockProps} isTethered={false} />);

      // Open the dropdown menu
      await waitFor(() => expect(getByTestId('Account-Menu-Button')).toBeInTheDocument());
      await userEvent.click(getByTestId('Account-Menu-Button'));

      await waitFor(() => {
        expect(getByTestId('Account-Menu-Dropdown')).toBeInTheDocument();
      });

      // Verify Link Account button is NOT present
      expect(queryByTestId('LinkAccount-Button')).not.toBeInTheDocument();

      // Verify Profile link IS present
      expect(getByTestId('Account-Profile-Link')).toBeInTheDocument();

      // Verify Logout IS present
      expect(getByTestId('Logout')).toBeInTheDocument();
    });

    it('should still show Profile and Logout options when Link Account button is hidden (tethered account)', async () => {
      mockGetLocaleByPathname.mockReturnValue(LOCALES.EN);

      const { getByTestId, queryByTestId } = render(<Header {...mockProps} isTethered={true} />);

      // Open the dropdown menu
      await waitFor(() => expect(getByTestId('Account-Menu-Button')).toBeInTheDocument());
      await userEvent.click(getByTestId('Account-Menu-Button'));

      await waitFor(() => {
        expect(getByTestId('Account-Menu-Dropdown')).toBeInTheDocument();
      });

      // Verify Link Account button is NOT present
      expect(queryByTestId('LinkAccount-Button')).not.toBeInTheDocument();

      // Verify Profile link IS present
      expect(getByTestId('Account-Profile-Link')).toBeInTheDocument();

      // Verify Logout IS present
      expect(getByTestId('Logout')).toBeInTheDocument();
    });
  });
});
