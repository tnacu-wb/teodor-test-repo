import {
  CustomerAccountDetails,
  FT_IB_COST_CENTRE_MANAGEMENT,
  LOCALES,
  PathParams,
  Scheme,
  SearchParams,
  CostCentreData,
} from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  ID_TOKEN_COOKIE,
  getPathForLocale,
  getServerUnleashToggles,
  getWorldlineReturnUrl,
  getDetailsFromToken,
  getCostCenterDetails,
  getAccountList,
} from '@whitbread-eos/utils/server';
import { ExternalLink, PlusIcon } from 'lucide-react';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { AccountHolder } from '~components/innBusiness/AccountHolder/account-holder';
import { WordlineButton } from '~components/innBusiness/WordlineButton';

import { PageSubtitle } from '../employees/components/InnBusiness/page-subtitle';
import { Analytics } from './components/Analytics/Analytics';
import { CostCentresTable } from './components/CostCentresTable';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

const PAGE_NAME = 'Cost Centre Management';

export default async function CostCentres({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const baseDataTestId = 'CostCentresPage';
  const LOG_PAGE_NAME = 'manage-cost-centres';
  const headerList = await headers();
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const locale = resolvedParams?.locale ?? LOCALES.EN;
  const { language } = getCountryLanguageByLocale(locale);
  const { employeeId, companyId } = getDetailsFromToken(token);
  const currentPath = headerList.get('WB-Url') ?? '';
  const flagsFallback = {
    [FT_IB_COST_CENTRE_MANAGEMENT]: false,
  };

  const [{ t, translations }, accounts, toggles] = await Promise.all([
    getTranslations(language, ['cards', 'icons', 'layout']),
    getAccountList(token, {}, { pageName: LOG_PAGE_NAME, userId: employeeId, companyId }),
    getServerUnleashToggles(currentPath, flagsFallback),
  ]);
  const icons = translations?.['icons'] ?? {};
  const allowedAccounts = accounts.filter(
    (acc: CustomerAccountDetails) => acc.scheme === ('DE' as Scheme)
  );
  if (!toggles[FT_IB_COST_CENTRE_MANAGEMENT] || !allowedAccounts || allowedAccounts.length === 0) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  const account =
    allowedAccounts.find(
      (acc: CustomerAccountDetails) => acc.tetheredGuid === resolvedSearchParams?.account
    ) ?? allowedAccounts[0];

  const costCentres: CostCentreData[] = await getCostCenterDetails(token, account.tetheredGuid);

  const wlReturnUrl = getWorldlineReturnUrl(
    locale,
    `manage/cost-centres?account=${account?.tetheredGuid}`
  );
  const wlPostUrl = process?.env?.NEXT_PUBLIC_WORLDLINE_POST_URL_DE;

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <h1 className={h1Style}>{t('cards.costCentreMgmt.title')}</h1>
        <>
          <AccountHolder
            locale={locale}
            className={accountHolderStyle}
            filterByScheme={'DE' as Scheme}
          />
          <AccountHolder
            locale={locale}
            className={accountHolderMobileStyle}
            mobile
            filterByScheme={'DE' as Scheme}
          />
        </>

        <div className={tableActionsStyle}>
          <div className={tableActionsLeftStyle}>
            <PageSubtitle
              icons={icons}
              title={t('cards.costCentreMgmt.heading')}
              message={t('cards.costCentreMgmt.info')}
            />
          </div>

          <WordlineButton
            baseDataTestId={`${baseDataTestId}-Add-cost-centre`}
            tetheredGuid={account?.tetheredGuid ?? ''}
            scheme={account?.scheme}
            iconSvg={<ExternalLink width={20} height={20} />}
            text={t('cards.costCentreMgmt.addNewButton')}
            page={'CostCentreNew.aspx'}
            returnUrl={wlReturnUrl}
            variant={'default'}
            postUrl={wlPostUrl ?? ''}
            prefixIconSvg={<PlusIcon width={20} height={20} />}
            parentButtonStyle={buttonStyle}
          />
        </div>

        <div className={tableContainerStyle}>
          <CostCentresTable
            pageSize={15}
            baseDataTestId={`${baseDataTestId}-Table`}
            initialItems={costCentres}
            icons={icons}
            account={account}
            worldlinePostUrl={wlPostUrl ?? ''}
            worldlineReturnUrl={wlReturnUrl}
          />
        </div>
      </div>
      <Analytics pageName={PAGE_NAME} />
    </TranslationProvider>
  );
}

const pageStyle =
  'p-12 min-w-[700px] mobile:min-w-full mobile:px-4 mobile:py-6 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
const h1Style =
  'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl mb-16';
const accountHolderStyle = 'mobile:hidden';
const accountHolderMobileStyle = 'hidden mobile:block';
const tableActionsStyle =
  'mt-12 flex justify-between items-center mobile:flex-col mobile:items-start';
const tableActionsLeftStyle =
  'flex flex-col text-xl font-bold mobile:w-full mobile:justify-between';
const buttonStyle = 'flex flex-row items-center gap-2 mobile:w-auto mobile:mt-4 mobile:px-4';
const tableContainerStyle = 'mt-6';
