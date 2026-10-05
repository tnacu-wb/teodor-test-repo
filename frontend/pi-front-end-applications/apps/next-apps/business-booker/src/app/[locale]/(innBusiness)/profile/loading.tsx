import { Skeleton } from '@whitbread-eos/atoms/ui';

import ChangePasswordSkeleton from './components/ChangePassword/ChangePasswordSkeleton';
import MealsAndExtrasSkeleton from './components/MealsAndExtras/MealsAndExtrasSkeleton';
import RoomPreferencesSkeleton from './components/RoomPreferences/RoomPreferencesSkeleton';
import YourProfileSkeleton from './components/YourProfile/YourProfileSkeleton';

export default function Loading() {
  return (
    <div className={pageStyle}>
      <Skeleton className={h1SkeletonStyle} />
      <YourProfileSkeleton />
      <ChangePasswordSkeleton />
      <RoomPreferencesSkeleton />
      <MealsAndExtrasSkeleton />
    </div>
  );
}

const pageStyle =
  'px-12 pt-12 pl-[4.125rem] pr-[4.125rem] min-w-[700px] mobile:min-w-full mobile:px-4';
const h1SkeletonStyle = 'h-[2.75rem] w-[20rem] mt-[4rem] mb-[4rem]';
