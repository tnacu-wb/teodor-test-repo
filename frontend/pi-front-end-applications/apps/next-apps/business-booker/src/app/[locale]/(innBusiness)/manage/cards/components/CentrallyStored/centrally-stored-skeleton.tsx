import { Skeleton } from '@whitbread-eos/atoms/ui';

export function CentrallyStoredSkeleton() {
  return (
    <div data-testid="CentrallyStoredTab-Skeleton">
      <div className="text-base font-normal mb-2">
        <Skeleton className="h-5 w-full" />
      </div>
      <div className="mt-12 flex justify-between items-center mobile:flex-col mobile:items-start mb-2">
        <div className="text-xl font-bold flex items-center gap-2">
          <Skeleton className="h-6 w-32" />
          <Skeleton className="h-7 w-7 ml-2" />
        </div>
        <Skeleton className="h-14 w-48 mobile:w-full mobile:mt-4" />
      </div>
      <div className="mt-6">
        <div className="relative w-full">
          <table className="text-base table-auto rounded-lg border border-separate border-spacing-0 border-lightGrey3 w-full font-normal text-4 mobile:table-fixed tablet:table-fixed">
            <thead className="bg-successTint px-4 py-2 h-10 font-normal">
              <tr>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:w-full tablet:w-full">
                  <Skeleton className="h-5 w-24" />
                </th>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:hidden tablet:hidden">
                  <Skeleton className="h-5 w-24" />
                </th>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate w-[160px]">
                  <Skeleton className="h-5 w-20" />
                </th>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:hidden tablet:hidden">
                  <Skeleton className="h-5 w-16" />
                </th>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:hidden tablet:hidden">
                  <Skeleton className="h-5 w-20" />
                </th>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:w-[100px] tablet:w-[100px]">
                  <Skeleton className="h-5 w-10" />
                </th>
              </tr>
            </thead>
            <tbody>
              {[...Array(6)].map((_, i) => (
                <tr key={i}>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3">
                    <Skeleton className="h-5 w-24" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 mobile:hidden tablet:hidden">
                    <Skeleton className="h-5 w-24" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 flex items-center">
                    <Skeleton className="h-6 w-10 mr-2" />
                    <Skeleton className="h-5 w-14" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 mobile:hidden tablet:hidden">
                    <Skeleton className="h-5 w-12" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 mobile:hidden tablet:hidden">
                    <div className="flex items-center">
                      <Skeleton className="inline-block h-3 w-3 rounded-md mr-2" />
                      <Skeleton className="h-5 w-12" />
                    </div>
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3">
                    <Skeleton className="h-5 w-10" />
                  </td>
                </tr>
              ))}
              <tr>
                <td className="border-t border-lightGrey2 px-4 h-[60px]" colSpan={6}>
                  <div className="mx-auto flex w-full justify-center gap-2">
                    {[...Array(2)].map((_, i) => (
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
