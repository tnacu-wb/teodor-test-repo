'use client';

import { ErrorTooltip } from '@whitbread-eos/atoms/ui';
import { useTranslation, cn } from '@whitbread-eos/utils';
import Image from 'next/image';
import { useRef, useEffect } from 'react';

interface Props {
  location: string;
  onChange: (value: string) => void;
  placeholder: string;
  locationIcon: string;
  clearIcon: string;
  onBlur: (event: React.FocusEvent<HTMLInputElement>) => void;
  onFocus: () => void;
  errorIcon: string;
  showError?: boolean;
  isMobileDialogVisible?: boolean;
  mobile?: boolean;
}

const LocationInput = ({
  location,
  onChange,
  placeholder,
  locationIcon,
  clearIcon,
  onBlur,
  onFocus,
  errorIcon,
  showError,
  isMobileDialogVisible,
  mobile,
}: Readonly<Props>) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const { t } = useTranslation();

  const handleClear = (e: React.MouseEvent<HTMLImageElement>) => {
    e.preventDefault();
    onChange('');
    if (inputRef?.current) {
      inputRef.current.focus();
    }
  };

  const handleClearKeyDown = (e: React.KeyboardEvent<HTMLImageElement>) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      onChange('');
      if (inputRef?.current) {
        inputRef.current.focus();
      }
    }
  };

  useEffect(() => {
    if (isMobileDialogVisible) {
      inputRef?.current?.focus();
    }
  }, [isMobileDialogVisible]);

  return (
    <>
      <div className={locationContainerStyle} data-testid="IB-Location-Container">
        <Image
          className={locationIconStyle}
          src={locationIcon}
          alt={'Location Image'}
          width={24}
          height={24}
        />
        <ErrorTooltip
          icon={errorIcon}
          className={errorTooltipStyle}
          content={t('content.form.invalidLocation')}
          open={showError}
          testId="IB-Location-ErrorTooltip"
          errorId="location-error-message"
        >
          <input
            ref={inputRef}
            className={cn(inputStyle, showError ? errorStyle : inputOutlineStyle)}
            placeholder={placeholder}
            value={location}
            onChange={(e) => onChange(e?.target?.value)}
            data-testid={mobile ? 'IB-Location-Input-Mobile' : 'IB-Location-Input'}
            onBlur={(e) => onBlur(e)}
            onFocus={() => onFocus()}
            aria-invalid={showError ? 'true' : 'false'}
            aria-describedby={
              showError
                ? mobile
                  ? 'location-error-message-mobile'
                  : 'location-error-message'
                : undefined
            }
          />
        </ErrorTooltip>
        <Image
          className={`${clearIconStyle} ${location ? 'visible w-6 h-6' : 'invisible w-0 h-0'}`}
          src={clearIcon}
          alt={'Location Clear'}
          width={24}
          height={24}
          onMouseDown={(e) => handleClear(e)}
          onKeyDown={(e) => handleClearKeyDown(e)}
          tabIndex={location ? 0 : -1}
          role="button"
          data-testid="IB-Location-Clear"
        />
      </div>
      <ErrorTooltip
        icon={errorIcon}
        content={t('content.form.invalidLocation')}
        open={showError}
        testId="IB-Location-ErrorTooltip-Mobile"
        mobile
        errorId="location-error-message-mobile"
      />
    </>
  );
};

export default LocationInput;

const locationContainerStyle =
  'relative flex justify-center items-center min-w-60 tablet:min-w-40 mobile:w-full';
const inputStyle =
  'peer truncate h-14 w-full text-darkGrey1 border-lightGrey2 rounded-l mobile:rounded border bg-transparent p-4 pl-14 pr-14 text-base transition-colors placeholder:text-darkGrey2 z-[41] border-r-0 mobile:border-r-[1px]';
const locationIconStyle = 'absolute left-4 w-6 h-6 text-transparent z-40';
const clearIconStyle =
  'absolute right-4 text-transparent z-[42] cursor-pointer focus:outline focus:outline-2 focus:outline-primaryColor focus:rounded focus:visible focus:w-6 focus:h-6';
const inputOutlineStyle =
  'focus:-outline-offset-2 focus:outline focus:outline-primaryColor focus:outline-2 hover:outline-1 hover:-outline-offset-1 hover:outline hover:outline-darkGrey1';
const errorStyle = '-outline-offset-2 outline outline-2 outline-error';
const errorTooltipStyle = 'max-w-[90%]';
