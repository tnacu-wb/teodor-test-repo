import { Box, BoxProps, Flex, Text } from '@chakra-ui/react';
import { FT_PI_SUMMER_SALE_PROMO_BANNER, type PromotionBanner } from '@whitbread-eos/api';
import { Alert, ChevronDown24, ChevronUp24, Icon, Notification } from '@whitbread-eos/atoms';
import { useFeatureToggle, renderSanitizedHtml, formatAssetsUrl } from '@whitbread-eos/utils';
import { useState, useEffect } from 'react';

interface SummerPromoProps {
  promotionBanner: PromotionBanner;
  showNotification?: boolean;
}

export default function SummerPromo({
  promotionBanner,
  showNotification,
}: Readonly<SummerPromoProps>) {
  const [showMore, setShowMore] = useState(false);
  const { [FT_PI_SUMMER_SALE_PROMO_BANNER]: isSummerPromoCodeEnabled } = useFeatureToggle();
  const bannerInfo = promotionBanner;

  const [width, setWidth] = useState(0);
  const isMdWidth = width >= 768;
  const [hasCellCodes, setHasCellCode] = useState(true);

  useEffect(() => {
    setWidth(window.innerWidth);
    const handleResize = () => setWidth(window.innerWidth);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  useEffect(() => {
    const { search } = window.location;
    const searchParameters = new URLSearchParams(search);
    setHasCellCode(
      searchParameters.has('CELLCODES') ||
        searchParameters.has('CORPID') ||
        searchParameters.has('PROMOID')
    );
  }, []);

  if (!hasCellCodes && bannerInfo?.enabled && isSummerPromoCodeEnabled) {
    const termsLink = (
      <span style={{ ...bannerTermsStyles }}>{renderSanitizedHtml(bannerInfo?.terms)}</span>
    );

    return (
      <Flex data-testid="SummerPromoContainer" {...bannerContainerStyles}>
        <Box {...boxContainerStyles}>
          <Text as="div" {...bannerTitleStyles} fontSize="lg" fontWeight="bold">
            <Box {...bannerTitleBoxStyles}>
              <Icon src={formatAssetsUrl(bannerInfo?.icon)} style={{ ...bannerIcon }} />
              {bannerInfo?.title}
            </Box>
            <Box {...cheveronContainerStyles} data-testid="chevronIconContainer">
              {showMore ? (
                <ChevronUp24
                  color="#FDB913"
                  onClick={() => setShowMore(false)}
                  data-testid="chevronUpIcon"
                />
              ) : (
                <ChevronDown24
                  color="#FDB913"
                  onClick={() => setShowMore(true)}
                  data-testid="chevronDownIcon"
                />
              )}
            </Box>
          </Text>
          <Text
            as="div"
            color="white"
            fontSize="sm"
            style={showMore || isMdWidth ? { display: 'block' } : hideTextStyles}
          >
            {bannerInfo?.description} {showNotification ? termsLink : null}
            {!showNotification && !isMdWidth && <Box data-testid="termsLink">{termsLink}</Box>}
          </Text>
        </Box>
        <Box {...notificationBoxStyles}>
          {showNotification ? (
            <Notification
              variant="alert"
              status="warning"
              title={promotionBanner?.srpNotificationTitle}
              description={promotionBanner?.srpNotificationText}
              svg={<Alert />}
              isInnerHTML
              wrapperStyles={wrapperStyles}
              style={notificationTextStyles}
            />
          ) : isMdWidth ? (
            termsLink
          ) : null}
        </Box>
      </Flex>
    );
  }
  return null;
}

const bannerContainerStyles = {
  backgroundColor: '#511E62',
  display: 'grid',
  gridTemplateColumns: {
    base: '1fr',
    md: '1fr 1fr',
  },
  px: {
    base: '1rem',
    md: '1.5rem',
    xl: '66px',
  },
  py: {
    base: '0.75rem',
    md: '0.5rem',
  },
} as BoxProps;

const bannerTitleBoxStyles = {
  display: 'flex',
  alignItems: 'center',
};

const bannerTitleStyles = {
  color: '#FDB913',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
};

const bannerTermsStyles = {
  textDecoration: 'underline',
  pointer: 'cursor',
  color: 'white',
  fontSize: 'sm',
};

const bannerIcon = {
  display: 'inline',
  width: '16px',
  height: '16px',
  marginRight: '6px',
};

const hideTextStyles = {
  display: 'none',
};

const cheveronContainerStyles = {
  display: {
    sm: 'block',
    md: 'none',
  },
};

const boxContainerStyles = {} as BoxProps;

const notificationBoxStyles = {
  display: 'flex',
  justifyContent: 'flex-end',
  alignItems: 'center',
  color: 'white',
  width: {
    xs: 'full',
    md: 'auto',
  },
};

const wrapperStyles = {
  padding: {
    xs: '6px 8px',
    md: '6px 24px 6px 12px',
  },
  width: {
    xs: 'full',
    md: 'auto',
  },
  marginTop: {
    xs: '5px',
    md: '0',
  },
  alignItems: 'center',
};

const notificationTextStyles = {
  fontSize: '13px',
  lineHeight: '150%',
  fontWeight: '700',
};
