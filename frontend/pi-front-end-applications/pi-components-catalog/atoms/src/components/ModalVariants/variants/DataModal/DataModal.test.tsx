import '@testing-library/jest-dom';
import React from 'react';

import { userEvent, render } from '../../../../utils/test-utils';
import DataModal from './DataModal.component';

const DATA_MODAL_TITLE = 'DATA_MODAL_TITLE';
const DATA_MODAL_CONTENT = 'DATA_MODAL_CONTENT';
const DATA_MODAL_FOOTER = 'DATA_MODAL_FOOTER';
const DATA_MODAL_CUSTOM_HEADER = 'DATA_MODAL_CUSTOM_HEADER';

const mockCloseHandler = jest.fn();
const mockProps = {
  isOpen: true,
  onClose: mockCloseHandler,
  variantProps: {
    title: DATA_MODAL_TITLE,
  },
};

const mockModalContent = <p>{DATA_MODAL_CONTENT}</p>;
const mockModalFooter = <p>{DATA_MODAL_FOOTER}</p>;
const mockCustomHeader = <p>{DATA_MODAL_CUSTOM_HEADER}</p>;

describe('DataModal', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('should render a Modal with title and content', () => {
    const { getByText } = render(<DataModal {...mockProps}>{mockModalContent}</DataModal>);
    expect(getByText(DATA_MODAL_TITLE)).toBeInTheDocument();
    expect(getByText(DATA_MODAL_CONTENT)).toBeInTheDocument();
  });

  it('should invoke close handler when close icon is clicked', () => {
    const { getByRole } = render(<DataModal {...mockProps}>{mockModalContent}</DataModal>);

    const closeIcon = getByRole('button', { name: /Close/ });
    userEvent.click(closeIcon);
    expect(mockCloseHandler).toHaveBeenCalledTimes(1);
  });

  it('should invoke close handler on pressing Escape', () => {
    render(<DataModal {...mockProps}>{mockModalContent}</DataModal>);

    userEvent.keyboard('{Escape}');
    expect(mockCloseHandler).toHaveBeenCalledTimes(1);
  });

  it('should not render the footer when not provided', () => {
    const { queryByTestId } = render(
      <DataModal {...mockProps} dataTestId="TestModal">
        {mockModalContent}
      </DataModal>
    );

    expect(queryByTestId('TestModal-ModalFooter')).toBeFalsy();
  });

  it('should render the footer when provided', () => {
    const { queryByTestId, getByText } = render(
      <DataModal
        {...mockProps}
        dataTestId="TestModal"
        variantProps={{ ...mockProps.variantProps, footer: mockModalFooter }}
      >
        {mockModalContent}
      </DataModal>
    );

    expect(queryByTestId('TestModal-ModalFooter')).toBeTruthy();
    expect(getByText(DATA_MODAL_FOOTER)).toBeInTheDocument();
  });

  it('should use the dataTestId if the prop has been added to the component', () => {
    const { queryByTestId } = render(
      <DataModal {...mockProps} dataTestId="TestModal">
        {mockModalContent}
      </DataModal>
    );

    expect(queryByTestId('TestModal-ModalContent')).toBeTruthy();
  });

  it('should display the custom header if the prop has been added to the component', () => {
    const { queryByText, getByText } = render(
      <DataModal
        {...{ ...mockProps, variantProps: { title: DATA_MODAL_TITLE, header: mockCustomHeader } }}
      >
        {mockModalContent}
      </DataModal>
    );

    expect(getByText(DATA_MODAL_CUSTOM_HEADER)).toBeInTheDocument();
    expect(queryByText(DATA_MODAL_TITLE)).not.toBeInTheDocument();
  });
});
