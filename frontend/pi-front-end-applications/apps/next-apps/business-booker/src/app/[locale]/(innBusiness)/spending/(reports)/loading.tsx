import { Skeleton } from '@whitbread-eos/atoms/ui';

export default function ReportLoading() {
  return (
    <div className={containerStyle}>
      <div className={textStyle}>
        <Skeleton className="h-12 w-1/4 mb-6" />
        <Skeleton className="h-6 w-1/3" />
      </div>
    </div>
  );
}

const containerStyle = 'py-12 px-12 w-full mobile:py-6 mobile:px-4';
const textStyle = 'pb-12';
