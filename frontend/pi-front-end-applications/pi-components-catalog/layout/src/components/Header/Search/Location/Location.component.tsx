'use client';

import { FormInnB, SearchPropertyType, SearchPlaceType, GlobalInnB } from '@whitbread-eos/api';
import { formatIBAssetsUrl, useTranslation, cn } from '@whitbread-eos/utils';
import { X } from 'lucide-react';
import React, { Dispatch, SetStateAction, useState, useEffect } from 'react';

import { LocationInput } from './LocationInput/index';
import { LocationResults } from './LocationResults/index';

export interface LocationProps {
  labels: FormInnB | Record<string, never>;
  location: string;
  setLocation: Dispatch<SetStateAction<string>>;
  selectedLocation: SearchPropertyType | SearchPlaceType | Record<string, never>;
  setSelectedLocation: Dispatch<
    SetStateAction<SearchPropertyType | SearchPlaceType | Record<string, never>>
  >;
  icons: Record<string, string>;
  mobile: boolean;
  globalLabels: GlobalInnB | Record<string, never>;
  hideEditSearch?: () => void;
  onOpenChange: (open: boolean) => void;
  isOpen?: boolean;
  showError: boolean;
  setShowError: (value: boolean) => void;
}

const Location = ({
  labels,
  location,
  setLocation,
  setSelectedLocation,
  selectedLocation,
  icons,
  mobile = false,
  globalLabels,
  hideEditSearch = () => {
    return;
  },
  onOpenChange,
  isOpen,
  showError,
  setShowError,
}: Readonly<LocationProps>) => {
  const [isInputBlured, setIsInputBlured] = useState(true);
  const [isMobileModalInputBlurred, setIsMobileModalInputBlurred] = useState(true);
  const { t } = useTranslation(['layout']);

  const [mobileModalLocation, setMobileModalLocation] = useState(location);
  const [isMobileModalVisible, setIsMobileModalVisible] = useState(false);

  useEffect(() => {
    if (mobile) {
      setMobileModalLocation(location);
    }
  }, [location, mobile]);

  // Sync isInputBlured when location is closed externally (e.g., overlay click)
  useEffect(() => {
    if (isOpen === false && !mobile) {
      setIsInputBlured(true);
    }
  }, [isOpen, mobile]);

  const handleMobileModalLocationChange = (value: string) => {
    setIsMobileModalInputBlurred(false);
    setMobileModalLocation(value);
  };

  const handleLocationChange = (value: string) => {
    setIsInputBlured(false);
    setLocation(value);

    if (!value) {
      setShowError(false);
    }
  };

  const handleInputBlur = (event: React.FocusEvent<HTMLInputElement>) => {
    if (selectedLocation?.suggestion) {
      setLocation(selectedLocation?.suggestion);
    }

    const relatedTarget = event.relatedTarget as HTMLElement | null;
    const resultsContainer = document.querySelector('[data-testid="Location-Results-Container"]');

    // Don't blur if focus is moving to results container or its children
    if (resultsContainer && relatedTarget && resultsContainer.contains(relatedTarget)) {
      return;
    }

    // At this point, focus is leaving the input and NOT going to results
    setIsInputBlured(true);

    // Close the dropdown when blurring away from input
    if (!mobile) {
      onOpenChange(false);
    }
  };

  const handleInputFocus = () => {
    setIsInputBlured(false);
    if (mobile) {
      setMobileModalLocation(location);
      setIsMobileModalVisible(true);
    }
  };

  const handleMobileInputFocus = () => {
    setIsMobileModalInputBlurred(false);
    setIsMobileModalVisible(true);
  };

  const handleMobileInputBlur = (event: React.FocusEvent<HTMLInputElement>) => {
    const relatedTarget = event.relatedTarget as HTMLElement | null;
    const resultsContainer = document.querySelector('[data-testid="Location-Results-Container"]');
    const mobileModalContainer = document.querySelector(
      '[data-testid="Location-Header"]'
    )?.parentElement;

    // Don't blur if focus is moving to results container or staying within mobile modal
    if (
      (resultsContainer && relatedTarget && resultsContainer.contains(relatedTarget)) ||
      (mobileModalContainer && relatedTarget && mobileModalContainer.contains(relatedTarget))
    ) {
      return;
    }

    setIsMobileModalInputBlurred(true);
  };

  const onMobileModalClose = () => {
    if (selectedLocation?.suggestion) {
      setShowError(false);
    }
    setIsMobileModalInputBlurred(true);
    onOpenChange(false);
    setIsMobileModalVisible(false);
  };

  const handleResultsFound = (resultsFound: boolean) => {
    setShowError(!resultsFound);
  };

  const displayNewMobileModal = (
    <div className={cn(mobileModalContainerStyle, isMobileModalVisible ? 'flex' : 'hidden')}>
      <div data-testid="Location-Header" className={mobileModalHeaderStyle}>
        <div className="flex mb-[45px]">
          <span className={mobileModalTitleStyle}>
            {t('layout.innbusinessLayout.header.search.location')}
          </span>
          <button
            className={mobileModalCloseStyle}
            onClick={onMobileModalClose}
            data-testid="Mobile-Modal-Close-Button"
          >
            <X className="stroke-lightGrey2 w-10 h-10" />
          </button>
        </div>
        <LocationInput
          location={mobileModalLocation}
          onChange={handleMobileModalLocationChange}
          locationIcon={formatIBAssetsUrl(labels?.whereIcon)}
          placeholder={labels?.where ?? ''}
          clearIcon={formatIBAssetsUrl(labels?.whereDismissIcon)}
          onBlur={handleMobileInputBlur}
          onFocus={handleMobileInputFocus}
          errorIcon={formatIBAssetsUrl(icons['icon.notification.error'])}
          showError={showError}
          isMobileDialogVisible={isMobileModalVisible}
          mobile
        />
      </div>
      <div className={mobileModalResultsStyle}>
        <LocationResults
          location={mobileModalLocation}
          setLocation={setLocation}
          labels={labels}
          isInputBlured={isMobileModalInputBlurred}
          selectedLocation={selectedLocation}
          setSelectedLocation={setSelectedLocation}
          onResultsFound={handleResultsFound}
          mobile={true}
          globalLabels={globalLabels}
          onOpenChange={(open: boolean) => onOpenChange(open)}
          onResultClick={onMobileModalClose}
        />
      </div>
    </div>
  );

  useEffect(() => {
    if (showError) {
      hideEditSearch();
    }
  }, [showError]);

  return (
    <div
      className={firstRowStyle}
      data-testid={
        mobile ? 'IB-Search-Container-Mobile-Location' : 'IB-Search-Container-Desktop-Location'
      }
    >
      <LocationInput
        location={location}
        onChange={handleLocationChange}
        locationIcon={formatIBAssetsUrl(labels?.whereIcon)}
        placeholder={labels?.where ?? ''}
        clearIcon={formatIBAssetsUrl(labels?.whereDismissIcon)}
        onBlur={handleInputBlur}
        onFocus={handleInputFocus}
        errorIcon={formatIBAssetsUrl(icons['icon.notification.error'])}
        showError={showError}
      />
      {!mobile ? (
        <LocationResults
          location={location}
          setLocation={setLocation}
          labels={labels}
          isInputBlured={isInputBlured}
          selectedLocation={selectedLocation}
          setSelectedLocation={setSelectedLocation}
          onResultsFound={handleResultsFound}
          mobile={false}
          globalLabels={globalLabels}
          onOpenChange={(open: boolean) => onOpenChange(open)}
        />
      ) : (
        displayNewMobileModal
      )}
    </div>
  );
};

export default Location;

const firstRowStyle = 'relative grow-[4] mobile:w-full bg-white';
const mobileModalContainerStyle =
  'fixed flex flex-col top-0 left-0 right-0 bottom-0 bg-baseWhite z-[1000000] py-6';
const mobileModalHeaderStyle = 'flex flex-col pb-4 px-4 border-b border-lightGrey3';
const mobileModalTitleStyle =
  'text-secondaryColor text-4xl font-black leading-none tracking-tight truncate pr-[40px]';
const mobileModalCloseStyle = 'absolute top-12 right-12 mobile:top-6 mobile:right-4';
const mobileModalResultsStyle = 'flex-1 min-h-0 overflow-y-auto overflow-x-hidden px-4';
