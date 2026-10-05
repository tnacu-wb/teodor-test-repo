'use client';

import { useEffect } from 'react';

import { Maintenance } from '~components/innBusiness/Maintenance';

export default function Error({ error }: { error: Error & { digest?: string } }) {
  useEffect(() => {
    console.error(error);
  }, [error]);

  return <Maintenance />;
}
