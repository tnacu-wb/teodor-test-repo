import { Skeleton } from '@whitbread-eos/atoms/ui';

export default function MemorableWordSkeleton() {
  return (
    <div className={containerStyle} data-testid="MemorableWordSkeleton">
      <Skeleton className={h4SkeletonStyle} />
      <Skeleton className={listSkeletonStyle} />
      <Skeleton className={listSkeletonStyle} />
      <Skeleton className={listFourSkeletonStyle} />
      <Skeleton className={listFourSkeletonStyle} />
      <Skeleton className={inputSkeletonStyle} />
      <Skeleton className={buttonSkeletonStyle} />
    </div>
  );
}

const h4SkeletonStyle = 'h-[2.5rem] w-[20rem] mb-[1rem]';
const listSkeletonStyle = 'h-[1rem] w-[20rem] mb-[.5rem]';
const listFourSkeletonStyle = 'h-[1rem] w-[25rem] mb-[.5rem]';
const inputSkeletonStyle = 'h-[2.5rem] w-[20rem] mt-[1rem]';
const buttonSkeletonStyle = 'h-[3rem] w-[20rem] mt-[1rem] mb-[1rem]';
const containerStyle = 'flex flex-col my-[3rem]';
