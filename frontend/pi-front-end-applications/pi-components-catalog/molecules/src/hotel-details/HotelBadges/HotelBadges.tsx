import { Box, Text, Wrap, WrapItem } from '@chakra-ui/react';
import type { FacilityItem } from '@whitbread-eos/api';
import { Badge, Icon } from '@whitbread-eos/atoms';
import { formatAssetsUrl, useStaticHotelInformation } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import { useMemo } from 'react';

import {
  BIGGER_ROOM_CODE,
  NEW_HOTEL_MESSAGING_FLAG_TEXT,
  NEW_ROOMS_MESSAGING_FLAG_TEXT,
  OPENING_SOON_MESSAGING_FLAG_TEXT,
  PREMIER_EXTRA_CODE,
  PREMIER_PLUS_FACILITY_CODE,
  STANDARD_EXTRA_FACILITY_CODE,
} from '../../utils/constants';

interface Props {
  hubBadge: string;
}

const Tooltip = dynamic(
  async () => {
    const { Tooltip } = await import('@whitbread-eos/atoms');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

export default function HotelBadges({ hubBadge }: Readonly<Props>) {
  const { brand, hotelFacilities, messagingFlag, hotelFlags, isLoading, isError, error } =
    useStaticHotelInformation();
  const { t } = useTranslation(['common']);

  const badges = (facility: FacilityItem) =>
    PREMIER_PLUS_FACILITY_CODE === facility.code || STANDARD_EXTRA_FACILITY_CODE === facility.code;
  const visibleBadges = (item: FacilityItem) => item.isVisible;

  const displayedBadges = useMemo(
    () => (hotelFacilities || []).filter(badges).filter(visibleBadges),
    [hotelFacilities]
  );

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }
  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  const isFlagBannerVisible = !!(hotelFlags?.isEnabled && hotelFlags?.flagBanner);

  if (!displayedBadges.length && !messagingFlag?.text && !hubBadge && !isFlagBannerVisible) {
    return null;
  }

  return (
    <Wrap data-testid="hdp_badgesList" align="center">
      {brand?.toLowerCase() === 'hub' && !!hubBadge && (
        <WrapItem>
          <Icon src={formatAssetsUrl(hubBadge)} />
        </WrapItem>
      )}
      {isFlagBannerVisible && (
        <WrapItem>
          <Badge
            variant="primary"
            badgecolor={hotelFlags?.flagBanner?.backgroundColour ?? undefined}
            color={hotelFlags?.flagBanner?.textColour ?? undefined}
            display="flex"
            alignItems="center"
            gap="xs"
          >
            {hotelFlags?.flagBanner?.backgroundImage && (
              <Icon src={formatAssetsUrl(hotelFlags.flagBanner.backgroundImage)} />
            )}
            <Text as="span" textStyle="label-m">
              {hotelFlags?.flagBanner?.text}
            </Text>
          </Badge>
        </WrapItem>
      )}
      {displayedBadges.map((badge) =>
        badge.code === PREMIER_PLUS_FACILITY_CODE ? (
          <WrapItem key={badge.name}>
            <Tooltip
              description={t('hoteldetails.title.badge.ultimate.tooltip')}
              variant="facilities"
              placement="bottom-start"
              closeOnClick={false}
              ml="2rem"
              colorScheme={getColor(badge.code)}
            >
              {renderHotelBadge(badge.code, badge.name ?? '')}
            </Tooltip>
          </WrapItem>
        ) : (
          <WrapItem key={badge.name}>
            {renderHotelBadge(badge.code ?? '', badge.name ?? '')}
          </WrapItem>
        )
      )}
      {messagingFlag?.text && messagingFlag?.text !== 'hub' && (
        <WrapItem>{renderHotelBadge(NEW_HOTEL_MESSAGING_FLAG_TEXT, messagingFlag.text)}</WrapItem>
      )}
    </Wrap>
  );

  function renderHotelBadge(badgeColor: string, badgeText: string) {
    return (
      <Box>
        <Badge variant="primary" badgecolor={getColor(badgeColor)} cursor="pointer">
          {badgeText}
        </Badge>
      </Box>
    );
  }
}

export function getColor(label: string) {
  switch (label) {
    case BIGGER_ROOM_CODE:
      return 'lightPurple';
    case PREMIER_PLUS_FACILITY_CODE:
      return 'primary';
    case NEW_HOTEL_MESSAGING_FLAG_TEXT:
    case NEW_ROOMS_MESSAGING_FLAG_TEXT:
    case OPENING_SOON_MESSAGING_FLAG_TEXT:
      return 'maroon';
    case PREMIER_EXTRA_CODE:
      return 'blue';
    default:
      return 'darkPink';
  }
}
