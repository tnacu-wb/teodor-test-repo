import { Button, Flex, FlexProps, Heading, Text } from '@chakra-ui/react';
import { AmendRemoveRoomModalLabels } from '@whitbread-eos/api';
import { ModalVariants } from '@whitbread-eos/atoms';

interface Props {
  isOpen: boolean;
  onClose: () => void;
  labels: AmendRemoveRoomModalLabels;
  roomNumber: number;
  onRemoveRoom: (reservationId: string, roomNumber: number) => void;
  reservationId: string;
}

export default function RemoveRoomModal({
  isOpen,
  onClose,
  labels,
  roomNumber,
  onRemoveRoom,
  reservationId,
}: Readonly<Props>) {
  const { title, confirmLabel, notificationLabel, removeModalRoom, cancelModalRoom } = labels;

  return (
    <ModalVariants
      isOpen={isOpen}
      onClose={onClose}
      variant="default"
      variantProps={{ title: '', delimiter: true }}
      headerStyles={{ textAlign: 'center', justifyContent: 'flex-end' }}
      updatedWidth={{ sm: '100%', md: '37.5rem', lg: '37.5rem', xl: '39.93rem' }}
    >
      <Flex {...modalContentStyles}>
        <Heading as="h2" {...headingStyles}>{`${title} ${roomNumber}`}</Heading>
        <Flex direction="column" {...modalTextStyles}>
          <Text marginBottom="sm" fontWeight="semibold" color="darkGrey1">
            {confirmLabel}
          </Text>
          <Text fontWeight="400" color="darkGrey2">
            {notificationLabel}
          </Text>
        </Flex>
        <Flex {...modalButtonsContainerStyles}>
          <Button
            data-testid="RemoveRoomModal-removeRoom"
            size="xsm"
            variant="secondary"
            marginRight={{ sm: '1.5rem', md: '1.46rem', lg: '1.5rem' }}
            onClick={() => onRemoveRoom(reservationId, roomNumber)}
            {...modalButtonsStyles}
          >
            {removeModalRoom}
          </Button>
          <Button
            data-testid="RemoveRoomModal-cancelRemove"
            size="xsm"
            variant="tertiary"
            transition="none"
            marginTop={{ base: 'md', sm: '0' }}
            onClick={onClose}
            {...modalButtonsStyles}
          >
            {cancelModalRoom}
          </Button>
        </Flex>
      </Flex>
    </ModalVariants>
  );
}

const modalContentStyles = {
  direction: 'column',
  alignItems: 'center',
  textAlign: 'center',
  mt: 'lg',
  mb: '2xl',
  px: {
    base: '1.5rem',
    xs: '1rem',
    sm: '1.06rem',
    lg: '1.5rem',
    xl: '1.53rem',
  },
} as FlexProps;

const headingStyles = {
  fontFamily: 'header',
  fontSize: 'xl',
  fontWeight: '600',
  lineHeight: '3',
  mb: 'sm',
  color: 'darkGrey1',
};

const modalTextStyles = {
  fontSize: 'sm',
  lineHeight: '2',
  mb: { base: 'xl', lg: 'lg' },
};

const modalButtonsContainerStyles = {
  w: '100%',
  flexDirection: { base: 'column', sm: 'row' },
  justifyContent: 'center',
  alignItems: 'center',
} as FlexProps;

const modalButtonsStyles = {
  w: {
    base: 'full',
    xs: '21.43rem',
    sm: '16.18rem',
    md: '15.93rem',
    lg: '16.5rem',
    xl: '17.87rem',
  },
  h: '3.5rem',
  borderRadius: '.25rem',
  p: 'md',
};
