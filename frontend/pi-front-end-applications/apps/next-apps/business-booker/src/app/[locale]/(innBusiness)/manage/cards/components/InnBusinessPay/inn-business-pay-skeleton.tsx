import { Skeleton } from '@whitbread-eos/atoms/ui';

export function InnBusinessPaySkeleton() {
  return (
    <div data-testid="InnBusinessPayTab-Skeleton">
      <div className="w-full mobile:hidden mb-4">
        <div className="text-2xl font-bold flex items-center">
          <Skeleton className="h-7 w-40" />
          <Skeleton className="h-6 w-6 ml-3" />
        </div>
        <Skeleton className="mt-2 h-5 w-56 inline-block" />
      </div>
      <div className="w-full hidden mobile:block mb-4">
        <div className="text-2xl font-bold flex items-center">
          <Skeleton className="h-7 w-40" />
          <Skeleton className="h-6 w-6 ml-3" />
        </div>
        <Skeleton className="mt-2 h-5 w-56 inline-block" />
      </div>
      <div className="mt-12 mb-2 flex justify-between items-center w-full mobile:flex-col mobile:items-start">
        <div className="flex w-full">
          <div className="text-xl font-bold flex mobile:w-full mobile:justify-between mobile:items-center">
            <Skeleton className="h-6 w-32" />
            <Skeleton className="h-6 w-7 ml-2" />
          </div>
        </div>
        <Skeleton className="h-14 w-48 mr-6 mobile:mr-0 mobile:w-full mobile:mt-4" />
        <Skeleton className="h-14 w-48 mobile:w-full mobile:mt-4" />
      </div>
      <div className="flex mobile:mt-10 mobile:flex-wrap items-center gap-y-2 mb-4">
        <Skeleton className="h-5 w-16 mr-4" />
        <div className="flex">
          <Skeleton className="h-5 w-5 ml-10 mobile:ml-2 mr-2 rounded-[1px]" />
          <Skeleton className="h-5 w-24 mr-4" />
          <Skeleton className="h-5 w-5 ml-10 mobile:ml-2 mr-2 rounded-[1px]" />
          <Skeleton className="h-5 w-24" />
        </div>
      </div>
      <div className="mt-6">
        <div className="relative w-full">
          <table className="text-base table-auto rounded-lg border border-separate border-spacing-0 border-lightGrey3 w-full font-normal text-4 mobile:table-fixed tablet:table-fixed">
            <thead className="bg-successTint px-4 py-2 h-10 font-normal">
              <tr>
                <th className="font-normal text-left px-4 tablet:hidden mobile:hidden">
                  <Skeleton className="h-5 w-20" />
                </th>
                <th className="font-normal text-left px-4 tablet:w-full mobile:w-[40%] mobile:px-2">
                  <Skeleton className="h-5 w-24" />
                </th>
                <th className="font-normal text-left px-4 tablet:hidden mobile:hidden">
                  <Skeleton className="h-5 w-24" />
                </th>
                <th className="font-normal text-left px-4 w-[160px] mobile:w-[30%] mobile:px-2">
                  <Skeleton className="h-5 w-20" />
                </th>
                <th className="font-normal text-left px-4 w-[160px] mobile:w-[30%] mobile:px-2">
                  <Skeleton className="h-5 w-20" />
                </th>
              </tr>
            </thead>
            <tbody>
              {[...Array(6)].map((_, i) => (
                <tr key={i}>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 tablet:hidden mobile:hidden">
                    <Skeleton className="h-5 w-20" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 mobile:px-2">
                    <Skeleton className="h-5 w-24" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 tablet:hidden mobile:hidden">
                    <Skeleton className="h-5 w-24" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 flex items-center mobile:px-2">
                    <Skeleton className="h-6 w-10 mr-2 mobile:hidden" />
                    <Skeleton className="h-5 w-14" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 mobile:px-2">
                    <div className="flex items-center">
                      <Skeleton className="inline-block h-3 w-3 rounded-md mr-2" />
                      <Skeleton className="h-5 w-12" />
                    </div>
                  </td>
                </tr>
              ))}
              <tr>
                <td className="border-t border-lightGrey2 px-4 h-[60px]" colSpan={6}>
                  <div className="mx-auto flex w-full justify-center gap-2">
                    {[1, 2].map((_, i) => (
                      <Skeleton key={i} className="h-7 w-7 rounded-sm" />
                    ))}
                    <Skeleton className="h-7 w-7 rounded-sm ml-2" />
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
