import { FT_IB_OUT_OF_POLICY_REPORT } from '@whitbread-eos/api';
import {
  getPathForLocale,
  getCountryLanguageByLocale,
  getServerUnleashToggles,
  getTranslations,
} from '@whitbread-eos/utils/server';
import { headers } from 'next/headers';

import CardLink from '~components/innBusiness/CardLink/card-link';

export interface CardListProps {
  icons: Record<string, string>;
  locale: string;
}

const PAGE_LABEL = 'IB | PAYAPP | Pay Application Apply';

const flagsFallback = {
  [FT_IB_OUT_OF_POLICY_REPORT]: false,
};

export const getOutOfPolicyReportFF = async () => {
  const headersList = await headers();
  const currentPath = headersList.get('WB-Url') ?? '';
  const toggles = await getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, {});
  return toggles?.[FT_IB_OUT_OF_POLICY_REPORT];
};

const CardList = async ({ icons, locale }: CardListProps) => {
  const { language } = getCountryLanguageByLocale(locale);
  const { t } = await getTranslations(language, 'spending');
  const showOutOfPolicyReportCard = await getOutOfPolicyReportFF();

  return (
    <div className={cardGridStyle}>
      <CardLink
        title={t('spending.reporting.card.management.reporting.title')}
        subtitle={t('spending.reporting.card.management.reporting.subtitle')}
        alt={`${t('spending.reporting.card.management.reporting.title')} icon`}
        icon={icons?.['icon.meeting-rooms-icon'] ?? ''}
        href={getPathForLocale(locale, 'spending/management-information-report')}
        baseDataTestId="ManagementInformationReportCard"
      />
      {showOutOfPolicyReportCard && (
        <CardLink
          title={t('spending.reporting.card.policy.reporting.title')}
          subtitle={t('spending.reporting.card.policy.reporting.subtitle')}
          alt={`${t('spending.reporting.card.policy.reporting.title')} icon`}
          icon={icons?.['icon.file.icon.purple'] ?? ''}
          href={getPathForLocale(locale, 'spending/out-of-policy-report')}
          baseDataTestId="OutOfPolicyReportCard"
        />
      )}
      <CardLink
        title={t('spending.reporting.card.emergency.report.title')}
        subtitle={t('spending.reporting.card.emergency.report.subtitle')}
        alt={`${t('spending.reporting.card.emergency.report.title')} icon`}
        icon={icons?.['icon.bookings-icon'] ?? ''}
        href={getPathForLocale(locale, 'spending/emergency-report')}
        baseDataTestId="EmergencyReportCard"
      />
    </div>
  );
};

const cardGridStyle = 'grid grid-cols-3 mobile:grid-cols-1 gap-8';

export default CardList;
