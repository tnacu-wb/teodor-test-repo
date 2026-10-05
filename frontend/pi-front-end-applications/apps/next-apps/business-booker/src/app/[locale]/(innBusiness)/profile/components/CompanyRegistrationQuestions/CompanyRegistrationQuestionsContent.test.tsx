import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import { act } from 'react-dom/test-utils';

import { CompanyRegistrationQuestionsContent } from './CompanyRegistrationQuestionsContent';
import { mockCompanyRegistrationQuestions } from './mockData';

const mockProps = {
  companyRegistrationQuestions: mockCompanyRegistrationQuestions,
  baseDataTestId: 'testId',
  handleEditMode: jest.fn(),
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});
describe('CompanyRegistrationQuestionsContent Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyRegistrationQuestionsContent component', async () => {
    const { getByTestId } = render(<CompanyRegistrationQuestionsContent {...mockProps} />);

    expect(getByTestId('testId-Company-Registration-Questions-Content')).toBeInTheDocument();
  });

  it('should click button and call handleEditMode', async () => {
    const { handleEditMode } = mockProps;
    const { getByTestId } = render(<CompanyRegistrationQuestionsContent {...mockProps} />);

    expect(getByTestId('testId-Company-Registration-Questions-Content')).toBeInTheDocument();

    const editButton = getByTestId('testId-Company-Registration-Questions-Button');

    expect(editButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(editButton);
    });

    await waitFor(() => {
      expect(handleEditMode).toBeCalled();
    });
  });
});
