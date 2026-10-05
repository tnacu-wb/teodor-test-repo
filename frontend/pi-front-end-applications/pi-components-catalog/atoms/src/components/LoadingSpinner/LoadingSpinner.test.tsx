import { render } from '@testing-library/react';
import React from 'react';

import LoadingSpinner from './LoadingSpinner.component';

describe('LoadingSpinner', () => {
  it('should render the component with custom text and style props', () => {
    const customText = 'Loading data';
    const customStyle = { backgroundColor: 'red' };
    const { getByTestId } = render(
      <LoadingSpinner loadingText={customText} wrapperStyle={customStyle} />
    );

    const loadingSpinner = getByTestId('loading-spinner');
    expect(loadingSpinner).toBeInTheDocument();
    expect(loadingSpinner).toHaveTextContent(customText);
    expect(loadingSpinner).toHaveStyle({
      backgroundColor: 'red',
    });
  });
});
