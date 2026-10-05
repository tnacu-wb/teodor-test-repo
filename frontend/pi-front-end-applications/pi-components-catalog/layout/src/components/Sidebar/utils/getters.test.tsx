import { LOCALES } from '@whitbread-eos/api';

import {
  getSecondLevelLinks,
  spendingSecondLevelLinks,
  spendingStatementsLink,
  spendingTabsLinks,
} from './getters';

describe('getSecondLevelLinks', () => {
  const mockTranslate = (key: string) => key;

  const activeLevels = {
    activeFirstLevelKey: 'manage',
    activeSecondLevelKey: 'manage/employees',
  };

  const userRoles = {
    isAccountHolder: true,
    isTravelManager: false,
    isCardHolder: true,
    isTethered: true,
    isBusinessPayManager: false,
    isGuestUser: false,
  };

  const locale = LOCALES.EN;

  it('should return manage links based on user roles and active levels', () => {
    const result = getSecondLevelLinks(activeLevels, userRoles, locale, false, mockTranslate);

    expect(result.manage).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          key: 'ManageEmployees',
          isActive: true,
          condition: true,
        }),
        expect.objectContaining({
          key: 'BookingAllowances',
          condition: false,
        }),
      ])
    );
  });

  it('should return manage links based on user roles and active levels with TM', () => {
    const roles = {
      ...userRoles,
      isTravelManager: true,
      isAccountHolder: false,
    };
    const result = getSecondLevelLinks(activeLevels, roles, locale, false, mockTranslate);

    expect(result.manage).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          key: 'ManageEmployees',
          isActive: true,
          condition: true,
        }),
        expect.objectContaining({
          key: 'BookingAllowances',
          condition: true,
        }),
      ])
    );
  });

  it('should return spending links for mobile view when activeSecondLevelKey matches', () => {
    const mobileActiveLevels = {
      ...activeLevels,
      activeSecondLevelKey: spendingTabsLinks[0],
    };

    const result = getSecondLevelLinks(mobileActiveLevels, userRoles, locale, true, mockTranslate);

    expect(result.spending).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          label: 'spending.spending.reporting.tab.company.spending',
          isActive: false,
        }),
        expect.objectContaining({
          label: 'spending.spending.reporting.tab.innbusinessPay',
          isActive: false,
        }),
      ])
    );
  });

  it('should return undefined for spending links when activeSecondLevelKey does not match', () => {
    const result = getSecondLevelLinks(activeLevels, userRoles, locale, false, mockTranslate);

    expect(result.spending).toBeUndefined();
  });

  it('should return spending links for desktop view when activeSecondLevelKey matches', () => {
    const desktopActiveLevels = {
      ...activeLevels,
      activeSecondLevelKey: spendingSecondLevelLinks[1],
    };

    const desktopUserRoles = {
      ...userRoles,
      isTravelManager: true,
    };

    const result = getSecondLevelLinks(
      desktopActiveLevels,
      desktopUserRoles,
      locale,
      false,
      mockTranslate
    );

    expect(result.spending).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          key: 'EmergencyReport',
          isActive: true,
        }),
        expect.objectContaining({
          key: 'OutOfPolicyReport',
          isActive: false,
        }),
      ])
    );
  });

  it('should return spending links for desktop view when activeSecondLevelKey OutOfPolicy matches', () => {
    const desktopActiveLevels = {
      ...activeLevels,
      activeSecondLevelKey: spendingSecondLevelLinks[2],
    };

    const desktopUserRoles = {
      ...userRoles,
      isOutOfPolicyReportActive: true,
      isTravelManager: true,
    };

    const result = getSecondLevelLinks(
      desktopActiveLevels,
      desktopUserRoles,
      locale,
      false,
      mockTranslate
    );

    expect(result.spending).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          key: 'OutOfPolicyReport',
          isActive: true,
        }),
      ])
    );
  });

  it('should return spending links for mobile view when activeSecondLevelKey matches a spending tab link', () => {
    const mobileActiveLevels = {
      ...activeLevels,
      activeSecondLevelKey: 'spending?tab=company',
    };

    const result = getSecondLevelLinks(mobileActiveLevels, userRoles, locale, true, mockTranslate);

    expect(result.spending).toEqual([
      {
        condition: false,
        href: '/en-gb/spending?tab=company',
        isActive: false,
        label: 'spending.spending.reporting.tab.company.spending',
        testId: 'Spending-Company',
      },
      {
        condition: false,
        href: '/en-gb/spending?tab=innbusiness-pay',
        isActive: false,
        label: 'spending.spending.reporting.tab.innbusinessPay',
        testId: 'Spending-InnBusiness-Pay',
      },
      {
        condition: false,
        href: '/en-gb/spending?tab=your-spending',
        isActive: false,
        label: 'spending.spending.reporting.tab.employee.spending',
        testId: 'Spending-Your-Spending',
      },
    ]);
  });

  it('should include Your-spending tab in mobile view when feature flag is enabled and user is travel manager', () => {
    const mobileActiveLevels = {
      ...activeLevels,
      activeSecondLevelKey: 'spending?tab=company',
    };

    const mobileUserRoles = {
      ...userRoles,
      isTravelManager: true,
      isYourSpendingTabEnabled: true,
    };

    const result = getSecondLevelLinks(
      mobileActiveLevels,
      mobileUserRoles,
      locale,
      true,
      mockTranslate
    );

    const yourSpendingTab = result.spending?.find((tab) => tab.testId === 'Spending-Your-Spending');
    expect(yourSpendingTab).toEqual({
      condition: true,
      href: '/en-gb/spending?tab=your-spending',
      isActive: false,
      label: 'spending.spending.reporting.tab.employee.spending',
      testId: 'Spending-Your-Spending',
    });
  });

  it('should set rendering condition to false for Your-spending tab when feature flag is disabled', () => {
    const mobileActiveLevels = {
      ...activeLevels,
      activeSecondLevelKey: 'spending?tab=company',
    };

    const mobileUserRoles = {
      ...userRoles,
      isTravelManager: true,
      isYourSpendingTabEnabled: false,
    };

    const result = getSecondLevelLinks(
      mobileActiveLevels,
      mobileUserRoles,
      locale,
      true,
      mockTranslate
    );

    const yourSpendingTab = result.spending?.find((tab) => tab.testId === 'Spending-Your-Spending');
    expect(yourSpendingTab).toEqual({
      condition: false,
      href: '/en-gb/spending?tab=your-spending',
      isActive: false,
      label: 'spending.spending.reporting.tab.employee.spending',
      testId: 'Spending-Your-Spending',
    });
  });

  it('should set rendering condition to false for Your-spending tab when user is guest user', () => {
    const mobileActiveLevels = {
      ...activeLevels,
      activeSecondLevelKey: 'spending?tab=company',
    };

    const mobileUserRoles = {
      ...userRoles,
      isGuestUser: true,
      isYourSpendingTabEnabled: true,
    };

    const result = getSecondLevelLinks(
      mobileActiveLevels,
      mobileUserRoles,
      locale,
      true,
      mockTranslate
    );

    const yourSpendingTab = result.spending?.find((tab) => tab.testId === 'Spending-Your-Spending');
    expect(yourSpendingTab).toEqual({
      condition: false,
      href: '/en-gb/spending?tab=your-spending',
      isActive: false,
      label: 'spending.spending.reporting.tab.employee.spending',
      testId: 'Spending-Your-Spending',
    });
  });

  it('should include Your-spending tab when on spendingSecondLevelLinks page', () => {
    const mobileActiveLevels = {
      ...activeLevels,
      activeSecondLevelKey: spendingSecondLevelLinks[0],
    };

    const mobileUserRoles = {
      ...userRoles,
      isTravelManager: true,
      isYourSpendingTabEnabled: true,
    };

    const result = getSecondLevelLinks(
      mobileActiveLevels,
      mobileUserRoles,
      locale,
      true,
      mockTranslate
    );

    const yourSpendingTab = result.spending?.find((tab) => tab.testId === 'Spending-Your-Spending');
    expect(yourSpendingTab).toMatchObject({
      condition: true,
      testId: 'Spending-Your-Spending',
    });
  });

  it('should return Spending Statements links for desktop view when activeSecondLevelKey matches', () => {
    const desktopActiveLevels = {
      activeFirstLevelKey: 'spending',
      activeSecondLevelKey: spendingStatementsLink[0],
    };

    const desktopUserRoles = {
      isAccountHolder: false,
      isTravelManager: false,
      isCardHolder: false,
      isTethered: false,
      isBusinessPayManager: false,
      isGuestUser: false,
    };

    const result = getSecondLevelLinks(
      desktopActiveLevels,
      desktopUserRoles,
      locale,
      false,
      mockTranslate
    );

    expect(result.spending).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          key: 'Statements',
          isActive: true,
        }),
      ])
    );
  });
  it('should return Spending Statements links for desktop view with searchParams when activeSecondLevelKey matches', () => {
    const desktopActiveLevels = {
      activeFirstLevelKey: 'spending',
      activeSecondLevelKey: spendingStatementsLink[0],
    };

    const desktopUserRoles = {
      isAccountHolder: false,
      isTravelManager: true,
      isCardHolder: false,
      isTethered: false,
      isBusinessPayManager: false,
      isGuestUser: false,
    };

    const searchParams = '12345';

    const result = getSecondLevelLinks(
      desktopActiveLevels,
      desktopUserRoles,
      locale,
      false,
      mockTranslate,
      searchParams
    );

    expect(result.spending).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          key: 'Statements',
          isActive: true,
          href: `/en-gb/spending/statements?account=${searchParams}`,
        }),
      ])
    );
  });
});

describe('getSecondLevelLinks - Cost Centre Management', () => {
  const mockTranslate = (key: string) => key;
  const locale = LOCALES.EN;

  it('should set CostCentreManagement as active and condition true when isCostCentreManagementActive and isTravelManager', () => {
    const activeLevels = {
      activeFirstLevelKey: 'manage',
      activeSecondLevelKey: 'manage/cost-centres',
    };
    const userRoles = {
      isAccountHolder: false,
      isTravelManager: true,
      isCardHolder: false,
      isCostCentreManagementActive: true,
      isTethered: false,
      isBusinessPayManager: false,
      isGuestUser: false,
    };

    const result = getSecondLevelLinks(activeLevels, userRoles, locale, false, mockTranslate);

    const costCentreLink = result.manage.find((l) => l.key === 'CostCentreManagement');
    expect(costCentreLink).toMatchObject({
      isActive: true,
      condition: undefined,
    });
  });

  it('should set CostCentreManagement condition true for isCostCentreManagementActive and isCardHolder', () => {
    const activeLevels = {
      activeFirstLevelKey: 'manage',
      activeSecondLevelKey: 'manage/other',
    };
    const userRoles = {
      isAccountHolder: false,
      isTravelManager: false,
      isCardHolder: true,
      isCostCentreManagementActive: true,
      isTethered: true,
      isBusinessPayManager: false,
      isGuestUser: false,
    };

    const result = getSecondLevelLinks(activeLevels, userRoles, locale, false, mockTranslate);

    const costCentreLink = result.manage.find((l) => l.key === 'CostCentreManagement');
    expect(costCentreLink).toMatchObject({
      isActive: false,
      condition: undefined,
    });
  });

  it('should set CostCentreManagement condition true for isCostCentreManagementActive and isAccountHolder', () => {
    const activeLevels = {
      activeFirstLevelKey: 'manage',
      activeSecondLevelKey: 'manage/other',
    };
    const userRoles = {
      isAccountHolder: true,
      isTravelManager: false,
      isCardHolder: false,
      isCostCentreManagementActive: true,
      isTethered: true,
      isBusinessPayManager: false,
      isGuestUser: false,
    };

    const result = getSecondLevelLinks(activeLevels, userRoles, locale, false, mockTranslate);

    const costCentreLink = result.manage.find((l) => l.key === 'CostCentreManagement');
    expect(costCentreLink).toMatchObject({
      isActive: false,
      condition: undefined,
    });
  });

  it('should set CostCentreManagement condition false if isCostCentreManagementActive is false', () => {
    const activeLevels = {
      activeFirstLevelKey: 'manage',
      activeSecondLevelKey: 'manage/cost-centres',
    };
    const userRoles = {
      isAccountHolder: true,
      isTravelManager: true,
      isCardHolder: false,
      isCostCentreManagementActive: false,
      isTethered: true,
      isBusinessPayManager: false,
      isGuestUser: false,
    };

    const result = getSecondLevelLinks(activeLevels, userRoles, locale, false, mockTranslate);

    const costCentreLink = result.manage.find((l) => l.key === 'CostCentreManagement');
    expect(costCentreLink).toMatchObject({
      isActive: true,
      condition: false,
    });
  });

  it('should set CostCentreManagement condition false if none of the roles are true', () => {
    const activeLevels = {
      activeFirstLevelKey: 'manage',
      activeSecondLevelKey: 'manage/cost-centres',
    };
    const userRoles = {
      isAccountHolder: false,
      isTravelManager: false,
      isCardHolder: false,
      isCostCentreManagementActive: true,
      isTethered: true,
      isBusinessPayManager: false,
      isGuestUser: false,
    };

    const result = getSecondLevelLinks(activeLevels, userRoles, locale, false, mockTranslate);

    const costCentreLink = result.manage.find((l) => l.key === 'CostCentreManagement');
    expect(costCentreLink).toMatchObject({
      isActive: true,
      condition: undefined,
    });
  });
});

describe('getSecondLevelLinks - Company Details for Business Pay Manager', () => {
  const mockTranslate = (key: string) => key;
  const locale = LOCALES.EN;

  it('should set CompanyDetails condition true isBusinessPayManager', () => {
    const activeLevels = {
      activeFirstLevelKey: 'manage',
      activeSecondLevelKey: 'manage/company',
    };
    const userRoles = {
      isAccountHolder: false,
      isTravelManager: false,
      isBusinessPayManager: true,
      isCardHolder: false,
      isTethered: false,
      isGuestUser: false,
    };

    const result = getSecondLevelLinks(activeLevels, userRoles, locale, false, mockTranslate);

    const companyDetailsLink = result.manage.find((l) => l.key === 'CompanyDetails');
    expect(companyDetailsLink).toMatchObject({
      key: 'CompanyDetails',
      label: 'common.layout.menu.manage.options.company',
      isActive: true,
      href: '/en-gb/manage/company',
      condition: true,
    });
  });

  it('should set CompanyDetails condition false when isBusinessPayManager and isTravelManager is false', () => {
    const activeLevels = {
      activeFirstLevelKey: 'manage',
      activeSecondLevelKey: 'manage/company',
    };
    const userRoles = {
      isAccountHolder: false,
      isTravelManager: false,
      isBusinessPayManager: false,
      isCardHolder: false,
      isTethered: false,
      isGuestUser: false,
    };

    const result = getSecondLevelLinks(activeLevels, userRoles, locale, false, mockTranslate);

    const companyDetailsLink = result.manage.find((l) => l.key === 'CompanyDetails');
    expect(companyDetailsLink).toMatchObject({
      key: 'CompanyDetails',
      isActive: true,
      condition: false,
    });
  });
});

describe('getSecondLevelLinks - Card management for Business Pay Manager', () => {
  const mockTranslate = (key: string) => key;
  const locale = LOCALES.EN;

  it('should set cardManagement condition true when showCardManagementForBPM is true', () => {
    const activeLevels = {
      activeFirstLevelKey: 'manage',
      activeSecondLevelKey: 'manage/cards',
    };
    const userRoles = {
      isAccountHolder: false,
      isTravelManager: false,
      isBusinessPayManager: true,
      isCardHolder: false,
      isTethered: false,
      isGuestUser: false,
    };

    const result = getSecondLevelLinks(activeLevels, userRoles, locale, false, mockTranslate);

    const cardManagementLink = result.manage.find((l) => l.key === 'CardManagement');
    expect(cardManagementLink).toMatchObject({
      key: 'CardManagement',
      label: 'common.layout.menu.manage.options.cards',
      isActive: true,
      href: '/en-gb/manage/cards',
      condition: true,
    });
  });

  it('should set CompanyDetails condition false when showCardManagementForBPM is false', () => {
    const activeLevels = {
      activeFirstLevelKey: 'manage',
      activeSecondLevelKey: 'manage/cards',
    };
    const userRoles = {
      isAccountHolder: false,
      isTravelManager: false,
      isBusinessPayManager: false,
      isCardHolder: false,
      isTethered: false,
      isGuestUser: false,
    };

    const result = getSecondLevelLinks(activeLevels, userRoles, locale, false, mockTranslate);

    const cardManagementLink = result.manage.find((l) => l.key === 'CardManagement');
    expect(cardManagementLink).toMatchObject({
      key: 'CardManagement',
      label: 'common.layout.menu.manage.options.cards',
      isActive: true,
      href: '/en-gb/manage/cards',
      condition: false,
    });
  });
});

describe('getSecondLevelLinks - InnBusiness-Pay tab conditional logic', () => {
  const mockTranslate = (key: string) => key;
  const locale = LOCALES.EN;

  it('should show InnBusiness-Pay when isYourSpendingTabEnabled=true and user is not guest', () => {
    const mobileActiveLevels = {
      activeFirstLevelKey: 'spending',
      activeSecondLevelKey: 'spending?tab=company',
    };

    const mobileUserRoles = {
      isAccountHolder: false,
      isTravelManager: false,
      isCardHolder: false,
      isTethered: false,
      isBusinessPayManager: false,
      isGuestUser: false,
      isYourSpendingTabEnabled: true,
    };

    const result = getSecondLevelLinks(
      mobileActiveLevels,
      mobileUserRoles,
      locale,
      true,
      mockTranslate
    );

    const innBusinessPayTab = result.spending?.find(
      (tab) => tab.testId === 'Spending-InnBusiness-Pay'
    );
    expect(innBusinessPayTab).toEqual({
      condition: true,
      href: '/en-gb/spending?tab=innbusiness-pay',
      isActive: false,
      label: 'spending.spending.reporting.tab.innbusinessPay',
      testId: 'Spending-InnBusiness-Pay',
    });
  });

  it('should hide InnBusiness-Pay when isYourSpendingTabEnabled=true but user is guest', () => {
    const mobileActiveLevels = {
      activeFirstLevelKey: 'spending',
      activeSecondLevelKey: 'spending?tab=company',
    };

    const mobileUserRoles = {
      isAccountHolder: false,
      isTravelManager: false,
      isCardHolder: false,
      isTethered: false,
      isBusinessPayManager: false,
      isGuestUser: true,
      isYourSpendingTabEnabled: true,
    };

    const result = getSecondLevelLinks(
      mobileActiveLevels,
      mobileUserRoles,
      locale,
      true,
      mockTranslate
    );

    const innBusinessPayTab = result.spending?.find(
      (tab) => tab.testId === 'Spending-InnBusiness-Pay'
    );
    expect(innBusinessPayTab).toEqual({
      condition: false,
      href: '/en-gb/spending?tab=innbusiness-pay',
      isActive: false,
      label: 'spending.spending.reporting.tab.innbusinessPay',
      testId: 'Spending-InnBusiness-Pay',
    });
  });

  it('should show InnBusiness-Pay when isYourSpendingTabEnabled=false and user is travel manager', () => {
    const mobileActiveLevels = {
      activeFirstLevelKey: 'spending',
      activeSecondLevelKey: 'spending?tab=company',
    };

    const mobileUserRoles = {
      isAccountHolder: false,
      isTravelManager: true,
      isCardHolder: false,
      isTethered: false,
      isBusinessPayManager: false,
      isGuestUser: false,
      isYourSpendingTabEnabled: false,
    };

    const result = getSecondLevelLinks(
      mobileActiveLevels,
      mobileUserRoles,
      locale,
      true,
      mockTranslate
    );

    const innBusinessPayTab = result.spending?.find(
      (tab) => tab.testId === 'Spending-InnBusiness-Pay'
    );
    expect(innBusinessPayTab).toEqual({
      condition: true,
      href: '/en-gb/spending?tab=innbusiness-pay',
      isActive: false,
      label: 'spending.spending.reporting.tab.innbusinessPay',
      testId: 'Spending-InnBusiness-Pay',
    });
  });

  it('should hide InnBusiness-Pay when isYourSpendingTabEnabled=false and user is not travel manager', () => {
    const mobileActiveLevels = {
      activeFirstLevelKey: 'spending',
      activeSecondLevelKey: 'spending?tab=company',
    };

    const mobileUserRoles = {
      isAccountHolder: false,
      isTravelManager: false,
      isCardHolder: false,
      isTethered: false,
      isBusinessPayManager: false,
      isGuestUser: false,
      isYourSpendingTabEnabled: false,
    };

    const result = getSecondLevelLinks(
      mobileActiveLevels,
      mobileUserRoles,
      locale,
      true,
      mockTranslate
    );

    const innBusinessPayTab = result.spending?.find(
      (tab) => tab.testId === 'Spending-InnBusiness-Pay'
    );
    expect(innBusinessPayTab).toEqual({
      condition: false,
      href: '/en-gb/spending?tab=innbusiness-pay',
      isActive: false,
      label: 'spending.spending.reporting.tab.innbusinessPay',
      testId: 'Spending-InnBusiness-Pay',
    });
  });
});

describe('getSecondLevelLinks - Your-Spending tab conditional logic', () => {
  const mockTranslate = (key: string) => key;
  const locale = LOCALES.EN;

  it('should hide Your-Spending when user is guest even with feature flag enabled', () => {
    const mobileActiveLevels = {
      activeFirstLevelKey: 'spending',
      activeSecondLevelKey: 'spending?tab=company',
    };

    const mobileUserRoles = {
      isAccountHolder: false,
      isTravelManager: true,
      isCardHolder: false,
      isTethered: false,
      isBusinessPayManager: false,
      isGuestUser: true,
      isYourSpendingTabEnabled: true,
    };

    const result = getSecondLevelLinks(
      mobileActiveLevels,
      mobileUserRoles,
      locale,
      true,
      mockTranslate
    );

    const yourSpendingTab = result.spending?.find((tab) => tab.testId === 'Spending-Your-Spending');
    expect(yourSpendingTab).toEqual({
      condition: false,
      href: '/en-gb/spending?tab=your-spending',
      isActive: false,
      label: 'spending.spending.reporting.tab.employee.spending',
      testId: 'Spending-Your-Spending',
    });
  });

  it('should show Your-Spending for non-guest with feature flag on spendingSecondLevelLinks page', () => {
    const mobileActiveLevels = {
      activeFirstLevelKey: 'spending',
      activeSecondLevelKey: spendingSecondLevelLinks[1],
    };

    const mobileUserRoles = {
      isAccountHolder: false,
      isTravelManager: false,
      isCardHolder: false,
      isTethered: false,
      isBusinessPayManager: false,
      isGuestUser: false,
      isYourSpendingTabEnabled: true,
    };

    const result = getSecondLevelLinks(
      mobileActiveLevels,
      mobileUserRoles,
      locale,
      true,
      mockTranslate
    );

    const yourSpendingTab = result.spending?.find((tab) => tab.testId === 'Spending-Your-Spending');
    expect(yourSpendingTab).toMatchObject({
      condition: true,
      testId: 'Spending-Your-Spending',
    });
  });
});
