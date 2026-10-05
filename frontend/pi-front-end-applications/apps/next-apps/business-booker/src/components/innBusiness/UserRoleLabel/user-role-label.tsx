import { LOCALES, AccessLevel } from '@whitbread-eos/api';
import { getCountryLanguageByLocale, getTranslations } from '@whitbread-eos/utils/server';

export type UserRoleProps = {
  type: AccessLevel;
  locale?: LOCALES;
};
const roleLabelType: Record<AccessLevel, string> = {
  [AccessLevel.Super]: 'userMgmt.manageEmployees.accountRole.TravelManager',
  [AccessLevel.Booker]: 'userMgmt.manageEmployees.accountRole.Booker',
  [AccessLevel.Self]: 'userMgmt.manageEmployees.accountRole.SelfBooker',
  [AccessLevel.Stayer]: 'userMgmt.manageEmployees.accountRole.Guest',
  [AccessLevel.BusinessPayManager]: 'userMgmt.manageEmployees.accountRole.bpManager',
  [AccessLevel.BusinessPayUser]: 'userMgmt.manageEmployees.accountRole.bpUser',
};

export async function UserRoleLabel({ locale, type }: UserRoleProps) {
  const { language } = getCountryLanguageByLocale(locale);
  const { t } = await getTranslations(language, 'users');
  const baseDataTestId = 'UserRoleLabel';

  return <span data-testid={baseDataTestId}>{t(roleLabelType[type as AccessLevel]) || type}</span>;
}
