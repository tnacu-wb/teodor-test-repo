import { Box, Text, type TextProps } from '@chakra-ui/react';
import { formatDataTestId } from '@whitbread-eos/utils';
import { ReactNode } from 'react';

export type DropdownOption = {
  id: number | string;
  icon?: ReactNode;
  label: string | number;
};

interface CustomContentProps {
  isOpen: boolean;
  onChange?: (option: DropdownOption | undefined) => void;
  setSelectedValue: (option: DropdownOption) => void;
  setIsOpen: (value: boolean) => void;
  options?: DropdownOption[];
  baseDataTestId: string;
  dataTestId: string;
  dropdownStyles?: {
    menuItemTextStyles?: TextProps;
  };
}

export default function DropdownCustomContent({
  isOpen,
  onChange,
  setSelectedValue,
  setIsOpen,
  options,
  baseDataTestId,
  dataTestId,
  dropdownStyles,
}: Readonly<CustomContentProps>) {
  const menuItemTextStyles = dropdownStyles?.menuItemTextStyles ?? menuItemLegacyTypography;

  if (isOpen) {
    return (
      <Box
        {...menuListStyles}
        data-testid={formatDataTestId(baseDataTestId, `${dataTestId}-entireList`)}
      >
        {options?.map?.((option, index) => (
          <Box
            {...menuItemStyles}
            data-testid={formatDataTestId(baseDataTestId, `${dataTestId}-${index}`)}
            key={option.id}
            role="menuitem"
            _hover={{
              background: 'var(--chakra-colors-lightGrey5)',
            }}
            _focus={{
              background: 'var(--chakra-colors-lightGrey5)',
            }}
            onClick={() => {
              if (onChange) {
                onChange(option);
              }
              setSelectedValue(option);
              setIsOpen(false);
            }}
          >
            {renderDropdownOption(option)}
          </Box>
        ))}
      </Box>
    );
  }
  return null;

  function renderDropdownOption(option: DropdownOption | undefined): ReactNode {
    return (
      <>
        {option?.icon && <Box pr="md">{option.icon}</Box>}
        <Text as="span" {...menuItemTextStyles}>
          {option?.label}
        </Text>
      </>
    );
  }
}

const menuItemLegacyTypography = {
  fontSize: 'var(--chakra-fontSizes-sm)',
  lineHeight: 'var(--chakra-lineHeights-1)',
} as TextProps;

const menuItemStyles = {
  alignItems: 'center',
  cursor: 'pointer',
  display: 'flex',
  textAlign: 'start',
  outline: 'transparent solid 2px',
  outlineOffset: '2px',
  paddingInline: '0.8rem',
  height: 'var(--chakra-space-2xl)',
  padding: '12px var(--chakra-space-md)',
} as const;

const menuListStyles = {
  outline: 'transparent solid 2px',
  outlineOffset: '2px',
  background: '#ffffff',
  boxShadow: '0 2px 12px var(--chakra-colors-lightGrey2)',
  minWidth: 'var(--chakra-space-full)',
  paddingTop: 'var(--chakra-space-2)',
  paddingBottom: 'var(--chakra-space-2)',
  zIndex: 1000,
  borderWidth: '2px',
  overflowY: 'scroll',
  position: 'absolute',
  top: '0px',
  paddingInline: '0px',
  width: 'var(--chakra-space-full)',
  borderColor: 'lightGrey3',
  maxHeight: '200px',
  transformOrigin: 'top left',
  opacity: 1,
  visibility: 'visible',
  transform: 'translate(0px, 57px)',
} as const;
