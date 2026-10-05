import { within } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { mockUseTranslation, render } from '../../../utils/test-utils';
import SidebarMobile from './SidebarMobile.component';

const mockMenuLabels = {
  bookings: {
    label: 'Bookings',
    icon: '/content/dam/global/icons/common/suitcase.svg',
    iconActive: '/content/dam/global/icons/common/suitcase-solid.svg',
  },
  spending: {
    label: 'Spending',
    icon: '/content/dam/global/icons/common/chart-bars.svg',
    iconActive: '/content/dam/global/icons/common/chart-bars-solid.svg',
  },
  home: {
    label: 'Home',
    icon: '/content/dam/global/icons/common/home.svg',
    iconActive: '/content/dam/global/icons/common/home-solid.svg',
  },
  manage: {
    label: 'Manage',
    icon: '/content/dam/global/icons/common/chart-pie.svg',
    iconActive: '/content/dam/global/icons/common/chart-pie-solid.svg',
    options: {},
  },
};

jest.mock('next/navigation', () => ({
  usePathname: () => {
    return '/en-gb/manage';
  },
}));
const mockProps = {
  userRole: 'SUPER',
  isAccountHolder: true,
  isTethered: true,
  isCardHolder: true,
  menuLabels: mockMenuLabels,
  icons: {},
  collapseIcon: undefined,
  expandIcon: undefined,
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(() => ({
    ['release_ib_spending']: true,
    ['release_ib_user_management']: true,
    ['release_ib_company_management']: true,
    ['release_ib_card_management']: true,
  })),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');
  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: (url) => {
      return url;
    },
    RolesRequired: serverUtils.RolesRequired,
    useTranslation: jest.fn(() => mockUseTranslation()), // Properly mock the function
    getPathForLocale: jest.fn(() => '/en'),
    getAvailableTabs: jest.fn((isTravelManager, isTethered, isAccountHolder) => {
      if (!isTravelManager && !isAccountHolder) {
        return [];
      }

      if (isTravelManager) {
        if (isTethered && !isAccountHolder) {
          return ['innbusiness'];
        }

        return ['innbusiness', 'innbusiness-pay'];
      }

      return ['innbusiness-pay'];
    }),
  };
});

describe('SidebarMobile Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render SidebarMobile component with Home link', () => {
    const { getByTestId } = render(<SidebarMobile {...mockProps} />);
    expect(getByTestId('Home-Sidebar-Link')).toBeInTheDocument();
  });

  it('should render SidebarMobile component with access to Manage link', () => {
    const { getByTestId } = render(<SidebarMobile {...mockProps} />);
    expect(getByTestId('Manage-Sidebar-Link')).toBeInTheDocument();
  });

  it('should render SidebarMobile component with Bookings link', () => {
    const { getByTestId } = render(<SidebarMobile {...mockProps} />);
    expect(getByTestId('Bookings-Sidebar-Link')).toBeInTheDocument();
  });

  it('should render SidebarMobile component with Spending link', () => {
    const { getByTestId } = render(<SidebarMobile {...mockProps} />);
    expect(getByTestId('Spending-Sidebar-Link')).toBeInTheDocument();
  });

  it('should render the correct icons for each menu item', () => {
    const { getByTestId } = render(<SidebarMobile {...mockProps} />);

    const homeLink = getByTestId('Home-Sidebar-Link');
    const homeIcon = within(homeLink).getByRole('img', { hidden: true });
    expect(homeIcon).toHaveAttribute('src', expect.stringContaining(mockMenuLabels.home.icon));
    expect(homeIcon).toHaveAttribute('alt', '');
    expect(homeIcon).toHaveAttribute('aria-hidden', 'true');

    const manageLink = getByTestId('Manage-Sidebar-Link');
    const manageIcon = within(manageLink).getByRole('img', { hidden: true });
    expect(manageIcon).toHaveAttribute(
      'src',
      expect.stringContaining(mockMenuLabels.manage.iconActive)
    );
    expect(manageIcon).toHaveAttribute('alt', '');
    expect(manageIcon).toHaveAttribute('aria-hidden', 'true');

    const spendingLink = getByTestId('Spending-Sidebar-Link');
    const spendingIcon = within(spendingLink).getByRole('img', { hidden: true });
    expect(spendingIcon).toHaveAttribute(
      'src',
      expect.stringContaining(mockMenuLabels.spending.icon)
    );
    expect(spendingIcon).toHaveAttribute('alt', '');
    expect(spendingIcon).toHaveAttribute('aria-hidden', 'true');
  });

  it('should render active icons when a menu item is active', () => {
    const { getByTestId } = render(<SidebarMobile {...mockProps} />);

    const manageLink = getByTestId('Manage-Sidebar-Link');
    const manageIcon = within(manageLink).getByRole('img', { hidden: true });

    expect(manageIcon).toHaveAttribute(
      'src',
      expect.stringContaining(mockMenuLabels.manage.iconActive)
    );
    expect(manageIcon).toHaveAttribute('alt', '');
    expect(manageIcon).toHaveAttribute('aria-hidden', 'true');
  });
});
