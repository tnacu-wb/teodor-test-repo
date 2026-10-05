import {
  LOCALES,
  SearchParams,
  Scheme,
  FT_IB_WL_CARD_EDIT,
  CARD_STATUS_WL_TYPE,
  PayAccountStatus,
} from '@whitbread-eos/api';
import { Button, CardIcon } from '@whitbread-eos/atoms/ui';
import {
  getTranslations,
  getPathForLocale,
  getCountryLanguageByLocale,
  formatIBAssetsUrl,
  getCommonIcons,
  getAccountList,
  getSelectedAccountHolder,
  getAllPibaCards,
  getAccessLevel,
  getWorldlineReturnUrl,
  getServerUnleashToggles,
  getAccountInfo,
  getAccountRegistrationRoleDetails,
} from '@whitbread-eos/utils/server';
import { Check } from 'lucide-react';
import { headers } from 'next/headers';
import Image from 'next/image';
import Link from 'next/link';

import { INN_BUSINESS_PAY_TAB_SEARCH_PARAM } from '~components/constants/constants';
import { AccountHolder } from '~components/innBusiness/AccountHolder/account-holder';
import { CardHolderRegistered } from '~components/innBusiness/CardHolderRegistered';
import { CardNoResults } from '~components/innBusiness/CardNoResults';
import { CardStatus } from '~components/innBusiness/CardStatus';
import {
  DataTable,
  DataTableCell,
  DataTableColumn,
  DataTableRow,
} from '~components/innBusiness/DataTable';
import { DataTableFilters } from '~components/innBusiness/DataTable/data-table';
import SuspendedNotification from '~components/innBusiness/SuspendedNotification';

import { PageSubtitle } from '../../../employees/components/InnBusiness/page-subtitle';
import { Analytics } from '../Analytics/Analytics';
import { EditButton } from './EditCardButton/edit-card-button';
import { EditCardsButton } from './edit-cards-button';
import { InnBusinessPayFilters } from './inn-business-pay-filters';

type Props = {
  locale?: LOCALES;
  token: string;
  searchParams?: SearchParams;
};

export async function InnBusinessPay({ locale, token, searchParams }: Props) {
  const flagsFallback = {
    [FT_IB_WL_CARD_EDIT]: false,
  };

  const baseDataTestId = 'InnBusinessPayTab';
  const { language } = getCountryLanguageByLocale(locale);
  const returnUrl = getWorldlineReturnUrl(locale, 'manage/cards?tab=innbusiness-pay');

  const headerList = await headers();
  const currentPath = headerList.get('WB-Url') ?? '';
  const [{ t }, icons, accounts, accessLevel, toggles] = await Promise.all([
    getTranslations(language, ['cards']),
    getCommonIcons(language),
    getAccountList(token),
    getAccessLevel(searchParams?.account),
    getServerUnleashToggles(currentPath, flagsFallback),
  ]);
  const isWLCardEditEnabled = toggles?.[FT_IB_WL_CARD_EDIT];
  const {
    isTravelManager,
    selectedAccount: { isCardHolder, isAccountHolder, isCostCenterHolder },
  } = accessLevel;
  const selectedAccountHolder = getSelectedAccountHolder(accounts, searchParams?.account ?? '');
  const accountHolderParam = `${INN_BUSINESS_PAY_TAB_SEARCH_PARAM.ACCOUNT}=${selectedAccountHolder?.tetheredGuid}`;
  const isCardHolderOnly = isCardHolder && !isAccountHolder && !isTravelManager;
  const canAddCard = isAccountHolder || isCostCenterHolder;
  const { isOnlyCardHolder } = getAccountRegistrationRoleDetails(selectedAccountHolder);
  const accountInfo =
    !selectedAccountHolder || isOnlyCardHolder
      ? null
      : await getAccountInfo(selectedAccountHolder.scheme, selectedAccountHolder.tetheredGuid);
  const isAccountSuspended = [PayAccountStatus.Suspended, PayAccountStatus.SuspendedHold].includes(
    (accountInfo?.status?.toLowerCase() as PayAccountStatus) ?? ''
  );
  const addCardButtonContent = (
    <>
      <Image
        alt={t('cards.cardMgmt.newCard.label')}
        src={formatIBAssetsUrl(t('cards.cardMgmt.newCard.icon'))}
        width={22}
        height={18}
        className={buttonIconStyle}
      />
      {t('cards.cardMgmt.newCard.label')}
    </>
  );

  const fetchPibaCards = (pageIndex: number, pageSize: number, filters?: DataTableFilters) =>
    getAllPibaCards(
      token,
      selectedAccountHolder?.scheme,
      selectedAccountHolder?.tetheredGuid,
      !!filters?.cancelledCards,
      isCardHolderOnly ? true : !!filters?.onlyMyCards,
      pageIndex,
      pageSize
    );

  const getInnBusinessPayTablePage = async (
    pageIndex: number,
    pageSize: number,
    filters?: DataTableFilters
  ) => {
    const result = await fetchPibaCards(pageIndex, pageSize, filters);
    return result?.innBusinessPayCardList ?? [];
  };

  const getInnBusinessPayTableTotal = async (filters?: DataTableFilters) => {
    const result = await fetchPibaCards(1, 1, filters);
    return result?.totalCount || 0;
  };

  const columns: DataTableColumn[] = [
    {
      id: 'myCard',
      label: t('cards.cardMgmt.columns.yourCard'),
      headerClassName: `${desktopOnlyStyleHeader} md:w-[10%]`,
      className: desktopOnlyStyle,

      render(cell: DataTableCell) {
        return cell ? <Check width={20} height={20} className="inline" /> : null;
      },
    },
    {
      id: 'cardHolderName',
      label: t('cards.cardMgmt.columns.cardHolderName'),
      headerClassName: cardHolderHeaderStyle,
      className: cardColumnStyle,
    },
    {
      id: 'cardRegistrationCount',
      label: t('cards.cardMgmt.columns.cardHolderRegistered'),
      headerClassName: `${desktopOnlyStyleHeader} md:w-[20%]`,
      className: desktopOnlyStyle,

      render(cell: DataTableCell, row: DataTableRow) {
        return (
          <CardHolderRegistered
            registered={cell !== 0}
            cardId={row?.cardId?.toString()}
            tetheredGuid={selectedAccountHolder?.tetheredGuid}
            scheme={selectedAccountHolder?.scheme ?? ('GB' as Scheme)}
            icons={icons}
          />
        );
      },
    },
    {
      id: 'cardNumber',
      label: t('cards.cardMgmt.columns.cardNumber'),
      headerClassName: cardNoHeaderStyle,
      className: cardNoStyle,

      render(cell: DataTableCell) {
        return (
          <>
            <CardIcon type="piba" className={cardIconStyle} icons={icons} /> {cell.slice(-8)}
          </>
        );
      },
    },
    {
      id: 'cardStatus',
      label: t('cards.cardMgmt.columns.cardStatus'),
      headerClassName: `${desktopOnlyStyleHeader} md:w-[18%]`,
      className: desktopOnlyStyle,
      render(cell: DataTableCell, row: DataTableRow) {
        return (
          <CardStatus
            locale={locale}
            status={cell}
            isActivated={row.isActivated}
            cardDetails={row}
            icons={icons}
            accountHolder={selectedAccountHolder}
            token={token}
          />
        );
      },
    },
  ];

  const editColumn = {
    id: 'cardAction',
    label: '',
    headerClassName: actionsHeaderStyle,
    className: actionsCellStyle,

    render(cell: DataTableCell, row: DataTableRow) {
      const shouldDisplayEditButton =
        !isAccountSuspended && row.cardStatus !== CARD_STATUS_WL_TYPE.CANCELLED;
      const href = getPathForLocale(
        locale,
        `manage/cards/innbusiness-pay/${row?.cardId}?${accountHolderParam}`
      );
      return shouldDisplayEditButton && <EditButton href={href} />;
    },
  };

  isWLCardEditEnabled && columns.push(editColumn);

  return (
    <div data-testid={`${baseDataTestId}-container`}>
      <SuspendedNotification
        account={selectedAccountHolder ?? null}
        locale={locale ?? LOCALES.EN}
        isShown={isAccountSuspended}
      />
      <AccountHolder locale={locale} className={accountHolderStyle} />
      <AccountHolder locale={locale} className={accountHolderMobileStyle} mobile />

      <div className={tableActionsStyle}>
        <div className={tableActionsRightStyle}>
          <div className={tableActionsLeftStyle}>
            <PageSubtitle
              title={t('cards.cardMgmt.cardManagement.title')}
              icons={icons}
              message={t('cards.cardMgmt.wlCards.tooltipMsg')}
            />
          </div>
        </div>

        {!isWLCardEditEnabled && !isAccountSuspended && (
          <EditCardsButton
            tetheredGuid={selectedAccountHolder.tetheredGuid}
            returnUrl={returnUrl}
            icons={icons}
            scheme={selectedAccountHolder.scheme}
          />
        )}

        {canAddCard &&
          (isAccountSuspended ? (
            <Button
              variant="default"
              data-testid={`${baseDataTestId}-add-card-button`}
              className={buttonStyle}
              disabled
            >
              {addCardButtonContent}
            </Button>
          ) : (
            <Link
              href={getPathForLocale(
                locale,
                `manage/cards/innbusiness-pay/add?${accountHolderParam}`
              )}
              className="mobile:w-full"
            >
              <Button
                variant="default"
                data-testid={`${baseDataTestId}-add-card-button`}
                className={buttonStyle}
              >
                {addCardButtonContent}
              </Button>
            </Link>
          ))}
      </div>

      <InnBusinessPayFilters locale={locale} isCardHolderOnly={isCardHolderOnly} />

      <div className={tableContainerStyle}>
        <DataTable
          analyticsComponent={Analytics}
          pageIndex={searchParams?.pageIndex}
          columns={columns}
          getPage={getInnBusinessPayTablePage}
          getTotal={getInnBusinessPayTableTotal}
          getFilters={() => ({
            cancelledCards: !!searchParams?.[INN_BUSINESS_PAY_TAB_SEARCH_PARAM.CANCELLED_CARDS],
            onlyMyCards: !!searchParams?.[INN_BUSINESS_PAY_TAB_SEARCH_PARAM.ONLY_MY_CARDS],
          })}
          noResultsComponent={
            <CardNoResults
              hideAddCardButton={!canAddCard || isAccountSuspended}
              locale={locale}
              addCardUrl={getPathForLocale(
                locale,
                `manage/cards/innbusiness-pay/add?${accountHolderParam}`
              )}
            />
          }
        />
      </div>
    </div>
  );
}

const tableActionsStyle =
  'mt-12 mb-2 flex justify-between items-center w-full mobile:flex-col mobile:items-start';
const tableActionsLeftStyle =
  'text-xl font-bold flex mobile:w-full mobile:justify-between mobile:items-center';
const tableActionsRightStyle = 'flex w-full';
const tableContainerStyle = 'mt-6';
const cardIconStyle = 'mr-2 mobile:hidden';
const cardNoHeaderStyle =
  'w-[30%] md:w-[15%] lg:w-[18%] md:table-cell mobile:px-2 mobile:whitespace-normal mobile:break-words mobile:overflow-visible mobile:text-clip tablet:whitespace-normal ib-word-break';
const cardNoStyle = 'flex items-center mobile:px-2';
const cardColumnStyle = 'md:table-cell mobile:px-2 truncate';
const desktopOnlyStyleHeader =
  'hidden md:table-cell mobile:whitespace-normal tablet:whitespace-normal';
const desktopOnlyStyle = 'hidden md:table-cell truncate';
const buttonStyle = 'mobile:w-full mobile:mt-4';
const cardHolderHeaderStyle = 'w-[40%] md:w-[25%] mobile:px-2';
const buttonIconStyle = 'mr-2';
const accountHolderStyle = 'mobile:hidden';
const accountHolderMobileStyle = 'hidden mobile:block';
const actionsHeaderStyle = 'w-[30%] md:w-[15%]';
const actionsCellStyle = 'mobile:whitespace-nowrap mobile:overflow-visible mobile:text-clip';
