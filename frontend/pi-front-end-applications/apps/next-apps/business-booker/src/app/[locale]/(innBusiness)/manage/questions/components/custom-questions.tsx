'use client';

import { ManagementInformationQuestion } from '@whitbread-eos/api';
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
  ExpandableTableRow,
  Button,
} from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';
import { useState, useEffect, useRef } from 'react';

import { CustomEmployeeQuestions } from './CustomEmployeeQuestions';

type Props = {
  questions: ManagementInformationQuestion[];
  icons: Record<string, string>;
  companyId: string;
  navigationGuardOwnerId?: string;
  onQuestionDirtyChange?: (questionId: string, isDirty: boolean) => void;
};

type QuestionWithTrackerId = ManagementInformationQuestion & {
  questionTrackerId: string;
};

export function CustomQuestions({
  questions,
  icons,
  companyId,
  navigationGuardOwnerId,
  onQuestionDirtyChange,
}: Props) {
  const baseDataTestId = 'CustomQuestions';
  const { t } = useTranslation('company');
  const newQuestionCounterRef = useRef(0);

  const mapQuestionsWithTrackerId = (
    questionsList: ManagementInformationQuestion[]
  ): QuestionWithTrackerId[] => {
    return questionsList.map((question, index) => ({
      ...question,
      questionTrackerId: question?.questionId
        ? `custom-${question.questionId}`
        : `custom-server-new-${index}`,
    }));
  };

  const [expandedRows, setExpandedRows] = useState<Record<string, boolean>>({});
  const [newQuestionsArray, setNewQuestionsArray] = useState<QuestionWithTrackerId[]>(() =>
    mapQuestionsWithTrackerId(questions)
  );
  const handleExpandedTableRow = (rowId: string, expanded: boolean) => {
    setExpandedRows((prev) => ({ ...prev, [rowId]: expanded }));
  };

  const handleAddNewQuestion = () => {
    const newQuestionLabel = t('coMngt.questions.newQuestion.label');
    const newQuestionExists = newQuestionsArray.some(
      (question) => question.managementHeader === newQuestionLabel
    );

    if (!newQuestionExists) {
      setNewQuestionsArray((prev) => [
        ...prev,
        {
          managementHeader: newQuestionLabel,
          questionTrackerId: `custom-new-${newQuestionCounterRef.current++}`,
        },
      ]);
    }
  };
  const handleDeleteQuestion = (questionId: string) => {
    setNewQuestionsArray((prev) => prev.filter((question) => question.questionId !== questionId));
  };

  useEffect(() => {
    setNewQuestionsArray(mapQuestionsWithTrackerId(questions));
  }, [questions]);

  return (
    <div data-testid={`${baseDataTestId}-container`} className={containerStyle}>
      <div className={textContainerStyle}>
        <p className={titleStyle}>{t('coMngt.questions.custom.title')}</p>
        <p className={subtitleStyle}>{t('coMngt.questions.custom.subtitle')}</p>
      </div>

      <Table data-testid={`${baseDataTestId}-table`} className={tableStyle}>
        <TableHeader data-testid={`${baseDataTestId}-header`}>
          <TableRow data-testid={`${baseDataTestId}-row`} className={noHoverStyle}>
            <TableHead data-testid={`${baseDataTestId}-question`}>
              {t('coMngt.questions.colum.question')}
            </TableHead>
          </TableRow>
        </TableHeader>

        <TableBody data-testid={`${baseDataTestId}-table-body`}>
          {newQuestionsArray?.map((question, index) => {
            return (
              <ExpandableTableRow
                key={question.questionTrackerId}
                testId={`${baseDataTestId}-row-custom-${index}`}
                expandIcon={formatIBAssetsUrl(icons?.['icon.expand-icon'])}
                collapseIcon={formatIBAssetsUrl(icons?.['icon.collapse-icon'])}
                expandableContent={
                  <CustomEmployeeQuestions
                    icons={icons}
                    companyId={companyId}
                    questionContent={question}
                    questionTrackerId={question.questionTrackerId}
                    isNavigationGuardOwner={navigationGuardOwnerId === question.questionTrackerId}
                    onQuestionDirtyChange={onQuestionDirtyChange}
                    onCollapse={(expanded: boolean) =>
                      handleExpandedTableRow(question.questionTrackerId, expanded)
                    }
                    onDelete={handleDeleteQuestion}
                  />
                }
                expanded={!!expandedRows[question.questionTrackerId]}
                onTableRowExpanded={(expanded: boolean) =>
                  handleExpandedTableRow(question.questionTrackerId, expanded)
                }
              >
                <TableCell>{question.managementHeader}</TableCell>
              </ExpandableTableRow>
            );
          })}
        </TableBody>
      </Table>

      <Button
        variant="outline"
        className={buttonStyle}
        data-testid={`${baseDataTestId}-button-add`}
        onClick={handleAddNewQuestion}
      >
        <Image
          alt="add"
          src={formatIBAssetsUrl(t('coMngt.questions.custom.createQuestionButton.icon'))}
          width={24}
          height={24}
          className={buttonIconStyle}
          data-testid={`${baseDataTestId}-button-add-icon`}
        />
        {t('coMngt.questions.custom.createQuestionButton')}
      </Button>
    </div>
  );
}

const containerStyle = 'mt-12';
const textContainerStyle = 'max-w-[620px]';
const titleStyle = 'text-xl font-bold';
const subtitleStyle = 'mt-2';
const tableStyle = 'mt-10 mobile:table-fixed tablet:table-fixed';
const noHoverStyle = 'hover:bg-transparent';
const buttonStyle = 'mt-10 h-14 font-semibold text-lg mobile:w-full';
const buttonIconStyle = 'mr-2';
