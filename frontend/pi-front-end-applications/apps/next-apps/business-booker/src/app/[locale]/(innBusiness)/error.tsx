'use client';

import { useEffect } from 'react';

import { SomethingWentWrong } from '~components/innBusiness/SomethingWentWrong';

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  useEffect(() => {
    console.error(error);
  }, [error]);

  return (
    <SomethingWentWrong className={containerStyle} onRetry={reset} testId="InnBusinessError" />
  );
}

const containerStyle = 'p-12 pt-36';
