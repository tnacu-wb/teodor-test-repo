'use client';

import { SearchPropertyType, GlobalInnB } from '@whitbread-eos/api';
import { Input } from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl, cn } from '@whitbread-eos/utils';
import { getLocationResults } from '@whitbread-eos/utils/server';
import debounce from 'lodash/debounce';
import Image from 'next/image';
import { useState, useRef, useCallback, useEffect } from 'react';

export type HotelSearchResult = Pick<SearchPropertyType, 'suggestion' | 'brand' | 'code'> & {
  id: string;
};

const MIN_LENGTH_SEARCH_TERM = 3;
const DEBOUNCE_DELAY = 300;

interface Props {
  globalLabels: GlobalInnB | Record<string, never>;
  locationIcon: string;
  onHotelSelect: (hotel: HotelSearchResult) => void;
  getHotelIcon: (brand: string) => string;
  baseDataTestId?: string;
}

export function HotelSearchInput({
  locationIcon,
  onHotelSelect,
  getHotelIcon,
  baseDataTestId = 'HotelSearchInput',
}: Props) {
  const { t } = useTranslation(['company', 'global']);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState<HotelSearchResult[]>([]);
  const [highlightedIndex, setHighlightedIndex] = useState<number | null>(null);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const searchContainerRef = useRef<HTMLDivElement>(null);
  const resultsDropdownRef = useRef<HTMLDivElement>(null);

  const debouncedSearch = useRef(
    debounce(async (term: string) => {
      if (term.length < MIN_LENGTH_SEARCH_TERM) {
        setSearchResults([]);
        setHighlightedIndex(null);
        setIsDropdownOpen(false);
        return;
      }
      setIsDropdownOpen(true);
      try {
        const results = await getLocationResults(term, true);
        const hotels = (results?.properties || [])
          .map((prop: SearchPropertyType) => ({
            suggestion: prop.suggestion,
            brand: prop.brand,
            code: prop.code,
            id: prop.code,
          }))
          .filter(
            (hotel: Partial<HotelSearchResult>): hotel is HotelSearchResult =>
              !!hotel.code && typeof hotel.suggestion === 'string'
          );

        setSearchResults(hotels);
        setHighlightedIndex(hotels.length > 0 ? 0 : null);
      } catch (error) {
        setSearchResults([]);
        setHighlightedIndex(null);
      }
    }, DEBOUNCE_DELAY)
  ).current;

  useEffect(() => {
    return () => {
      debouncedSearch.cancel();
    };
  }, [debouncedSearch]);

  const handleSearchChange = useCallback(
    (value: string) => {
      const newSearchTerm = value;
      setSearchTerm(newSearchTerm);
      debouncedSearch(newSearchTerm);
    },
    [debouncedSearch]
  );

  const handleSelectHotel = (hotel: HotelSearchResult) => {
    onHotelSelect(hotel);
    setSearchTerm('');
    setSearchResults([]);
    setHighlightedIndex(null);
    setIsDropdownOpen(false);
    debouncedSearch.cancel();
  };

  const closeDropdown = useCallback(() => {
    setSearchResults([]);
    setHighlightedIndex(null);
    setIsDropdownOpen(false);
    debouncedSearch.cancel();
  }, [debouncedSearch]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        searchContainerRef.current &&
        !searchContainerRef.current.contains(event.target as Node) &&
        resultsDropdownRef.current &&
        !resultsDropdownRef.current.contains(event.target as Node)
      ) {
        closeDropdown();
      }
    };

    if (isDropdownOpen && searchResults.length > 0) {
      document.addEventListener('mousedown', handleClickOutside);
    } else {
      document.removeEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [searchResults.length, isDropdownOpen, closeDropdown]);

  const formattedLocationIconUrl = formatIBAssetsUrl(locationIcon);

  return (
    <div className={styles.container} ref={searchContainerRef}>
      <Image
        className={styles.locationIcon}
        src={formattedLocationIconUrl}
        alt="Location Icon"
        width={20}
        height={20}
      />
      <Input
        data-testid={`${baseDataTestId}-search-input`}
        placeholder={t('company.coMngt.alerts.hotel.placeholder')}
        value={searchTerm}
        onChange={handleSearchChange}
        className={styles.searchInput}
        role="combobox"
        aria-expanded={isDropdownOpen && searchResults.length > 0}
        aria-controls={`${baseDataTestId}-search-results`}
        aria-activedescendant={
          highlightedIndex !== null && searchResults.length > highlightedIndex
            ? `${baseDataTestId}-search-result-${searchResults[highlightedIndex]?.id}`
            : undefined
        }
      />
      {isDropdownOpen && (
        <div
          data-testid={`${baseDataTestId}-search-results`}
          ref={resultsDropdownRef}
          className={styles.dropdownContainer}
          role="listbox"
          id={`${baseDataTestId}-search-results`}
        >
          <div className={styles.resultsList}>
            {searchResults.map((hotel, index) => (
              <div
                key={hotel.id}
                id={`${baseDataTestId}-search-result-${hotel.id}`}
                data-testid={`${baseDataTestId}-search-result-${hotel.id}`}
                className={cn(
                  styles.resultItemBase,
                  index === highlightedIndex
                    ? styles.resultItemHighlighted
                    : styles.resultItemDefault
                )}
                onClick={() => handleSelectHotel(hotel)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    handleSelectHotel(hotel);
                  }
                }}
                onMouseDown={(e) => e.preventDefault()}
                onMouseEnter={() => setHighlightedIndex(index)}
                role="option"
                aria-selected={index === highlightedIndex}
                tabIndex={0}
              >
                <Image
                  className={styles.resultIcon}
                  src={getHotelIcon(hotel.brand)}
                  alt={hotel.brand}
                  width={24}
                  height={24}
                />
                <span className={styles.resultText}>{hotel.suggestion}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

const styles = {
  container: 'relative flex items-center',
  locationIcon: 'absolute left-4 w-5 h-5 text-gray-500',
  searchInput: 'w-full pl-11 pr-4',
  dropdownContainer:
    'absolute top-full left-0 z-10 mt-1 w-full md:w-[620px] rounded shadow-[0px_2px_8px_0px_rgba(0,0,0,0.20)] outline outline-1 outline-offset-[-1px] outline-zinc-300 bg-white overflow-hidden',
  loadingMessage: 'px-4 py-2.5 text-center text-Neutral-900-(Dark-Grey-1)',
  resultsList: 'max-h-[17rem] overflow-y-auto',
  noResultsMessage: 'px-4 py-2.5 text-center text-Neutral-900-(Dark-Grey-1)',
  resultItemBase: 'flex items-center px-4 py-2.5 gap-4 cursor-pointer',
  resultItemDefault: 'bg-Neutral-White hover:bg-Neutral-100-(Light-Grey-5)',
  resultItemHighlighted: 'bg-Neutral-100-(Light-Grey-5)',
  resultIcon: 'flex-shrink-0 w-6 h-6',
  resultText: 'flex-1 text-Neutral-900-(Dark-Grey-1) text-base font-medium leading-normal',
};
