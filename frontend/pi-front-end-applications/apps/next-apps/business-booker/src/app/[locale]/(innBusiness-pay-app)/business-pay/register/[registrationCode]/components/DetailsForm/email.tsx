import { useTranslation } from '@whitbread-eos/utils';

import { actionsLeftStyle, fieldStyle, nameStyle, wrapperDivStyle } from './full-name';

type Props = {
  baseDataTestId: string;
  value: string;
};

export const Email = ({ baseDataTestId, value }: Props) => {
  const { t } = useTranslation(['auth']);

  return (
    <div className={fieldStyle} data-testid={`${baseDataTestId}-Email-Wrapper`}>
      <div className={actionsLeftStyle} data-testid={`${baseDataTestId}`}>
        <div className={wrapperDivStyle}>
          <span data-testid={`${baseDataTestId}-Email-Label`} className={nameStyle}>
            {t('auth.payApp.login.email')}
          </span>
        </div>
      </div>
      <span data-testid={`${baseDataTestId}-Email-Value`}>{value}</span>
    </div>
  );
};
