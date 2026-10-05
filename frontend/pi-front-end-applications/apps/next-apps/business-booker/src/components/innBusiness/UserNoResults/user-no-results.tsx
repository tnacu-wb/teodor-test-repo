import { LOCALES } from '@whitbread-eos/api';
import { TableRow, TableCell, SanitizedContent } from '@whitbread-eos/atoms/ui';
import {
  cn,
  getCountryLanguageByLocale,
  getSearchParams,
  getTranslations,
  getPathForLocale,
  sanitize,
} from '@whitbread-eos/utils/server';

export type UserNoResultsProps = {
  locale?: LOCALES;
  className?: string;
};

export async function UserNoResults({ locale, className }: UserNoResultsProps) {
  const baseDataTestId = 'UserNoResults';
  const { language } = getCountryLanguageByLocale(locale);

  const [{ t }, searchParams] = await Promise.all([
    getTranslations(language, 'users'),
    getSearchParams(),
  ]);

  const userSearch = sanitize(searchParams?.get('userSearch') ?? '');

  return (
    <TableRow className={tableRowStyle}>
      <TableCell className={tableCellStyle}>
        <div data-testid={baseDataTestId} className={cn(containerStyle, className)}>
          <span data-testid={`${baseDataTestId}-first-row`} className={nameStyle}>
            <SanitizedContent replacements={{ '%searchTerm%': `'${userSearch}'` }}>
              {t('userMgmt.manageEmployees.seachresults.noEmployeesFoundErrorMsg')}
            </SanitizedContent>
          </span>
          <span data-testid={`${baseDataTestId}-second-row`} className={emailStyle}>
            <SanitizedContent
              replacements={{
                '{addEmployeesLink}': getPathForLocale(locale!, 'manage/employees/add'),
              }}
            >
              {t('userMgmt.manageEmployees.seachresults.retryMsg')}
            </SanitizedContent>
          </span>
        </div>
      </TableCell>
    </TableRow>
  );
}

const emailStyle = 'font-normal text-base text-darkGrey2';
const nameStyle = 'font-normal text-base text-darkGrey2';
const containerStyle = 'flex flex-col py-4 px-6 w-full';
const tableCellStyle = 'px-0 border-t-0';
const tableRowStyle = 'hover:bg-transparent';
