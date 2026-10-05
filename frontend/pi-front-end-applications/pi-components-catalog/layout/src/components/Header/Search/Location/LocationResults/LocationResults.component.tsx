'use client';

import {
  FormInnB,
  HotelBrand,
  SearchPropertyType,
  SearchPlaceType,
  GlobalInnB,
} from '@whitbread-eos/api';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { getLocationResults } from '@whitbread-eos/utils/server';
import debounce from 'lodash/debounce';
import Image from 'next/image';
import React, { useEffect, useState, useRef, Dispatch, SetStateAction } from 'react';

const MIN_LENGTH_SEARCH_TERM = 3;
interface Props {
  location: string;
  labels: FormInnB | Record<string, never>;
  setLocation: Dispatch<SetStateAction<string>>;
  isInputBlured: boolean;
  selectedLocation: SearchPropertyType | SearchPlaceType | Record<string, never>;
  setSelectedLocation: Dispatch<
    SetStateAction<SearchPropertyType | SearchPlaceType | Record<string, never>>
  >;
  onResultsFound: (resultsFound: boolean) => void;
  mobile: boolean;
  globalLabels: GlobalInnB | Record<string, never>;
  onOpenChange: (open: boolean) => void;
  onResultClick?: (value: string) => void;
}

const LocationResults = ({
  location = '',
  setLocation,
  labels,
  isInputBlured,
  selectedLocation,
  setSelectedLocation,
  onResultsFound,
  mobile,
  globalLabels,
  onOpenChange,
  onResultClick,
}: Readonly<Props>) => {
  const { t } = useTranslation();

  const [isOpen, setIsOpen] = useState(false);
  interface ResultsState {
    hotels: SearchPropertyType[];
    places: SearchPlaceType[];
  }
  const [results, setResults] = useState<ResultsState>({ hotels: [], places: [] });

  const resultsContainerRef = useRef<HTMLDivElement | null>(null);
  const [focusedIndex, setFocusedIndex] = useState<number>(-1);
  const itemRefs = useRef<(HTMLDivElement | HTMLButtonElement | null)[]>([]);
  const [hasClickedResult, setHasClickedResult] = useState(false);

  const handleResultClick = (value: SearchPropertyType | SearchPlaceType) => {
    setHasClickedResult(true);
    setLocation(value?.suggestion);
    setSelectedLocation(value);
    onResultClick?.(value?.suggestion);
    onOpenChange(false); // Close the results container and overlay
    setFocusedIndex(-1); // Reset focus after selection
  };

  const getHotelIcon = (brand: string) => {
    if (brand === HotelBrand.HUB) {
      return formatIBAssetsUrl(globalLabels?.brand?.hubLogo);
    } else if (brand === HotelBrand.ZIP) {
      return formatIBAssetsUrl(globalLabels?.brand?.zipLogo);
    }
    return formatIBAssetsUrl(globalLabels?.brand?.piLogo);
  };

  const getResults = async (searchTerm: string) => {
    const newResults = await getLocationResults(searchTerm);

    if (newResults?.properties?.length || newResults?.places?.length) {
      const allLocationResults = [...newResults.places, ...newResults.properties];
      const matchLocation =
        allLocationResults.find((item) => item.suggestion === searchTerm) ?? allLocationResults[0];

      // On mobile, only set when input is blurred to avoid updating while typing
      if (matchLocation) {
        if (mobile) {
          if (isInputBlured) {
            setSelectedLocation(matchLocation);
          }
        } else {
          setSelectedLocation(matchLocation);
        }
      }

      setIsOpen(true);
      setResults({
        hotels: newResults?.properties,
        places: newResults?.places,
      });
      onResultsFound(true);
    } else {
      onResultsFound(false);
      setIsOpen(false);
    }
  };

  const debouncedSearch = useRef(debounce(getResults, 300)).current;

  useEffect(() => {
    if (location.trim() === selectedLocation?.suggestion) {
      return;
    }
    if (location && location.trim().length >= MIN_LENGTH_SEARCH_TERM && !hasClickedResult) {
      debouncedSearch(location);
    } else {
      setIsOpen(false);
      if (!mobile) {
        setSelectedLocation({});
      }
      setHasClickedResult(false);
      setFocusedIndex(-1); // Reset focused index when location changes
    }
  }, [location]);

  useEffect(() => {
    if (mobile) {
      // On mobile, keep results visible as long as there are results, regardless of blur state
      onOpenChange(isOpen && (!!results?.places?.length || !!results?.hotels?.length));
      return;
    }

    const hasSearchInput = location.trim().length > 0;
    onOpenChange(!isInputBlured && (hasSearchInput || isOpen));
  }, [isOpen, isInputBlured, location, mobile, results?.places?.length, results?.hotels?.length]);

  useEffect(() => {
    if (isOpen && resultsContainerRef.current) {
      setFocusedIndex(-1); // Reset when container opens/regains focus
    }
  }, [isOpen]);

  // Handle blur from results container
  const handleResultsBlur = (e: React.FocusEvent<HTMLDivElement>) => {
    const relatedTarget = e.relatedTarget as HTMLElement | null;
    const resultsContainer = e.currentTarget;

    // Check if focus is moving outside the results container
    if (!resultsContainer.contains(relatedTarget)) {
      // Always reset to -1 to ensure first item is ready for arrow navigation on return
      setFocusedIndex(-1);
      onOpenChange(false); // Close results container
    }
  };

  // Handle keyboard navigation
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (!isOpen || isInputBlured) return;

      const allResults = [...results.places, ...results.hotels];
      const totalItems = allResults.length;

      if (e.key === 'ArrowDown') {
        e.preventDefault();
        // When focusedIndex is -1, pressing ArrowDown will set it to 0 (first item)
        setFocusedIndex((prev) => (prev < totalItems - 1 ? prev + 1 : prev));
      } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        // ArrowUp from first item (0) returns to -1 (no selection)
        setFocusedIndex((prev) => (prev > 0 ? prev - 1 : -1));
      } else if (e.key === 'Enter' && focusedIndex >= 0) {
        e.preventDefault();
        const selectedItem = allResults[focusedIndex];
        if (selectedItem) {
          handleResultClick(selectedItem);
        }
      } else if (e.key === 'Tab') {
        // Explicitly reset on Tab to ensure clean state
        setFocusedIndex(-1);
      }
    };

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, isInputBlured, focusedIndex, results.places, results.hotels, handleResultClick]);

  // Scroll focused item into view
  useEffect(() => {
    if (focusedIndex >= 0 && itemRefs.current[focusedIndex]) {
      itemRefs.current[focusedIndex]?.scrollIntoView?.({
        block: 'nearest',
        behavior: 'smooth',
      });
    }
  }, [focusedIndex]);

  const displayPlaces = () => {
    return (
      <div
        className={`${resultsColStyle} outline-none`}
        data-testid="IB-Places-List"
        ref={resultsContainerRef}
        tabIndex={-1}
        onFocus={() => setFocusedIndex(-1)} // Reset when this container gains focus
      >
        {results?.places.map((place: SearchPlaceType, index: number) => {
          const isFocused = index === focusedIndex;
          return (
            <div
              ref={(el) => {
                itemRefs.current[index] = el;
              }}
              onMouseDown={() => handleResultClick(place)}
              onMouseEnter={() => setFocusedIndex(index)}
              className={`${resultRowStyle} ${placeRowStyle} ${isFocused ? 'bg-lightGrey5' : ''}`}
              key={place?.suggestion}
              tabIndex={-1} // Prevent default tab navigation
            >
              <span className={resultTextStyle}>{place?.suggestion}</span>
            </div>
          );
        })}
      </div>
    );
  };

  const displayHotels = () => {
    return (
      <div className={resultsColStyle}>
        <span className={hotelsSeparatorStyle}>{t(labels?.hotelsLabel || '')}</span>
        <div
          className={`${resultsColStyle} outline-none`}
          data-testid="IB-Hotels-List"
          ref={results?.places?.length ? null : resultsContainerRef}
          tabIndex={-1}
          onFocus={() => setFocusedIndex(-1)} // Reset when this container gains focus
        >
          {results?.hotels.map((hotel: SearchPropertyType, index: number) => {
            const itemIndex = results.places.length + index;
            const isFocused = itemIndex === focusedIndex;
            return (
              <button
                ref={(el) => {
                  itemRefs.current[itemIndex] = el;
                }}
                onMouseDown={() => handleResultClick(hotel)}
                onMouseEnter={() => setFocusedIndex(itemIndex)}
                className={`${resultRowStyle} ${hotelRowStyle} ${isFocused ? 'bg-lightGrey5' : ''}`}
                key={hotel?.suggestion}
                tabIndex={-1}
              >
                <Image
                  className={hotelBadgeStyle}
                  alt={hotel?.suggestion}
                  src={getHotelIcon(hotel?.brand)}
                  width={24}
                  height={24}
                />
                <span className={resultTextStyle}>{hotel?.suggestion}</span>
              </button>
            );
          })}
        </div>
      </div>
    );
  };
  return (
    isOpen &&
    !isInputBlured && (
      <div
        data-testid="Location-Results-Container"
        className={mobile ? containerMobileStyle : containerStyle}
        role="listbox"
        tabIndex={0}
        onMouseDown={(e) => e.preventDefault()}
        onFocus={() => setFocusedIndex(-1)} // Reset when main container gains focus
        onBlur={handleResultsBlur} // Handle blur from container
      >
        {!!results?.places?.length && <div>{displayPlaces()}</div>}
        {!!results?.hotels?.length && <div>{displayHotels()}</div>}
      </div>
    )
  );
};

export default LocationResults;

// ensure container touches input else will lose focus and trigger blur
const containerStyle =
  'absolute left-0 top-14 mt-1 flex flex-col py-2 z-50 w-[25rem] overflow-hidden rounded-md border bg-popover shadow-md';
const containerMobileStyle =
  'absolute top-[11rem] left-[34px] mobile:pt-2 mobile:rounded-none mobile:overflow-y-auto mobile:mx-[-16px]';
const resultsColStyle = 'flex flex-col';
const resultRowStyle =
  'flex items-center px-4 text-sm leading-4 hover:bg-lightGrey5 cursor-pointer';
const resultTextStyle = 'truncate';
const hotelRowStyle = 'py-2';
const placeRowStyle = 'py-3';
const hotelsSeparatorStyle = 'text-sm leading-4 font-semibold px-4 pt-5 pb-2.5';
const hotelBadgeStyle = 'flex shrink-0 w-6 h-6 mr-4';
