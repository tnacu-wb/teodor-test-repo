import { CustomerAccountDetails, LOCALES, Scheme } from '@whitbread-eos/api';
import { Alert, AlertDescription, AlertTitle, SanitizedContent } from '@whitbread-eos/atoms/ui';
import {
  getTranslations,
  getCountryLanguageByLocale,
  formatAccountNumber,
  getWorldlineReturnUrl,
} from '@whitbread-eos/utils/server';
import { Info } from 'lucide-react';
import React from 'react';

import { WordlineButton } from '../WordlineButton';

type MakeAPaymentLinkProps = {
  tetheredGuid: string;
  scheme?: Scheme;
  text: string;
  makePaymentPage: string;
  returnUrl: string;
  postUrl?: string;
};
const MakeAPaymentLink = ({
  tetheredGuid,
  scheme,
  text,
  makePaymentPage,
  returnUrl,
  postUrl,
}: MakeAPaymentLinkProps) => (
  <WordlineButton
    baseDataTestId={`Notifications-AccountSuspended-Inline-Make-a-payment`}
    tetheredGuid={tetheredGuid}
    scheme={scheme}
    text={text}
    page={makePaymentPage}
    variant="link"
    size="md"
    returnUrl={returnUrl}
    postUrl={postUrl}
  />
);

type Props = {
  account: CustomerAccountDetails | null;
  locale: LOCALES;
  isShown?: boolean;
};

export async function SuspendedNotification({ locale, account, isShown }: Props) {
  const { language } = getCountryLanguageByLocale(locale);
  const { t } = await getTranslations(language, ['notifications']);

  const returnUrl = getWorldlineReturnUrl(locale, 'homepage');

  const wlPostUrl =
    account?.scheme === 'DE'
      ? process?.env?.NEXT_PUBLIC_WORLDLINE_POST_URL_DE
      : process?.env?.NEXT_PUBLIC_WORLDLINE_POST_URL;
  const makePaymentPage = account?.scheme === 'DE' ? 'InterimPayment.aspx' : 'CardPayment.aspx';

  const suspendedDescriptionPartOne = t('notifications.notification.account.suspended.subtitle')
    .replace('{account_name}', `<strong>${account?.accountName ?? ''}</strong>`)
    .replace(
      '{account_number}',
      `<strong>${formatAccountNumber(account?.accountNumber ?? '')}</strong>`
    )
    .split('{make_a_payment_link}')[0];
  const suspendedDescriptionPartTwo = t(
    'notifications.notification.account.suspended.subtitle'
  ).split('{make_a_payment_link}')[1];

  if (!isShown || !account) {
    return <></>;
  }

  return (
    <Alert variant="red" className="mb-4" data-testid="Notifications-AccountSuspended">
      <Info className="w-4 h-4" />
      <AlertTitle className="text-sm font-semibold">
        {t('notifications.notification.account.suspended.title')}
      </AlertTitle>
      <AlertDescription>
        <SanitizedContent>{suspendedDescriptionPartOne}</SanitizedContent>
        <MakeAPaymentLink
          tetheredGuid={account?.tetheredGuid ?? ''}
          scheme={account?.scheme}
          text={t('notifications.notification.account.suspended.subtitle.makeAPaymentLink')}
          makePaymentPage={makePaymentPage}
          returnUrl={returnUrl}
          postUrl={wlPostUrl ?? ''}
        />
        <SanitizedContent>{suspendedDescriptionPartTwo}</SanitizedContent>
      </AlertDescription>
    </Alert>
  );
}
