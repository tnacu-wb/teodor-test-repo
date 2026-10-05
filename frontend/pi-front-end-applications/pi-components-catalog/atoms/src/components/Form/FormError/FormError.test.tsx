import '@testing-library/jest-dom';
import React from 'react';
import type { FieldErrors } from 'react-hook-form';

import { render } from '../../../utils/test-utils';
import FormError from './FormError.component';

describe('Form Errors', () => {
  it('should render the component', () => {
    const errorsList: FieldErrors = {
      title: {
        type: 'required',
        message: 'The title is required',
      },
    };

    const props = {
      errors: errorsList,
      name: 'title',
    };

    const { getByText } = render(<FormError {...props} />);
    const text = getByText('The title is required');
    expect(text).toBeInTheDocument();
  });
});
