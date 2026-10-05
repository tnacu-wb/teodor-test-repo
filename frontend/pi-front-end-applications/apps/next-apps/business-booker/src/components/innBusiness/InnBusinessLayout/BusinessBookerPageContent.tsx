'use client';

import { Box } from '@chakra-ui/react';

import useSetOrientation from '~hooks/use-orientation';
import useSetScreenSize from '~hooks/use-screensize';

export interface BusinessBookerPageContentProps {
  isHotelDetailsPage?: boolean;
}

export function BusinessBookerPageContent({
  isHotelDetailsPage = false,
}: Readonly<BusinessBookerPageContentProps>) {
  useSetScreenSize();
  useSetOrientation();

  return (
    <>
      {isHotelDetailsPage && (
        <Box
          w="full"
          position="sticky"
          bottom="73"
          data-testid="HotelDetailsBasket"
          id="hotel-details-mobile-basket"
          sx={{
            '.basket-breakdown__total--mobile': {
              marginBottom: '-40px',
              ['button']: {
                top: '7px',
              },
            },
          }}
        />
      )}
    </>
  );
}
