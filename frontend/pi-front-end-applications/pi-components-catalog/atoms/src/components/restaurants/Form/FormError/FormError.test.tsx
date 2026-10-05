import '@testing-library/jest-dom';
import type { FieldErrors } from 'react-hook-form';

import { render } from '../../../../utils/test-utils';
import FormError from './FormError.component';

describe('Form Errors', () => {
  it('should render the component', () => {
    const errorsList: FieldErrors = {
      firstname: {
        type: 'required',
        message: 'The firstname is required',
      },
    };

    const props = {
      errors: errorsList,
      name: 'firstname',
    };

    const { getByText } = render(<FormError {...props} />);
    const text = getByText('The firstname is required');
    expect(text).toBeInTheDocument();
  });
});
