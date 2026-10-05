'use client';

import { TableRow, TableCell } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';

export function NoResultsStatements() {
  const dataTestId = 'NoResultsStatements';
  const { t } = useTranslation('spending');

  return (
    <TableRow data-testid={`${dataTestId}-container`}>
      <TableCell className={cellStyle} colSpan={6}>
        <div className={containerStyle}>
          <h2 className={titleStyle}>{t('statementsInvoicesPayments.statements.empty.title')}</h2>
          <p className={textStyle}>
            {t('statementsInvoicesPayments.statements.empty.description')}
          </p>
        </div>
      </TableCell>
    </TableRow>
  );
}

const containerStyle = 'flex flex-col items-start justify-center m-[1.5rem]';
const titleStyle = 'text-darkGrey1 font-bold text-[1.125rem] mb-[.5rem]';
const textStyle = 'text-darkGrey1';
const cellStyle = 'px-0 border-0';
