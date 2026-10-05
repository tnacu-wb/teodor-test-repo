'use client';

import { analytics } from '@whitbread-eos/utils';
import React, { useEffect } from 'react';

type Props = {
  pageName?: string;
  checkBox?: boolean;
  validation?: string[];
};

const Analytics: React.FC<Props> = ({ pageName, checkBox, validation }: Props) => {
  useEffect(() => {
    const currentData = window?.analyticsData?.innBusiness ?? {};
    analytics.update({
      innBusiness: {
        ...currentData,
        checkBox: checkBox,
      },
    });
  }, [checkBox]);

  useEffect(() => {
    if (validation === undefined) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    analytics.update({
      ...currentData,
      validation: validation.join(','),
    });
  }, [validation]);

  useEffect(() => {
    if (pageName === undefined) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const currentValidation = window?.analyticsData?.validation;
    analytics.update({
      ...currentData,
      pageName: pageName,
    });

    if (currentValidation) {
      analytics.remove(['validation']);
    }
  }, [pageName]);

  return <></>;
};

export default Analytics;
