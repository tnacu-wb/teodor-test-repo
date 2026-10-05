import { Skeleton } from '@whitbread-eos/atoms/ui';

export function InnBusinessSkeleton() {
  return (
    <div data-testid="InnBusinessTab-Skeleton">
      <div className="mt-12">
        <div>
          <div className="mt-12 flex justify-between items-center w-full mobile:flex-col mobile:items-start">
            <div className="flex w-full">
              <div className="flex flex-col mobile:w-full mobile:justify-between mobile:flex-col">
                <div className="flex items-center text-xl font-bold">
                  <Skeleton className="h-7 w-48" />
                  <Skeleton className="h-7 w-7 ml-2" />
                </div>
                <div>
                  <Skeleton className="h-5 w-56 mt-4" />
                </div>
              </div>
              <Skeleton className="h-14 w-48 ml-auto mobile:w-14 mobile:h-14" />
            </div>
            <Skeleton className="h-14 w-48 ml-6 mobile:w-full mobile:mt-4 mobile:ml-0" />
          </div>
          <div className="mt-6 flex justify-between items-center w-full mobile:flex-col mobile:items-start">
            <div className="flex flex-col mobile:w-full mobile:justify-between mobile:flex-col">
              <div className="relative flex justify-center items-center min-w-60 tablet:min-w-40 mobile:w-full">
                <div className="relative w-full">
                  <Skeleton className="h-14 w-80 rounded-lg" />
                </div>
                <Skeleton className="absolute right-3 h-7 w-7" />
              </div>
            </div>
          </div>
          <div className="mt-6">
            <div className="relative w-full">
              <table className="text-base table-auto rounded-lg border border-separate border-spacing-0 border-lightGrey3 w-full font-normal text-4 mobile:table-fixed tablet:table-fixed">
                <thead className="bg-successTint px-4 py-2 h-10 font-normal">
                  <tr className="hover:bg-transparent">
                    <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:w-full tablet:w-full">
                      <Skeleton className="h-5 w-24" />
                    </th>
                    <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:hidden tablet:hidden">
                      <Skeleton className="h-5 w-24" />
                    </th>
                    <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:hidden tablet:hidden">
                      <Skeleton className="h-5 w-20" />
                    </th>
                    <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:w-[100px] tablet:w-[100px]"></th>
                  </tr>
                </thead>
                <tbody>
                  {[...Array(6)].map((_, i) => (
                    <tr key={i}>
                      <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3">
                        <div className="flex items-center gap-4">
                          <Skeleton className="w-10 h-10 rounded-full bg-lightGrey3 mobile:hidden" />
                          <div className="flex flex-col py-2 gap-2">
                            <Skeleton className="h-5 w-40" />
                            <Skeleton className="h-4 w-32" />
                          </div>
                        </div>
                      </td>
                      <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 mobile:hidden tablet:hidden">
                        <Skeleton className="h-5 w-24" />
                      </td>
                      <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 mobile:hidden tablet:hidden">
                        <div className="flex items-center">
                          <Skeleton className="inline-block h-3 w-3 rounded-md mr-2" />
                          <Skeleton className="h-5 w-16" />
                        </div>
                      </td>
                      <td className="px-4 mobile:truncate border-t-0 py-0 border-b border-lightGrey3 flex justify-end items-center h-[61px]">
                        <Skeleton className="h-5 w-16" />
                      </td>
                    </tr>
                  ))}
                  <tr className="hover:bg-transparent">
                    <td className="border-lightGrey2 h-[60px] px-0 border-t-0 py-0" colSpan={4}>
                      <div className="flex justify-center items-center w-full p-4 mobile:px-6 border-t border-lightGrey3">
                        <Skeleton className="w-72 h-12 rounded gap-4" />
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
