import { Box, FlexProps, Link, Text } from '@chakra-ui/react';
import { BB_MENU_IDS, type BusinessNavItems } from '@whitbread-eos/api';
import { ChevronRight, Icon } from '@whitbread-eos/atoms';
import { formatDataTestId, formatTextWithSpace, logout } from '@whitbread-eos/utils';
import NextLink from 'next/link';

import NavigationItem from '../../../HeaderLeisure/NavigationMenuMobile/NavigationItem';
import { containerItemStyle } from '../../../HeaderLeisure/NavigationMenuMobile/NavigationMenuMobile.style';

interface Props {
  navigationLabels: BusinessNavItems[];
  navTitleLabel: string;
  baseDataTestId: string;
  toggleSideNav: (sideNav: string) => void;
  isLogoutButton: (title: string) => boolean;
}

export default function MobileNavitem({
  navTitleLabel,
  navigationLabels,
  baseDataTestId,
  toggleSideNav,
  isLogoutButton,
}: Readonly<Props>) {
  const findLabel = (title: string, labels: BusinessNavItems[]) => {
    return labels.find((item: BusinessNavItems) => item.navTitle === title);
  };
  const isCompanyLabel = (title: string, labels: BusinessNavItems[]) => {
    const labelItem = findLabel(title, labels);
    if (labelItem) {
      return labelItem.id === BB_MENU_IDS.COMPANY || labelItem.id === BB_MENU_IDS.COMPANY_NAME;
    }
  };
  const hasSubNav = (title: string, labels: BusinessNavItems[]) => {
    const labelItem = findLabel(title, labels);
    if (labelItem?.subNav?.length !== 0) {
      return labelItem?.subNav && labelItem.subNav[0].navOptions?.length !== 0;
    }
  };

  let clickAction;
  let testId;
  let content;
  if (isLogoutButton(navTitleLabel)) {
    clickAction = logout;
    testId = 'MobileLogoutButton';
    content = (
      <Link style={{ textDecoration: 'none' }}>
        <Text>{navTitleLabel}</Text>
      </Link>
    );
  } else if (hasSubNav(navTitleLabel, navigationLabels)) {
    clickAction = () => toggleSideNav(navTitleLabel);
    testId = 'TriggerSideNav';
    content = (
      <NavigationItem
        title={navTitleLabel}
        icon={
          <Box pt="0.37rem" pl="sm">
            <Icon svg={<ChevronRight />} />
          </Box>
        }
      />
    );
  } else {
    testId = 'MobileLink';
    content = (
      <NextLink
        href={findLabel(navTitleLabel, navigationLabels)?.url ?? '/'}
        passHref
        legacyBehavior
      >
        <Text>{navTitleLabel}</Text>
      </NextLink>
    );
  }

  return (
    <Box
      onClick={clickAction}
      {...customContainerItemStyle}
      {...(isCompanyLabel(navTitleLabel, navigationLabels) && customCompanyStyle)}
      data-testid={formatDataTestId(
        baseDataTestId,
        formatTextWithSpace(`${navTitleLabel}-${testId}`)
      )}
      key={`navItem-${navTitleLabel}`}
    >
      {content}
    </Box>
  );
}

const customCompanyStyle = {
  bgColor: '#F8F8F8',
};

const customContainerItemStyle = {
  ...containerItemStyle,
  flexDirection: 'column',
  ml: '0',
  pl: 'md',
  w: '100%',
} as FlexProps;
