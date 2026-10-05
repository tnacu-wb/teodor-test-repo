import {
  Box,
  BoxProps,
  Flex,
  Menu,
  MenuButton,
  MenuButtonProps,
  MenuGroup,
  MenuItem,
  MenuItemProps,
  MenuList,
  MenuProps,
  Text,
  type TextProps,
  useOutsideClick,
} from '@chakra-ui/react';
import { JSXElementConstructor, memo, ReactNode, useEffect, useRef, useState } from 'react';

import { Error24, Success24 } from '../../assets/icons';
import ChevronDown from '../../assets/icons/ChevronDown';
import ChevronUp from '../../assets/icons/ChevronUp';
import { formatDataTestId } from '../../utils/formatters';
import Icon from '../Icon';

export type DropdownOption = {
  id: number | string;
  icon?: ReactNode;
  label: string | number;
  code?: string;
};

export interface DropdownStyles {
  menuStyles?: MenuProps;
  menuButtonStyles?: MenuButtonProps;
  menuItemStyles?: MenuItemProps;
  menuButtonWrapperStyles?: BoxProps;
  wrapperStyles?: BoxProps;
  menuListStyles?: BoxProps;
  errorHoverMenuButtonStyles?: BoxProps;
  labelTextStyles?: TextProps;
  menuButtonTextStyles?: TextProps;
  menuItemTextStyles?: TextProps;
}

export interface DropdownProps {
  variant?: 'default' | 'error';
  dropdownStyles?: DropdownStyles;
  placeholder?: string;
  label?: string;
  icon?: ReactNode;
  hasError?: boolean;
  disabled?: boolean;
  skipChevron?: boolean;
  showStatusIcon?: boolean;
  children?: JSXElementConstructor<any>;
  onChange?: (option: DropdownOption | undefined) => void;
  options?: DropdownOption[];
  isOpenMenu?: boolean;
  matchWidth?: boolean;
  dataTestId?: string;
  selectedId?: string;
  onDisplayContent?: (param: boolean) => void;
  onBlur?: () => void;
  className?: string;
  tabbingAccessibility?: (
    event: React.KeyboardEvent,
    focusableRefs: React.MutableRefObject<HTMLElement[]>,
    isOpen?: boolean
  ) => void;
  focusableRefs?: React.MutableRefObject<HTMLElement[]>;
  accessibilityRole?: 'menu' | 'combobox';
}

function Dropdown(props: Readonly<DropdownProps>) {
  const {
    variant = 'default',
    options = [],
    children: CustomContent,
    dropdownStyles,
    isOpenMenu = false,
    matchWidth,
    dataTestId,
    hasError,
    disabled,
    showStatusIcon,
    selectedId,
    onDisplayContent,
    onBlur,
    className,
    tabbingAccessibility,
    focusableRefs,
    accessibilityRole = 'menu',
  } = props;
  const baseDataTestId = 'DropdownComp';
  const [selectedValue, setSelectedValue] = useState<DropdownOption | undefined>(undefined);
  const [isOpen, setIsOpen] = useState(false);
  const isCombobox = accessibilityRole === 'combobox';
  const displayIcon = showStatusIcon && !disabled;
  const isValid = !hasError && selectedValue;
  const ref = useRef<HTMLDivElement>(null) as React.RefObject<HTMLDivElement>;

  useEffect(() => {
    if (isOpenMenu !== isOpen) {
      setIsOpen(isOpenMenu);
    }
  }, [isOpenMenu]);
  useOutsideClick({
    ref: ref,
    handler: () => {
      setIsOpen(false);
      onDisplayContent?.(false);
      if (isOpen) {
        onBlur?.();
      }
    },
  });
  useEffect(() => {
    if (selectedId) {
      const o = getOptionById(selectedId);
      setSelectedValue(o);
    }
  }, [selectedId, options]);

  return (
    <>
      {!showStatusIcon && renderDropdownField()}
      {showStatusIcon && (
        <Flex {...dropdownWithStatusIconWrapperStyle}>
          {renderDropdownField()}
          {renderStatusIcons()}
        </Flex>
      )}
    </>
  );

  function renderStatusIcons() {
    return (
      <Box>
        {displayIcon && hasError && (
          <Box {...errorIconStyle} data-testid="inputIconError">
            <Error24 />
          </Box>
        )}
        {displayIcon && isValid && (
          <Box {...errorIconStyle} data-testid="inputIconSuccess">
            <Success24 />
          </Box>
        )}
      </Box>
    );
  }

  function renderDropdownField() {
    return (
      <Menu
        gutter={1}
        isOpen={isOpen}
        {...dropdownStyles?.menuStyles}
        matchWidth={matchWidth}
        data-testid={formatDataTestId(baseDataTestId, 'Container')}
      >
        <Box
          w="full"
          pos="relative"
          {...dropdownStyles?.wrapperStyles}
          data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}
          className={className}
        >
          {props.label && (
            <Text
              data-testid={formatDataTestId(baseDataTestId, 'Label')}
              pos="absolute"
              color={getLabelColor(isOpen)}
              {...labelStyles}
              {...dropdownStyles?.labelTextStyles}
            >
              {props.label}
            </Text>
          )}
          <Box ref={ref} as="span" {...dropdownStyles?.menuButtonWrapperStyles}>
            <MenuButton
              onClick={() => {
                setIsOpen(!isOpen);
                onDisplayContent?.(!isOpen);
              }}
              ref={(el) => {
                if (el && isCombobox) {
                  el.setAttribute('role', 'combobox');
                  el.setAttribute('aria-haspopup', 'listbox');
                  const ariaLabel = props.label || props.placeholder;
                  if (ariaLabel) el.setAttribute('aria-label', ariaLabel);
                }
                if (!el || !focusableRefs) return;

                if (!focusableRefs.current.includes(el)) {
                  focusableRefs.current = [...focusableRefs.current, el];
                }
              }}
              onKeyDown={(event) => {
                if (tabbingAccessibility && focusableRefs) {
                  tabbingAccessibility(event, focusableRefs, isOpen);
                }
              }}
              _hover={{
                color: 'darkGrey2',
                ...dropdownStyles?.errorHoverMenuButtonStyles,
                ...(isOpen
                  ? {
                      border: '2px solid var(--chakra-colors-primary)',
                      borderColor: 'var(--chakra-colors-primary)',
                      borderWidth: '2px',
                    }
                  : {
                      border: '1px solid var(--chakra-colors-darkGrey1)',
                      borderColor: 'var(--chakra-colors-darkGrey1)',
                      borderWidth: '1px',
                    }),
              }}
              _focus={{
                ...(!isOpen &&
                  hasError && {
                    border: '2px solid var(--chakra-colors-error)',
                  }),
              }}
              {...dropdownStyles?.menuButtonStyles}
              _focusVisible={{
                border: `2px solid var(--chakra-colors-${hasError ? 'error' : 'primary'})`,
              }}
              disabled={props.disabled}
              type="button"
              data-testid={formatDataTestId(baseDataTestId, `${dataTestId}-menuButton`)}
            >
              <Flex
                align="center"
                whiteSpace="nowrap"
                data-testid={formatDataTestId(baseDataTestId, `${dataTestId}-insideMenuButton`)}
              >
                {!selectedValue?.icon && props.icon && (
                  <Box
                    data-testid={formatDataTestId(baseDataTestId, `${dataTestId}-menuButtonFlag`)}
                    mr="md"
                  >
                    {props.icon}
                  </Box>
                )}
                {selectedValue ? (
                  <Flex
                    w="100%"
                    textOverflow="ellipsis"
                    overflow="hidden"
                    alignItems="center"
                    data-testid={formatDataTestId(baseDataTestId, `${dataTestId}-menuButtonText`)}
                  >
                    {renderDropdownOption(selectedValue, true)}
                  </Flex>
                ) : (
                  props.placeholder && (
                    <Box
                      as="span"
                      {...placeholderStyle}
                      {...dropdownStyles?.menuButtonTextStyles}
                      data-testid={formatDataTestId(
                        baseDataTestId,
                        `${dataTestId}-menuButtonOptionsText`
                      )}
                    >
                      {props.placeholder}
                    </Box>
                  )
                )}
                {!props.skipChevron && (
                  <Icon
                    ml="auto"
                    data-testid={formatDataTestId(
                      baseDataTestId,
                      `${dataTestId}-menuButtonOptionsEndChevron`
                    )}
                    svg={isOpen ? <ChevronUp /> : <ChevronDown />}
                  />
                )}
              </Flex>
            </MenuButton>

            {CustomContent ? (
              <CustomContent
                isOpen={isOpen}
                onChange={props.onChange}
                setSelectedValue={setSelectedValue}
                setIsOpen={setIsOpen}
                options={options}
                baseDataTestId={baseDataTestId}
                dataTestId={dataTestId}
                dropdownStyles={dropdownStyles}
                focusableRefs={focusableRefs}
                tabbingAccessibility={tabbingAccessibility}
              />
            ) : (
              <MenuList
                ref={isCombobox ? (el) => el?.setAttribute('role', 'listbox') : undefined}
                css={{
                  '&::-webkit-scrollbar': {
                    width: '8px',
                  },
                  '&::-webkit-scrollbar-track': {
                    width: '8px',
                  },
                  '&::-webkit-scrollbar-thumb': {
                    background: 'var(--chakra-colors-lightGrey3)',
                    borderRadius: '24px',
                  },
                }}
                data-testid={formatDataTestId(baseDataTestId, `${dataTestId}-entireList`)}
                {...dropdownStyles?.menuListStyles}
              >
                <MenuGroup
                  data-testid={formatDataTestId(baseDataTestId, `${dataTestId}-entireListGroup`)}
                  w="full"
                >
                  {options.map((option, index) => (
                    <MenuItem
                      ref={
                        isCombobox
                          ? (el) => {
                              if (!el) return;
                              el.setAttribute('role', 'option');
                              el.setAttribute(
                                'aria-selected',
                                String(option.id) === String(selectedValue?.id) ? 'true' : 'false'
                              );
                            }
                          : undefined
                      }
                      data-testid={formatDataTestId(baseDataTestId, `${dataTestId}-${index}`)}
                      key={option.id}
                      value={option.id}
                      type="button"
                      {...dropdownStyles?.menuItemStyles}
                      onClick={() => {
                        setIsOpen(false);
                        if (option.id === selectedValue?.id) return;
                        props.onChange?.(option);
                        setSelectedValue(option);
                        onBlur?.();
                      }}
                    >
                      {renderDropdownOption(option)}
                    </MenuItem>
                  ))}
                </MenuGroup>
              </MenuList>
            )}
          </Box>
        </Box>
      </Menu>
    );
  }

  function getLabelColor(isDropdownOpen: boolean) {
    let color = labelColorMap[variant];
    if (isDropdownOpen) {
      color = labelColorMap[`${variant}Open`];
    }
    if (props.disabled) {
      color = labelColorMap[`${variant}Disabled`];
    }
    return color;
  }

  function renderDropdownOption(
    option: DropdownOption | undefined,
    buttonSelector = false
  ): ReactNode {
    const typographyStyles = buttonSelector
      ? dropdownStyles?.menuButtonTextStyles
      : dropdownStyles?.menuItemTextStyles;
    return (
      <>
        {option?.icon && <Box pr="md">{option.icon}</Box>}
        {buttonSelector ? (
          <Box
            overflow="hidden"
            textOverflow="ellipsis"
            whiteSpace="nowrap"
            data-testid={formatDataTestId(baseDataTestId, `${dataTestId}-buttonSelectorLabel`)}
          >
            <Text as="span" {...typographyStyles}>
              {option?.label}
            </Text>
          </Box>
        ) : (
          <Text as="span" {...typographyStyles}>
            {option?.label}
          </Text>
        )}
      </>
    );
  }
  function getOptionById(option: string): DropdownOption | undefined {
    return options.find((o) => String(o.id) === option);
  }
}

const labelStyles = {
  top: '-7px',
  left: 'var(--chakra-space-xmd)',
  bgColor: 'var(--chakra-colors-baseWhite)',
  fontSize: 'var(--chakra-fontSizes-sm)',
  lineHeight: 'var(--chakra-lineHeights-1)',
  pr: 'xs',
  pl: 'xs',
  m: '0',
  _hover: {
    color: 'darkGrey2',
  },
};

const labelColorMap = {
  default: 'var(chakra-colors-darkGrey1)',
  defaultOpen: 'var(--chakra-colors-primary)',
  defaultDisabled: 'var(--chakra-colors-lightGrey2)',
  error: 'var(--chakra-colors-error)',
  errorOpen: 'var(--chakra-colors-error)',
  errorDisabled: 'var(--chakra-colors-error)',
};

const placeholderStyle = {
  w: '100%',
  textOverflow: 'ellipsis',
  overflow: 'hidden',
};

const errorIconStyle = {
  ml: 'md',
  alignSelf: 'center',
};

const dropdownWithStatusIconWrapperStyle = {
  align: 'center',
  w: 'full',
};

export default memo(Dropdown);
