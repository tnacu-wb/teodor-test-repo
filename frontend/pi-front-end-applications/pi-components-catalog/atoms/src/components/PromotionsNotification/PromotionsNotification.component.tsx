import { Box, BoxProps, Flex, FlexProps, Text } from '@chakra-ui/react';
import { PageName, HIRoomRate } from '@whitbread-eos/api';
import {
  renderSanitizedHtml,
  formatAssetsUrl,
  type PromotionsInformation,
} from '@whitbread-eos/utils';
import { useState, useEffect } from 'react';

import { Alert, Info } from '../../assets/icons';
import Icon from '../Icon';
import Notification from '../Notification';

interface PromotionsNotificationProps {
  promotionBannerData?: PromotionsInformation;
  viewType?: string;
  roomRates?: HIRoomRate[];
  page?: string;
  elementName?: string;
}

function hasMatchingPromotionCode(promoCode: string, roomrates: HIRoomRate[]) {
  if (!promoCode || !Array.isArray(roomrates)) return false;
  return roomrates?.some((item) => item?.promotionCode === promoCode);
}

export default function PromotionsNotification({
  promotionBannerData,
  viewType = '',
  roomRates,
  page = '',
  elementName = '',
}: PromotionsNotificationProps) {
  const {
    showPromo: enabled,
    promoExpiredMessage: expired,
    promoInvalidMessage: invalid,
    promoBannerColour: backgroundColor,
    promoBannerIcon: icon,
    promoBannerTitle: title,
    promoBannerSubtitle: description,
    isWithinPromoWindow: withinWindow,
    promotionCode: promoCode,
    promoBannerVisibility: promoBannerVisibilityPages,
  } = promotionBannerData || {};

  const amendMessage = promotionBannerData?.promoAmendMessage ?? null;
  const amendPromoCode = promotionBannerData?.promoBookingInfo?.promotionCode ?? null;

  const [hasCellCodes, setHasCellCode] = useState(true);

  useEffect(() => {
    if (typeof window !== 'undefined') {
      const params = new URLSearchParams(window.location.search);
      setHasCellCode(params.has('CELLCODES') || params.has('CORPID'));
    }
  }, []);

  const isBannerEnabled =
    !invalid && !expired && (viewType === 'banner' || viewType === 'any') && withinWindow;

  const isWarningEnabled = (viewType === 'warning' || viewType === 'any') && (invalid || expired);

  let shouldPromoInfoDisplay = !hasCellCodes && enabled && (isBannerEnabled || isWarningEnabled);

  const restrictedPages = [PageName.HDP];

  const renderPromoBannerStayDates = () => {
    return amendPromoCode && enabled && !withinWindow ? (
      <Box data-testid="stayDatesPromoBanner">
        <Notification
          variant="info"
          status="info"
          description={amendMessage as string}
          svg={<Info />}
          isInnerHTML
          wrapperStyles={amendNotificationWrapperStyles}
          prefixDataTestId="amendPromoBannerWarning"
        />
      </Box>
    ) : null;
  };
  if (PageName.AMEND && elementName === PageName.STAY_DATES) {
    return renderPromoBannerStayDates();
  }

  const showPromoBannerRoomsAndGuests = () => {
    return amendPromoCode && enabled ? (
      <Box data-testid="roomsAndGuestsPromoBanner">
        <Notification
          variant="info"
          status="info"
          description={amendMessage as string}
          svg={<Info />}
          isInnerHTML
          wrapperStyles={amendNotificationWrapperStyles}
          prefixDataTestId="amendPromoBannerWarning"
        />
      </Box>
    ) : null;
  };
  if (PageName.AMEND && elementName === PageName.ROOMS_AND_GUESTS) {
    return showPromoBannerRoomsAndGuests();
  }

  if (shouldPromoInfoDisplay === true && roomRates && roomRates?.length > 0) {
    const hasMatchingPromotionCodes = hasMatchingPromotionCode(promoCode as string, roomRates);
    if (!hasMatchingPromotionCodes) shouldPromoInfoDisplay = false;
  }

  if (
    shouldPromoInfoDisplay &&
    promoBannerVisibilityPages !== null &&
    (promoBannerVisibilityPages?.length === 0 ||
      promoBannerVisibilityPages?.includes(page as PageName))
  ) {
    return (
      <Box data-testid="PromoContainer" {...containerStyles()}>
        {isBannerEnabled && (
          <Box {...getBannerContainerStyles(backgroundColor as string, viewType)}>
            <Flex {...bannerTitleStyles(viewType)}>
              <Flex {...bannerIconTitleWrapperStyles}>
                <Icon src={formatAssetsUrl(icon as string)} {...bannerIconStyles} />
                <Text {...bannerTitleTextStyles}>{renderSanitizedHtml(title as string)}</Text>
              </Flex>

              {!restrictedPages.includes(page as PageName) && description && (
                <Flex {...bannerDescriptionWrapperStyles}>
                  <Text {...bannerDescriptionTextStyles}>{renderSanitizedHtml(description)}</Text>
                </Flex>
              )}
            </Flex>
          </Box>
        )}

        {isWarningEnabled ? (
          <Notification
            variant="alert"
            status="warning"
            description={(invalid || expired) as string}
            svg={<Alert />}
            isInnerHTML
            wrapperStyles={notificationWrapperStyles}
            style={notificationTextStyles}
            prefixDataTestId="promoBannerWarning"
          />
        ) : null}
      </Box>
    );
  }

  return null;
}

const notificationWrapperStyles = {
  marginTop: { base: 'md', md: 'lg' },
  zIndex: '1',
  w: '100vw',
  maxW: '100%',
  position: 'relative',
  left: '50%',
  transform: 'translateX(-50%)',
} as any;

const containerStyles = (): BoxProps => ({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  w: 'full',
  position: 'relative',
  bg: 'white',
  marginTop: { base: 'md', md: 'lg' },
});

const getBannerContainerStyles = (bgColor: string, viewType: string): BoxProps => ({
  backgroundColor: bgColor,
  display: 'grid',
  gridTemplateColumns: { base: '1fr' },
  p: '0.75rem 1rem',
  w: { mobile: 'full', md: '45rem', lg: '50.5rem', xl: viewType === 'any' ? '100%' : '54rem' },
  borderRadius: viewType === 'warning' ? 0 : '8px',
  lineHeight: '120%',
  minH: 'full',
  alignItems: 'center',
});

const bannerTitleStyles = (viewType: string): FlexProps => ({
  display: 'flex',
  gap: '2px',
  fontSize: 'lg',
  align: 'center',
  alignItems: { base: viewType === 'any' ? 'center' : 'flex-start' },
  flexWrap: viewType === 'any' ? 'wrap' : 'nowrap',
  flexDirection: { base: viewType === 'any' ? 'row' : 'column' },
  lineHeight: '120%',
  minH: { base: '2.5625rem' },
  justifyContent: { base: viewType === 'any' ? 'left' : 'center' },
});

const bannerIconTitleWrapperStyles: FlexProps = {
  align: 'center',
};

const bannerIconStyles = {
  display: 'inline',
  width: '1rem',
  height: '1rem',
  marginRight: '0.375rem',
};

const bannerTitleTextStyles = {
  as: 'span',
  fontSize: 'lg',
  fontWeight: 'bold',
  color: 'white',
} as any;

const bannerDescriptionWrapperStyles: FlexProps = {
  alignItems: 'center',
};

const bannerDescriptionTextStyles = {
  as: 'span',
  color: 'white',
  fontSize: 'sm',
  fontWeight: 'normal',
  ml: { base: 0, md: '6px' },
  alignItems: 'center',
} as any;

const notificationTextStyles = {
  fontSize: '1rem',
  lineHeight: '1.5rem',
  fontWeight: 'normal',
  border: 0,
  color: 'darkGrey1',
};
const amendNotificationWrapperStyles = {
  marginTop: { base: 'md', md: 'lg' },
  zIndex: '1',
  w: 'auto',
} as BoxProps;
