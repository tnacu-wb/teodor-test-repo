import { BoxProps } from '@chakra-ui/react';
import { Notification, Success } from '@whitbread-eos/atoms';

interface Props {
  description: string;
  dataTestId: string;
  styles?: BoxProps;
}

export default function RoomSuccessNotification({
  description,
  dataTestId,
  styles,
}: Readonly<Props>) {
  const wrapperStyles = {
    width: 'auto',
    mt: '2xl',
    mb: 0,
    mx: 'var(--chakra-space-lg)',
    ...styles,
  };

  return (
    <Notification
      variant="success"
      status="success"
      description={description}
      prefixDataTestId={dataTestId}
      svg={<Success />}
      wrapperStyles={wrapperStyles}
    />
  );
}
