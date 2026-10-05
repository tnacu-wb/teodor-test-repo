import { LOCALES, CompanyType } from '@whitbread-eos/api';
import { Button, TableCell, TableRow } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  formatIBAssetsUrl,
  ID_TOKEN_COOKIE,
  getCompanyDetails,
  getTranslations,
  getEmployeesWithFilteringOptions,
  getSearchParams,
  getPathForLocale,
  cn,
  sanitize,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import Image from 'next/image';
import Link from 'next/link';

import {
  DataTable,
  DataTableCell,
  DataTableColumn,
  DataTableRow,
  LoadMoreRowsAndToken,
} from '~components/innBusiness/DataTable';
import {
  DATA_TABLE_PAGE_SIZE,
  DataTableRowLoadMore,
} from '~components/innBusiness/DataTable/data-table';
import { DownloadButton } from '~components/innBusiness/DownloadButton/download-button';
import { Analytics } from '~components/innBusiness/ManageEmployeesAnalytics';
import { getAnalyticsPageName } from '~components/innBusiness/ManageEmployeesAnalytics/utils';
import { UserActions } from '~components/innBusiness/UserActions';
import { UserInitials } from '~components/innBusiness/UserInitials';
import { UserNoResults } from '~components/innBusiness/UserNoResults';
import { UserRoleLabel } from '~components/innBusiness/UserRoleLabel';
import { UserStatus } from '~components/innBusiness/UserStatus';

import { CompanyEmployees } from './company-employees';
import { ManagePendingEmployees } from './manage-pending-employees';
import { PageSubtitle } from './page-subtitle';
import { SearchEmployeeInput } from './search-employee';

type Props = {
  locale?: LOCALES;
};

export const getEmployeesTotal = async (): Promise<number> => {
  return DATA_TABLE_PAGE_SIZE;
};

const LOG_PAGE_NAME = 'manage-employees';

export async function InnBusiness({ locale }: Props) {
  const baseDataTestId = 'InnBusinessTab';
  const { language } = getCountryLanguageByLocale(locale);
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';

  const { companyId, employeeId, accessLevel } = getDetailsFromToken(token);
  const logContext = {
    pageName: LOG_PAGE_NAME,
    userId: employeeId,
    companyId: companyId,
  };

  const [{ t, translations }, companyDetailsObject, searchParams] = await Promise.all([
    getTranslations(language, ['users', 'icons']),
    getCompanyDetails(companyId, token, true, logContext),
    getSearchParams(),
  ]);

  const icons = translations?.['icons'] ?? {};

  const getEmployeesPage = async (): Promise<DataTableRowLoadMore> => {
    const result = await getEmployeesWithFilteringOptions(
      companyId,
      DATA_TABLE_PAGE_SIZE,
      token,
      1,
      userSearch,
      '',
      undefined,
      false,
      false,
      logContext
    );

    return { rows: result?.employees ?? [], pageToken: result?.pageToken };
  };

  const { numberOfEmployees, companyName } =
    companyDetailsObject?.requestedCompany?.companyDetails ?? {};
  const companyType = companyDetailsObject?.requestedCompany?.companyType;

  const columns: DataTableColumn[] = [
    {
      id: 'accountName',
      label: t('users.userMgmt.manageEmployees.seachresults.header.name'),
      headerClassName: cardLabelHeaderStyle,

      render(_cell: DataTableCell, row: DataTableRow) {
        const type = {
          title: row?.title || '',
          firstName: row?.firstName || '',
          lastName: row?.lastName || '',
          emailAddress: row?.emailAddress || '',
        };

        return <UserInitials type={type} />;
      },
    },
    {
      id: 'accessLevel',
      label: t('users.userMgmt.manageEmployees.seachresults.header.userRole'),
      headerClassName: desktopOnlyStyle,
      className: desktopOnlyStyle,

      render(cell: DataTableCell) {
        return <UserRoleLabel locale={locale} type={cell} />;
      },
    },
    {
      id: 'employeeStatus',
      label: t('users.userMgmt.manageEmployees.seachresults.header.status'),
      headerClassName: desktopOnlyStyle,
      className: desktopOnlyStyle,

      render(cell: DataTableCell, row: DataTableRow) {
        const userDetails = {
          title: row?.title || '',
          firstName: row?.firstName || '',
          lastName: row?.lastName || '',
          email: row?.emailAddress || '',
          employeeId: row.id,
        };

        return (
          <UserStatus
            locale={locale}
            type={cell}
            userDetails={userDetails}
            companyId={companyId}
            token={token}
          />
        );
      },
    },
    {
      id: 'actions',
      label: '',
      headerClassName: actionsHeaderStyle,
      className: actionStyle,

      render(_cell: DataTableCell, row: DataTableRow) {
        return <UserActions locale={locale} row={row} />;
      },
    },
  ];

  const userSearch = sanitize((searchParams?.get('userSearch') as string) ?? '');

  const loadMoreLabel = t('users.userMgmt.manageEmployees.seachresults.loadMore');

  const getExtraRows = async (loadMoreToken: string, clickCount: number) => {
    'use server';

    const { employees: rows, pageToken } = await getEmployeesWithFilteringOptions(
      companyId,
      DATA_TABLE_PAGE_SIZE,
      token,
      1,
      userSearch,
      loadMoreToken,
      undefined,
      false,
      false,
      logContext
    );

    const columnsForLoadMore: DataTableColumn[] = [
      {
        id: 'accountName',
        label: '',
        headerClassName: cardLabelHeaderStyle,

        render(_cell: DataTableCell, row: DataTableRow) {
          const type = {
            title: row?.title || '',
            firstName: row?.firstName || '',
            lastName: row?.lastName || '',
            emailAddress: row?.emailAddress || '',
          };

          return <UserInitials type={type} />;
        },
      },
      {
        id: 'accessLevel',
        label: '',
        headerClassName: desktopOnlyStyle,
        className: desktopOnlyStyle,

        render(cell: DataTableCell) {
          return <UserRoleLabel locale={locale} type={cell} />;
        },
      },
      {
        id: 'employeeStatus',
        label: '',
        headerClassName: desktopOnlyStyle,
        className: desktopOnlyStyle,

        render(cell: DataTableCell, row: DataTableRow) {
          const userDetails = {
            title: row?.title || '',
            firstName: row?.firstName || '',
            lastName: row?.lastName || '',
            email: row?.emailAddress || '',
            employeeId: row.id,
          };

          return (
            <UserStatus
              locale={locale}
              type={cell}
              userDetails={userDetails}
              companyId={companyId}
              token={token}
            />
          );
        },
      },
      {
        id: 'actions',
        label: '',
        headerClassName: actionsHeaderStyle,
        className: actionStyle,

        render(_cell: DataTableCell, row: DataTableRow) {
          return <UserActions locale={locale} row={row} />;
        },
      },
    ];

    const result = (
      <>
        {rows.map((row: DataTableRow, rowIndex: number) => (
          <TableRow
            data-row={JSON.stringify(row)}
            data-testid={`${baseDataTestId}-row-loadMore-${clickCount}-${rowIndex}`}
            key={`loadMore-${rowIndex}`}
          >
            {columnsForLoadMore.map((column: DataTableColumn) => {
              const cell = row[column.id];
              return (
                <TableCell
                  key={column.id}
                  data-testid={`${baseDataTestId}-row-loadMore-${clickCount}-${column.id}-${rowIndex}`}
                  className={cn(cellStyle, column.className)}
                >
                  {column.render ? column.render(cell, row) : cell}
                </TableCell>
              );
            })}
          </TableRow>
        ))}
        <Analytics
          employeeStatusAnalyticsDataExtraRows={rows}
          extraRowsRequestedCount={clickCount}
        />
      </>
    );

    return { result, pageToken } as LoadMoreRowsAndToken;
  };

  return (
    <div data-testid={`${baseDataTestId}-container`}>
      <ManagePendingEmployees companyId={companyId} locale={locale} accessLevel={accessLevel} />
      <div className={tableActionsStyle} data-testid={`${baseDataTestId}-first-row`}>
        <div className="flex w-full">
          <div className={tableActionsLeftStyle}>
            <PageSubtitle
              icons={icons}
              title={t('users.userMgmt.manageEmployees.employeeManagement.title')}
              message={t('users.userMgmt.manageEmployees.employeeManagement.tooltipMsg')}
            />
            <CompanyEmployees
              numberOfEmployees={numberOfEmployees}
              companyName={companyName}
              locale={locale}
            />
          </div>
          {companyType !== CompanyType.BUSINESS_PAY && (
            <DownloadButton
              className={downloadButtonStyle}
              testId={baseDataTestId}
              token={token}
              companyId={companyId}
              icons={icons}
            />
          )}
        </div>

        <Link href={getPathForLocale(locale, `manage/employees/add`)} className="mobile:w-full">
          <Button
            variant="default"
            data-testid={`${baseDataTestId}-add-employee-button`}
            className={addEmployeeButtonStyle}
          >
            <Image
              alt={'Add employeee button'}
              src={formatIBAssetsUrl(icons['icon.addEmployee-icon'])}
              width={26}
              height={26}
              className={buttonIconStyle}
              data-testid={`${baseDataTestId}-add-employee-button-icon`}
            />
            {t('users.userMgmt.manageEmployees.addEmployeeCta.label')}
          </Button>
        </Link>
      </div>
      <div className={tableActionsStyleSecondRow} data-testid={`${baseDataTestId}-second-row`}>
        <div className={tableActionsLeftStyle}>
          <SearchEmployeeInput
            valueFromUrl={userSearch}
            searchIcon={icons['icon.search-icon']}
            searchInputPlaceHolder={t('users.userMgmt.manageEmployees.seach.placeholder')}
            clearIcon={icons['icon.input.clear']}
          />
        </div>
      </div>

      <div className={tableContainerStyle}>
        <DataTable
          analyticsComponent={Analytics}
          pageIndex={searchParams['pageIndex'] as string}
          columns={columns}
          getPage={getEmployeesPage}
          getTotal={getEmployeesTotal}
          noResultsComponent={<UserNoResults locale={locale} />}
          hasLoadMorePagination={true}
          getExtraRows={getExtraRows}
          loadMoreLabel={loadMoreLabel}
        />
      </div>

      <Analytics pageName={getAnalyticsPageName(searchParams)} />
    </div>
  );
}

const tableActionsStyle =
  'mt-12 flex justify-between items-center w-full mobile:flex-col mobile:items-start';
const tableActionsStyleSecondRow =
  'mt-6 flex justify-between items-center w-full mobile:flex-col mobile:items-start';
const tableActionsLeftStyle = 'flex flex-col mobile:w-full mobile:justify-between mobile:flex-col';
const addEmployeeButtonStyle = 'ml-6 mobile:w-full mobile:mt-4 mobile:ml-0';
const buttonIconStyle = 'mr-2';
const downloadButtonStyle = 'border border-secondaryColor ml-auto';
const tableContainerStyle = 'mt-6';
const actionsHeaderStyle = 'mobile:w-[100px] tablet:w-[100px]';
const actionStyle = 'flex justify-end items-center h-[61px]';
const desktopOnlyStyle = 'mobile:hidden tablet:hidden';
const cardLabelHeaderStyle = 'mobile:w-full tablet:w-full';
const cellStyle = 'mobile:w-full tablet:w-full mobile:truncate';
