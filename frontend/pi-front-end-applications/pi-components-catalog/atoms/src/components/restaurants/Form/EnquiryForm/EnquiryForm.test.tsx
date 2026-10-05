import '@testing-library/jest-dom';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render, screen } from '../../../../utils/test-utils';
import EnquiryForm from './EnquiryForm';

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
}));

const Component = () => {
  const { control } = useForm();
  const defaultprop = {
    formField: {
      name: 'input-feild',
      relatedFields: [
        {
          name: 'input',
        },
      ],
    },
    control: control,
    errors: {},
  };
  return <EnquiryForm {...defaultprop} />;
};

describe('Enquiry Form Component', () => {
  beforeEach(() => {
    render(<Component />);
  });

  it('should render the component', () => {
    expect(screen.getByTestId('EnquiryForm')).toBeInTheDocument();
  });
});
