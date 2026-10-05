import { PathParams, FT_IB_CONTACT_US_CARD_LIVE_CHAT, CountryCode } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  getCommonIcons,
  getDetailsFromToken,
  getServerUnleashToggles,
  ID_TOKEN_COOKIE,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';

import { ContactUsContent } from './components/ContactUsContent';

type Props = {
  params?: Promise<PathParams>;
};
const PAGE_LABEL = 'IB | ContactUs';
const LOG_PAGE_NAME = 'contact-us' as const;

export default async function ContactUsPage({ params }: Props) {
  const resolvedParams = await params;
  const headerList = await headers();
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { employeeId, companyId } = getDetailsFromToken(token);
  const logContext = { pageName: LOG_PAGE_NAME, userId: employeeId, companyId };

  const locale = resolvedParams?.locale;
  const country = locale?.split('-')[1].toLowerCase();
  const currentPath = headerList.get('WB-Url') ?? '';
  const flagsFallback = {
    [FT_IB_CONTACT_US_CARD_LIVE_CHAT]: false,
  };

  const [{ translations }, icons, toggles] = await Promise.all([
    getTranslations(language, ['contact']),
    getCommonIcons(language, logContext),
    getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, new URLSearchParams(), {
      // 5th param is context object
      country: country || CountryCode.GB,
    }),
  ]);

  return (
    <TranslationProvider value={translations}>
      <ContactUsContent
        icons={icons}
        isContactUsLiveChatCardEnabled={toggles?.[FT_IB_CONTACT_US_CARD_LIVE_CHAT]}
      />
    </TranslationProvider>
  );
}
