import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { DeleteApplicationModal } from './delete-application-modal';

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    useTranslation: () => ({
      t: (str: string) => str,
    }),
  };
});

describe('DeleteApplicationModal Component', () => {
  it('should render the delete modal', async () => {
    const { getByTestId } = render(
      <DeleteApplicationModal
        isOpen={true}
        onClose={jest.fn()}
        onConfirm={jest.fn()}
        companyName="Company Name"
      />
    );

    expect(getByTestId(`DeleteApplicationModal`)).toBeInTheDocument();
  });

  it('should not render the delete modal', async () => {
    const { queryByTestId } = render(
      <DeleteApplicationModal
        isOpen={false}
        onClose={jest.fn()}
        onConfirm={jest.fn()}
        companyName="Company Name"
      />
    );

    expect(queryByTestId(`DeleteApplicationModal`)).not.toBeInTheDocument();
  });

  it('should call onClose when cancel button is clicked', async () => {
    const onClose = jest.fn();
    const { getByTestId } = render(
      <DeleteApplicationModal
        isOpen={true}
        onClose={onClose}
        onConfirm={jest.fn()}
        companyName="Company Name"
      />
    );

    getByTestId(`DeleteApplicationModal-CancelButton`).click();

    expect(onClose).toHaveBeenCalled();
  });

  it('should call onConfirm when delete button is clicked', async () => {
    const onConfirm = jest.fn();
    const { getByTestId } = render(
      <DeleteApplicationModal
        isOpen={true}
        onClose={jest.fn()}
        onConfirm={onConfirm}
        companyName="Company Name"
      />
    );

    getByTestId(`DeleteApplicationModal-DeleteButton`).click();

    expect(onConfirm).toHaveBeenCalled();
  });

  it('should be disabled when isLoading is true', async () => {
    const { getByTestId } = render(
      <DeleteApplicationModal
        isOpen={true}
        onClose={jest.fn()}
        onConfirm={jest.fn()}
        companyName="Company Name"
        isLoading={true}
      />
    );

    expect(getByTestId(`DeleteApplicationModal-CancelButton`)).toBeDisabled();
    expect(getByTestId(`DeleteApplicationModal-DeleteButton`)).toBeDisabled();
  });
});
