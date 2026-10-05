import { Skeleton } from '@whitbread-eos/atoms/ui';

import { AccountSelectorSkeleton, WelcomeSkeleton } from './components';

export default function Loading() {
  return (
    <div
      className={
        'p-12 flex flex-col w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto'
      }
      data-testid={'Homepage-Skeleton'}
    >
      <Skeleton className={'w-full h-[4rem] mt-px mb-[5rem]'} />
      <WelcomeSkeleton />
      <Skeleton className={'h-6 w-full md:w-3/4 lg:w-1/3 mb-4'} />
      <AccountSelectorSkeleton />
    </div>
  );
}
