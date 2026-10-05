export function InnBusinessPaySectionSkeleton() {
  return (
    <div
      className="px-12 pt-12 mobile:min-w-full mobile:px-4 mt-10 mb-8"
      data-testid="inn-business-pay-skeleton"
    >
      <div className="h-7 w-48 bg-lightGrey4 rounded mb-6 animate-pulse" />

      <div
        className="mb-6 w-[620px] mobile:w-auto bg-lightGrey4 h-16 rounded animate-pulse"
        data-testid="notification-skeleton"
      />

      {[1, 2].map((index) => (
        <div key={index} className="mb-6" data-testid="card-skeleton">
          <div className="w-full max-w-[620px] bg-white border border-lightGrey4 rounded-lg p-6">
            <div className="flex flex-col gap-6">
              <div className="flex flex-col gap-4">
                <div className="flex justify-between items-center">
                  <div className="h-7 w-48 bg-lightGrey4 rounded animate-pulse" />
                  <div className="h-5 w-24 bg-lightGrey4 rounded animate-pulse" />
                </div>
                <div className="h-5 w-32 bg-lightGrey4 rounded animate-pulse -mt-2" />
                <div className="flex flex-wrap gap-2">
                  <div className="h-7 w-24 bg-lightGrey4 rounded-full animate-pulse" />
                  <div className="h-7 w-32 bg-lightGrey4 rounded-full animate-pulse" />
                </div>
              </div>

              {[1, 2, 3].map((prefIndex) => (
                <div
                  key={prefIndex}
                  className="flex flex-col gap-1"
                  data-testid="preference-skeleton"
                >
                  <div className="h-6 w-64 bg-lightGrey4 rounded animate-pulse" />
                  <div className="h-5 w-48 bg-lightGrey4 rounded animate-pulse" />
                </div>
              ))}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
