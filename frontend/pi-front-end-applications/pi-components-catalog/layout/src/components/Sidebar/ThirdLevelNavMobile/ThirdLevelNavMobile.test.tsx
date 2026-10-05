import { LOCALES } from '@whitbread-eos/api';
import { useSearchParams } from 'next/navigation';
import React from 'react';

import { render, mockUseTranslation, act, fireEvent } from '../../../utils/test-utils';
import ThirdLevelNavMobile from './ThirdLevelNavMobile.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getPathForLocale: (locale, path) => {
    return `/${locale}/${path}`;
  },
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: mockUseTranslation,
    MANAGE_TABS: serverUtils.MANAGE_TABS,
  };
});

jest.mock('next/navigation', () => {
  const actualNavigation = jest.requireActual('next/navigation');
  return {
    ...actualNavigation,
    usePathname: jest.fn(() => 'manage/cards'),
    useSearchParams: jest.fn(() => ({
      get: jest.fn(),
    })),
    useRouter: jest.fn(() => ({
      push: jest.fn(),
    })),
  };
});

const getDefaultMockProps = () => ({
  icons: {
    someKey: 'icon-path',
  },
  toggleSecondLevel: () => {
    return;
  },
  activeSecondLevelKey: 'someKey',
  activeFirstLevelKey: 'manage',
  isTravelManager: true,
  isGuestUser: false,
  isYourSpendingTabEnabled: true,
  availableManageEmployeesTabs: ['inn-business-pay', 'manage'],
  availableSpendingTabs: ['company', 'innbusiness-pay'],
  availableCardsTabs: ['innbusiness', 'innbusiness-pay'],
});

describe('ThirdLevelNavMobile Component', () => {
  let mockProps: ReturnType<typeof getDefaultMockProps>;

  beforeEach(() => {
    jest.clearAllMocks();
    mockProps = getDefaultMockProps();
    (useSearchParams as jest.Mock).mockReturnValue({
      get: (key) => key,
    });
  });

  it('should render ThirdLevelNavMobile component with centrally-stored url', () => {
    (useSearchParams as jest.Mock).mockReturnValue({
      get: (key) => {
        if (key === 'tab') return 'innbusiness';
      },
    });
    const { getByTestId, getByText } = render(<ThirdLevelNavMobile {...mockProps} />);
    expect(getByTestId('Mobile-Nav-Third-Level-Container')).toBeInTheDocument();
    expect(getByText('cards.cardMgmt.tabs.centrallyStored')).toBeInTheDocument();
  });

  it('should render ThirdLevelNavMobile component with innbusiness-pay url', () => {
    (useSearchParams as jest.Mock).mockReturnValue({
      get: (key) => {
        if (key === 'tab') return 'innbusiness-pay';
      },
    });
    const { getByTestId, getByText } = render(<ThirdLevelNavMobile {...mockProps} />);
    expect(getByTestId('Mobile-Nav-Third-Level-Container')).toBeInTheDocument();
    expect(getByText('cards.cardMgmt.tabs.innBusinessPay')).toBeInTheDocument();
  });

  it('should render ThirdLevelNavMobile component with innbusiness-pay url from path', () => {
    (useSearchParams as jest.Mock).mockReturnValue({
      get: (key) => {
        if (key === 'tab') return 'innbusiness-pay';
      },
    });
    mockProps.activeSecondLevelKey = 'manage/cards/innbusiness-pay';
    const { getByTestId, getByText } = render(<ThirdLevelNavMobile {...mockProps} />);
    expect(getByTestId('Mobile-Nav-Third-Level-Container')).toBeInTheDocument();
    expect(getByText('cards.cardMgmt.tabs.innBusinessPay')).toBeInTheDocument();
  });

  it('should render ThirdLevelNavMobile component with default tab when no tab is provided', () => {
    (useSearchParams as jest.Mock).mockReturnValue({
      get: jest.fn(() => null),
    });
    const { getByTestId } = render(<ThirdLevelNavMobile {...mockProps} />);
    expect(getByTestId('Mobile-Nav-Third-Level-Container')).toBeInTheDocument();
  });

  it('should handle missing icons gracefully', () => {
    const propsWithoutIcons = { ...mockProps, icons: {} };
    const { getByTestId } = render(<ThirdLevelNavMobile {...propsWithoutIcons} />);
    expect(getByTestId('Mobile-Nav-Third-Level-Container')).toBeInTheDocument();
  });

  it('should display the correct tabs for manage employees page', async () => {
    jest.mock('next/navigation', () => {
      const actualNavigation = jest.requireActual('next/navigation');
      return {
        ...actualNavigation,
        usePathname: jest.fn(() => '/en-gb/manage/employees'),
      };
    });
    mockProps.activeSecondLevelKey = 'manage/employees';
    mockProps.activeFirstLevelKey = 'manage';
    const mockRouterPush = jest.fn();
    (jest.requireMock('next/navigation').useRouter as jest.Mock).mockReturnValue({
      push: mockRouterPush,
    });

    const { getByTestId } = render(<ThirdLevelNavMobile {...mockProps} />);
    const expandButton = getByTestId('Third-Level-Expand-Button');
    await act(async () => {
      fireEvent.click(expandButton);
    });

    const IBPayLink = getByTestId('Manage-Employees-Inn-Business-Pay-Sidebar-Link');
    const manage = getByTestId('Manage-Employees-Inn-Business-Sidebar-Link');
    expect(manage).toBeInTheDocument();
    expect(IBPayLink).toBeInTheDocument();
  });

  it('should display the correct tabs for spending page', async () => {
    mockProps.activeSecondLevelKey = 'spending';
    mockProps.activeFirstLevelKey = 'manage';
    const mockRouterPush = jest.fn();
    (jest.requireMock('next/navigation').useRouter as jest.Mock).mockReturnValue({
      push: mockRouterPush,
    });

    const { getByTestId } = render(<ThirdLevelNavMobile {...mockProps} />);
    const expandButton = getByTestId('Third-Level-Expand-Button');
    await act(async () => {
      fireEvent.click(expandButton);
    });

    const company = getByTestId('Spending-Company-Sidebar-Link');
    const innbusinessPay = getByTestId('Spending-InnBusiness-Pay-Sidebar-Link');
    expect(company).toBeInTheDocument();
    expect(innbusinessPay).toBeInTheDocument();
  });

  it('should navigate to spending page and close the menu when activeSecondLevelKey is in spendingSecondLevelLinks', async () => {
    mockProps.activeSecondLevelKey = 'spending/management-information-report';
    const mockRouterPush = jest.fn();
    const mockToggleSecondLevel = jest.fn();

    (jest.requireMock('next/navigation').useRouter as jest.Mock).mockReturnValue({
      push: mockRouterPush,
    });

    const { getByTestId } = render(
      <ThirdLevelNavMobile {...mockProps} toggleSecondLevel={mockToggleSecondLevel} />
    );

    const expandButton = getByTestId('Third-Level-Expand-Button');
    await act(async () => {
      fireEvent.click(expandButton);
    });

    const backButton = getByTestId('backButton');
    fireEvent.click(backButton);

    expect(mockRouterPush).toHaveBeenCalledWith('/en-gb/spending');
    expect(mockToggleSecondLevel).not.toHaveBeenCalled();
  });

  it('should close the menu and call toggleSecondLevel when activeSecondLevelKey is not in spendingSecondLevelLinks', async () => {
    mockProps.activeSecondLevelKey = 'manage/employees';
    const mockToggleSecondLevel = jest.fn();
    const mockRouterPush = jest.fn();

    const { getByTestId } = render(
      <ThirdLevelNavMobile {...mockProps} toggleSecondLevel={mockToggleSecondLevel} />
    );

    const expandButton = getByTestId('Third-Level-Expand-Button');
    await act(async () => {
      fireEvent.click(expandButton);
    });

    const backButton = getByTestId('backButton');
    fireEvent.click(backButton);

    expect(mockToggleSecondLevel).toHaveBeenCalled();
    expect(mockRouterPush).not.toHaveBeenCalled();
  });

  it('should display the correct label for spending/management-information-report page', () => {
    mockProps.activeSecondLevelKey = 'spending/management-information-report';
    mockProps.activeFirstLevelKey = 'spending';

    const { getByTestId, getByText } = render(<ThirdLevelNavMobile {...mockProps} />);
    const container = getByTestId('Mobile-Nav-Third-Level-Container');

    expect(container).toBeInTheDocument();
    expect(
      getByText('layout.innbusinessLayout.menu.spending.options.managementReport')
    ).toBeInTheDocument();
  });

  it('should navigate to spending page and close the menu when activeSecondLevelKey is in spendingSecondLevelLinks', async () => {
    mockProps.activeSecondLevelKey = 'spending/statements';
    const mockRouterPush = jest.fn();
    const mockToggleSecondLevel = jest.fn();

    (jest.requireMock('next/navigation').useRouter as jest.Mock).mockReturnValue({
      push: mockRouterPush,
    });

    const { getByTestId } = render(
      <ThirdLevelNavMobile
        {...mockProps}
        toggleSecondLevel={mockToggleSecondLevel}
        isStatementsPage={true}
      />
    );

    const expandButton = getByTestId('Third-Level-Expand-Button');
    await act(async () => {
      fireEvent.click(expandButton);
    });

    const backButton = getByTestId('backButton');
    fireEvent.click(backButton);

    expect(mockRouterPush).toHaveBeenCalledWith(
      '/en-gb/spending?tab=innbusiness-pay&account=account'
    );
    expect(mockToggleSecondLevel).not.toHaveBeenCalled();
  });

  it('should render empty when not travel manager on spending page and isYourSpendingTabEnabled is false', () => {
    mockProps.activeFirstLevelKey = 'spending';
    mockProps.isTravelManager = false;
    mockProps.isYourSpendingTabEnabled = false;
    const { container } = render(<ThirdLevelNavMobile {...mockProps} />);
    expect(container.querySelector('[data-testid="Mobile-Nav-Third-Level-Container"]')).toBeNull();
  });

  it('should render empty when is guest user on spending page and isYourSpendingTabEnabled is true', () => {
    mockProps.activeFirstLevelKey = 'spending';
    mockProps.isGuestUser = true;
    mockProps.isYourSpendingTabEnabled = true;
    const { container } = render(<ThirdLevelNavMobile {...mockProps} />);
    expect(container.querySelector('[data-testid="Mobile-Nav-Third-Level-Container"]')).toBeNull();
  });

  it('should render empty when availableManageEmployeesTabs is empty', () => {
    mockProps.activeSecondLevelKey = 'manage/employees';
    mockProps.availableManageEmployeesTabs = [];

    const { container } = render(<ThirdLevelNavMobile {...mockProps} />);
    expect(container.querySelector('[data-testid="Mobile-Nav-Third-Level-Container"]')).toBeNull();
  });

  it('should render empty when availableCardsTabs is empty', () => {
    mockProps.activeSecondLevelKey = 'manage/cards';
    mockProps.activeFirstLevelKey = 'manage';
    mockProps.availableCardsTabs = [];

    const { container } = render(<ThirdLevelNavMobile {...mockProps} />);
    expect(container.querySelector('[data-testid="Mobile-Nav-Third-Level-Container"]')).toBeNull();
  });

  it('should display correct label for spending/company page', () => {
    mockProps.activeFirstLevelKey = 'spending';
    mockProps.activeSecondLevelKey = 'spending';
    mockProps.isTravelManager = true; // Reset to true
    (useSearchParams as jest.Mock).mockReturnValue({
      get: (key) => {
        if (key === 'tab') return 'company';
        return null;
      },
    });

    const { getByTestId, getByText } = render(<ThirdLevelNavMobile {...mockProps} />);
    const container = getByTestId('Mobile-Nav-Third-Level-Container');

    expect(container).toBeInTheDocument();
    expect(
      getByText('layout.innbusinessLayout.menu.spending.options.companySpending')
    ).toBeInTheDocument();
  });

  it('should navigate to spending page when activeSecondLevelKey is transactions', async () => {
    mockProps.activeSecondLevelKey = 'spending/transactions';
    mockProps.activeFirstLevelKey = 'spending';
    mockProps.isTravelManager = true;
    const mockRouterPush = jest.fn();
    const mockToggleSecondLevel = jest.fn();

    (jest.requireMock('next/navigation').useRouter as jest.Mock).mockReturnValue({
      push: mockRouterPush,
    });

    const { getByTestId } = render(
      <ThirdLevelNavMobile
        {...mockProps}
        toggleSecondLevel={mockToggleSecondLevel}
        isTransactionsPage={true}
      />
    );

    const expandButton = getByTestId('Third-Level-Expand-Button');
    await act(async () => {
      fireEvent.click(expandButton);
    });

    const backButton = getByTestId('backButton');
    fireEvent.click(backButton);

    expect(mockRouterPush).toHaveBeenCalledWith(
      '/en-gb/spending?tab=innbusiness-pay&account=account'
    );
    expect(mockToggleSecondLevel).not.toHaveBeenCalled();
  });

  it('should display the correct label for transactions page', () => {
    mockProps.activeSecondLevelKey = 'spending/transactions';
    mockProps.activeFirstLevelKey = 'spending';
    mockProps.isTravelManager = true;

    const { getByTestId, getByText } = render(
      <ThirdLevelNavMobile {...mockProps} isTransactionsPage={true} />
    );
    const container = getByTestId('Mobile-Nav-Third-Level-Container');

    expect(container).toBeInTheDocument();
    expect(getByText('spending.spending.transactions.title')).toBeInTheDocument();
  });

  it('should render correct label for manage page without cards pathname', () => {
    mockProps.activeFirstLevelKey = 'manage';
    mockProps.activeSecondLevelKey = 'manage/allowances';
    (jest.requireMock('next/navigation').usePathname as jest.Mock).mockReturnValue(
      '/en-gb/manage/allowances'
    );

    const { getByTestId, getByText } = render(<ThirdLevelNavMobile {...mockProps} />);
    const container = getByTestId('Mobile-Nav-Third-Level-Container');

    expect(container).toBeInTheDocument();
    expect(getByText('common.layout.menu.manage.label')).toBeInTheDocument();
  });

  it('should display Your-spending tab when feature flag is enabled and user is travel manager', async () => {
    mockProps.activeSecondLevelKey = 'spending';
    mockProps.activeFirstLevelKey = 'spending';
    mockProps.isTravelManager = true;
    mockProps.isYourSpendingTabEnabled = true;

    const { getByTestId } = render(<ThirdLevelNavMobile {...mockProps} />);
    const expandButton = getByTestId('Third-Level-Expand-Button');
    await act(async () => {
      fireEvent.click(expandButton);
    });

    const yourSpending = getByTestId('Spending-Your-Spending-Sidebar-Link');
    expect(yourSpending).toBeInTheDocument();
  });

  it('should NOT display Your-spending tab when feature flag is disabled', async () => {
    mockProps.activeSecondLevelKey = 'spending';
    mockProps.activeFirstLevelKey = 'spending';
    mockProps.isTravelManager = true;
    mockProps.isYourSpendingTabEnabled = false;

    const { getByTestId, queryByTestId } = render(<ThirdLevelNavMobile {...mockProps} />);
    const expandButton = getByTestId('Third-Level-Expand-Button');
    await act(async () => {
      fireEvent.click(expandButton);
    });

    const yourSpending = queryByTestId('Spending-Your-Spending-Sidebar-Link');
    expect(yourSpending).not.toBeInTheDocument();
  });

  it('should NOT display Your-spending tab when user is guest', async () => {
    mockProps.activeSecondLevelKey = 'spending';
    mockProps.activeFirstLevelKey = 'spending';
    mockProps.isGuestUser = true;
    mockProps.isYourSpendingTabEnabled = true;

    const { container } = render(<ThirdLevelNavMobile {...mockProps} />);
    expect(container.querySelector('[data-testid="Mobile-Nav-Third-Level-Container"]')).toBeNull();
  });

  it('should display correct label for Your-spending tab', () => {
    mockProps.activeFirstLevelKey = 'spending';
    mockProps.activeSecondLevelKey = 'spending';
    mockProps.isTravelManager = true;
    mockProps.isYourSpendingTabEnabled = true;
    (useSearchParams as jest.Mock).mockReturnValue({
      get: (key) => {
        if (key === 'tab') return 'your-spending';
        return null;
      },
    });

    const { getByTestId, getByText } = render(<ThirdLevelNavMobile {...mockProps} />);
    const container = getByTestId('Mobile-Nav-Third-Level-Container');

    expect(container).toBeInTheDocument();
    expect(getByText('spending.spending.reporting.tab.employee.spending')).toBeInTheDocument();
  });
});
