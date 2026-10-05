import {
  Box,
  Stack,
  Text,
  type StackProps,
  Flex,
  List,
  Link,
  ListItem,
  StyleProps,
  TextProps,
  BoxProps,
  ButtonProps,
  AccordionProps,
  AccordionPanelProps,
  AccordionItemProps,
} from '@chakra-ui/react';
import {
  Area,
  BusinessNavItems,
  Language,
  SubNavCategory,
  SubNavCategoryItem,
  SubNavItemLinks,
  BB_MENU_IDS,
} from '@whitbread-eos/api';
import {
  LanguageOptions,
  LanguageSelectorSwitcher,
  Popover,
  Accordion,
} from '@whitbread-eos/atoms';
import { formatDataTestId, logout } from '@whitbread-eos/utils';
import NextLink from 'next/link';
import { ReactNode } from 'react';

import NavItem from '../../HeaderLeisure/NavItem';
import {
  boxStyles,
  linkStyles,
  listItemStyles,
  listStyles,
} from '../../HeaderLeisure/NavigationMenu/NavigationMenu.style';

export interface Props {
  navigationLabels: BusinessNavItems[];
  language: Language;
  languageMenuLabels: LanguageOptions[];
  tickIcon: React.ReactElement;
  dataTestId: string;
  isLogoutButton: (title: string) => boolean;
}

export default function BBNavigationMenu({
  navigationLabels,
  language,
  languageMenuLabels,
  tickIcon,
  dataTestId,
  isLogoutButton,
}: Readonly<Props>) {
  const navStyle = language === 'en' ? stackStyle : stackStyleDE;

  return (
    <Stack as="nav" {...navStyle} data-testid={formatDataTestId(dataTestId, 'NavigationLinks')}>
      <Box data-testid={formatDataTestId(dataTestId, 'LanguageSelectorWrapper')}>
        <LanguageSelectorSwitcher
          currentLanguage={language as any}
          tickIcon={tickIcon}
          languagesList={languageMenuLabels}
          area={Area.BB}
        />
      </Box>
      {navigationLabels.map((navigationLabel, index) => {
        return (
          <Box
            key={index}
            {...boxStyles}
            data-testid={formatDataTestId(dataTestId, navigationLabel.navTitle)}
          >
            <Popover
              triggerItem={
                <Box className={navigationLabel.id === BB_MENU_IDS.COMPANY ? 'assist-no-show' : ''}>
                  <NavItem title={navigationLabel.navTitle} />
                </Box>
              }
              {...{ ...noBoxShadowStyle }}
            >
              {navigationLabel.subNav && (
                <Flex w="17rem">{renderSubNavItems(navigationLabel.subNav)}</Flex>
              )}
            </Popover>
          </Box>
        );
      })}
    </Stack>
  );

  function renderListItem(
    isLogout: boolean,
    option: SubNavCategoryItem | SubNavItemLinks,
    key: number,
    testId: string
  ) {
    if (isLogout) {
      return (
        <ListItem
          {...listItemStyles}
          key={key}
          data-testid={formatDataTestId(dataTestId, `${testId}-${option.title}`)}
        >
          <Link onClick={logout}>
            <Text {...linkStyles}>{option.title}</Text>
          </Link>
        </ListItem>
      );
    } else {
      return (
        <ListItem
          {...listItemStyles}
          key={key}
          data-testid={formatDataTestId(dataTestId, `${testId}-${option.title}`)}
        >
          <NextLink href={option.url ?? '/'} passHref legacyBehavior>
            <Text {...linkStyles}>{option.title}</Text>
          </NextLink>
        </ListItem>
      );
    }
  }

  function renderSubNavItemMenu(list: SubNavItemLinks[]) {
    return list.map((option: SubNavItemLinks, index: number) => {
      return renderListItem(false, option, index, 'subNavListItem');
    });
  }

  function renderSubNavItems(list: SubNavCategory[]) {
    return list.map((item) => {
      return (
        <List key={item.title} {...listStyles}>
          {item.navOptions.map((option: SubNavCategoryItem, index: number) => {
            return !option.subMenuLinks ? (
              renderListItem(isLogoutButton(option.title), option, index, 'listItem')
            ) : (
              <Accordion
                data-testid={formatDataTestId(dataTestId, `listItemWithMenu-${option.title}`)}
                accordionItems={formatAccordionItemProps(
                  option.title,
                  renderSubNavItemMenu(option.subMenuLinks)
                )}
                bgColor="baseWhite"
                accordionOverwriteStyles={accordionOverwriteStyles}
              />
            );
          })}
        </List>
      );
    });
  }

  function formatAccordionItemProps(title: string, content: ReactNode) {
    return [
      {
        title,
        content,
      },
    ];
  }
}

const accordionItemButtonStyle = {
  whiteSpace: 'nowrap',
  fontWeight: 'normal',
  padding: 0,
} as BoxProps & ButtonProps;

const accordionItemTextStyle = {
  fontWeight: 'normal',
  mb: '2',
  mt: '1',
  lineHeight: '1',
  _hover: {
    textDecoration: 'underline',
  },
} as TextProps;

const accordionIconStyle = {
  position: 'absolute',
  right: '0.75rem',
  height: '0.5em',
  paddingRight: '0px',
  paddingLeft: '0px',
  fontSize: '3rem',
} as AccordionProps;

const accordionItemPanelStyle = {
  pl: 'lg',
  _last: {
    paddingBottom: '0px',
  },
} as AccordionPanelProps;

const accordionItemStyle = {
  borderTopWidth: '0px',
  _last: {
    borderBottomWidth: '0px',
  },
} as AccordionItemProps;

const accordionOverwriteStyles = {
  button: accordionItemButtonStyle,
  text: accordionItemTextStyle,
  panel: accordionItemPanelStyle,
  item: accordionItemStyle,
  icon: accordionIconStyle,
};

const stackStyle = {
  direction: 'row',
  spacing: { lg: 'xl', xl: '5xl' },
  justifyContent: 'flex-end',
} as StackProps;

const stackStyleDE = {
  flexDirection: 'row',
  justifyContent: 'flex-end',
  alignItems: 'center',
  gap: 8,
} as StackProps;

const noBoxShadowStyle = {
  _focus: {
    boxShadow: 'none',
  },
} as StyleProps;
