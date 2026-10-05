import type { FlexProps, LinkProps } from '@chakra-ui/react';
import { Box, Flex, Link, Stack, Text, VStack } from '@chakra-ui/react';
import type { FacilityItem } from '@whitbread-eos/api';
import { Icon, Path } from '@whitbread-eos/atoms';
import { formatAssetsUrl, useSemanticTypography } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { useState } from 'react';

import FacilitiesModal from '../../FacilitiesModal';

interface Props {
  facilities: FacilityItem[] | undefined;
  isLessThanSm: boolean | undefined;
  isLessThanMd: boolean | undefined;
  containerRef?: React.RefObject<HTMLElement>;
}

export default function RoomFacilitiesComponent({
  facilities,
  isLessThanSm,
  isLessThanMd,
  containerRef,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const getTypographyProps = useSemanticTypography();

  // NOTE: Facilities are sorted in ascending (lowest to highest) weight order, and if of equal weight - alphabetically (both managed in AEM)
  const visibleFacilities = facilities
    ?.filter((item) => item?.isVisible)
    .slice(0, isLessThanMd && !isLessThanSm ? 3 : 5);

  const facilityNameLegacyTypography = {
    fontSize: 'xs',
    lineHeight: '2',
  };

  const facilityNameSemanticTypography = {
    textStyle: 'body-s-regular',
  };

  const linkNameLegacyTypography = {
    fontSize: 'sm',
  };

  const linkNameSemanticTypography = {
    textStyle: 'link-s-regular',
  };

  const linkLayoutStyles = {
    color: 'btnSecondaryEnabled',
    textDecoration: 'underline',
    textAlign: 'center',
  } as LinkProps;

  return (
    <>
      {renderItems()}
      {renderModal()}
    </>
  );

  function renderItems() {
    const containerStyles = {
      flexDirection: { base: 'column', sm: 'row' },
      alignItems: { base: 'flex-start', sm: 'initial' },
      maxWidth: 'full',
      overflow: 'auto',
    } as FlexProps;

    const plusIconStyles = {
      w: 'var(--chakra-space-xl)',
      h: 'var(--chakra-space-xl)',
      justifyContent: 'center',
      alignItems: 'center',
      bgColor: 'lightGrey5',
      borderRadius: 'full',
    } as FlexProps;

    const iconStyles = {
      h: { base: '6', sm: '8' },
      mr: { base: '3', sm: '0' },
    };

    return (
      <Flex {...containerStyles}>
        <Stack
          direction={{ base: 'row' }}
          spacing={{ base: 'sm', sm: 'xl' }}
          flexWrap={{ base: 'wrap', sm: 'initial' }}
          data-testid="room-facilities-wrap"
        >
          {visibleFacilities?.map((roomFacility) => (
            <Box key={roomFacility?.code}>
              <VStack spacing="sm">
                <Icon {...iconStyles} src={formatAssetsUrl(roomFacility?.icon as string)} />
                {!isLessThanSm && (
                  <Text
                    textAlign="center"
                    {...getTypographyProps(
                      facilityNameLegacyTypography,
                      facilityNameSemanticTypography
                    )}
                  >
                    {roomFacility?.name}
                  </Text>
                )}
              </VStack>
            </Box>
          ))}
          {facilities && facilities?.length > 5 && !isLessThanSm && (
            <Box>
              <VStack spacing="sm">
                <Flex {...plusIconStyles}>
                  <Icon svg={<Path color={'var(--chakra-colors-darkGrey2)'} />} cursor="pointer" />
                </Flex>
                <Link
                  {...linkLayoutStyles}
                  {...getTypographyProps(linkNameLegacyTypography, linkNameSemanticTypography)}
                  onMouseDown={openModal}
                  data-testid="facilities-desktop-link"
                >
                  {t('hoteldetails.seeall.facilities')}
                </Link>
              </VStack>
            </Box>
          )}
        </Stack>
        {isLessThanSm && (
          <Box>
            <Link
              {...linkLayoutStyles}
              {...getTypographyProps(linkNameLegacyTypography, linkNameSemanticTypography)}
              mt="md"
              display="block"
              onMouseDown={openModal}
              data-testid="facilities-mobile-link"
            >
              {t('hoteldetails.seeall.facilities')}
            </Link>
          </Box>
        )}
      </Flex>
    );
  }

  function renderModal() {
    return (
      <FacilitiesModal
        facilities={facilities}
        isModalVisible={isModalVisible}
        onModalClose={() => setIsModalVisible(false)}
        containerRef={containerRef}
      />
    );
  }

  function openModal() {
    setIsModalVisible(true);
  }
}
