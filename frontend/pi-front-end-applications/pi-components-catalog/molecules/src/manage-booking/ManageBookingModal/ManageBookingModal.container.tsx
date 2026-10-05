import { GET_STATIC_CONTENT, SITE_LEISURE } from '@whitbread-eos/api';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';

import ManageBookingModal from './ManageBookingModal.component';

export interface BookingModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function ManageBookingModalContainer({
  onClose,
  isOpen,
}: Readonly<BookingModalProps>) {
  const { language, country } = useCustomLocale();
  const { data: labels } = useQueryRequest(
    ['GetStaticContent', language, country],
    GET_STATIC_CONTENT,
    {
      country,
      language,
      site: SITE_LEISURE,
      businessBooker: false,
    }
  );

  return (
    <ManageBookingModal
      labels={labels}
      onClose={onClose}
      isOpen={isOpen}
      baseTestId="ManageBookingModal"
    />
  );
}
