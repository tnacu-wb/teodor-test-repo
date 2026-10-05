import { MenuButtonProps, type TextProps } from '@chakra-ui/react';
import { Dropdown, ButtonProps } from '@whitbread-eos/atoms';
import { useSemanticTypography } from '@whitbread-eos/utils';

import { SORT_TYPES, NEW_PI_SORT_TYPES, NEW_BB_SORT_TYPES } from './constants';

type SortByLabels = { sortByDistance: string; sortByPrice: string; sortByRecommended: string };
type SortByOption = { id: string; label: string };

interface Props {
  labels: SortByLabels;
  selectedOption: string;
  isDisabled?: boolean;
  onChange: (value: any) => void;
  isPiSortOrderDropdownEnabled?: boolean;
  isBbSortOrderDropdownEnabled?: boolean;
  buttonProps?: Partial<ButtonProps>;
  isSplitView?: boolean;
}

export default function SortBy({
  labels,
  selectedOption,
  isDisabled = false,
  onChange,
  isPiSortOrderDropdownEnabled = false,
  isBbSortOrderDropdownEnabled = false,
  buttonProps,
  isSplitView = false,
}: Readonly<Props>) {
  const getTypographyProps = useSemanticTypography();

  const labelTypographyStyles = getTypographyProps(
    sortByLabelLegacyTypography,
    sortByLabelSemanticTypography
  );
  const menuButtonTypographyStyles = getTypographyProps(
    sortByMenuButtonLegacyTypography,
    sortByMenuButtonSemanticTypography
  );
  const menuItemTypographyStyles = getTypographyProps(
    sortByMenuItemLegacyTypography,
    sortByMenuItemSemanticTypography
  );

  const wrapperStyles = isSplitView
    ? { ...dropdownWrapperStyles, marginRight: 0 }
    : dropdownWrapperStyles;

  let dropdownStyles = {
    wrapperStyles,
    menuButtonStyles: {
      ...dropdownMenuStyles,
      ...menuButtonTypographyStyles,
    },
    menuButtonTextStyles: menuButtonTypographyStyles,
    menuItemStyles: menuItemTypographyStyles,
  };

  if (buttonProps) {
    dropdownStyles = {
      wrapperStyles,
      menuButtonStyles: {
        ...dropdownMenuStyles,
        ...menuButtonTypographyStyles,
        ...buttonProps,
      },
      menuButtonTextStyles: menuButtonTypographyStyles,
      menuItemStyles: menuItemTypographyStyles,
    };
  }

  const sortByOptions: SortByOption[] = getSortByOptions(
    labels,
    isPiSortOrderDropdownEnabled,
    isBbSortOrderDropdownEnabled
  );

  return (
    <Dropdown
      options={sortByOptions}
      matchWidth
      dropdownStyles={{
        ...dropdownStyles,
        labelTextStyles: labelTypographyStyles,
        menuItemTextStyles: menuItemTypographyStyles,
      }}
      selectedId={selectedOption}
      onChange={onChange}
      dataTestId="sort-by"
      disabled={isDisabled}
    />
  );
}

const dropdownWrapperStyles = {
  w: {
    base: '8.125rem',
    xs: '10.25rem',
    sm: '12.125rem',
    lg: '11.5rem',
  },
  marginRight: {
    mobile: '0',
    xs: 'lg',
  },
  zIndex: '9',
};
const dropdownMenuStyles = {
  w: 'full',
  h: 'var(--chakra-space-2xl)',
  borderRadius: '0.1875rem',
  borderColor: 'lightGrey2',
  _hover: {
    borderColor: 'primary',
  },
  _focus: {
    borderColor: 'lightGrey2',
  },
} as MenuButtonProps;

const sortByLabelLegacyTypography = {
  fontSize: 'var(--chakra-fontSizes-sm)',
  lineHeight: 'var(--chakra-lineHeights-1)',
} as TextProps;

const sortByLabelSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const sortByMenuButtonLegacyTypography = {
  fontSize: 'var(--chakra-fontSizes-md)',
  lineHeight: 'var(--chakra-lineHeights-3)',
} as TextProps;

const sortByMenuButtonSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const sortByMenuItemLegacyTypography = {
  fontSize: 'var(--chakra-fontSizes-sm)',
  lineHeight: 'var(--chakra-lineHeights-1)',
} as TextProps;

const sortByMenuItemSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

function getSortByOptions(
  labels: SortByLabels,
  isPiSortOrderDropdownEnabled: boolean,
  isBbSortOrderDropdownEnabled: boolean
): SortByOption[] {
  const getOptions = (sortTypes: any, order: (keyof SortByLabels)[]) =>
    order.map((key, index) => ({
      id: sortTypes[index + 1],
      label: labels[key],
    }));

  if (isPiSortOrderDropdownEnabled) {
    return getOptions(NEW_PI_SORT_TYPES, ['sortByRecommended', 'sortByDistance', 'sortByPrice']);
  }

  if (isBbSortOrderDropdownEnabled) {
    return getOptions(NEW_BB_SORT_TYPES, ['sortByDistance', 'sortByRecommended', 'sortByPrice']);
  }

  return getOptions(SORT_TYPES, ['sortByDistance', 'sortByPrice']);
}
