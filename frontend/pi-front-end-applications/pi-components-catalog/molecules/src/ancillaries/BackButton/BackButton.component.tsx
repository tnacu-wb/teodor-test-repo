import type { FlexProps, TextProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import { ChevronLeft24, Icon } from '@whitbread-eos/atoms';
import { formatDataTestId, useSemanticTypography } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';

interface Props {
  prefixDataTestId?: string;
}

export default function BackButton({ prefixDataTestId }: Readonly<Props>) {
  const router = useRouter();
  const { t } = useTranslation(['common']);
  const getTypographyProps = useSemanticTypography();

  return (
    <Flex
      as="button"
      type="button"
      onClick={() => {
        router.back();
      }}
      {...buttonWrapper}
      data-testid={formatDataTestId(prefixDataTestId, 'BackToHDPButton')}
    >
      <Icon svg={<ChevronLeft24 />} />
      <Text
        {...textLayoutStyle}
        {...getTypographyProps(textLegacyTypography, textSemanticTypography)}
      >
        {t('booking.summary.back')}
      </Text>
    </Flex>
  );
}

const buttonWrapper = {
  mt: 'xl',
  alignItems: 'center',
  w: 'fit-content',
  justifyContent: 'flex-start',
  cursor: 'pointer',
  background: 'none',
  border: 'none',
  padding: '0',
} as FlexProps;

const textLayoutStyle = {
  pl: 'sm',
  color: 'darkGrey1',
} as TextProps;

const textLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: 'md',
  lineHeight: '3',
} as TextProps;

const textSemanticTypography = {
  textStyle: 'label-l',
} as TextProps;
