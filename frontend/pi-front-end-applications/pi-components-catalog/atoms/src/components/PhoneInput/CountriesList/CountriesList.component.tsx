import type { BoxProps, InputProps, ListItemProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, Image, Input, List, ListItem, Text, useTheme } from '@chakra-ui/react';
import type { Country } from '@whitbread-eos/api';
import { getInputTypographyOverride } from '@whitbread-eos/utils';
import { memo, useCallback, useEffect, useRef, useState } from 'react';

type Props = Omit<BoxProps, 'onChange'> & {
  className?: string;
  formatAssetsUrl: (path: string) => string;
  options: Country[];
  optionTextStyles?: TextProps;
  searchInputTextStyles?: InputProps;
  placeholder?: string;
  onChange: (country: Country) => void;
  onClose?: () => void;
  showDialingCode?: boolean;
};

function CountriesList({
  formatAssetsUrl,
  options,
  optionTextStyles,
  searchInputTextStyles,
  placeholder,
  onChange,
  onClose,
  showDialingCode = true,
  ...rest
}: Props) {
  const theme = useTheme();
  const searchInputTypographyOverride = getInputTypographyOverride(
    searchInputTextStyles,
    theme?.textStyles
  );
  const [filteredList, setFilteredList] = useState(options);
  const [focusedIndex, setFocusedIndex] = useState<number>(-1);
  const containerRef = useRef<HTMLDivElement>(null);
  const searchInputRef = useRef<HTMLInputElement>(null);
  const itemRefs = useRef<(HTMLLIElement | null)[]>([]);

  const handleSearch = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      const value = event.target.value.toLowerCase();
      const result =
        options?.filter((item: Country) => {
          // remove dialingCode optional chaining when data integrity validator is added
          return (
            item.countryName.toLowerCase().includes(value) ||
            item.dialingCode.toLowerCase().includes(value)
          );
        }) || [];
      setFilteredList(result);
      setFocusedIndex(-1);
    },
    [options]
  );

  const handleListKeyDown = useCallback(
    (event: React.KeyboardEvent) => {
      if (filteredList.length === 0) return;

      if (event.key === 'ArrowDown') {
        event.preventDefault();
        setFocusedIndex((prev) => (prev < filteredList.length - 1 ? prev + 1 : 0));
      } else if (event.key === 'ArrowUp') {
        event.preventDefault();
        setFocusedIndex((prev) => (prev > 0 ? prev - 1 : filteredList.length - 1));
      } else if (event.key === 'Enter' && focusedIndex >= 0) {
        event.preventDefault();
        onChange(filteredList[focusedIndex]);
      } else if (event.key === 'Escape' && onClose) {
        event.preventDefault();
        onClose();
      }
    },
    [filteredList, focusedIndex, onChange, onClose]
  );

  const handleListFocus = useCallback(() => {
    if (focusedIndex === -1 && filteredList.length > 0) {
      setFocusedIndex(0);
    }
  }, [focusedIndex, filteredList.length]);

  useEffect(() => {
    searchInputRef.current?.focus();
    if (options.length > 0) {
      setFocusedIndex(0);
    }
  }, [options.length]);

  useEffect(() => {
    if (focusedIndex >= 0 && itemRefs?.current?.[focusedIndex]) {
      const element = itemRefs.current[focusedIndex];
      if (element && typeof element.scrollIntoView === 'function') {
        if (focusedIndex === 0 && containerRef.current) {
          containerRef.current.scrollTop = 0;
        } else {
          element.scrollIntoView({
            block: 'nearest',
          });
        }
      }
    }
  }, [focusedIndex]);

  return (
    <Box ref={containerRef} {...containerStyle} {...rest}>
      <Box {...searchContainerStyle}>
        <Input
          ref={searchInputRef}
          autoComplete="off"
          isInvalid={false}
          placeholder={placeholder}
          {...searchInputStyle}
          {...searchInputTextStyles}
          {...searchInputTypographyOverride}
          onChange={handleSearch}
          onKeyDown={handleListKeyDown}
        />
      </Box>

      <List
        role="listbox"
        aria-activedescendant={
          focusedIndex >= 0 && filteredList[focusedIndex]
            ? `country-option-${filteredList[focusedIndex].countryCode}`
            : undefined
        }
        onKeyDown={handleListKeyDown}
        onFocus={handleListFocus}
        tabIndex={0}
      >
        {filteredList?.map((item: Country, index: number) => (
          <ListItem
            key={`${item.countryCode}-${item.countryName}`}
            id={`country-option-${item.countryCode}`}
            ref={(el) => (itemRefs.current[index] = el)}
            role="option"
            aria-selected={focusedIndex === index}
            {...listItemStyle}
            {...(focusedIndex === index ? { bg: 'lightGrey5' } : {})}
            onClick={() => {
              onChange(item);
            }}
            onMouseEnter={() => setFocusedIndex(index)}
          >
            <Flex>
              <Image
                borderRadius="full"
                boxSize="1.5rem"
                src={formatAssetsUrl(item.flagSrc)}
                title={item.countryName}
                alt={item.countryName}
                mr={'sm'}
              />
              {/* show dialing code for phone field Only */}
              {showDialingCode && item.dialingCode && (
                <Text as="span" mr={3} className="countryDialingCode" {...optionTextStyles}>
                  {item.dialingCode}
                </Text>
              )}
              <Text as="span" {...optionTextStyles}>
                {item.countryName}
              </Text>
            </Flex>
          </ListItem>
        ))}
      </List>
    </Box>
  );
}

const containerStyle: BoxProps = {
  bg: 'white',
  borderRadius: 'lg',
  boxShadow: '0px 1px 30px rgba(0, 0, 0, 0.1)',
  height: 'auto',
  maxH: 'xs',
  my: 1,
  overflow: 'auto',
  position: 'absolute',
  width: 'full',
  zIndex: 999,
};

const listItemStyle: ListItemProps = {
  cursor: 'pointer',
  px: 4,
  py: 3,
  _hover: { bg: 'lightGrey5' },
};

const searchContainerStyle: BoxProps = {
  bg: 'white',
  p: 4,
  position: 'sticky',
  top: 0,
  zIndex: 1,
};

const searchInputStyle: InputProps = {
  borderRadius: 'md',
  size: 'md',
  _focusWithin: { borderColor: 'primary' },
};

export default memo(CountriesList);
