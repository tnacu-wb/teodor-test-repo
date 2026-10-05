import { Box, Flex } from '@chakra-ui/react';
import { FacilityItem } from '@whitbread-eos/api';
import { formatAssetsUrl } from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';
import Image from 'next/image';
import { useMemo } from 'react';

import {
  ACCEPTED_HOTEL_CARD_FACILITIES,
  ACCESSIBLE_FACILITY_CODES,
  AIR_CONDITIONING_FACILITY_CODES,
} from './constants';

const Tooltip = dynamic(
  async () => {
    const { Tooltip } = await import('@whitbread-eos/atoms');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

interface Props {
  facilities: FacilityItem[];
  roomTypes: (string | undefined)[] | undefined;
  isDLPPage?: boolean;
  testId?: string;
}

export default function HotelFacilities({
  facilities,
  roomTypes,
  isDLPPage,
  testId,
}: Readonly<Props>) {
  const srpFacilities: FacilityItem[] = useMemo(() => {
    const airConditioningFacility = facilities.filter((facility) =>
      AIR_CONDITIONING_FACILITY_CODES.flat().includes(facility?.code ?? '')
    );
    const accessibleFacility = facilities.filter((facility) =>
      ACCESSIBLE_FACILITY_CODES.flat().includes(facility?.code ?? '')
    );
    const otherFacilities = facilities.filter((facility) =>
      ACCEPTED_HOTEL_CARD_FACILITIES.flat().includes(facility?.code ?? '')
    );

    const uniqueFacilities = [...airConditioningFacility, ...otherFacilities];

    if (roomTypes?.includes('DIS')) {
      return uniqueFacilities.concat(...accessibleFacility);
    }
    return uniqueFacilities;
  }, [facilities, roomTypes]);

  const dlpFacilities: FacilityItem[] = useMemo(() => {
    return facilities.filter((facility) => facility.isVisible).slice(0, 4);
  }, [facilities]);

  const facilitiesList = isDLPPage ? dlpFacilities : srpFacilities;

  return (
    <Flex data-testid={testId}>
      {facilitiesList.map((facility) => (
        <Tooltip
          key={facility.code}
          description={facility?.name ?? ''}
          variant="facilities"
          placement="bottom-start"
          position="relative"
          alertElementStyles={alertStyles}
          {...tooltipStyles}
        >
          <Box as="span" mr="xmd" data-testid={facility.name}>
            <Image
              width={25}
              height={24}
              src={formatAssetsUrl(facility?.icon ?? '')}
              alt={facility?.name ?? ''}
              data-testid="svg-container"
            />
          </Box>
        </Tooltip>
      ))}
    </Flex>
  );
}

const alertStyles = {
  py: 'sm',
  padding: 0,
  h: '2.125rem',
};

const tooltipStyles = {
  marginLeft: '-1.2rem',
  fontSize: 'sm',
  lineHeight: '3',
  paddingLeft: 0,
  fontWeight: 'normal',
  top: 'xs',
};
