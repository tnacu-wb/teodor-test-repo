'use client';

interface CustomProgressBarProps {
  solidFillPercentage: number;
  patternFillPercentage: number;
  dataTestId: string;
  hasTransparency: boolean;
}

export function ProgressBar({
  solidFillPercentage,
  patternFillPercentage,
  dataTestId,
  hasTransparency,
}: CustomProgressBarProps) {
  return (
    <div
      className={`w-full space-y-4 ${hasTransparency ? 'opacity-50' : ''}`}
      data-testid={dataTestId}
    >
      <div className="relative h-[25px] bg-white rounded-sm border-2 border-primaryColor overflow-hidden">
        <div
          className="absolute top-0 left-0 h-full bg-primaryColor border-r-2 border-primaryColor"
          style={{ width: `${solidFillPercentage}%` }}
        />
        <div
          className="absolute top-0 h-full border-r-2 border-primaryColor"
          style={{
            left: `${solidFillPercentage}%`,
            width: `${patternFillPercentage}%`,
            backgroundImage:
              'repeating-linear-gradient(135deg, var(--primaryColor) 10px, var(--primaryColor) 12px, transparent 12px, transparent 16px)',
          }}
        />
      </div>
    </div>
  );
}
