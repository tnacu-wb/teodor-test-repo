import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES } from '@whitbread-eos/atoms';

import { render } from '../../utils/test-utils';
import Notice from './Notice.component';

const formField: FieldsType = {
  type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
  name: 'backButton',
  label: 'backButton',
  testid: 'Notice',
};

const mockProps = {
  formField,
  description: '',
};
describe('should render <Notice/> component', () => {
  it('should render a <Notice/> ', () => {
    const { getByTestId } = render(<Notice {...mockProps} description="test" />);
    const structure = getByTestId('Notice');
    expect(structure).toBeInTheDocument();
  });

  it('should render a <Notice/> with the description from the formField props when given', () => {
    const props = {
      ...mockProps,
      description: 'This is a prop description',
      formField: {
        type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
        name: 'backButton',
        label: 'backButton',
        testid: 'Notice',
        props: {
          description: 'This is a formField prop description',
        },
      },
    };

    const { getByTestId } = render(<Notice {...props} />);
    const element = getByTestId('Notice');

    expect(element).toHaveTextContent('This is a formField prop description');
  });

  it('should render a <Notice/> with a link when given', () => {
    const props = {
      ...mockProps,
      formField: {
        type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
        name: 'backButton',
        label: 'backButton',
        testid: 'Notice',
        props: {
          linkPath: 'https://test.com',
          linkLabel: 'Test',
        },
      },
    };

    const { getByRole } = render(<Notice {...props} />);
    const element = getByRole('link');

    expect(element).toBeInTheDocument();
  });

  it('should render a <Notice/> with the link from props instead of to the one from form field', () => {
    const props = {
      ...mockProps,
      linkPath: 'https://testprops.com',
      linkLabel: 'Props Link',
      formField: {
        type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
        name: 'backButton',
        label: 'backButton',
        testid: 'Notice',
        props: {
          linkPath: 'https://testformfield.com',
          linkLabel: 'Form Field Link',
        },
      },
    };

    const { getByRole } = render(<Notice {...props} />);
    const element = getByRole('link');

    expect(element).toHaveTextContent('Props Link');
  });
});
