import { Box, BoxProps, Flex, Text, StyleProps, useMultiStyleConfig } from '@chakra-ui/react';
import { formatDataTestId, ternaryCondition, useSemanticTypography } from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';
import { ReactNode, useState, useEffect, useId } from 'react';

import { Info as InfoIcon, RoundedArrowRight, RoundedArrowLeft } from '../../assets/icons';
import { semanticTextStyles } from '../../theme/adapters/semanticTypography';
import { truncateLabel } from '../../utils/truncateLabel';
import Button from '../Button';
import Icon from '../Icon';

const Tooltip = dynamic(
  async () => {
    const { default: Tooltip } = await import('../Tooltip/Tooltip.component');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

export interface TabsOptionsItem {
  index?: number;
  label?: string;
  description?: string;
  images?: ReactNode;
  content?: ReactNode;
  roomTypeInventoryCount?: number | null;
  roomTypeInventoryRoomTypesWithCount?: string | null;
}

interface Props extends Omit<BoxProps, 'onChange'> {
  options: TabsOptionsItem[] | undefined;
  orientation?: 'horizontal' | 'vertical';
  variant?: 'sm' | 'tabsGroup' | 'greyTabsGroup';
  prefixDataTestId?: string;
  singleContent?: ReactNode;
  shortMobileLabels?: boolean;
  showRoomInventory?: boolean;
  styles?: { tab: StyleProps; tabList: StyleProps; tabPanel?: StyleProps };
  labelStyles?: { selected?: StyleProps; unselected?: StyleProps };
  setStartingTab?: (index: number) => void;
  startingTab?: number;
  tabScrollSize?: number;
  isScrollable?: boolean;
  isMobileView?: boolean;
  hasRoomLabels?: boolean;
  index?: number;
  defaultIndex?: number;
  onChange?: (index: number) => void;
}

const ROOM_COUNT_OVERFLOW = 7;
const LABEL_LENGTH_OVERFLOW = 7;

export default function Tabs({
  prefixDataTestId,
  singleContent,
  shortMobileLabels = false,
  showRoomInventory = false,
  styles,
  labelStyles,
  isScrollable,
  isMobileView,
  hasRoomLabels,
  options: optionsProp,
  orientation,
  variant,
  setStartingTab,
  startingTab,
  tabScrollSize: tabScrollSizeProp,
  index: indexProp,
  defaultIndex,
  onChange,
  ...boxProps
}: Readonly<Props>) {
  const options = optionsProp ?? [];
  const tabsId = useId();
  const getTypographyProps = useSemanticTypography();

  const [selectedIndex, setSelectedIndex] = useState(indexProp ?? defaultIndex ?? startingTab ?? 0);

  // Sync with external index changes
  useEffect(() => {
    if (indexProp !== undefined) {
      setSelectedIndex(indexProp);
    }
  }, [indexProp]);

  // Sync with external startingTab changes
  useEffect(() => {
    if (startingTab !== undefined) {
      setSelectedIndex(startingTab);
    }
  }, [startingTab]);

  const tabStyles = useMultiStyleConfig('Tabs', { variant, orientation });

  const handleTabClick = (newIndex: number) => {
    setSelectedIndex(newIndex);
    onChange?.(newIndex);
    setStartingTab?.(newIndex);
  };

  const currentTab = startingTab ?? selectedIndex;
  const tabScrollSize = tabScrollSizeProp ?? 0;
  const isDisabledLeftArrow = currentTab <= 0;
  const isDisabledRightArrow = currentTab + tabScrollSize >= options.length;

  return (
    <Box
      {...boxProps}
      data-testid={formatDataTestId(prefixDataTestId, 'TabsComponent')}
      sx={{
        ...(isScrollable ? scrollableTabsStyles : {}),
        ...((boxProps.sx as Record<string, unknown>) ?? {}),
      }}
      __css={tabStyles.root}
    >
      <Box role="tablist" display="flex" __css={tabStyles.tablist} {...styles?.tabList}>
        {isScrollable && (
          <Button
            data-testid={formatDataTestId('TabScrollLeft', 'Button')}
            className="tabs-scroll-arrow tabs-scroll-arrow--left"
            variant="tertiary"
            isDisabled={isDisabledLeftArrow}
            onClick={() => {
              if (currentTab - tabScrollSize < 0) {
                handleTabClick(currentTab - 1);
              } else {
                handleTabClick(currentTab - tabScrollSize);
              }
            }}
          >
            <Icon
              svg={
                <RoundedArrowLeft
                  color={isDisabledLeftArrow ? 'var(--chakra-colors-lightGrey2)' : ''}
                />
              }
            />
          </Button>
        )}
        {options.map((option: TabsOptionsItem, i: number) => {
          const idx = option.index ?? i;
          const isSelected = selectedIndex === idx;
          return (
            <Box
              as="button"
              type="button"
              role="tab"
              key={idx}
              id={`${tabsId}-tab-${idx}`}
              aria-selected={isSelected}
              aria-controls={`${tabsId}-panel-${idx}`}
              tabIndex={isSelected ? 0 : -1}
              px={1}
              data-testid={formatDataTestId(option.label, 'TabButton')}
              onClick={() => handleTabClick(idx)}
              __css={tabStyles.tab}
              {...styles?.tab}
              display={
                !isScrollable || currentTab === idx || currentTab + 1 === idx ? 'flex' : 'none'
              }
            >
              <Flex
                flexDir="column"
                width="100%"
                data-testid={formatDataTestId(option.label, 'TabButtonColumn')}
              >
                {/* showRoomInventory: for CCUI - which is desktop only */}
                {ternaryCondition(
                  showRoomInventory,
                  renderRoomInventoryLabel(
                    option.label,
                    option.roomTypeInventoryCount,
                    option.roomTypeInventoryRoomTypesWithCount
                  ),
                  renderLabel(
                    options.length > ROOM_COUNT_OVERFLOW,
                    shortMobileLabels,
                    option.index ?? 0,
                    option.label,
                    isSelected
                  )
                )}

                {!!option.description &&
                  renderDescription(
                    option.description,
                    options.length > ROOM_COUNT_OVERFLOW,
                    shortMobileLabels
                  )}
                {!!option.images && option.images}
              </Flex>
            </Box>
          );
        })}
        {isScrollable && (
          <Button
            data-testid={formatDataTestId('TabScrollRight', 'Button')}
            className="tabs-scroll-arrow tabs-scroll-arrow--right"
            variant="tertiary"
            onClick={() => {
              if (currentTab + tabScrollSize + 1 >= options.length) {
                handleTabClick(currentTab + 1);
              } else {
                handleTabClick(currentTab + tabScrollSize);
              }
            }}
            isDisabled={isDisabledRightArrow}
          >
            <Icon
              svg={
                <RoundedArrowRight
                  color={isDisabledRightArrow ? 'var(--chakra-colors-lightGrey2)' : ''}
                />
              }
            />
          </Button>
        )}
      </Box>
      {singleContent ? (
        singleContent
      ) : (
        <Box data-testid={formatDataTestId(prefixDataTestId, 'TabPanels')}>
          {optionsProp?.map((option, i) => {
            const idx = option.index ?? i;
            if (selectedIndex !== idx) return null;
            return (
              <Box
                role="tabpanel"
                key={idx}
                id={`${tabsId}-panel-${idx}`}
                aria-labelledby={`${tabsId}-tab-${idx}`}
                __css={tabStyles.tabpanel}
                {...styles?.tabPanel}
              >
                {option.content}
              </Box>
            );
          })}
        </Box>
      )}
    </Box>
  );

  function renderRoomInventoryLabel(
    label?: string,
    roomTypeInventoryCount?: number | null,
    roomTypeInventoryRoomTypesWithCount?: string | null
  ) {
    if (roomTypeInventoryCount && roomTypeInventoryRoomTypesWithCount) {
      return (
        <Tooltip
          description={roomTypeInventoryRoomTypesWithCount}
          variant={'infoGrey'}
          svg={<InfoIcon />}
          placement="bottom-start"
        >
          <Text as="h4" data-testid={formatDataTestId(label, 'TabButtonWithToolTipLabel')}>
            {label} ({roomTypeInventoryCount})
          </Text>
        </Tooltip>
      );
    } else {
      return (
        <Text as="h4" data-testid={formatDataTestId(label, 'TabButtonLabel')}>
          {label} ({roomTypeInventoryCount})
        </Text>
      );
    }
  }

  function formatLabel(label: string, index: number) {
    if (hasRoomLabels) {
      if (isMobileView && !isScrollable) {
        return index + 1;
      } else {
        // show full label only for rooms tabs
        return label;
      }
    }
    // show ellipsis if label is too long
    return truncateLabel(label, LABEL_LENGTH_OVERFLOW);
  }

  function renderLabel(
    overflow: boolean,
    shortMobileLabels: boolean,
    index: number,
    label = '',
    isSelected = false
  ) {
    const tabsLabelLegacyTypography = hasRoomLabels ? { fontSize: '17px' } : {};
    const tabsLabelSemanticTypography = isSelected
      ? (labelStyles?.selected ?? {})
      : (labelStyles?.unselected ?? {});
    const tabsLabelTypographyProps = getTypographyProps(
      tabsLabelLegacyTypography,
      tabsLabelSemanticTypography
    );

    if (shortMobileLabels && overflow) {
      return (
        <Tooltip description={label} variant={'infoGrey'} svg={<InfoIcon />}>
          <Text
            as="h3"
            data-testid={formatDataTestId(label, 'TabButtonLabel')}
            style={{ fontSize: '17px' }}
            {...tabsLabelTypographyProps}
          >
            {formatLabel(label, index)}
          </Text>
        </Tooltip>
      );
    } else {
      return (
        <Text
          as="h3"
          data-testid={formatDataTestId(label, 'TabButtonLabel')}
          {...tabsLabelTypographyProps}
        >
          {label}
        </Text>
      );
    }
  }

  function renderDescription(description: string, overflow: boolean, shortMobileLabels: boolean) {
    const tabDescriptionTypographyProps = getTypographyProps({}, tabDescriptionSemanticTypography);
    const isSemanticDescription = 'textStyle' in tabDescriptionTypographyProps;
    // the greyTabsGroup theme's nested `h5` selector outranks a textStyle class, so force via native style
    const tabDescriptionSemanticStyle = isSemanticDescription
      ? semanticTextStyles[tabDescriptionSemanticTypography.textStyle]
      : undefined;

    if (shortMobileLabels && overflow) {
      return (
        <Tooltip description={description} variant={'infoGrey'} svg={<InfoIcon />}>
          <Text
            as="h5"
            data-testid={formatDataTestId(description, 'TabButtonDescription')}
            style={tabDescriptionSemanticStyle}
          >
            {/* subtract the length of the ellipsis */}
            {description.substring(0, LABEL_LENGTH_OVERFLOW - 3) + '...'}
          </Text>
        </Tooltip>
      );
    } else {
      return (
        <Text
          as="h5"
          data-testid={formatDataTestId(description, 'TabButtonDescription')}
          style={tabDescriptionSemanticStyle}
        >
          {description}
        </Text>
      );
    }
  }
}

const tabDescriptionSemanticTypography = {
  textStyle: 'body-s-regular',
};

const scrollableTabsStyles = {
  '.tabs-scroll-arrow': {
    width: '35px',
    '&:disabled': {
      backgroundColor: 'lightGrey5',
    },
    '&:hover:disabled': {
      backgroundColor: 'lightGrey5',
    },
  },
};
