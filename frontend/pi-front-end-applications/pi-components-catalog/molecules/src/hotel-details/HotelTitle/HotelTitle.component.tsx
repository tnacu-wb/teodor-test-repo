import { Heading } from '@chakra-ui/react';
import { useSemanticTypography } from '@whitbread-eos/utils';

interface Props {
  name: string | undefined;
}

export default function HotelTitle({ name }: Readonly<Props>) {
  const getTypographyProps = useSemanticTypography();
  const titleTypographyProps = getTypographyProps(titleLegacyTypography, titleSemanticTypography);
  const isSemanticTitleTypography = 'textStyle' in titleTypographyProps;

  return (
    <Heading
      as="h1"
      data-testid="hdp_hotelTitle"
      {...(isSemanticTitleTypography ? { size: 'none' } : {})}
      {...titleTypographyProps}
    >
      {name}
    </Heading>
  );
}

const titleLegacyTypography = {
  fontFamily: 'header',
  fontWeight: 'semibold',
  lineHeight: { base: 4, sm: 5 },
  fontSize: { base: '2xl', sm: '3xxl' },
  letterSpacing: 'normal',
  textTransform: 'none',
};

const titleSemanticTypography = {
  textStyle: { base: 'heading-m', md: 'heading-xl' },
};
