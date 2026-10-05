/* Recommendation: Use UPPERCASE for page names. This is added as an experimental way to have everything w.r.t. Pages, associated Feature Flags, etc at one place. This needs to be taken care of by everyone from the next time/when you refactor
 */
import {
  FT_CCUI_AMEND_GUEST_ADDRESS,
  FT_CCUI_PRE_POPULATE_BILLING_ADDRESS,
  FT_PI_BB_CCUI_TRIP_ADVISOR,
  FT_CCUI_GDP_BILLING_ADDRESS,
  FT_CCUI_GDP_MULTI_BOOKING,
  FT_CCUI_GDP_SINGLE_BOOKING,
  FT_CCUI_ADDITIONAL_INFORMATION,
  FT_CCUI_PRE_CHECK_IN_LABEL,
  FT_CCUI_CHANGE_PAYMENT_METHOD,
  FT_CCUI_ACCOMPANYING_GUEST_DETAILS,
  FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS,
  FT_CCUI_SEARCH_NEGOTIATED_RATES_BY_CORP_ID,
  FT_PI_DISCOUNT_RATE,
  FT_PI_BB_CCUI_NONSILENT_SUBSTITUTION_PER_ROOMCLASS,
  FT_PI_BB_CCUI_MAXROOMS_AMEND,
  FT_PI_BB_CCUI_DISABLE_PAYMENTS,
  FT_CCUI_ENABLE_72H_UK_NOTIFICATION,
  FT_CCUI_ENABLE_72H_DE_NOTIFICATION,
  FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE,
  FT_PI_BB_CCUI_ROOMS_DISCLAIMER,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  FT_CCUI_PRICE_PER_NIGHT,
  FT_PI_BB_CCUI_CHOOSE_ROOM_TYPE,
  FT_PI_BB_CCUI_SHOW_MEALS_FREE,
  FT_PI_CCUI_CONSOLIDATE_MOBILE_LANDLINE,
  FT_PI_BB_CCUI_HDP_IMAGE_GALLERY_REDESIGN,
  FT_PI_BB_CCUI_SRP_MULTIPLE_IMAGES,
  FT_CCUI_SHOW_PROMOTION_BOX,
  FT_CCUI_UNIQUE_PROMO_CODE,
  FT_CCUI_BOOKING_HISTORY_REDESIGN,
  FT_CCUI_PROMO_CODE_LANDING_PAGE,
  FT_CCUI_PROMO_CODE_SITE_WIDE,
  FT_PI_PIB_CCUI_SHOW_LOWEST_PRICE_MONTH_TAB,
  FT_PI_PIB_CCUI_SHOW_FILTER_ROOM_TYPES,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT,
  FT_PI_PIB_CCUI_CITY_TAX_AMEND,
  FT_PI_PIB_CCUI_ENABLE_HUB_HOTELS_POA,
  FT_CCUI_FREE_FNB_AND_EXTRAS,
  FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE,
  FT_SRP_DYNAMIC_FILTERS,
  FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
  FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION,
  FT_PI_REDIS_RQ_CACHE,
} from '@whitbread-eos/api';

const commonFeatureToggles = {
  [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
  [FT_PI_REDIS_RQ_CACHE]: false,
};

const globalFeatureToggles = {
  [FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION]: false,
};

export const PAGE = {
  SEARCH: {
    featureToggles: {
      appPage: 'CCUI | SRP | Search Results Page',
      flagsWithFallback: {
        ...globalFeatureToggles,
        ...commonFeatureToggles,
        [FT_CCUI_SEARCH_NEGOTIATED_RATES_BY_CORP_ID]: false,
        [FT_PI_DISCOUNT_RATE]: false,
        [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: false,
        [FT_CCUI_PRICE_PER_NIGHT]: false,
        [FT_PI_BB_CCUI_SHOW_MEALS_FREE]: false,
        [FT_PI_BB_CCUI_SRP_MULTIPLE_IMAGES]: false,
        [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
        [FT_CCUI_PROMO_CODE_SITE_WIDE]: false,
        [FT_CCUI_UNIQUE_PROMO_CODE]: false,
        [FT_SRP_DYNAMIC_FILTERS]: false,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: false,
      },
    },
  },
  AMEND: {
    DETAILS: {
      featureToogles: {
        appPage: 'ccui:amend:details',
        flagsWithFallback: {
          ...globalFeatureToggles,
          ...commonFeatureToggles,
          [FT_CCUI_AMEND_GUEST_ADDRESS]: false,
          [FT_PI_DISCOUNT_RATE]: false,
          [FT_PI_BB_CCUI_MAXROOMS_AMEND]: false,
          [FT_PI_BB_CCUI_SHOW_MEALS_FREE]: false,
          [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
          [FT_CCUI_PROMO_CODE_SITE_WIDE]: false,
          [FT_CCUI_UNIQUE_PROMO_CODE]: false,
          [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: false,
          [FT_PI_PIB_CCUI_CITY_TAX_AMEND]: false,
        },
      },
    },
  },
  SRP: {
    featureToggles: {
      appPage: 'CCUI | SRP | Search Results Page',
      flagsWithFallback: {
        ...globalFeatureToggles,
        ...commonFeatureToggles,
      },
    },
  },
  SEARCH_ACCOUNT: {
    featureToggles: {
      appPage: 'CCUI | Search Account Page',
      flagsWithFallback: {
        ...commonFeatureToggles,
      },
    },
  },
  HDP: {
    featureToggles: {
      appPage: 'CCUI | Hotel Details Page',
      flagsWithFallback: {
        ...globalFeatureToggles,
        ...commonFeatureToggles,
        [FT_CCUI_PRE_POPULATE_BILLING_ADDRESS]: false,
        [FT_PI_BB_CCUI_TRIP_ADVISOR]: false,
        [FT_CCUI_SEARCH_NEGOTIATED_RATES_BY_CORP_ID]: false,
        [FT_PI_DISCOUNT_RATE]: false,
        [FT_PI_BB_CCUI_NONSILENT_SUBSTITUTION_PER_ROOMCLASS]: false,
        [FT_CCUI_ENABLE_72H_UK_NOTIFICATION]: false,
        [FT_CCUI_ENABLE_72H_DE_NOTIFICATION]: false,
        [FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE]: false,
        [FT_PI_BB_CCUI_ROOMS_DISCLAIMER]: false,
        [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: false,
        [FT_PI_BB_CCUI_CHOOSE_ROOM_TYPE]: false,
        [FT_CCUI_PRICE_PER_NIGHT]: false,
        [FT_PI_BB_CCUI_SHOW_MEALS_FREE]: false,
        [FT_PI_BB_CCUI_HDP_IMAGE_GALLERY_REDESIGN]: false,
        [FT_CCUI_SHOW_PROMOTION_BOX]: false,
        [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
        [FT_CCUI_PROMO_CODE_SITE_WIDE]: false,
        [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: false,
        [FT_CCUI_UNIQUE_PROMO_CODE]: false,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: false,
      },
    },
  },
  GUEST_DETAILS: {
    featureToggles: {
      appPage: 'CCUI | GDP | Guest Details Page',
      flagsWithFallback: {
        ...globalFeatureToggles,
        ...commonFeatureToggles,
        [FT_CCUI_GDP_BILLING_ADDRESS]: false,
        [FT_CCUI_GDP_MULTI_BOOKING]: false,
        [FT_CCUI_GDP_SINGLE_BOOKING]: false,
        [FT_CCUI_PRE_CHECK_IN_LABEL]: false,
        [FT_CCUI_ADDITIONAL_INFORMATION]: false,
        [FT_CCUI_ACCOMPANYING_GUEST_DETAILS]: false,
        [FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS]: false,
        [FT_PI_CCUI_CONSOLIDATE_MOBILE_LANDLINE]: false,
        [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
        [FT_CCUI_PROMO_CODE_SITE_WIDE]: false,
        [FT_CCUI_UNIQUE_PROMO_CODE]: false,
        [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: false,
        [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: false,
        [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: false,
      },
    },
  },
  PAGE_HELPER: {
    BOOKINGS: {
      featureToggles: {
        appPage: 'CCUI | Bookings Page',
        flagsWithFallback: {
          ...globalFeatureToggles,
          ...commonFeatureToggles,
          [FT_CCUI_CHANGE_PAYMENT_METHOD]: false,
          [FT_CCUI_SEARCH_NEGOTIATED_RATES_BY_CORP_ID]: false,
          [FT_CCUI_BOOKING_HISTORY_REDESIGN]: false,
          [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
          [FT_CCUI_PROMO_CODE_SITE_WIDE]: false,
          [FT_CCUI_UNIQUE_PROMO_CODE]: false,
          [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: false,
        },
      },
    },
    REPEAT_BOOKING: {
      featureToggles: {
        appPage: 'CCUI | Repeat Booking Page',
        flagsWithFallback: {
          ...globalFeatureToggles,
          ...commonFeatureToggles,
          [FT_CCUI_SEARCH_NEGOTIATED_RATES_BY_CORP_ID]: false,
        },
      },
    },
  },
  PAYMENT: {
    featureToggles: {
      appPage: 'CCUI | PMTP | Payment Page',
      flagsWithFallback: {
        ...globalFeatureToggles,
        ...commonFeatureToggles,
        [FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS]: false,
        [FT_PI_BB_CCUI_DISABLE_PAYMENTS]: false,
        [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
        [FT_CCUI_PROMO_CODE_SITE_WIDE]: false,
        [FT_CCUI_UNIQUE_PROMO_CODE]: false,
        [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: false,
        [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: false,
        [FT_PI_PIB_CCUI_ENABLE_HUB_HOTELS_POA]: false,
        [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: false,
        [FT_CCUI_GDP_BILLING_ADDRESS]: false,
      },
    },
  },
  CHOOSE_ROOMTYPE: {
    featureToggles: {
      appPage: 'CCUI | Choose Roomtype Page',
      flagsWithFallback: {
        ...globalFeatureToggles,
        ...commonFeatureToggles,
        [FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE]: false,
        [FT_PI_BB_CCUI_CHOOSE_ROOM_TYPE]: false,
        [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
        [FT_CCUI_PROMO_CODE_SITE_WIDE]: false,
        [FT_CCUI_UNIQUE_PROMO_CODE]: false,
        [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: false,
      },
    },
  },
  PRICE_FINDER: {
    featureToggles: {
      appPage: 'CCUI | Price Finder Page',
      flagsWithFallback: {
        ...globalFeatureToggles,
        ...commonFeatureToggles,
        [FT_PI_PIB_CCUI_SHOW_LOWEST_PRICE_MONTH_TAB]: false,
        [FT_PI_PIB_CCUI_SHOW_FILTER_ROOM_TYPES]: false,
        [FT_CCUI_UNIQUE_PROMO_CODE]: false,
      },
    },
  },
  ANCILLARIES: {
    featureToogles: {
      appPage: 'CCUI | Ancillaries Page',
      flagsWithFallback: {
        ...globalFeatureToggles,
        ...commonFeatureToggles,
        [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
        [FT_CCUI_PROMO_CODE_SITE_WIDE]: false,
        [FT_CCUI_UNIQUE_PROMO_CODE]: false,
        [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: false,
        [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: false,
        [FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE]: false,
        [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: false,
        [FT_PI_BB_CCUI_CHOOSE_ROOM_TYPE]: false,
        [FT_PI_BB_CCUI_SHOW_MEALS_FREE]: false,
        [FT_CCUI_FREE_FNB_AND_EXTRAS]: false,
      },
    },
  },
  UNIQUE_PROMOTIONS: {
    featureToggles: {
      appPage: 'CCUI | Unique Promotions',
      flagsWithFallback: {
        ...globalFeatureToggles,
        ...commonFeatureToggles,
        [FT_CCUI_UNIQUE_PROMO_CODE]: false,
      },
    },
  },
  CONFIRMATION: {
    featureToggles: {
      appPage: 'CCUI | Confirmation Page',
      flagsWithFallback: {
        ...globalFeatureToggles,
        ...commonFeatureToggles,
        [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: false,
        [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
        [FT_CCUI_PROMO_CODE_SITE_WIDE]: false,
      },
    },
  },
};
