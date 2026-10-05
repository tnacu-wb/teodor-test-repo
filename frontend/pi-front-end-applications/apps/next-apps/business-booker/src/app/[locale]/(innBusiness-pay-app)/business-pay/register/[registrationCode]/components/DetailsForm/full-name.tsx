import { useTranslation } from '@whitbread-eos/utils';

type Props = {
  baseDataTestId: string;
  value: string;
};

export const FullName = ({ baseDataTestId, value }: Props) => {
  const { t } = useTranslation(['auth']);
  return (
    <div className={fieldStyle} data-testid={`${baseDataTestId}-FullName-Wrapper`}>
      <div className={actionsLeftStyle} data-testid={`${baseDataTestId}`}>
        <div className={wrapperDivStyle}>
          <span data-testid={`${baseDataTestId}-FullName-Label`} className={nameStyle}>
            {t('auth.payApp.login.fullName')}
          </span>
        </div>
      </div>
      <span data-testid={`${baseDataTestId}-FullName-Value`}>{value}</span>
    </div>
  );
};

export const fieldStyle =
  'flex flex-col gap-2 py-6 first:pt-0 last:pb-0 border-b last:border-0 border-lightGrey3';
export const nameStyle =
  'whitespace-nowrap text-lg leading-[1.375rem] text-darkGrey1 font-bold flex pb-4 mobile:pb-2 items-center mt-[0.125rem] mr-2';
export const actionsLeftStyle = 'flex flex-row items-center justify-between mobile:flex';
export const buttonStyle = 'self-start mobile:self-start h-[1.625rem]';
export const wrapperDivStyle = 'flex mobile:flex-wrap mobile:pb-4';
