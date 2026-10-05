import { Box, Flex, Text } from '@chakra-ui/react';
import type { TabItem } from '@whitbread-eos/api';
import { theme } from '@whitbread-eos/atoms';
import { useSemanticTypography } from '@whitbread-eos/utils';
import React from 'react';

import RoomFacilitiesList from '../Facilities/RoomFacilities';
import HotelRoomImages from '../HotelRoomImages';

interface Props {
  tabItems: TabItem[] | undefined;
  isLessThanSm: boolean | undefined;
  isLessThanMd: boolean | undefined;
  isLessThanLg: boolean | undefined;
  containerRef?: React.RefObject<HTMLElement>;
}

export const HotelRoomContent = ({
  tabItems,
  isLessThanSm,
  isLessThanMd,
  isLessThanLg,
  containerRef,
}: Readonly<Props>) => {
  const getTypographyProps = useSemanticTypography();

  const roomNameLegacyTypography = {
    fontSize: { base: 'md', sm: '1.188rem' },
    lineHeight: 3,
    fontWeight: 'semibold',
  };

  const roomNameSemanticTypography = {
    textStyle: 'heading-s',
  };

  const roomDescriptionLegacyTypography = {};

  const roomDescriptionSemanticTypography = {
    textStyle: 'body-m-regular',
  };

  const isRelativeToContainer = !!containerRef;

  return (
    <Flex overflowX="auto" margin={isRelativeToContainer ? 0 : -4}>
      {tabItems?.map((tabItem, idx) => (
        <Flex key={tabItem?.roomName} {...cardStyles(idx)}>
          <Box flex={1}>
            <HotelRoomImages
              images={tabItem?.images}
              thumbnailHeight={getThumbnailHeight()}
              isLessThanSm={isLessThanSm}
              isLessThanLg={isLessThanLg}
              containerRef={containerRef}
            />
          </Box>
          <Box flex={1} p={6}>
            {!isRelativeToContainer && (
              <Text
                {...getTypographyProps(roomNameLegacyTypography, roomNameSemanticTypography)}
                data-testid={`hdp_roomName-${tabItem?.roomName}`}
              >
                {tabItem?.roomName}
              </Text>
            )}
            <Text
              mt={2}
              mb="lg"
              color={theme.colors.darkGrey2}
              {...getTypographyProps(
                roomDescriptionLegacyTypography,
                roomDescriptionSemanticTypography
              )}
              data-testid={`hdp_roomDescription-${tabItem?.roomName}`}
            >
              {tabItem?.roomDescription}
            </Text>
            <RoomFacilitiesList
              facilities={tabItem.facilities}
              isLessThanSm={isLessThanSm}
              isLessThanMd={isLessThanMd}
              containerRef={containerRef}
            />
          </Box>
        </Flex>
      ))}
    </Flex>
  );

  function cardStyles(tabItemIdx: number) {
    return {
      border: `1px solid ${theme.colors.lightGrey3}`,
      borderRadius: 'base',
      mt: isRelativeToContainer ? '3rem' : 6,
      mb: isRelativeToContainer ? 0 : 3,
      width: 'full',
      direction: {
        base: 'column',
        mobile: 'column',
        sm: 'column',
        md: 'column',
        lg: (tabItems && tabItems.length > 1) || isRelativeToContainer ? 'column' : 'row',
        xl: (tabItems && tabItems.length > 1) || isRelativeToContainer ? 'column' : 'row',
      } as const,
      mr: tabItems && tabItems?.length > 1 && tabItemIdx < tabItems?.length - 1 ? 6 : 0,
      minWidth: isRelativeToContainer ? 'unset' : getCardMinWidth(),
      maxWidth: isRelativeToContainer
        ? 'unset'
        : {
            base: 'full',
            md: tabItems && tabItems?.length > 1 ? 'full' : '38.875rem',
            lg: 'full',
          },
    };

    function getCardMinWidth() {
      switch (true) {
        case tabItems && tabItems.length > 1 && tabItems.length <= 4:
          return {
            xl: `calc(100% / ${tabItems?.length} - 1.5rem + 1.5rem / ${tabItems?.length})`,
            lg: `calc(100% / ${tabItems?.length} - 1.5rem + 1.5rem / ${tabItems?.length})`,
            md: '39.813rem',
            sm: '29.438rem',
            mobile: '19.438rem',
            base: '16rem',
          };
        case tabItems && tabItems?.length > 4:
          return {
            xl: `calc(100% / 4 - 1.5rem + 1.5rem / 4)`,
            lg: `calc(100% / 4 - 1.5rem + 1.5rem / 4)`,
            md: '39.813rem',
            sm: '29.438rem',
            mobile: '19.438rem',
            base: '16rem',
          };
        default:
          return {
            xl: 'full',
            lg: 'full',
            md: '38.875rem',
            sm: 'full',
            mobile: 'full',
            base: 'full',
          };
      }
    }
  }

  function getThumbnailHeight() {
    switch (true) {
      case tabItems?.length === 2:
        return {
          xl: '22.563rem',
          lg: '21.125rem',
          md: '22.063rem',
          sm: '16.563rem',
          mobile: '10.625rem',
          base: '10.625rem',
        };
      case tabItems?.length === 3:
        return {
          xl: '14.75rem',
          lg: '13rem',
          md: '22.063rem',
          sm: '16.563rem',
          mobile: '10.625rem',
          base: '10.625rem',
        };
      case tabItems && tabItems?.length >= 4:
        return {
          xl: '10.875rem',
          lg: '10rem',
          md: '22.063rem',
          sm: '16.563rem',
          mobile: '10.625rem',
          base: '10.625rem',
        };
      default:
        return {
          xl: '22.563rem',
          lg: '22rem',
          md: '24rem',
          sm: '16.563rem',
          mobile: '10.625rem',
          base: '10.625rem',
        };
    }
  }
};
