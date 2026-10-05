'use client';

import { FT_PI_PROMO_CODE_LANDING_PAGE } from '@whitbread-eos/api';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

import useFeatureToggle from './use-feature-toggle';

export default function usePromotionsNotification() {
  const router = useRouter();
  const { PROMOID } = router.query;

  const [showPromotionsNotification, setShowPromotionsNotification] = useState(false);

  const { [FT_PI_PROMO_CODE_LANDING_PAGE]: isPromoCodeLandingPageEnabled } = useFeatureToggle();

  useEffect(() => {
    const isLandingPagePromoCodePresent = !!PROMOID;

    if (isPromoCodeLandingPageEnabled && isLandingPagePromoCodePresent) {
      setShowPromotionsNotification(true);
    }

    return () => {
      setShowPromotionsNotification(false);
    };
  }, [isPromoCodeLandingPageEnabled]);

  return { showPromotionsNotification };
}
