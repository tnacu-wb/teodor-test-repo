'use client';

import { ManagementInformationQuestion, BusinessQuestionType } from '@whitbread-eos/api';
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
  ExpandableTableRow,
} from '@whitbread-eos/atoms/ui';
import { useTranslation, cn, formatIBAssetsUrl } from '@whitbread-eos/utils';
import { useState } from 'react';

import { BusinessEmployeeQuestions } from './BusinessEmployeeQuestions';

type Props = {
  purchaseOrderManagement: ManagementInformationQuestion;
  customerReferenceManagement: ManagementInformationQuestion;
  companyId: string;
  icons: Record<string, string>;
  navigationGuardOwnerId?: string;
  onQuestionDirtyChange?: (questionId: string, isDirty: boolean) => void;
};

export function BusinessQuestions({
  purchaseOrderManagement,
  customerReferenceManagement,
  companyId,
  icons,
  navigationGuardOwnerId,
  onQuestionDirtyChange,
}: Props) {
  const baseDataTestId = 'BusinessQuestions';
  const { t } = useTranslation('company');

  const statusColumn = (active: boolean) => {
    return (
      <>
        <span className={cn(bulletStyle, active ? 'bg-success' : 'bg-error')} />
        {active ? t('coMngt.questions.status.active') : t('coMngt.questions.status.inactive')}
      </>
    );
  };
  const [expandedRows, setExpandedRows] = useState<Record<string, boolean>>({});

  const handleExpandedTableRow = (rowId: string, expanded: boolean) => {
    setExpandedRows((prev) => ({ ...prev, [rowId]: expanded }));
  };

  return (
    <div data-testid={`${baseDataTestId}-container`} className={containerStyle}>
      <div className={textContainerStyle}>
        <p className={titleStyle}>{t('coMngt.questions.account.title')}</p>
        <p className={subtitleStyle}>{t('coMngt.questions.account.subtitle')}</p>
      </div>

      <Table data-testid={`${baseDataTestId}-table`} className={tableStyle}>
        <TableHeader data-testid={`${baseDataTestId}-header`}>
          <TableRow data-testid={`${baseDataTestId}-row`} className={noHoverStyle}>
            <TableHead data-testid={`${baseDataTestId}-question`}>
              {t('coMngt.questions.colum.question')}
            </TableHead>
            <TableHead data-testid={`${baseDataTestId}-status`}>
              {t('coMngt.questions.colum.status')}
            </TableHead>
          </TableRow>
        </TableHeader>

        <TableBody data-testid={`${baseDataTestId}-table-body`}>
          <ExpandableTableRow
            testId={`${baseDataTestId}-row-purchase-order`}
            expandIcon={formatIBAssetsUrl(icons?.['icon.expand-icon'])}
            collapseIcon={formatIBAssetsUrl(icons?.['icon.collapse-icon'])}
            expandableContent={
              <BusinessEmployeeQuestions
                icons={icons}
                companyId={companyId}
                typeOfQuestion={BusinessQuestionType.PurchaseOrder}
                questionContent={purchaseOrderManagement}
                questionTrackerId="business-purchase-order"
                isNavigationGuardOwner={navigationGuardOwnerId === 'business-purchase-order'}
                onQuestionDirtyChange={onQuestionDirtyChange}
                onCollapse={(expanded: boolean) =>
                  handleExpandedTableRow(BusinessQuestionType.PurchaseOrder, expanded)
                }
              />
            }
            expanded={!!expandedRows[BusinessQuestionType.PurchaseOrder]}
            onTableRowExpanded={(expanded: boolean) =>
              handleExpandedTableRow(BusinessQuestionType.PurchaseOrder, expanded)
            }
          >
            <TableCell>{purchaseOrderManagement.managementHeader}</TableCell>
            <TableCell>{statusColumn(purchaseOrderManagement.active!)}</TableCell>
          </ExpandableTableRow>
          <ExpandableTableRow
            testId={`${baseDataTestId}-row-customer-reference`}
            expandIcon={formatIBAssetsUrl(icons?.['icon.expand-icon'])}
            collapseIcon={formatIBAssetsUrl(icons?.['icon.collapse-icon'])}
            expandableContent={
              <BusinessEmployeeQuestions
                icons={icons}
                companyId={companyId}
                typeOfQuestion={BusinessQuestionType.CustomerReference}
                questionContent={customerReferenceManagement}
                questionTrackerId="business-customer-reference"
                isNavigationGuardOwner={navigationGuardOwnerId === 'business-customer-reference'}
                onQuestionDirtyChange={onQuestionDirtyChange}
                onCollapse={(expanded: boolean) =>
                  handleExpandedTableRow(BusinessQuestionType.CustomerReference, expanded)
                }
              />
            }
            expanded={!!expandedRows[BusinessQuestionType.CustomerReference]}
            onTableRowExpanded={(expanded: boolean) =>
              handleExpandedTableRow(BusinessQuestionType.CustomerReference, expanded)
            }
          >
            <TableCell>{customerReferenceManagement.managementHeader}</TableCell>
            <TableCell>{statusColumn(customerReferenceManagement.active!)}</TableCell>
          </ExpandableTableRow>
        </TableBody>
      </Table>
    </div>
  );
}

const containerStyle = 'mt-12 pb-12 border-b-[1px] border-lightGrey3';
const textContainerStyle = 'max-w-[620px]';
const titleStyle = 'text-xl font-bold';
const subtitleStyle = 'mt-2';
const tableStyle = 'mt-10 mobile:table-fixed tablet:table-fixed';
const noHoverStyle = 'hover:bg-transparent';
const bulletStyle = 'inline-block h-3 w-3 rounded-md bg-secondaryColor mr-2';
