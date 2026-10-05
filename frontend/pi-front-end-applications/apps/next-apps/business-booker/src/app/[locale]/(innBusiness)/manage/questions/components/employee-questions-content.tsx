'use client';

import { ManagementInformationQuestion } from '@whitbread-eos/api';
import { useCallback, useState } from 'react';

import { BusinessQuestions } from './business-questions';
import { CustomQuestions } from './custom-questions';

type Props = {
  purchaseOrderManagement: ManagementInformationQuestion;
  customerReferenceManagement: ManagementInformationQuestion;
  userDefinedManagement: ManagementInformationQuestion[];
  companyId: string;
  icons: Record<string, string>;
};

export function EmployeeQuestionsContent({
  purchaseOrderManagement,
  customerReferenceManagement,
  userDefinedManagement,
  companyId,
  icons,
}: Props) {
  const [dirtyQuestionIds, setDirtyQuestionIds] = useState<string[]>([]);

  const handleQuestionDirtyChange = useCallback((questionId: string, isDirty: boolean) => {
    setDirtyQuestionIds((prev) => {
      if (isDirty) {
        if (prev.includes(questionId)) {
          return prev;
        }

        return [...prev, questionId];
      }

      return prev.filter((id) => id !== questionId);
    });
  }, []);

  const navigationGuardOwnerId = dirtyQuestionIds[0];

  return (
    <>
      <BusinessQuestions
        purchaseOrderManagement={purchaseOrderManagement}
        customerReferenceManagement={customerReferenceManagement}
        companyId={companyId}
        icons={icons}
        navigationGuardOwnerId={navigationGuardOwnerId}
        onQuestionDirtyChange={handleQuestionDirtyChange}
      />
      <CustomQuestions
        companyId={companyId}
        icons={icons}
        questions={userDefinedManagement}
        navigationGuardOwnerId={navigationGuardOwnerId}
        onQuestionDirtyChange={handleQuestionDirtyChange}
      />
    </>
  );
}
