'use client';

import { HotelBrand, GlobalInnB } from '@whitbread-eos/api';
import { formatIBAssetsUrl, getHotelInformationIB } from '@whitbread-eos/utils/server';
import { useCallback, useEffect } from 'react';
import { useFormContext } from 'react-hook-form';

import { HotelSearchInput, HotelSearchResult } from './HotelSearchInput';
import { SelectedHotelsList } from './SelectedHotelsList';

type Props = {
  globalLabels: GlobalInnB | Record<string, never>;
  locationIcon: string;
  initialHotelIds?: string[];
  language: string;
};

const MAX_SELECTED_HOTELS = 100;

export function HotelAlertsSelector({
  globalLabels,
  locationIcon,
  initialHotelIds = [],
  language,
}: Props) {
  const baseDataTestId = 'HotelAlertsSelector';
  const { watch, setValue } = useFormContext();
  const selectedHotels = watch('selectedHotels');

  useEffect(() => {
    async function fetchHotelDetails() {
      const validHotelIds = initialHotelIds.filter((hotelId) => hotelId && hotelId.trim() !== '');

      if (!validHotelIds.length) {
        return;
      }

      try {
        const hotelPromises = validHotelIds.map(async (hotelId: string) => {
          try {
            const hotelData = await getHotelInformationIB(hotelId, language);

            return {
              id: hotelData?.hotelId || hotelId,
              code: hotelData?.hotelId || hotelId,
              suggestion: hotelData?.name || `${hotelId} (Hotel)`,
              brand: hotelData?.brand || 'PINN',
            };
          } catch (error) {
            return {
              id: hotelId,
              code: hotelId,
              suggestion: `${hotelId} (Hotel)`,
              brand: 'PINN',
            };
          }
        });

        const hotels = await Promise.all(hotelPromises);
        setValue('selectedHotels', hotels, {
          shouldValidate: true,
          shouldDirty: false,
        });
      } catch (error) {
        const fallbackHotels = validHotelIds.map((hotelId: string) => ({
          id: hotelId,
          code: hotelId,
          suggestion: `${hotelId} (Hotel)`,
          brand: 'PINN',
        }));
        setValue('selectedHotels', fallbackHotels, {
          shouldValidate: true,
          shouldDirty: false,
        });
      }
    }

    fetchHotelDetails();
  }, [initialHotelIds, setValue, language]);

  const getHotelIcon = useCallback(
    (brand: string) => {
      let iconPath = globalLabels?.brand?.piLogo;
      if (brand === HotelBrand.HUB) {
        iconPath = globalLabels?.brand?.hubLogo;
      } else if (brand === HotelBrand.ZIP) {
        iconPath = globalLabels?.brand?.zipLogo;
      }

      const formattedUrl = formatIBAssetsUrl(iconPath ?? '');
      return formattedUrl;
    },
    [globalLabels]
  );

  const addHotel = useCallback(
    (hotel: HotelSearchResult) => {
      if (
        selectedHotels.length < MAX_SELECTED_HOTELS &&
        !selectedHotels.some((h: HotelSearchResult) => h.id === hotel.id)
      ) {
        setValue('selectedHotels', [...selectedHotels, hotel], {
          shouldValidate: true,
          shouldDirty: true,
        });
      }
    },
    [selectedHotels, setValue]
  );

  const removeHotel = useCallback(
    (hotelId: string) => {
      setValue(
        'selectedHotels',
        selectedHotels.filter((hotel: HotelSearchResult) => hotel.id !== hotelId),
        { shouldValidate: true, shouldDirty: true }
      );
    },
    [selectedHotels, setValue]
  );

  return (
    <div data-testid={`${baseDataTestId}-container`} className="flex flex-col gap-6">
      <HotelSearchInput
        globalLabels={globalLabels}
        locationIcon={locationIcon}
        onHotelSelect={addHotel}
        getHotelIcon={getHotelIcon}
        baseDataTestId={baseDataTestId}
      />
      <SelectedHotelsList
        selectedHotels={selectedHotels}
        onRemoveHotel={removeHotel}
        getHotelIcon={getHotelIcon}
        baseDataTestId={baseDataTestId}
      />
    </div>
  );
}
