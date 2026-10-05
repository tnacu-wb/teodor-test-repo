import { Box, BoxProps, Text } from '@chakra-ui/react';
import { formatDate, useCustomLocale } from '@whitbread-eos/utils';
import { useMemo } from 'react';

type LabelsType = {
  openingOn: string;
};
interface Props {
  hotelOpeningDate: string;
  labels: LabelsType;
  testId: string;
}

export default function HotelOpeningInformation({
  hotelOpeningDate,
  labels: { openingOn },
  testId,
}: Readonly<Props>) {
  const { language: currentLang } = useCustomLocale();

  const hotelOpeningLabel = useMemo(() => {
    return hotelOpeningDate ? formatDate(hotelOpeningDate, 'd LLLL y', currentLang) : null;
  }, [hotelOpeningDate, currentLang]);

  return (
    <>
      {hotelOpeningDate && (
        <Box {...boxStyles} data-testid={testId}>
          <Text>{openingOn}</Text>
          <Text>{hotelOpeningLabel}</Text>
        </Box>
      )}
    </>
  );
}

const boxStyles = {
  color: 'info',
  fontWeight: 'semibold',
  fontSize: 'sm',
  margin: '0 0 auto 0',
  paddingBottom: {
    sm: 'md',
  },
} as BoxProps;
