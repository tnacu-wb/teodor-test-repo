import '@testing-library/jest-dom';
import { fireEvent, render, screen } from '@testing-library/react';

import { EmployeeQuestionsContent } from './employee-questions-content';

jest.mock('./business-questions', () => ({
  BusinessQuestions: ({ navigationGuardOwnerId, onQuestionDirtyChange }: any) => (
    <div>
      <span data-testid="business-owner">{navigationGuardOwnerId || ''}</span>
      <button
        data-testid="business-dirty"
        onClick={() => onQuestionDirtyChange?.('business-purchase-order', true)}
      />
      <button
        data-testid="business-clean"
        onClick={() => onQuestionDirtyChange?.('business-purchase-order', false)}
      />
    </div>
  ),
}));

jest.mock('./custom-questions', () => ({
  CustomQuestions: ({ navigationGuardOwnerId, onQuestionDirtyChange }: any) => (
    <div>
      <span data-testid="custom-owner">{navigationGuardOwnerId || ''}</span>
      <button
        data-testid="custom-dirty"
        onClick={() => onQuestionDirtyChange?.('custom-q1', true)}
      />
      <button
        data-testid="custom-clean"
        onClick={() => onQuestionDirtyChange?.('custom-q1', false)}
      />
    </div>
  ),
}));

const props = {
  purchaseOrderManagement: {
    questionId: 'purchase-order',
    managementHeader: 'Purchase Order',
  },
  customerReferenceManagement: {
    questionId: 'customer-reference',
    managementHeader: 'Customer Reference',
  },
  userDefinedManagement: [],
  companyId: 'COMP_1',
  icons: {},
};

describe('EmployeeQuestionsContent', () => {
  it('keeps first dirty owner and transfers ownership after it is cleaned', () => {
    render(<EmployeeQuestionsContent {...(props as any)} />);

    fireEvent.click(screen.getByTestId('business-dirty'));
    fireEvent.click(screen.getByTestId('custom-dirty'));

    expect(screen.getByTestId('business-owner')).toHaveTextContent('business-purchase-order');
    expect(screen.getByTestId('custom-owner')).toHaveTextContent('business-purchase-order');

    fireEvent.click(screen.getByTestId('business-clean'));

    expect(screen.getByTestId('business-owner')).toHaveTextContent('custom-q1');
    expect(screen.getByTestId('custom-owner')).toHaveTextContent('custom-q1');

    fireEvent.click(screen.getByTestId('custom-clean'));

    expect(screen.getByTestId('business-owner')).toHaveTextContent('');
    expect(screen.getByTestId('custom-owner')).toHaveTextContent('');
  });
});
