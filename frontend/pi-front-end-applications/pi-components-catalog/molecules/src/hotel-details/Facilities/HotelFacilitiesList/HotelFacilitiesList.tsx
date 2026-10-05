import { Box, HStack, Link, Stack, Text, VStack } from '@chakra-ui/react';
import type { FacilityItem } from '@whitbread-eos/api';
import { Icon } from '@whitbread-eos/atoms';
import {
  formatAssetsUrl,
  formatDataTestId,
  useSemanticTypography,
  useStaticHotelInformation,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

import FacilitiesModal from '../FacilitiesModal';

interface Props {
  isLessThanSm: boolean | undefined;
  isLessThanMd?: boolean;
  isSoftBundlesVisible?: boolean;
}

const headingLayoutStyles = {
  my: 'md',
};

const headingLegacyTypography = {
  fontSize: { base: 'xl', sm: 'lg' },
  fontWeight: 'bold',
  lineHeight: '3',
};

const headingSemanticTypography = {
  textStyle: 'heading-s',
};

const desktopSeeAllLinkLayoutStyles = {
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
};

const desktopSeeAllLinkLegacyTypography = {
  fontSize: 'sm',
};

const desktopSeeAllLinkSemanticTypography = {
  textStyle: 'link-s-regular',
};

const facilityNameSemanticTypography = {
  textStyle: 'body-s-regular',
};

const desktopFacilityNameLegacyTypography = {
  fontSize: 'xs',
  lineHeight: '2',
};

const linkStyles = {
  fontSize: 'sm',
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
};

const HotelFacilitiesList = ({
  isLessThanSm,
  isLessThanMd = false,
  isSoftBundlesVisible = false,
}: Readonly<Props>) => {
  const { hotelFacilities, isLoading, isError, error } = useStaticHotelInformation();
  const getTypographyProps = useSemanticTypography();
  const { t } = useTranslation(['common']);
  const [isModalVisible, setIsModalVisible] = useState(false);

  const isLessThanBreakpoint = isSoftBundlesVisible ? isLessThanMd : isLessThanSm;

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!hotelFacilities?.length) {
    return null;
  }

  // NOTE: Facilities are sorted in ascending (lowest to highest) weight order, and if of equal weight - alphabetically (both managed in AEM).
  const visibleFacilities = hotelFacilities
    ?.filter((item: FacilityItem) => item.isVisible)
    .slice(0, isLessThanBreakpoint ? 4 : 6);

  const mobileFacilityNameTypographyProps = getTypographyProps(
    {},
    facilityNameSemanticTypography
  ) as Record<string, unknown>;

  const desktopFacilityNameTypographyProps = getTypographyProps(
    desktopFacilityNameLegacyTypography,
    facilityNameSemanticTypography
  ) as Record<string, unknown>;

  const headingContent = (
    <Box {...headingLayoutStyles} data-testid="hdp_hotelFacilitiesText">
      <Text as="span" {...getTypographyProps(headingLegacyTypography, headingSemanticTypography)}>
        {t('hoteldetails.hotel.facilities')}
      </Text>
      {!isLessThanBreakpoint && (
        <Link
          {...desktopSeeAllLinkLayoutStyles}
          {...getTypographyProps(
            desktopSeeAllLinkLegacyTypography,
            desktopSeeAllLinkSemanticTypography
          )}
          onClick={openModal}
          pl="sm"
        >
          {t('hoteldetails.seeall')}
        </Link>
      )}
    </Box>
  );

  const itemsContent = (
    <Stack
      direction={
        isLessThanBreakpoint ? { base: 'column', md: 'row' } : { base: 'column', sm: 'row' }
      }
      spacing={'0'}
      overflowX="auto"
      ml={{ mobile: 0, sm: '-1.125rem', md: 0 }}
      mr={{ mobile: 0, sm: '-2.125rem', md: 0 }}
      pl={{ mobile: 0, sm: '1.125rem', md: 0 }}
      pr={{ mobile: 0, sm: '2.125rem', md: 0 }}
      data-testid={'hdp_hotelFacilities'}
    >
      {visibleFacilities?.map((hotelFacility, index) => (
        <Box key={hotelFacility.code}>
          {isLessThanBreakpoint ? (
            <HStack
              cursor="pointer"
              onClick={openModal}
              data-testid={formatDataTestId(hotelFacility.name, 'HotelFacilitiesHStack')}
            >
              <Icon src={formatAssetsUrl(hotelFacility.icon as string)} w="6" />
              <Text {...mobileFacilityNameTypographyProps}>
                {hotelFacility?.name?.replace(/-/gm, '\u2011')}
              </Text>
            </HStack>
          ) : (
            <VStack
              cursor="pointer"
              onClick={openModal}
              data-testid={formatDataTestId(hotelFacility.name, 'HotelFacilitiesVStack')}
              m={['0 16px']}
              ml={index === 0 ? 0 : undefined}
            >
              <Icon
                src={formatAssetsUrl(hotelFacility.icon as string)}
                cursor="pointer"
                h="6"
                onClick={openModal}
              />
              <Text textAlign="center" {...desktopFacilityNameTypographyProps}>
                {hotelFacility?.name?.replace(/-/gm, '\u2011')}
              </Text>
            </VStack>
          )}
        </Box>
      ))}
      {isLessThanBreakpoint && (
        <Box>
          <Link
            {...linkStyles}
            mt="md"
            display="block"
            onClick={openModal}
            data-testid="hdp_mobileSeeAllHotelFacilities"
          >
            {t('hoteldetails.seeall')}
          </Link>
        </Box>
      )}
    </Stack>
  );

  const modalContent = (
    <FacilitiesModal
      facilities={hotelFacilities}
      isModalVisible={isModalVisible}
      onModalClose={() => setIsModalVisible(false)}
    />
  );

  return (
    <>
      {headingContent}
      {itemsContent}
      {modalContent}
    </>
  );

  function openModal() {
    setIsModalVisible(true);
  }
};

export { HotelFacilitiesList };
