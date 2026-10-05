import { CustomerAccountDetails, LOCALES } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getCommonIcons,
  getAccountPayments,
} from '@whitbread-eos/utils/server';

import PaymentsTableClient from './payments-table-client';

type Props = {
  token: string;
  account: CustomerAccountDetails;
  locale: LOCALES;
};

export const PER_PAGE = 15;

export async function PaymentsTable({ token, account, locale }: Props) {
  const { language } = getCountryLanguageByLocale(locale);
  const icons = await getCommonIcons(language);
  const baseDataTestId = 'PaymentsTable';
  const response = await getAccountPayments(
    token,
    account.accountNumber,
    1,
    PER_PAGE + 1,
    account.tetheredGuid
  );

  const payments = response?.payments ?? [];

  return (
    <div data-testid={`${baseDataTestId}-Container`}>
      <PaymentsTableClient
        icons={icons}
        baseDataTestId={baseDataTestId}
        initialItems={payments}
        token={token}
        account={account}
        pageSize={PER_PAGE}
        locale={locale}
      />
    </div>
  );
}
