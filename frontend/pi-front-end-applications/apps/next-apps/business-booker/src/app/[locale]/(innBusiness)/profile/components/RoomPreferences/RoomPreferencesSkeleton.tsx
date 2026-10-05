import { Skeleton } from '@whitbread-eos/atoms/ui';

export default function RoomPreferencesSkeleton() {
  return (
    <div data-testid="RoomPreferencesSkeleton">
      <Skeleton className={titleSkeletonStyle} />
      <Skeleton className={roomPreferencesSkeletonStyle} />
      <Skeleton className={buttonSkeletonStyle} />
    </div>
  );
}

const titleSkeletonStyle = 'h-[2rem] w-[14rem] mt-[1.5rem] mb-[1.5rem]';
const roomPreferencesSkeletonStyle = 'h-[1.5rem] w-[32rem] mt-[1.5rem] mb-[1.5rem]';
const buttonSkeletonStyle = 'mobile:w-[21.438rem] w-[19.313rem] h-[3.5rem] mt-[1.5rem] mb-[4rem]';
