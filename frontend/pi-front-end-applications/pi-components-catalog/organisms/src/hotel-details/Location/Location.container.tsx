import { Channel } from '@whitbread-eos/api';
import { useStaticHotelInformation } from '@whitbread-eos/utils';

import LocationComponent from './Location.component';
import LocationWithDynamicMap from './LocationWithDynamicMap.component';

interface Props {
  channel?: Channel;
}

export function isDynamicMap(channel?: Channel) {
  return channel === Channel.Ccui;
}

export default function LocationContainer({ channel }: Readonly<Props>) {
  const { brand, coordinates, isLoading, isError, error } = useStaticHotelInformation();
  const isDynamicMapEnabled = isDynamicMap(channel);

  const commonProps = {
    isLoading,
    isError,
    error,
    data: { coordinates, brand },
  };

  if (isDynamicMapEnabled) {
    return <LocationWithDynamicMap {...commonProps} />;
  }

  return <LocationComponent {...commonProps} />;
}
