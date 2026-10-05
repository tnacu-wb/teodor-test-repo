import { CustomerAccountDetails, LOCALES, PathParams, SearchParams } from '@whitbread-eos/api';
import { FormPage } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  TranslationProvider,
  getTranslations,
  ID_TOKEN_COOKIE,
  getPathForLocale,
  formatIBAssetsUrl,
  getDetailsFromToken,
  getAccountList,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { AccountSelector } from '../../homepage/components';
import { MemorableWordForm } from './components/memorable-word-component/memorable-word-component';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

const LOG_PAGE_NAME = 'spending-memorable-word';
export default async function MemorableWordPage({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const baseDataTestId = 'MemorableWordPage';
  const locale = resolvedParams?.locale ?? LOCALES.EN;
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { language } = getCountryLanguageByLocale(locale);
  const { employeeId, companyId } = getDetailsFromToken(token);

  const [{ t, translations }, accounts] = await Promise.all([
    getTranslations(language, ['spending', 'users', 'icons']),
    getAccountList(token, {}, { pageName: LOG_PAGE_NAME, userId: employeeId, companyId }),
  ]);

  const icons = translations?.['icons'] ?? {};

  if (!accounts || !accounts.length) {
    redirect(getPathForLocale(locale, 'homepage'));
  }

  let account =
    accounts?.find(
      (acc: CustomerAccountDetails) => acc.tetheredGuid === resolvedSearchParams?.account
    ) ?? null;

  if (!account && accounts?.length) {
    account = accounts?.[0];
  }

  if (!account) {
    redirect(getPathForLocale(locale, 'homepage'));
  }

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <FormPage
          baseDataTestId={baseDataTestId}
          backIcon={formatIBAssetsUrl(icons?.['icon.arrow.left.purple'] ?? '')}
          iconClassName="px-2 py-3 max-w-auto"
          backHref={getPathForLocale(
            resolvedParams?.locale,
            `spending?tab=innbusiness-pay&account=${account.tetheredGuid}`
          )}
          title={t('spending.spending.memorable.word.title')}
          isCentered={false}
        >
          <p data-testid={`${baseDataTestId}-description`} className={descriptionStyle}>
            {t('spending.spending.memorable.word.description')}
          </p>
          <AccountSelector
            locale={resolvedParams?.locale ?? LOCALES.EN}
            parentDataTestId={baseDataTestId}
            hideDropdown={true}
            className={selectorStyle}
          />
          <MemorableWordForm
            baseDataTestId={baseDataTestId}
            icons={icons}
            locale={resolvedParams?.locale}
            account={account}
          />
        </FormPage>
      </div>
    </TranslationProvider>
  );
}

const pageStyle = 'bg-lightGrey5';
const descriptionStyle = 'font-normal max-w-[39.375rem] pt-[0.6rem] text-base';
const selectorStyle = 'my-[3rem]';
