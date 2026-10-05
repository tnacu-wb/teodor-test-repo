import { LOCALES } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getPathForLocale,
  getTranslations,
} from '@whitbread-eos/utils/server';
import Link from 'next/link';

import { DataTableRow } from '~components/innBusiness/DataTable';

export type UserActionsProps = {
  row: DataTableRow;
  locale?: LOCALES;
};

export async function UserActions({ locale, row }: UserActionsProps) {
  const { language } = getCountryLanguageByLocale(locale);
  const { t } = await getTranslations(language, 'users');
  const baseDataTestId = 'UserActions';

  return (
    <Link
      data-testid={baseDataTestId}
      className={editStyle}
      href={getPathForLocale(locale, `manage/employees/${row.id}`)}
    >
      {t('userMgmt.manageEmployees.searchResults.editCta.label')}
    </Link>
  );
}
const editStyle = 'text-secondaryColor underline';
