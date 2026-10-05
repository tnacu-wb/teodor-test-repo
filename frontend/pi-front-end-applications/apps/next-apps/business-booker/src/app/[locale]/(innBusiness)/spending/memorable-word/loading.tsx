import { Skeleton } from '@whitbread-eos/atoms/ui';

import { AccountSelectorSkeleton } from '../../homepage/components';
import MemorableWordSkeleton from './components/memorable-word-component/memorable-word-skeleton';

export default function Loading() {
  return (
    <div className={pageStyle}>
      <Skeleton className={h1SkeletonStyle} />
      <Skeleton className={descriptionSkeletonStyle} />
      <AccountSelectorSkeleton />
      <MemorableWordSkeleton />
    </div>
  );
}

const pageStyle =
  'px-12 pt-12 pl-[4.125rem] pr-[4.125rem] min-w-[700px] mobile:min-w-full mobile:px-4';
const h1SkeletonStyle = 'h-[2.75rem] w-[40rem] mt-[1rem] mb-[1rem]';
const descriptionSkeletonStyle = 'h-[4rem] w-[35rem] mt-[1rem] mb-[3rem]';
