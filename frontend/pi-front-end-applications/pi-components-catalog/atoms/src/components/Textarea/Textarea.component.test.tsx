import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import Textarea from './Textarea.component';

describe('TextareaComponent', () => {
  it('render the Textarea Component', () => {
    const { getByRole } = render(
      <Textarea size="md" variant="outline" maxLength={25} placeholder="Enter text here ..." />
    );
    expect(getByRole('textbox')).toBeInTheDocument();
  });
  it('render the Textarea Component with error, if error', () => {
    const { getByText } = render(
      <Textarea
        error="error message"
        size="md"
        variant="outline"
        placeholder="Enter text here ..."
      />
    );
    const error = getByText('error message');
    expect(error).toBeInTheDocument();
  });
});
