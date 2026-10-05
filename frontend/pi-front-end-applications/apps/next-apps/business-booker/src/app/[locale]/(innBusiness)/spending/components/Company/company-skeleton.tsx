import { Skeleton } from '@whitbread-eos/atoms/ui';

export default function CompanySpendingSkeleton() {
  return (
    <div data-testid="CompanySpendingSkeleton" className={containerStyle}>
      <div className="w-full md:w-[14rem] mb-[3rem]">
        <Skeleton className={'h-4 w-1/2 mb-2'} />
        <Skeleton className={'h-4 mobile:w-1/2 w-full mb-4'} />
        <Skeleton className={'h-12 w-1/2'} />
      </div>
      <Skeleton className={chartSkeletonStyle} />
      <div className="flex justify-between w-full mobile:flex-col">
        <Skeleton className={'h-[14.375rem] w-full md:w-1/3'} />
        <Skeleton className={'h-[14.375rem] w-full md:w-1/3 md:mx-[2rem] mobile:my-[2rem]'} />
        <Skeleton className={'h-[14.375rem] w-full md:w-1/3'} />
      </div>
    </div>
  );
}

const containerStyle = 'my-[3rem]';
const chartSkeletonStyle = 'h-[22.625rem] w-full mb-[3rem] mobile:hidden';
