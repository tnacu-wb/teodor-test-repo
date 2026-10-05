import type { TextProps } from '@chakra-ui/react';
import { Text } from '@chakra-ui/react';
import { Button } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import type { ChooseRoomContinueBtnProps } from './ChooseRoomContinueBtn.container';

interface Props extends Omit<
  ChooseRoomContinueBtnProps,
  'selectedPMSRoomTypes' | 'handleBooking' | 'channel'
> {
  onBookReservation: () => void;
}

export default function ChooseRoomContinueBtn({
  dataTestId,
  bookRsvIsLoading,
  bookRsvIsError,
  bookRsvError,
  onBookReservation,
  isDisabledContinueBtn,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  if (bookRsvIsError) {
    return <Text>{(bookRsvError as Error).message}</Text>;
  }

  return (
    <Button
      onClick={onBookReservation}
      isDisabled={bookRsvIsLoading || isDisabledContinueBtn}
      size="full"
      variant="primary"
      data-testid={formatDataTestId(dataTestId, 'ContinueButton')}
    >
      <Text {...continueTextStyle}>{t('booking.summary.continue')}</Text>
    </Button>
  );
}

const continueTextStyle = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
} as TextProps;
