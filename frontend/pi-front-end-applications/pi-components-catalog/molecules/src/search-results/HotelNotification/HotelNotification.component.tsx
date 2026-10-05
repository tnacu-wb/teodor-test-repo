import { Alert, Notification } from '@whitbread-eos/atoms';

interface Props {
  description: string;
  wide?: boolean;
}

export default function HotelNotification({ description, wide }: Readonly<Props>) {
  const maxW = wide
    ? { mobile: '100%', xs: '100%', sm: '100%', md: '100%', lg: '100%', xl: '100%' }
    : {
        mobile: '18rem',
        xs: '21.4375rem',
        sm: '33.75rem',
        md: '45rem',
        lg: '50.5rem',
        xl: '54rem',
      };
  return (
    <Notification
      variant="alert"
      status="warning"
      description={description}
      svg={<Alert />}
      m="auto"
      maxW={maxW}
      w="100%"
    />
  );
}
