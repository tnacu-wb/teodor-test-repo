import { CurrentBalanceItem } from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';

import { ProgressBar } from '~components/innBusiness/ProgressBar';

type Props = {
  currentBalance: CurrentBalanceItem;
  baseDataTestId: string;
  isAccountSuspended: boolean;
};

export function SpendingProgress({ currentBalance, baseDataTestId, isAccountSuspended }: Props) {
  const total = currentBalance?.creditLimit?.amount ?? 0;

  const solidFillPercentage = total
    ? Math.round(((currentBalance?.outstanding?.amount ?? 0) / total) * 100)
    : 0;
  const patternFillPercentage = total
    ? Math.round(((currentBalance?.newTransactions?.amount ?? 0) / total) * 100)
    : 0;

  return (
    <ProgressBar
      solidFillPercentage={solidFillPercentage}
      patternFillPercentage={patternFillPercentage}
      dataTestId={`${baseDataTestId}-ProgressBar`}
      hasTransparency={isAccountSuspended}
    />
  );
}

export function SpendingProgressSkeleton() {
  return <Skeleton className={'h-[25px] w-full mb-4'} />;
}
