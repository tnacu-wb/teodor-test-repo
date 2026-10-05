'use client';

import { TableRow, TableCell, TableBody } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';

export function CostCentresNoResults() {
  const dataTestId = 'CostCentresTable-NoResults';
  const { t } = useTranslation('cards');

  return (
    <TableBody>
      <TableRow data-testid={`${dataTestId}-container`}>
        <TableCell className={cellStyle} colSpan={3}>
          <div className={containerStyle}>
            <h2 className={titleStyle}>{t('costCentreMgmt.noCostCentres.message')}</h2>
          </div>
        </TableCell>
      </TableRow>
    </TableBody>
  );
}

const containerStyle = 'flex flex-col items-start justify-center m-[1.5rem]';
const titleStyle = 'text-darkGrey1 font-bold text-[1.125rem] mb-[.5rem]';
const cellStyle = 'px-0 border-0';
