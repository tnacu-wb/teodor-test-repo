'use client';

import { useTranslation, cn } from '@whitbread-eos/utils';
import Image from 'next/image';

import { HotelSearchResult } from './HotelSearchInput';

interface Props {
  selectedHotels: HotelSearchResult[];
  onRemoveHotel: (hotelId: string) => void;
  getHotelIcon: (brand: string) => string;
  baseDataTestId?: string;
}

export function SelectedHotelsList({
  selectedHotels,
  onRemoveHotel,
  getHotelIcon,
  baseDataTestId = 'SelectedHotelsList',
}: Props) {
  const { t } = useTranslation(['company', 'global']);

  return (
    <div data-testid={`${baseDataTestId}-selected-list-container`} className={styles.listContainer}>
      <ul
        data-testid={`${baseDataTestId}-selected-list`}
        className={cn(styles.listBase, selectedHotels.length > 10 && styles.listScrollable)}
      >
        {selectedHotels.map((hotel) => (
          <li
            key={hotel.id}
            data-testid={`${baseDataTestId}-selected-item-${hotel.id}`}
            className={styles.listItem}
          >
            <div className={styles.itemContent}>
              <Image
                className={styles.itemIcon}
                src={getHotelIcon(hotel.brand)}
                alt={hotel.brand}
                width={24}
                height={24}
              />
              <span className={styles.itemText}>{hotel.suggestion}</span>
            </div>
            <button
              data-testid={`${baseDataTestId}-remove-button-${hotel.id}`}
              onClick={() => onRemoveHotel(hotel.id)}
              className={styles.removeButton}
              aria-label={`${t('company.coMngt.alerts.hotel.delete')} ${hotel.suggestion}`}
            >
              {t('company.coMngt.alerts.hotel.delete')}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

const styles = {
  countContainer: 'text-sm',
  countBold: 'text-zinc-600 font-bold leading-tight',
  countNormal: 'text-zinc-600 font-normal leading-tight',
  listContainer: 'inline-flex flex-col justify-start items-start gap-6 w-full',
  noHotelsMessage: 'text-gray-500',
  listBase: 'w-full space-y-4 overflow-y-auto',
  listScrollable: 'max-h-[30rem]',
  listItem: 'self-stretch inline-flex justify-between items-center w-full',
  itemContent: 'flex items-center flex-1 gap-4',
  itemIcon: 'flex-shrink-0 w-6 h-6',
  itemText: 'flex-1 justify-start text-zinc-800 text-base font-bold leading-normal',
  removeButton: 'text-purple-950 text-sm font-medium leading-tight',
};
