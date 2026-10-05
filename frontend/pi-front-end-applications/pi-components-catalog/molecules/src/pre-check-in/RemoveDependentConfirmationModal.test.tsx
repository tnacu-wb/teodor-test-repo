import '@testing-library/jest-dom';
import * as React from 'react';

import { render } from '../utils/test-utils';
import RemoveDependentConfirmationModal from './RemoveDependentConfirmationModal';

describe('Page', () => {
  it('should render Pre Check-in Success Modal', () => {
    const { getByTestId } = render(
      <RemoveDependentConfirmationModal
        isOpen={true}
        onClose={jest.fn}
        handleDependentDelete={jest.fn}
      />
    );

    expect(getByTestId('PreCheckIn-ModalConfirmationButton')).toBeInTheDocument();
    expect(getByTestId('PreCheckIn-ModalDenyButton')).toBeInTheDocument();
  });
});
