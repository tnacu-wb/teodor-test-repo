import { HotelsSearchCriteria } from '@whitbread-eos/api';
import { UseFormSetValue } from 'react-hook-form';

import HotelDropdownModal from './HotelDropdownModal.component';

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onHotelSelected: () => void;
  paramsForQuery: HotelsSearchCriteria;
  handleSetValue?: UseFormSetValue<{
    [key: string]: any;
  }>;
}

export default function HotelDropdownModalContainer({ ...rest }: Readonly<Props>) {
  return <HotelDropdownModal {...rest} />;
}
