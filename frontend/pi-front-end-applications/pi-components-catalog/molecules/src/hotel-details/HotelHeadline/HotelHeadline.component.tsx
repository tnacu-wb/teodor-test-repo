import { Text } from '@chakra-ui/react';
import { useSemanticTypography } from '@whitbread-eos/utils';

interface Props {
  headline: string;
}

export default function HotelHeadlineComponent({ headline }: Readonly<Props>) {
  const getTypographyProps = useSemanticTypography();

  if (!headline) {
    return null;
  }

  return (
    <Text
      mt="md"
      data-testid="hdp_hotelHeadline"
      {...getTypographyProps({}, headlineSemanticTypography)}
    >
      {headline}
    </Text>
  );
}

const headlineSemanticTypography = {
  textStyle: 'body-m-regular',
};
