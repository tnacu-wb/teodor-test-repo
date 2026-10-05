import { Box } from '@chakra-ui/react';
import {
  FT_PI_BREAKFAST_PROMO_CODE,
  HIRoomRate,
  PROMO_CODE_COOKIE,
  SESSION_STORAGE_PROMO_COOKIE_SET,
  PageName,
} from '@whitbread-eos/api';
import { useFeatureToggle, setCookieWithDefaultDomain, getCookie } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

interface Props {
  roomRates?: HIRoomRate[];
  promoCodeFromUrl?: string | null; // string = URL value; null = URL-aware but no code; undefined = not URL-aware
}

export default function PromoCode({ roomRates, promoCodeFromUrl }: Readonly<Props>) {
  const { t } = useTranslation();
  const router = useRouter();

  const { [FT_PI_BREAKFAST_PROMO_CODE]: isBreakfastPromoCodeEnabled } = useFeatureToggle();

  const breakfastPromoCode = t('config.experiments.breakfastPromoCode.code');
  const enabled = t('config.experiments.breakfastPromoCode.isAvailable') === 'true';
  const availableText = t('config.experiments.breakfastPromoCode.available');
  const notAvailableText = t('config.experiments.breakfastPromoCode.notAvailable');
  const selectionInvalidText = t('config.experiments.breakfastPromoCode.selectionInvalid');
  const [hasValidCookie, setHasValidCookie] = useState(false);
  const isValidUrlCode = !!promoCodeFromUrl && promoCodeFromUrl === breakfastPromoCode;

  useEffect(() => {
    setHasValidCookie(getCookie(PROMO_CODE_COOKIE) === breakfastPromoCode);
  }, [breakfastPromoCode]);

  useEffect(() => {
    if (promoCodeFromUrl === undefined || !isBreakfastPromoCodeEnabled) return;

    const currentPromoCodeCookie = getCookie(PROMO_CODE_COOKIE);
    const isPromoCookieSetByUrlFlow =
      sessionStorage.getItem(SESSION_STORAGE_PROMO_COOKIE_SET) === 'true';

    if (isValidUrlCode && enabled) {
      setCookieWithDefaultDomain(PROMO_CODE_COOKIE, breakfastPromoCode, undefined);
      sessionStorage.setItem(SESSION_STORAGE_PROMO_COOKIE_SET, 'true');
    } else if (currentPromoCodeCookie === breakfastPromoCode || isPromoCookieSetByUrlFlow) {
      setCookieWithDefaultDomain(PROMO_CODE_COOKIE, null, -1);
      sessionStorage.removeItem(SESSION_STORAGE_PROMO_COOKIE_SET);
    }
    setHasValidCookie(getCookie(PROMO_CODE_COOKIE) === breakfastPromoCode);
  }, [promoCodeFromUrl, isBreakfastPromoCodeEnabled, isValidUrlCode, enabled, breakfastPromoCode]);

  if (!isBreakfastPromoCodeEnabled) {
    return null;
  }

  const promoBannerVisibilityPages: PageName[] = t(
    'config.experiments.breakfastPromoCode.promoBannerVisibility'
  )
    .split(',')
    .map((page) => page.trim())
    .filter(Boolean) as PageName[];

  const getPageName = (): PageName | null => {
    if (router.pathname.includes('home')) return PageName.HOME;
    if (router.pathname.includes('search')) return PageName.SRP;
    if (router.pathname.includes('hotels')) return PageName.HDP;
    return null;
  };
  const currentPage = getPageName();
  const isPromoBannerVisible =
    promoBannerVisibilityPages.length === 0 ||
    (currentPage !== null && promoBannerVisibilityPages.includes(currentPage));

  // URL-based path: valid code but feature is not available — show notAvailable banner without cookie
  if (isValidUrlCode && !enabled && isPromoBannerVisible) {
    return (
      <Box data-testid="HeaderBreakfastPromoCode" {...headerBreakfast} {...notAvailable}>
        {notAvailableText}
      </Box>
    );
  }

  // Cookie-based path: valid cookie drives the available / notAvailable / selectionInvalid states
  if (hasValidCookie && isPromoBannerVisible) {
    let styles = enabled ? available : notAvailable;
    let text = enabled ? availableText : notAvailableText;

    if (enabled && Array.isArray(roomRates)) {
      const hasPromoRate = roomRates.some(
        (roomRate: HIRoomRate) => roomRate.promotionCode === breakfastPromoCode
      );
      if (!hasPromoRate) {
        styles = notAvailable;
        text = selectionInvalidText;
      }
    }

    return (
      <Box data-testid="HeaderBreakfastPromoCode" {...headerBreakfast} {...styles}>
        {text}
      </Box>
    );
  }
  return null;
}

const headerBreakfast = {
  align: 'center',
  padding: '0.125rem',
  fontWeight: 'bold',
  fontSize: '1rem',
};

const available = {
  background: 'var(--chakra-colors-promoCodeAvailable)',
};

const notAvailable = {
  background: 'var(--chakra-colors-promoCodeNotAvailable)',
  color: 'var(--chakra-colors-baseWhite)',
};
