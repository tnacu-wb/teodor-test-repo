'use client';

import { Skeleton } from '@whitbread-eos/atoms/ui';

export function EditContactPreferencesSkeleton() {
  return (
    <div className={containerStyle} data-testid="EditContactPreferencesSkeleton">
      <Skeleton className={titleSkeletonStyle} />
      <div className={descriptionContainerStyle}>
        <Skeleton className={descriptionSkeletonStyle} />
        <Skeleton className={descriptionSkeletonStyle} />
      </div>
      <div className={buttonWrapperStyle}>
        <Skeleton className={buttonSkeletonStyle} />
      </div>
    </div>
  );
}

const containerStyle = 'w-[531px] flex flex-col gap-2 mobile:w-full';
const titleSkeletonStyle = 'h-[1.75rem] w-[18rem]';
const descriptionContainerStyle = 'space-y-2';
const descriptionSkeletonStyle = 'h-[1.5rem] w-full';
const buttonWrapperStyle = 'w-[309px] mt-2';
const buttonSkeletonStyle = 'w-full h-[56px]';
