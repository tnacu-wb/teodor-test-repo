import { Flex, Spinner } from '@chakra-ui/react';
import {
  FT_PI_PROMO_CODE_LANDING_PAGE,
  FT_CCUI_PROMO_CODE_LANDING_PAGE,
  FT_BB_PROMO_CODE_LANDING_PAGE,
} from '@whitbread-eos/api';
import {
  useFeatureToggle,
  formatDataTestId,
  type PromoActionsType,
  deleteCookie,
} from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import React, { useEffect, useState } from 'react';

import { Price, Dismiss } from '../../assets/icons';
import Icon from '../Icon';

interface Props {
  rateDiscountTags?: string[];
  customStyleName: string;
  promoActions?: PromoActionsType;
}

const isNonEmptyString = (value: unknown): value is string =>
  typeof value === 'string' && value.trim().length > 0;

export default function PromoTag({
  rateDiscountTags,
  customStyleName,
  promoActions,
}: Readonly<Props>) {
  const router = useRouter();
  const [isRemoving, setIsRemoving] = useState(false);
  const {
    [FT_PI_PROMO_CODE_LANDING_PAGE]: isPromoCodeLandingPageEnabled,
    [FT_CCUI_PROMO_CODE_LANDING_PAGE]: isCcuiPromoCodeLandingPageEnabled,
    [FT_BB_PROMO_CODE_LANDING_PAGE]: isBBPromoCodeLandingPageEnabled,
  } = useFeatureToggle();

  const isPromoTagEnabled =
    isPromoCodeLandingPageEnabled ||
    isCcuiPromoCodeLandingPageEnabled ||
    isBBPromoCodeLandingPageEnabled;

  const [validTags, setValidTags] = useState<string[]>([]);

  useEffect(() => {
    if (isPromoTagEnabled) {
      const filteredTags = rateDiscountTags?.filter(isNonEmptyString) ?? [];
      setValidTags(filteredTags);
    } else {
      setValidTags([]);
    }
  }, [isPromoTagEnabled, rateDiscountTags]);

  if (validTags.length === 0) return null;

  const getCustomStyle = (customStyleName: string) => {
    switch (customStyleName) {
      case 'rateItem':
        return customStyles.rateItemStyles;
      case 'basketComponent':
        return customStyles.basketComponentStyles;
      case 'BookingSummaryCard':
        return customStyles.BookingSummaryCardStyles;
      default:
        return {};
    }
  };

  const mergerdStyles = {
    ...basePlanRateStyles,
    ...getCustomStyle(customStyleName),
  };

  return (
    <Flex data-testid="PromotionTagComponent" {...promoWrapper}>
      {validTags.map((tag, index) => (
        <Flex key={index} {...mergerdStyles}>
          <Icon svg={<Price />} {...iconStyles} />
          {tag.trim()}
          {promoActions && promoActions?.shouldShowRemoveButton && (
            <Flex
              {...iconDissMissStyles}
              data-testid={formatDataTestId('promoDiscountTag', 'remove')}
              onClick={() => {
                deleteCookie('appliedPromoBoxCode');
                if (isRemoving) return;
                setIsRemoving(true);
                router.reload();
              }}
            >
              {isRemoving ? (
                <Spinner size="xs" thickness="2px" />
              ) : (
                <Icon svg={<Dismiss color="#00798e" width="0.875rem" height="0.875rem" />} />
              )}
            </Flex>
          )}
        </Flex>
      ))}
    </Flex>
  );
}

const iconDissMissStyles = {
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  marginLeft: '0.4rem',
  cursor: 'pointer',
  transition: 'transform 0.2s ease, opacity 0.2s ease',
  _hover: {
    transform: 'scale(1.2)',
    opacity: 0.8,
  },
};

const iconStyles = {
  display: 'flex',
  alignItems: 'center',
  height: '1rem',
  width: '1rem',
  marginRight: '0.1875rem',
};

const basePlanRateStyles = {
  color: 'primary',
  backgroundColor: 'tooltipInfo',
  display: 'inline-flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  height: '1.625rem',
  borderRadius: '0.25rem',
  padding: '0.25rem 0.5rem',
  fontSize: '0.8125rem',
  fontWeight: 'bold',
  whiteSpace: 'nowrap',
};

const promoWrapper = {
  display: 'inline-flex',
};

const customStyles = {
  rateItemStyles: { mx: 'sm' },
  basketComponentStyles: { ml: 'sm' },
  BookingSummaryCardStyles: { mt: 'sm' },
};
