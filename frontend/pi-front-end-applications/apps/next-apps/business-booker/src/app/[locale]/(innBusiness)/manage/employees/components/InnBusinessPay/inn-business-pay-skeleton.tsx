import { Skeleton } from '@whitbread-eos/atoms/ui';

export function InnBusinessPaySkeleton() {
  return (
    <div data-testid="InnBusinessPayTab-Skeleton">
      <div className="mt-12">
        <div>
          <div>
            <div className="mb-8">
              <div className="flex items-center text-xl font-bold leading-6">
                <Skeleton className="h-7 w-1/3" />
                <Skeleton className="h-7 w-7 ml-2" />
              </div>
              <div className="mt-4">
                <Skeleton className="h-5 w-3/4" />
              </div>
            </div>
            <div className="mobile:w-full mobile:grid-cols-none grid grid-cols-2 gap-4">
              {[...Array(3)].map((_, i) => (
                <div
                  key={i}
                  className="p-4 cursor-pointer rounded-sm bg-white border border-lightGrey3 flex flex-col"
                >
                  <Skeleton className="h-7 w-40 mb-2" />
                  <Skeleton className="h-5 w-48 mb-2" />
                  <div className="mt-12 flex justify-between items-center w-full mobile:flex-col mobile:items-start">
                    <div className="ml-auto flex items-center gap-2">
                      <Skeleton className="h-5 w-32 mr-2" />
                      <Skeleton className="h-5 w-5" />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
          <div className="mt-12 border-t-[1px] border-lightGrey3 pt-12">
            <div className="flex items-center text-xl font-bold leading-6 mb-6">
              <Skeleton className="h-7 w-96" />
            </div>
            <div className="w-1/2 mobile:w-full mt-6 p-6 rounded-lg bg-white border border-lightGrey3 flex flex-col">
              <Skeleton className="mb-6 h-6 w-16" />
              <Skeleton className="h-7 w-80 mb-4" />
              <Skeleton className="h-5 w-72 mb-8" />
              <Skeleton className="h-14 w-full rounded-sm mt-4" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
