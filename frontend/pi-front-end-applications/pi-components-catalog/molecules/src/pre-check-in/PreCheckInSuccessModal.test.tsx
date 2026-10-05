import '@testing-library/jest-dom';
import * as React from 'react';

import { render } from '../utils/test-utils';
import PreCheckInSuccessModal from './PreCheckInSuccessModal';

describe('Page', () => {
  it('should render Pre Check-in Success Modal', () => {
    const { getByTestId } = render(<PreCheckInSuccessModal isOpen={true} onClose={jest.fn} />);

    expect(getByTestId('PreCheckIn-ModalCloseButton')).toBeInTheDocument();
  });
});
