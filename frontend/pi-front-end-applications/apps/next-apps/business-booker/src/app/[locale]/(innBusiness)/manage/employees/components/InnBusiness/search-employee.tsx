'use client';

import { Input } from '@whitbread-eos/atoms/ui';
import { useUpdateSearchParams, formatIBAssetsUrl, sanitize } from '@whitbread-eos/utils';
import debounce from 'lodash/debounce';
import Image from 'next/image';
import { useRouter } from 'next/navigation';
import { useState, useCallback, useRef } from 'react';

type Props = {
  searchInputPlaceHolder: string;
  valueFromUrl?: string;
  searchIcon?: string;
  clearIcon?: string;
};

export const SearchEmployeeInput = ({
  searchIcon,
  valueFromUrl,
  searchInputPlaceHolder,
  clearIcon,
}: Props) => {
  const baseDataTestId = 'SearchEmployeeInput';
  const router = useRouter();
  const { updateSearchParams } = useUpdateSearchParams();

  const [inputValue, setInputValue] = useState(valueFromUrl ?? '');
  const debouncedUpdateUrl = useRef(
    debounce((value: string) => {
      if (value.length >= 3) {
        router.push(updateSearchParams('userSearch', sanitize(value)));
      } else {
        clearUserSearchQueryFromUrl();
      }
    }, 300)
  ).current;

  const clearUserSearchQueryFromUrl = useCallback(() => {
    const currentUrl = updateSearchParams('userSearch');
    router.push(currentUrl);
  }, [updateSearchParams, router]);

  const handleSearchEmployee = (value: string) => {
    setInputValue(value);

    debouncedUpdateUrl(value);
  };

  const handleClearInput = () => {
    setInputValue('');
    clearUserSearchQueryFromUrl();
  };

  return (
    <>
      <div className={inputContainerStyle} data-testid={`${baseDataTestId}-search-input-container`}>
        <Input
          placeholder={searchInputPlaceHolder}
          id={`${baseDataTestId}-search-employees`}
          disabled={false}
          value={inputValue}
          onChange={handleSearchEmployee}
          className={inputStyle}
          hasLabels={false}
        />
        <Image
          alt={'Search icon'}
          src={inputValue ? formatIBAssetsUrl(clearIcon) : formatIBAssetsUrl(searchIcon)}
          width={26}
          height={26}
          className={`${buttonIconStyle} ${inputValue ? 'cursor-pointer' : ''}`}
          data-testid={`${baseDataTestId}-search-input-icon`}
          onClick={handleClearInput}
        />
      </div>
    </>
  );
};

const inputContainerStyle =
  'relative flex justify-center items-center min-w-60 tablet:min-w-40 mobile:w-full';
const inputStyle =
  'truncate h-14 w-80 text-darkGrey1 border border-lightGrey2 focus:outline-secondaryColor hover:border-secondaryColor rounded-lg bg-transparent text-base font-normal placeholder:text-darkGrey2 p-4 mobile:w-full';
const buttonIconStyle = 'absolute right-3';
