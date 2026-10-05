import {
  Box,
  Flex,
  type FlexProps,
  Modal,
  ModalBody,
  ModalContent,
  type ModalContentProps,
  ModalOverlay,
  Slide,
  Text,
  type TextProps,
} from '@chakra-ui/react';
import { Area, BusinessNavItems, Language } from '@whitbread-eos/api';
import { BurgerMenu, ChevronRight, Icon, LanguageOptions } from '@whitbread-eos/atoms';
import { formatDataTestId, formatTextWithSpace } from '@whitbread-eos/utils';
import { useState } from 'react';

import HeaderSideNav from '../../HeaderLeisure/NavigationMenuMobile/HeaderSideNav';
import LanguageSelectorSideNav from '../../HeaderLeisure/NavigationMenuMobile/LanguageSideNav';
import NavigationItem from '../../HeaderLeisure/NavigationMenuMobile/NavigationItem';
import MobileNavItem from './MobileNavItem';
import MobileSideNav from './MobileSideNav';

interface Props {
  navigationLabels: BusinessNavItems[];
  languageMenuLabels: LanguageOptions[];
  language: Language;
  tickIcon: React.ReactElement;
  isLogoutButton: (title: string) => boolean;
  genericLabels: {
    language: string;
    mobileMenuButton: string;
  };
}

export default function BBNavigationMenuMobile({
  navigationLabels,
  languageMenuLabels,
  language,
  tickIcon,
  isLogoutButton,
  genericLabels,
}: Readonly<Props>) {
  const baseDataTestId = 'BusinessNavMobile';
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedSideNav, setSelectedSideNav] = useState('default');
  const navTitleLabels = navigationLabels.map((navigationLabel) => navigationLabel.navTitle);
  const indexOfSelectedSideNav = navTitleLabels.indexOf(selectedSideNav);
  const toggleSideNav = (sideNav: string) => {
    setSelectedSideNav(sideNav);
  };

  return (
    <Flex flexDir="column" data-testid={baseDataTestId}>
      <Flex
        {...burgerMenuContainerStyle}
        sx={{
          '@media print': {
            marginLeft: 'auto',
          },
        }}
        data-testid={formatDataTestId(baseDataTestId, 'toggleButton')}
        onClick={() => {
          setIsModalVisible(true);
        }}
      >
        <Icon
          _hover={{
            cursor: 'pointer',
          }}
          svg={<BurgerMenu data-testid="burgerMenu" />}
        />
        <Text {...burgerMenuTextStyle}>{genericLabels.mobileMenuButton}</Text>
      </Flex>
      <Modal
        isOpen={isModalVisible}
        onClose={() => {
          setIsModalVisible(false);
        }}
        size="full"
      >
        <ModalOverlay zIndex="var(--chakra-zIndices-banner)" data-testid="modalOverlay" />
        <Slide
          direction="left"
          in={isModalVisible}
          style={{ zIndex: 'var(--chakra-zIndices-overlay)' } as any}
        >
          <ModalContent {...modalContentStyle} data-testid="modalSideBar">
            {renderSideNav(selectedSideNav)}
          </ModalContent>
        </Slide>
      </Modal>
    </Flex>
  );

  function renderSideNav(selectedSideNav: string) {
    switch (selectedSideNav) {
      case 'languageSelector': {
        const labels = {
          language: genericLabels.language,
        };
        return (
          <LanguageSelectorSideNav
            currentLanguage={language}
            languagesList={languageMenuLabels}
            onClickHeaderTitle={() => toggleSideNav('default')}
            tickIcon={tickIcon}
            area={Area.BB}
            labels={labels}
          />
        );
      }
      case navTitleLabels[indexOfSelectedSideNav]:
        return (
          <MobileSideNav
            title={navigationLabels[indexOfSelectedSideNav].navTitle}
            labels={navigationLabels[indexOfSelectedSideNav].subNav}
            onClickHeaderTitle={() => toggleSideNav('default')}
            baseTestId={formatTextWithSpace(navigationLabels[indexOfSelectedSideNav].navTitle)}
          />
        );

      default:
        return (
          <>
            <HeaderSideNav />
            <ModalBody p="0" data-testid="defaultSideNav">
              <Flex flexDir="column">
                <Box
                  data-testid={formatDataTestId(baseDataTestId, 'TriggerLanguageSideNav')}
                  onClick={() => toggleSideNav('languageSelector')}
                >
                  <Box {...customContainerItemStyle} _hover={{ color: 'primary' }}>
                    <NavigationItem
                      title={
                        <>
                          {renderFlag(language)}
                          <Box pl="sm">
                            <Text color="darkGrey1">{findLanguageName(language)}</Text>
                          </Box>
                        </>
                      }
                      icon={
                        <Box pt="0.37rem" pl="sm">
                          <Icon svg={<ChevronRight />} />
                        </Box>
                      }
                    />
                  </Box>
                </Box>
                {navTitleLabels.map((navTitleLabel) => {
                  return (
                    <MobileNavItem
                      key={navTitleLabel}
                      navTitleLabel={navTitleLabel}
                      navigationLabels={navigationLabels}
                      toggleSideNav={toggleSideNav}
                      baseDataTestId={baseDataTestId}
                      isLogoutButton={isLogoutButton}
                    />
                  );
                })}
              </Flex>
            </ModalBody>
          </>
        );
    }
  }

  function findLanguageName(language: string) {
    const country = languageMenuLabels.find((languageObject) => languageObject.locale == language);
    if (country) {
      return country.languageName;
    }
  }

  function renderFlag(language: string) {
    const country = languageMenuLabels.find((languageObject) => languageObject.locale == language);
    if (country) {
      return country.icon;
    }
  }
}

const containerItemStyle = {
  p: 'md',
  pl: 0,
  borderBottom: 'var(--chakra-space-px) solid var(--chakra-colors-lightGrey2)',
  w: { mobile: '16.87rem', xs: '20.31rem', sm: '20.75rem', md: '21.69rem' },
  ml: 'auto',
  _hover: {
    cursor: 'pointer',
  },
};

const burgerMenuContainerStyle = {
  pt: '0.5rem',
  flexDir: 'column',
  justifyContent: 'center',
  alignItems: 'center',
} as FlexProps;

const burgerMenuTextStyle = {
  pt: '0.375rem',
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'normal',
} as TextProps;

const modalContentStyle = {
  borderRadius: 0,
  position: 'absolute',
  zIndex: 'var(--chakra-zIndices-overlay)',
  left: 0,
  display: { lg: 'none' },
  w: { mobile: '72', xs: '21.44rem', sm: '21.875rem', md: '23.25rem' },
} as ModalContentProps;

const customContainerItemStyle = {
  ...containerItemStyle,
  flexDirection: 'column',
  ml: '0',
  pl: 'md',
  w: '100%',
} as FlexProps;
