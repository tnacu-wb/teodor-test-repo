'use client';

import { LOCALES } from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';

type Props = {
  title: string;
  onEditChange: (value: boolean) => void;
  showEditButton: boolean;
  isIncomplete?: boolean;
  locale?: LOCALES;
  mobile?: boolean;
};

export function CompanyContainer({
  title,
  isIncomplete = false,
  onEditChange,
  showEditButton,
}: Props) {
  const baseDataTestId = 'CompanyContainer';
  const { t } = useTranslation('company');

  return (
    <div className={actionsLeftStyle} data-testid={`${baseDataTestId}`}>
      <div className={wrapperDivStyle}>
        <span
          data-testid={`${baseDataTestId}-display-company-info-widget-title`}
          className={nameStyle}
        >
          {t(title)}
        </span>
        {isIncomplete && showEditButton && (
          <span className={badgeStyle}>{t('coMngt.incomplete')}</span>
        )}
      </div>
      {showEditButton && (
        <Button
          variant="editButton"
          size="editButton"
          data-testid={`${baseDataTestId}-edit`}
          className={buttonStyle}
          onClick={() => onEditChange(true)}
        >
          {t('coMngt.edit')}
        </Button>
      )}
    </div>
  );
}

const nameStyle =
  'whitespace-nowrap text-lg leading-[1.375rem] text-darkGrey1 font-bold flex pb-4 mobile:pb-2 items-center mt-[0.125rem] mr-2 mobile:text-[1rem]';
const actionsLeftStyle = 'flex flex-row items-center justify-between mobile:flex';
const buttonStyle = 'self-start mobile:self-start h-[1.625rem]';
const badgeStyle =
  'self-start px-2 py-1 rounded-full border border-lightGrey4 text-xs text-darkGrey2 align-middle inline-block font-bold bg-alertYellow1';
const wrapperDivStyle = 'flex mobile:flex-wrap mobile:pb-4';
