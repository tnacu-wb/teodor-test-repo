'use client';

import { useTranslation } from '@whitbread-eos/utils';

type SomethingWentWrongContentProps = {
  testId?: string;
  className?: string;
  handleRetry: () => void;
};

export function SomethingWentWrongContent({
  testId,
  className,
  handleRetry,
}: SomethingWentWrongContentProps) {
  const { t } = useTranslation(['payApplication']);
  return (
    <div className={`${containerStyle} ${className}`.trim()} data-testid={testId}>
      <div className={textStyle}>
        <svg
          width="20"
          height="20"
          viewBox="0 0 20 20"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          className={iconStyle}
        >
          <path
            fillRule="evenodd"
            clipRule="evenodd"
            d="M10 0C15.5228 0 20 4.47715 20 10C20 15.5228 15.5228 20 10 20C4.47715 20 0 15.5228 0 10C0 4.47715 4.47715 0 10 0ZM10 1.25C5.16751 1.25 1.25 5.16751 1.25 10C1.25 14.8325 5.16751 18.75 10 18.75C14.8325 18.75 18.75 14.8325 18.75 10C18.75 5.16751 14.8325 1.25 10 1.25ZM10 13.75C10.6904 13.75 11.25 14.3096 11.25 15C11.25 15.6904 10.6904 16.25 10 16.25C9.30964 16.25 8.75 15.6904 8.75 15C8.75 14.3096 9.30964 13.75 10 13.75ZM10 4.0625C10.4746 4.0625 10.8669 4.41519 10.9289 4.87279L10.9375 5L10.9375 11.25C10.9375 11.7678 10.5178 12.1875 10 12.1875C9.52538 12.1875 9.13314 11.8348 9.07106 11.3772L9.0625 11.25L9.0625 5C9.0625 4.48223 9.48223 4.0625 10 4.0625Z"
            fill="#58595B"
          />
        </svg>
        {t('application.sent.failed.message')}
      </div>
      <button
        className={buttonStyle}
        onClick={handleRetry}
        type="button"
        data-testid={`${testId}-retry-button`}
      >
        {t('application.sent.tryAgain')}
      </button>
    </div>
  );
}

const containerStyle = 'flex flex-col items-center justify-center text-base font-normal';
const textStyle = 'flex items-center';
const iconStyle = 'mr-2.5';
const buttonStyle = 'text-secondaryColor underline';
