import { Text, Box } from '@chakra-ui/react';
import { Timer } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';

interface Props {
  numberOfRoomsAvailable?: number;
}

export default function RoomsRemainingBadgeComponent({ numberOfRoomsAvailable }: Readonly<Props>) {
  const { t } = useTranslation();

  if (
    numberOfRoomsAvailable === undefined ||
    numberOfRoomsAvailable < 1 ||
    numberOfRoomsAvailable > 9
  ) {
    return null;
  }
  const roomsRemainingTranslation = t('hoteldetails.rateSelector.roomsRemainingMessage', {
    count: numberOfRoomsAvailable,
  });

  return (
    <Box {...urgencyMessage} data-testid="urgencyMessage">
      <Box {...roomsRemainigPill} data-testid="roomsRemaingPill">
        <Box {...imageStyle} data-testid="timerImage">
          <Timer />
        </Box>
        <Text {...urgentMessagingText}>{roomsRemainingTranslation}</Text>
      </Box>
    </Box>
  );
}

const urgencyMessage = {
  w: { base: '100%', md: 'fit-content' },
  h: 'fit-content',
  mb: { base: '5px', md: '20px' },
};

const roomsRemainigPill = {
  py: '5px',
  px: '12px',
  bg: '#00798e',
  color: 'white',
  borderRadius: '10px',
  display: 'flex',
  gap: '10px',
  justifyContent: { base: 'flex-start', md: 'center' },
};

const urgentMessagingText = {
  fontSize: '14px',
  fontWeight: '500',
};

const imageStyle = {
  mt: '2px',
};
