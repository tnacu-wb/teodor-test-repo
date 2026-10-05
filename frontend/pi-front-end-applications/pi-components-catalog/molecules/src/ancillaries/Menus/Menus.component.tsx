import type { FlexProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, Heading, Link, Text } from '@chakra-ui/react';
import type { Menu } from '@whitbread-eos/api';
import { Icon, Restaurant24 } from '@whitbread-eos/atoms';
import { formatDataTestId, useSemanticTypography } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface Props {
  availableMenus: Menu[];
  prefixDataTestId?: string;
}

export default function Menus({ availableMenus, prefixDataTestId }: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'Menus');
  const getTypographyProps = useSemanticTypography();
  return (
    <Box
      id="menus"
      {...menusContainerStyle}
      data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}
    >
      <Heading
        as="h5"
        {...headingLayoutStyle}
        data-testid={formatDataTestId(baseDataTestId, 'Heading-Title')}
      >
        <Text {...getTypographyProps(headingLegacyTypography, headingSemanticTypography)}>
          {t('upsell.meals.menu')}
        </Text>
      </Heading>
      <Flex {...menusWrapperStyle}>
        <Icon {...iconStyle} svg={<Restaurant24 />} />
        {availableMenus.length > 0 &&
          availableMenus.map((menu: Menu, index: number) => (
            <Link
              isExternal
              key={menu.name}
              href={menu.menuSrc}
              data-testid={formatDataTestId(baseDataTestId, 'Item')}
            >
              <Text
                as="span"
                {...linkLayoutStyles(index)}
                {...getTypographyProps(linkLegacyTypography, linkSemanticTypography)}
              >
                {menu.name}
              </Text>
            </Link>
          ))}
      </Flex>
    </Box>
  );
}

const menusContainerStyle = {
  border: '1px solid var(--chakra-colors-lightGrey3)',
  p: 'var(--chakra-space-md) var(--chakra-space-lg)',
  mt: 'xl',
};

const menusWrapperStyle = {
  pt: 'md',
  flexDirection: { mobile: 'column', sm: 'row' },
  alignItems: { mobile: 'flex-start', sm: 'center' },
  rowGap: 'sm',
} as FlexProps;

const iconStyle = {
  display: { mobile: 'none', sm: 'block' },
};

const linkLayoutStyles = (index: number) =>
  ({
    textDecoration: 'underline',
    color: 'btnSecondaryEnabled',
    ml: { mobile: 0, sm: index === 0 ? 'md' : 'lg' },
    _focus: {
      outline: 'none',
    },
  }) as TextProps;

const linkLegacyTypography = {
  fontSize: 'md',
  fontWeight: 'normal',
} as TextProps;

const linkSemanticTypography = {
  textStyle: 'link-m-regular',
} as TextProps;

const headingLayoutStyle = {
  color: 'darkGrey1',
} as TextProps;

const headingLegacyTypography = {
  fontSize: 'lg',
  lineHeight: '3',
  fontWeight: 'semibold',
} as TextProps;

const headingSemanticTypography = {
  textStyle: 'title-m-regular',
} as TextProps;
