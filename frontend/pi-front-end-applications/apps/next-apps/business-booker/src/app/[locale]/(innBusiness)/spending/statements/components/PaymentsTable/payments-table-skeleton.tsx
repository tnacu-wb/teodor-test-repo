import React from 'react';

import DataTableClientSkeleton from '~components/innBusiness/DataTable/data-table-client-skeleton';

import {
  dateLabelHeaderStyle,
  desktopOnlyStyle,
  labelHeaderStyle,
  labelStyleTextRight,
} from './payments-columns';

type Props = {
  t: (key: string) => string;
};

const PaymentsTableSkeleton: React.FC<Props> = ({ t }) => {
  const skeletonColumns = [
    {
      label: t('Date'),
      headerClassName: dateLabelHeaderStyle,
    },
    {
      id: 'paymentDescription',
      label: t('Description'),
      headerClassName: desktopOnlyStyle,
    },
    {
      label: t('Status'),
      headerClassName: labelHeaderStyle,
    },
    {
      id: 'paymentValue',
      label: t('Value'),
      headerClassName: labelStyleTextRight,
    },
  ];

  return <DataTableClientSkeleton baseTestId="PaymentsTableSkeleton" columns={skeletonColumns} />;
};

export default PaymentsTableSkeleton;
