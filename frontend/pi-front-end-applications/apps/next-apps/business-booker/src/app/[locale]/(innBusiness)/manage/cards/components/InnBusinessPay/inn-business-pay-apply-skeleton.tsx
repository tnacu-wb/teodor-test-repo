import { Skeleton } from '@whitbread-eos/atoms/ui';

export function InnBusinessPayApplySkeleton() {
  return (
    <div data-testid="InnBusinessPayApplyTab-Skeleton">
      <div className="mt-12">
        <div className="flex flex-col gap-12">
          <div className="flex bg-lightGrey5 px-6 rounded-lg mobile:flex-col-reverse mb-12 py-8">
            <div className="flex flex-col w-[40%] mobile:w-full mobile:mt-8 gap-10 mobile:gap-6">
              <div className="flex flex-col gap-4">
                <Skeleton className="h-8 w-3/4 mb-4" />
                <Skeleton className="h-6 w-5/6" />
              </div>
              <div className="flex flex-col gap-4">
                <Skeleton className="h-14 w-full rounded-sm" />
                <Skeleton className="h-14 w-full rounded-sm" />
              </div>
            </div>
            <div className="flex justify-center items-center grow">
              <Skeleton className="w-[14rem] h-[12rem] mobile:w-[13rem] rounded-lg" />
            </div>
          </div>
          <div>
            <div className="flex gap-6 mobile:gap-4 mobile:flex-col mb-[3rem]">
              {[...Array(3)].map((_, i) => (
                <div
                  key={i}
                  className="flex flex-col rounded-lg border border-lightGrey3 px-6 pb-6 pt-4 gap-4 flex-1"
                >
                  <Skeleton className="h-12 w-12 rounded-full" />
                  <Skeleton className="h-7 w-2/3" />
                  <Skeleton className="h-5 w-5/6" />
                </div>
              ))}
            </div>
            <div className="flex flex-col max-w-[38.75rem] gap-8 mobile:w-full mobile:max-w-none">
              <div className="flex flex-col gap-2 text-darkGrey1">
                <Skeleton className="h-6 w-1/2" />
                <Skeleton className="h-5 w-4/5" />
              </div>
              <Skeleton className="h-14 max-w-[18.125rem] rounded-sm mobile:w-full mobile:max-w-none" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
