import { Box, BoxProps, ModalBody, Text } from '@chakra-ui/react';
import { Area } from '@whitbread-eos/api';
import { LanguageOptions } from '@whitbread-eos/atoms';
import { formatNextLocaleLink } from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import { ReactNode } from 'react';

import HeaderSideNav from '../HeaderSideNav';
import NavigationItem from '../NavigationItem';
import { containerItemStyle } from '../NavigationMenuMobile.style';

interface Props {
  currentLanguage: string | undefined;
  languagesList: LanguageOptions[];
  onClickHeaderTitle: () => void;
  tickIcon: React.ReactElement;
  area: Area;
  labels: {
    language: string;
  };
}

interface LanguageItemProp {
  languagesList: LanguageOptions[];
  locale: string;
  icon?: ReactNode;
  fontWeight?: string;
  withStyleHover?: boolean;
}

export default function LanguageSelectorSideNav({
  currentLanguage,
  languagesList,
  onClickHeaderTitle,
  tickIcon,
  area = Area.PI,
  labels,
}: Readonly<Props>) {
  const router = useRouter();
  const path = router?.asPath;

  return (
    <>
      <HeaderSideNav title={labels.language} onClickTitle={onClickHeaderTitle} />
      <ModalBody p="0" data-testid="languageSelectorSideNav">
        {languagesList.map((language) => {
          const link = formatNextLocaleLink(path, language.locale, currentLanguage as string, area);

          return (
            <Box
              onClick={() => {
                window.location = link as unknown as Location;
              }}
              key={language.locale}
            >
              {renderLanguageItem({
                languagesList: languagesList ?? [],
                fontWeight: currentLanguage === language.locale ? 'medium' : '',
                locale: language.locale,
                icon: currentLanguage == language.locale && tickIcon,
              })}
            </Box>
          );
        })}
      </ModalBody>
    </>
  );
}

export function renderLanguageItem({
  languagesList,
  locale = '',
  fontWeight,
  icon,
  withStyleHover = false,
}: LanguageItemProp) {
  return (
    <Box {...customContainerItemStyle} _hover={{ color: withStyleHover && 'primary' }}>
      <NavigationItem
        title={
          <>
            {renderFlag(locale, languagesList)}
            <Box pl="sm">
              <Text fontWeight={fontWeight} color="darkGrey1" mb={0}>
                {findLanguageName(locale, languagesList)}
              </Text>
            </Box>
          </>
        }
        icon={icon}
      />
    </Box>
  );
}

function renderFlag(language: string, languagesList: LanguageOptions[]) {
  const country = languagesList.find((languageObject) => languageObject.locale == language);
  if (country) {
    return country.icon;
  }
}

function findLanguageName(language: string, languagesList: LanguageOptions[]) {
  const country = languagesList.find((languageObject) => languageObject.locale == language);
  if (country) {
    return country.languageName;
  }
}

const customContainerItemStyle = {
  ...containerItemStyle,
  ml: 0,
  w: '100%',
  pl: 'md',
  cursor: 'pointer',
} as BoxProps;
