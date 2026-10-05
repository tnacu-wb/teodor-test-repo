import { BadgeProps, BoxProps, Grid, GridItem, Wrap, WrapItem } from '@chakra-ui/react';
import { FacilityItem, MessagingFlag } from '@whitbread-eos/api';
import { Badge, BadgeVariant } from '@whitbread-eos/atoms';

import {
  BIGGER_ROOM_CODE,
  NEW_HOTEL_MESSAGING_FLAG_TEXT,
  NEW_ROOMS_MESSAGING_FLAG_TEXT,
  OPENING_SOON_MESSAGING_FLAG_TEXT,
  PREMIER_EXTRA_CODE,
  PREMIER_PLUS_FACILITY_CODE,
  MLOS,
} from '../../utils/constants';

interface Props {
  hotelOpeningDate?: string;
  isHotelOpeningSoon?: boolean;
  labels?: LabelsType;
  testId: string;
  hotelFacilities?: FacilityItem[];
  messagingFlag?: MessagingFlag;
  isColumnDisplay?: boolean;
  styles?: BoxProps;
  hasMlosRestriction?: boolean;
}
type LabelsType = {
  openingSoon?: string;
  premierPlus: string;
  mlos?: string;
};
type BadgeType = {
  badgeColor: string;
  badgeText?: string;
  variant?: BadgeVariant;
  isColumnDisplay?: boolean;
  badgeLegacyTypography?: Pick<
    BadgeProps,
    'fontSize' | 'fontWeight' | 'lineHeight' | 'letterSpacing' | 'fontFamily' | 'textTransform'
  >;
  badgeSemanticTypography?: Pick<BadgeProps, 'textStyle'>;
};

export default function HotelBadges({
  hasMlosRestriction,
  hotelOpeningDate,
  isHotelOpeningSoon,
  labels,
  testId,
  hotelFacilities,
  messagingFlag,
  isColumnDisplay,
  styles,
}: Props) {
  const badgeLegacyTypography = { fontSize: '13px' };
  const badgeSemanticTypography = { textStyle: 'label-s' };

  const premierPlusBadge = hotelFacilities?.find(
    (facility: FacilityItem) => facility.code === PREMIER_PLUS_FACILITY_CODE
  );

  const hasBadges =
    (hotelOpeningDate && isHotelOpeningSoon) ||
    premierPlusBadge ||
    (messagingFlag?.text && messagingFlag?.text !== 'hub') ||
    hasMlosRestriction;

  const badges = (
    <>
      {hasMlosRestriction &&
        renderHotelBadge({
          badgeColor: MLOS,
          badgeText: labels?.mlos,
          isColumnDisplay,
          variant: 'solid',
          badgeLegacyTypography,
          badgeSemanticTypography,
        })}
      {hotelOpeningDate &&
        isHotelOpeningSoon &&
        renderHotelBadge({
          badgeColor: OPENING_SOON_MESSAGING_FLAG_TEXT,
          badgeText: labels?.openingSoon,
          isColumnDisplay,
          badgeLegacyTypography,
          badgeSemanticTypography,
        })}
      {premierPlusBadge &&
        renderHotelBadge({
          badgeColor: premierPlusBadge?.code ?? '',
          badgeText: labels?.premierPlus,
          isColumnDisplay,
          badgeLegacyTypography,
          badgeSemanticTypography,
        })}
      {messagingFlag?.text &&
        messagingFlag?.text !== 'hub' &&
        renderHotelBadge({
          badgeColor: NEW_HOTEL_MESSAGING_FLAG_TEXT,
          badgeText: messagingFlag.text,
          isColumnDisplay,
          badgeLegacyTypography,
          badgeSemanticTypography,
        })}
    </>
  );

  if (!hasBadges) {
    return null;
  }

  return isColumnDisplay ? (
    <Grid
      {...styles}
      data-testid={testId}
      templateColumns="repeat(2, 1fr)"
      justifyItems="flex-start"
      gridGap="sm"
      width="fit-content"
    >
      {badges}
    </Grid>
  ) : (
    <Wrap {...styles} data-testid={testId}>
      {badges}
    </Wrap>
  );
}

function renderHotelBadge({
  badgeColor,
  badgeText,
  variant = 'secondary',
  isColumnDisplay,
  badgeLegacyTypography,
  badgeSemanticTypography,
}: BadgeType) {
  if (!badgeText) {
    return null;
  }

  const badge = (
    <Badge
      badgecolor={getColor(badgeColor)}
      color={variant === 'secondary' ? getColor(badgeColor) : 'baseWhite'}
      variant={variant}
      badgeLegacyTypography={badgeLegacyTypography}
      badgeSemanticTypography={badgeSemanticTypography}
      width="100%"
      textAlign="center"
      cursor="pointer"
    >
      {badgeText}
    </Badge>
  );

  return isColumnDisplay ? <GridItem width="100%">{badge}</GridItem> : <WrapItem>{badge}</WrapItem>;
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
    case MLOS:
      return 'green';
    default:
      return 'darkPink';
  }
}
