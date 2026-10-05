import { analytics } from '@whitbread-eos/utils';
import { useEffect } from 'react';
import { FieldErrors } from 'react-hook-form';

import useSatelliteTrack from '~hooks/use-satellite-track';

type Props = {
  pageName?: string;
  errors?: FieldErrors<any>;
  monthlyAccountSpend?: string;
  companyHotelPolicy?: string;
  timeTrading?: string;
  businessType?: string;
  track?: string;
};

// Keep track of page names for the session
const trackedPages = new Set<string>();

export function Analytics({
  pageName,
  errors,
  monthlyAccountSpend,
  companyHotelPolicy,
  timeTrading,
  businessType,
  track,
}: Props) {
  const satelliteTrack = useSatelliteTrack();

  useEffect(() => {
    if (pageName) {
      analytics.update({
        pageName,
      });
    }
  }, [pageName]);

  useEffect(() => {
    if (track && !trackedPages.has(track)) {
      satelliteTrack(track);
      trackedPages.add(track);
    }
  }, [satelliteTrack, track]);

  useEffect(() => {
    analytics.update({
      validation: errors
        ? Object.values(errors)
            .map((error) => error?.message)
            .join(', ')
        : '',
    });
  }, [errors]);

  useEffect(() => {
    analytics.update({
      innBusiness: {
        ...(window.analyticsData.innBusiness ?? {}),
        monthlyAccountSpend: monthlyAccountSpend ?? '',
        companyHotelPolicy: companyHotelPolicy ?? '',
        timeTrading: timeTrading ?? '',
        businessType: businessType ?? '',
      },
    });
  }, [monthlyAccountSpend, companyHotelPolicy, timeTrading, businessType]);

  return null;
}
