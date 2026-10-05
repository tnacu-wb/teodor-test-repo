import { LOCALES } from '@whitbread-eos/api';
import { Tabs, TabsList, StaticTabsTrigger, SearchParamLink } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations,
  getSearchParams,
} from '@whitbread-eos/utils/server';

type Props = {
  locale: LOCALES;
  selectedTab: string;
  isTravelManager: boolean;
  showYourSpendingTab: boolean;
};

export const SPENDING_TABS = {
  COMPANY: 'company',
  INN_BUSINESS_PAY: 'innbusiness-pay',
  YOUR_SPENDING: 'your-spending',
};

export async function SpendingTabs({
  selectedTab,
  locale,
  isTravelManager,
  showYourSpendingTab,
}: Props) {
  const baseDataTestId = 'SpendingTabs';
  const { language } = getCountryLanguageByLocale(locale);

  const [{ t }, searchParams] = await Promise.all([
    getTranslations(language, 'spending'),
    getSearchParams(),
  ]);

  return (
    <Tabs className={tabsStyle}>
      <TabsList>
        {isTravelManager && (
          <SearchParamLink
            name="tab"
            value={SPENDING_TABS.COMPANY}
            clear
            prefetch
            searchParams={searchParams}
          >
            <StaticTabsTrigger
              data-testid={`${baseDataTestId}-InnBusinessTab`}
              active={selectedTab === SPENDING_TABS.COMPANY}
            >
              {t('spending.reporting.tab.company.spending')}
            </StaticTabsTrigger>
          </SearchParamLink>
        )}

        <SearchParamLink
          name="tab"
          value={SPENDING_TABS.INN_BUSINESS_PAY}
          clear
          prefetch
          searchParams={searchParams}
        >
          <StaticTabsTrigger
            data-testid={`${baseDataTestId}-innbusinessPayTab`}
            active={selectedTab === SPENDING_TABS.INN_BUSINESS_PAY}
          >
            {t('spending.reporting.tab.innbusinessPay')}
          </StaticTabsTrigger>
        </SearchParamLink>
        {showYourSpendingTab && (
          <SearchParamLink
            name="tab"
            value={SPENDING_TABS.YOUR_SPENDING}
            clear
            prefetch
            searchParams={searchParams}
          >
            <StaticTabsTrigger
              data-testid={`${baseDataTestId}-YourSpendingTab`}
              active={selectedTab === SPENDING_TABS.YOUR_SPENDING}
            >
              {t('spending.reporting.tab.employee.spending')}
            </StaticTabsTrigger>
          </SearchParamLink>
        )}
      </TabsList>
    </Tabs>
  );
}

const tabsStyle = 'mb-12 mobile:hidden';
