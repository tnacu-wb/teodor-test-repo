import { LOCALES } from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations,
  cn,
  getPathForLocale,
} from '@whitbread-eos/utils/server';
import Link from 'next/link';
import { Suspense } from 'react';

import { AccountHolder } from '~components/innBusiness/AccountHolder/account-holder';

type Props = {
  locale: LOCALES;
  parentDataTestId: string;
  className?: string;
  hideDropdown?: boolean;
  isHomepage?: boolean;
  accountID?: string;
};

export async function AccountSelector({
  locale,
  parentDataTestId,
  className = '',
  hideDropdown = false,
  isHomepage = false,
  accountID,
}: Props) {
  const baseDataTestId = `${parentDataTestId}-InnBusinessPay`;
  const { language } = getCountryLanguageByLocale(locale);
  const { t } = await getTranslations(language, 'homepage');

  return (
    <Suspense fallback={<AccountSelectorSkeleton />}>
      <div data-testid={`${baseDataTestId}-Container`} className={cn(className, containerStyle)}>
        <AccountHolder
          locale={locale}
          className={accountHolderStyle}
          hideDropdown={hideDropdown}
          prefetchAccounts
        />
        <AccountHolder
          locale={locale}
          className={accountHolderMobileStyle}
          hideDropdown={hideDropdown}
          mobile
          prefetchAccounts
        />
        {isHomepage && (
          <div
            className={viewSpendingContainerStyle}
            data-testid={`${baseDataTestId}-ViewSpendingDetails-Container`}
          >
            <Link
              className={viewSpendingStyle}
              href={getPathForLocale(locale, `spending?tab=innbusiness-pay&account=${accountID}`)}
              data-testid={`${baseDataTestId}-ViewSpendingDetails-Button`}
              prefetch
            >
              {t('home.innbusinessPay.viewSpending')}
            </Link>
          </div>
        )}
      </div>
    </Suspense>
  );
}

export function AccountSelectorSkeleton() {
  return (
    <div className={'flex flex-col'} data-testid={'AccountSelectorSkeleton'}>
      <Skeleton className={'h-8 w-full md:w-2/4 lg:w-1/5 mb-2'} />
      <div className={'flex flex-row w-full md:w-2/4 lg:w-1/5 gap-4'}>
        <Skeleton className={'h-6 flex-1'} />
        <Skeleton className={'h-6 flex-1'} />
      </div>
    </div>
  );
}

const containerStyle = 'relative';
const accountHolderStyle = 'mobile:hidden';
const accountHolderMobileStyle = 'hidden mobile:block';
const viewSpendingContainerStyle = 'absolute right-0 bottom-0 mobile:relative mobile:mt-[.75rem]';
const viewSpendingStyle = 'cursor-pointer underline text-secondaryColor';
