import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';

import { ReviewChangesModal } from './review-changes-modal';

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

describe('ReviewChangesModal Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render ReviewChangesModal component', async () => {
    render(<ReviewChangesModal testId="ReviewChangesModal" open={true} />);

    const modal = await screen.findByTestId('ReviewChangesModal');

    expect(modal).toHaveTextContent('userMgmt.employee.edit.review.heading');
    expect(modal).toHaveTextContent('userMgmt.employee.edit.review.description');
  });

  it('should render ReviewChangesModal component with dialogTitle and dialogDescription props', async () => {
    const { queryByText } = render(
      <ReviewChangesModal
        testId="ReviewChangesModal"
        open={true}
        dialogTitle="Some dialog title"
        dialogDescription="Some dialog description"
      />
    );

    await waitFor(() => {
      expect(queryByText('Some dialog title')).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(queryByText('Some dialog description')).toBeInTheDocument();
    });

    expect(queryByText('Review changes')).not.toBeInTheDocument();
    expect(
      queryByText('You may have unsaved changes. Do you want to discard or save them?')
    ).not.toBeInTheDocument();
  });
});
