import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import { Control, FieldErrors, useForm } from 'react-hook-form';

import { MemorableWordInput } from './MemorableWordInput';

const mockProps = {
  name: 'memorableWord',
  icons: { icon: 'test' },
  control: {} as Control<{ memorableWord: string }, any>,
  errors: {} as FieldErrors<{ memorableWord: string }>,
  trigger: jest.fn(),
  clearErrors: jest.fn(),
};

jest.mock('@whitbread-eos/atoms/ui', () => ({
  SanitizedContent: ({ children }: { children: React.ReactNode; replacements: any }) => (
    <div>{children}</div>
  ),
  FormInputShowHide: (props: any) => {
    const { ...inputProps } = props;
    return <input data-testid={props.id || 'memorableWord-Form-Input'} {...inputProps} />;
  },
}));

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: (url: string) => url,
  };
});

describe('MemorableWordInput Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  function renderWithForm(props = {}) {
    const Wrapper = (wrapperProps: any) => {
      const { control } = useForm<{ memorableWord: string }>();
      return <MemorableWordInput {...mockProps} {...wrapperProps} control={control} />;
    };
    return render(<Wrapper {...props} />);
  }

  it('should render MemorableWordInput component', async () => {
    const { getByTestId } = renderWithForm();

    await waitFor(() => {
      expect(getByTestId('MemorableWordInput-form')).toBeInTheDocument();
    });
  });

  it('should display validation rules', async () => {
    const { getByTestId } = renderWithForm();

    expect(getByTestId('MemorableWordInput-list-title')).toBeInTheDocument();
    expect(getByTestId('MemorableWordInput-list-content')).toBeInTheDocument();
  });

  it('should trigger validation on blur', async () => {
    const { getByTestId } = renderWithForm();

    const input = getByTestId('memorableWord');
    fireEvent.blur(input);

    await waitFor(() => {
      expect(mockProps.trigger).toHaveBeenCalledWith('memorableWord');
    });
  });
});
