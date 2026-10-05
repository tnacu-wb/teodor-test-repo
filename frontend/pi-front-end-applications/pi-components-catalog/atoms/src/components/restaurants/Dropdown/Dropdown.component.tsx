import {
  Menu,
  MenuButton,
  MenuButtonProps,
  MenuGroup,
  MenuItem,
  MenuList,
  Flex,
  Box,
  Text,
  useOutsideClick,
  BoxProps,
  MenuProps,
} from '@chakra-ui/react';
import { useAppData, formatDataTestId } from '@whitbread-eos/utils';
import { useEffect, ReactNode, useState, useRef, memo } from 'react';

import ChevronDown from '../../../assets/icons/ChevronDown';
import ChevronUp from '../../../assets/icons/ChevronUp';
import Icon from '../../Icon';

export type DropdownOption = {
  id: string | number;
  value?: string;
  label: string;
};

interface dropdownStyles {
  menuStyles?: MenuProps;
  menuButtonStyles?: MenuButtonProps;
  menuButtonWrapperStyles?: BoxProps;
  wrapperStyles?: BoxProps;
  menuListStyles?: BoxProps;
  errorHoverMenuButtonStyles?: BoxProps;
}

export interface DropdownProps {
  name?: string;
  isPartOfForm?: boolean;
  variant?: string;
  dropdownStyles?: dropdownStyles;
  placeholder?: string;
  label?: string;
  hasError?: boolean;
  onChange?: (option: DropdownOption | undefined) => void;
  options?: DropdownOption[];
  matchWidth?: boolean;
  selectedId?: number | string;
  onDisplayContent?: (param: boolean) => void;
  onBlur?: () => void;
  optional?: boolean;
  optionalText?: string;
}

const Dropdown = (props: DropdownProps) => {
  const {
    options = [],
    dropdownStyles,
    variant,
    isPartOfForm = true,
    matchWidth,
    hasError,
    selectedId,
    onBlur,
    onDisplayContent,
  } = props;
  const baseDataTestId = 'DropdownComp';
  const appData = useAppData();
  const [selectedValue, setSelectedValue] = useState<DropdownOption | undefined>(undefined);
  const [isOpen, setIsOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null) as React.RefObject<HTMLDivElement>;
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
  }, [selectedId, options, getOptionById]);

  return <>{renderDropdownField()}</>;

  function renderDropdownField() {
    return (
      <Menu
        variant={variant}
        gutter={1}
        isOpen={isOpen}
        matchWidth={matchWidth}
        data-testid={formatDataTestId(baseDataTestId, 'Container')}
      >
        <Box w="full" pos="relative" data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
          {props.label && (
            <Text data-testid={formatDataTestId(baseDataTestId, 'Label')} {...labelStyles}>
              {props.label}{' '}
              {props.optional && (
                <Text as="span" fontWeight="400">
                  ({props.optionalText})
                </Text>
              )}
            </Text>
          )}
          <Box ref={ref} as="span">
            <MenuButton
              onClick={() => {
                setIsOpen(!isOpen);
                onDisplayContent?.(!isOpen);
              }}
              border={
                isPartOfForm
                  ? hasError
                    ? '1px solid var(--chakra-colors-error)'
                    : '1px solid var(--chakra-colors-primary)'
                  : 'none'
              }
              borderRadius={'4px'}
              _focus={{
                ...(!isOpen &&
                  isPartOfForm && {
                    border: hasError
                      ? '2px solid var(--chakra-colors-error)'
                      : '2px solid var(--chakra-colors-primary)',
                  }),
              }}
              className={`header-menu-button-${appData?.restaurantBrandName} header-menu-button`}
              {...dropdownStyles?.menuButtonStyles}
              type="button"
              data-testid={formatDataTestId(baseDataTestId, `menuButton-${props.name}`)}
            >
              <Flex
                align="center"
                whiteSpace="nowrap"
                data-testid={formatDataTestId(baseDataTestId, 'insideMenuButton')}
              >
                {selectedValue ? (
                  <Flex
                    w="100%"
                    textOverflow="ellipsis"
                    overflow="hidden"
                    data-testid={formatDataTestId(baseDataTestId, 'menuButtonText')}
                  >
                    {renderDropdownOption(selectedValue)}
                  </Flex>
                ) : (
                  props.placeholder && (
                    <Box
                      as="span"
                      {...placeholderStyle}
                      data-testid={formatDataTestId(baseDataTestId, 'menuButtonOptionsText')}
                    >
                      {props.placeholder}
                    </Box>
                  )
                )}
                <Icon
                  ml="auto"
                  data-testid={formatDataTestId(baseDataTestId, 'menuButtonOptionsEndChevron')}
                  svg={isOpen ? <ChevronUp /> : <ChevronDown />}
                />
              </Flex>
            </MenuButton>

            <MenuList
              css={{
                '&::-webkit-scrollbar': {
                  width: '3px',
                },
                '&::-webkit-scrollbar-thumb': {
                  background: 'var(--chakra-colors-primary)',
                },
              }}
              minWidth="fit-content"
              data-testid={formatDataTestId(baseDataTestId, 'entireList')}
              {...dropdownStyles?.menuListStyles}
            >
              <MenuGroup data-testid={formatDataTestId(baseDataTestId, 'entireListGroup')} w="full">
                {options.map((option, index) => (
                  <MenuItem
                    {...menuItem}
                    data-testid={formatDataTestId(baseDataTestId, `li-${index}`)}
                    key={option.id}
                    value={option.id}
                    type="button"
                    onClick={() => {
                      const o = getOptionById(option.id);
                      setIsOpen(false);
                      if (o?.id === selectedValue?.id) return;
                      if (props.onChange) props.onChange(o);

                      setSelectedValue(o);
                      onBlur?.();
                    }}
                  >
                    {renderDropdownOption(option)}
                  </MenuItem>
                ))}
              </MenuGroup>
            </MenuList>
          </Box>
        </Box>
      </Menu>
    );
  }

  function renderDropdownOption(option: DropdownOption | undefined): ReactNode {
    return <>{option?.label}</>;
  }
  function getOptionById(value: string | number) {
    return options.find((o) => o.id === value);
  }
};

const labelStyles = {
  fontSize: 'var(--chakra-fontSizes-md)',
  color: '#333333',
  fontWeight: 'bold',
  m: '0',
  mb: 'sm',
};

const placeholderStyle = {
  w: '100%',
  textOverflow: 'ellipsis',
  overflow: 'hidden',
};

const menuItem = {
  _hover: {
    background: '#008FA8',
    color: 'white',
  },
};

export default memo(Dropdown);
