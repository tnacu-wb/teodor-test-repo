import { Flex, FlexProps, PopoverArrowProps, Text } from '@chakra-ui/react';
import { Area, CCUI_LOCALE_COOKIE } from '@whitbread-eos/api';
import { formatNextLocaleLink } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';

import { formatDataTestId } from '../../utils/formatters';
import Popover from '../Popover';

export interface Props {
  currentLanguage: string | undefined;
  prefixDataTestId?: string;
  tickIcon: React.ReactElement;
  languagesList: LanguageOptions[];
  area: Area;
}

export interface LanguageOptions {
  locale: 'en' | 'de';
  languageName: string;
  icon: React.ReactElement;
}

export default function LanguageSelector({
  currentLanguage = '',
  prefixDataTestId,
  tickIcon,
  languagesList,
  area,
}: Readonly<Props>) {
  const baseTestId = formatDataTestId(prefixDataTestId, 'languageSelectorContainer');
  const router = useRouter();
  const path = router?.asPath;
  const { t } = useTranslation(['common']);

  return (
    <Flex
      justifyContent="flex-end"
      mr={{ lg: '2.125rem ', xl: '1.875rem' }}
      data-testid={baseTestId}
    >
      <Popover
        triggerItem={renderFlag(currentLanguage)}
        styles={{ arrowStyles: popoverArrowStyles }}
      >
        <Flex {...childrenWrapperStyle} cursor="pointer">
          {languagesList.map((language, index) => {
            const link = formatNextLocaleLink(path, language.locale, currentLanguage, area);

            return (
              <Flex
                {...childrenContainerStyle(index, languagesList.length - 1)}
                key={language.locale}
                onClick={() => {
                  document.cookie = `${CCUI_LOCALE_COOKIE}=${
                    language.locale === 'de' ? 'de' : 'gb'
                  }; max-age=31536000; path=/`;
                  window.location = link as unknown as Location;
                }}
              >
                <Flex>
                  {renderFlag(language.locale)}
                  <Text
                    color="darkGrey1"
                    ml="sm"
                    fontWeight={currentLanguage === language.locale ? 'medium' : ''}
                  >
                    {t(language.languageName)}
                  </Text>
                </Flex>
                {currentLanguage === language.locale && tickIcon}
              </Flex>
            );
          })}
        </Flex>
      </Popover>
    </Flex>
  );

  function renderFlag(language: string) {
    const country = languagesList.find((languageObject) => languageObject.locale == language);
    if (country) {
      return country.icon;
    }
  }
}

const popoverArrowStyles = {
  boxShadow: '-2px 2px 1px 0 var(--chakra-colors-lightGrey4) !important',
  width: '140% !important;',
  height: '143% !important;',
  top: '-2.5px !important;',
  left: '-1px !important;',
} as PopoverArrowProps;

const childrenWrapperStyle = {
  flexDir: 'column',
  justifyContent: 'center',
  w: '11.375rem',
} as FlexProps;

const childrenContainerStyle = (index: number, listLength: number) => {
  return {
    h: 'var(--chakra-space-lg)',
    mb: index != listLength && 'var(--chakra-space-md)',
    justifyContent: 'space-between',
    _hover: {
      p: {
        textDecoration: 'underline',
      },
    },
  } as FlexProps;
};
