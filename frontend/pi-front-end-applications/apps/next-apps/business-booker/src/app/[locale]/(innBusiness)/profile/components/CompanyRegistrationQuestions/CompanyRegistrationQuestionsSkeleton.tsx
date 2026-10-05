import { Skeleton } from '@whitbread-eos/atoms/ui';

export default function CompanyRegistrationQuestionsSkeleton() {
  return (
    <div data-testid="CompanyRegistrationQuestionsSkeleton">
      <Skeleton className={titleSkeletonStyle} />
      <Skeleton className={questionSkeletonStyle} />
      <Skeleton className={questionSkeletonStyle} />
      <Skeleton className={questionSkeletonStyle} />
      <Skeleton className={buttonSkeletonStyle} />
    </div>
  );
}

const titleSkeletonStyle = 'h-[2rem] w-[16rem] mt-[1.5rem] mb-[1.5rem]';
const questionSkeletonStyle = 'h-[1.5rem] w-[14rem] mt-[1.5rem] mb-[1.5rem]';
const buttonSkeletonStyle = 'mobile:w-[21.438rem] w-[19.313rem] h-[3.5rem] mt-[1.5rem] mb-[4rem]';
