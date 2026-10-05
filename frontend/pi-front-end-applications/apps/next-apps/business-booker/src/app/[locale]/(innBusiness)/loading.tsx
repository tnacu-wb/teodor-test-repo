import { Skeleton } from '@whitbread-eos/atoms/ui';

export default function Loading() {
  return (
    <div className={containerStyle}>
      <Skeleton className={firstSkeletonStyle} />

      <Skeleton className={secondSkeletonStyle} />
      <Skeleton className={secondSkeletonStyle} />
    </div>
  );
}

const containerStyle = 'p-12';
const firstSkeletonStyle = 'h-10 w-3/4 mb-10';
const secondSkeletonStyle = 'h-40 mb-10';
