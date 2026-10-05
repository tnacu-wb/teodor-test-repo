import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { useForm } from 'react-hook-form';

import { render } from '../../../../utils/test-utils';
import AgentOverrideButton from './AgentOverrideButton.component';

const mockHandleResetField = jest.fn();

const Component = () => {
  const { control } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'agentOverrideSubmitButton',
    testid: 'AgentOverrideSubmit',
    label: 'Save',
    props: {
      type: 'submit',
    },
  };
  const props: any = {
    formField,
    control,
    errors: '',
    handleResetField: mockHandleResetField,
  };

  return <AgentOverrideButton {...props} />;
};

const ComponentWithNoManagerApproval = () => {
  const { control } = useForm({
    defaultValues: {
      managerName: 'Test',
    },
  });

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'agentOverrideSubmitButton',
    testid: 'AgentOverrideSubmit',
    label: 'Save',
    props: {
      type: 'submit',
    },
  };
  const props: any = {
    formField,
    control,
    errors: '',
    handleResetField: mockHandleResetField,
  };

  return <AgentOverrideButton {...props} />;
};

let testId = 'AgentOverrideSubmit';
const ComponentWithDisabledSubmit = () => {
  const { control } = useForm({
    defaultValues: {
      selectReason: '',
      callerName: '',
      managerName: '',
    },
  });

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'agentOverrideSubmitButton',
    testid: testId,
    label: 'Save',
    props: {
      type: 'submit',
    },
  };
  const props: any = {
    formField,
    control,
    errors: '',
    handleResetField: mockHandleResetField,
  };

  return <AgentOverrideButton {...props} />;
};

describe('<AgentOverrideButton/>', () => {
  it('should render a <AgentOverrideButton/> ', () => {
    const { getByTestId } = render(<Component />);
    const button = getByTestId('AgentOverrideSubmit-Button');
    expect(button).toBeInTheDocument();
  });

  it('should render a <AgentOverrideButton/> with no manager approval needed', () => {
    const { getByTestId } = render(<ComponentWithNoManagerApproval />);
    const button = getByTestId('AgentOverrideSubmit-Button');
    expect(button).toBeInTheDocument();
  });

  it('should render a <AgentOverrideButton/> with disabled submit if no fields are completed', () => {
    const { getByTestId } = render(<ComponentWithDisabledSubmit />);
    const button = getByTestId('AgentOverrideSubmit-Button');
    expect(button).toBeDisabled();
  });

  it('should render a <AgentOverrideButton/> without testId', () => {
    testId = undefined;
    const { getByTestId } = render(<ComponentWithDisabledSubmit />);
    const button = getByTestId('Submit-Button');
    expect(button).toBeDisabled();
  });
});
