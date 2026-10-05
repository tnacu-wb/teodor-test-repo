import EmployeeQuestions from '.';
import '@testing-library/jest-dom';

import { render, userEvent } from '../../utils/test-utils';

const dropdownError = 'Please select an answer.';
const inputError = 'This field is required.';
const maxLengthError = 'Please enter no more than 50 characters';
const regexError = 'Please enter a valid answer.';
const referenceError =
  'Customer Reference may not exceed 24 characters and may only contain letters, numbers, spaces, hyphens or dots.';
const poError = 'Please enter a valid purchase order';
const getMockProps = () => ({
  companyManagementDetails: {
    purchaseOrderManagement: {
      questionId: 0,
      label: 'Purchase order number',
      mandatory: true,
      managementHeader: 'Purchase order number',
      active: true,
      location: 'B',
      managementInformationAnswer: {
        answerType: 'F',
        answers: [],
      },
    },
    customerReferenceManagement: {
      questionId: 1,
      label: 'Customer reference',
      mandatory: true,
      managementHeader: 'Customer reference',
      active: true,
      location: 'B',
      managementInformationAnswer: {
        answerType: 'F',
        answers: [],
      },
    },
    userDefinedManagement: [
      {
        questionId: 2,
        label: 'What is the capital of England?',
        mandatory: false,
        managementHeader: 'England Capital',
        active: true,
        location: 'B',
        managementInformationAnswer: {
          answerType: 'F',
        },
      },
      {
        questionId: 3,
        label: 'Who is the best football team?',
        mandatory: false,
        managementHeader: 'Who is the best football team?',
        active: true,
        location: 'B',
        managementInformationAnswer: {
          answerType: 'U',
          answers: ['Arsenal', 'Manchester United', 'Real Madrid'],
        },
      },
    ],
  },
  setPOValue: jest.fn(),
  setCustomerRefValue: jest.fn(),
  setUserDefinedQuestions: jest.fn(),
  poValue: '',
  customerRefValue: '',
  userDefinedQuestions: [],
  purchaseOrder: {
    answer: 'ABCE',
  },
  customerRef: {
    answer: 'ABCE',
  },
  setValidateQuestions: jest.fn(),
  validateQuestions: false,
  setHasError: jest.fn(),
  hasError: false,
  setCustomerRef: jest.fn(),
  setPurchaseOrder: jest.fn(),
  setHasEmployeeQuestionsErrors: jest.fn(),
  hasEmployeeQuestionsErrors: false,
});
let mockProps = getMockProps();

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => {
      switch (key) {
        case 'employeeQuestions.customDropdownMessage':
          return dropdownError;
        case 'config.errorMessages.refAnswer.required':
          return inputError;
        case 'booking.bac.referenceDetails.maxLengthMessage':
          return maxLengthError;
        case 'employeeQuestions.customFreeTextRegex':
          return regexError;
        case 'config.errorMessages.cardDetails.purchaseOrder.valid':
          return poError;
        case 'paymentQuestions.refrenceValid':
          return referenceError;

        default:
          return 'default';
      }
    },
  }),
}));

describe('EmployeeQuestions', () => {
  beforeEach(() => {
    jest.resetAllMocks();
    window.HTMLElement.prototype.scrollIntoView = jest.fn();
    mockProps = getMockProps();
  });
  it('should render 1 input fields and 1 dropdown', () => {
    const { getAllByRole } = render(<EmployeeQuestions {...mockProps} />);
    expect(getAllByRole('textbox').length).toEqual(3);
    expect(getAllByRole('button').length).toEqual(1);
  });

  it('should call setUserDefinedQuestions when input field is populated', () => {
    const { getByTestId } = render(<EmployeeQuestions {...mockProps} />);
    const input = getByTestId('input-EmployeeQuestions-2');
    userEvent.type(input, 'London');
    expect(mockProps.setUserDefinedQuestions).toHaveBeenCalledTimes(7);
  });

  it('should call setUserDefinedQuestions when dropdown is selected', () => {
    const updatedMockProps = {
      ...mockProps,
      userDefinedQuestions: [
        {
          answer: 'Arsenal',
          question: 'Who is the best football team?',
          questionId: 3,
        },
      ],
    };
    const { getByTestId, getByText } = render(<EmployeeQuestions {...updatedMockProps} />);
    const dropdownToggle = getByTestId('DropdownComp-EmployeeQuestions-3-entireList');
    userEvent.click(dropdownToggle);
    const selectedOption = getByText('Arsenal');
    userEvent.click(selectedOption);
    expect(mockProps.setUserDefinedQuestions).toHaveBeenCalledTimes(2);
  });

  it('should call setUserDefinedQuestions for each character that is typed', () => {
    const updatedMockProps = {
      ...mockProps,
      userDefinedQuestions: [
        {
          answer: '',
          question: 'What is the capital of England?',
          questionId: 2,
          mandatory: true,
        },
      ],
    };
    const { getByTestId } = render(<EmployeeQuestions {...updatedMockProps} />);
    const input = getByTestId('input-EmployeeQuestions-2');
    userEvent.type(input, 'London');
    expect(mockProps.setUserDefinedQuestions).toHaveBeenCalledTimes(7);
  });

  it('should show * next to mandatory input questions', () => {
    mockProps.companyManagementDetails.userDefinedManagement[0].mandatory = true;
    const { getByTestId } = render(<EmployeeQuestions {...mockProps} />);
    const input = getByTestId('input-EmployeeQuestions-2');
    expect(input).toHaveAttribute('placeholder', 'What is the capital of England?*');
  });

  it('should show * next to mandatory dropdown questions', () => {
    mockProps.companyManagementDetails.userDefinedManagement[1].mandatory = true;
    const { getByText } = render(<EmployeeQuestions {...mockProps} />);
    const dropdown = getByText('Who is the best football team?*');
    expect(dropdown).toBeInTheDocument();
  });

  it('should call setValidateQuestions and set validateQuestions back to false ', () => {
    const updatedMockProps = {
      ...mockProps,
      validateQuestions: true,
      userDefinedQuestions: [
        {
          answer: '',
          question: 'What is the capital of England?',
          questionId: 2,
          mandatory: true,
          hasError: true,
        },
      ],
    };
    render(<EmployeeQuestions {...updatedMockProps} />);
    expect(updatedMockProps.setValidateQuestions).toHaveBeenCalledWith(false);
  });

  it('should return the object sorted by questionId if sorted', () => {
    const updatedMockProps = {
      ...mockProps,
      validateQuestions: true,
      userDefinedQuestions: [
        {
          answer: '',
          question: 'What is the capital of England?',
          questionId: 2,
          mandatory: true,
          managementHeader: 'England Capital',
          hasError: true,
        },
        {
          answer: '',
          question: 'Who is the best football team?',
          questionId: 3,
          mandatory: true,
          managementHeader: 'Who is the best football team?',
          hasError: true,
        },
      ],
    };
    render(<EmployeeQuestions {...updatedMockProps} />);
    expect(updatedMockProps.setUserDefinedQuestions).toHaveBeenCalledWith([
      {
        answer: '',
        hasError: false,
        mandatory: false,
        question: 'What is the capital of England?',
        questionId: 2,
        managementHeader: 'England Capital',
      },
      {
        answer: '',
        hasError: false,
        mandatory: false,
        question: 'Who is the best football team?',
        questionId: 3,
        managementHeader: 'Who is the best football team?',
      },
    ]);
  });

  it('should return the object sorted by questionId if not sorted', () => {
    const updatedMockProps = {
      ...mockProps,
      validateQuestions: true,
      userDefinedQuestions: [
        {
          answer: '',
          question: 'Who is the best football team?',
          questionId: 3,
          mandatory: true,
          hasError: true,
          managementHeader: 'Who is the best football team?',
        },
        {
          answer: '',
          question: 'What is the capital of England?',
          questionId: 2,
          mandatory: true,
          hasError: true,
          managementHeader: 'England Capital',
        },
      ],
    };
    render(<EmployeeQuestions {...updatedMockProps} />);
    expect(updatedMockProps.setUserDefinedQuestions).toHaveBeenCalledWith([
      {
        answer: '',
        hasError: false,
        mandatory: false,
        question: 'What is the capital of England?',
        questionId: 2,
        managementHeader: 'England Capital',
      },
      {
        answer: '',
        hasError: false,
        mandatory: false,
        question: 'Who is the best football team?',
        questionId: 3,
        managementHeader: 'Who is the best football team?',
      },
    ]);
  });

  it('should show display error message for mandatory fields if not answered', () => {
    const updatedMockProps = {
      ...mockProps,
      validateQuestions: true,
      purchaseOrder: {
        answer: '',
        hasError: true,
        mandatory: true,
      },
      customerRef: {
        answer: '',
        hasError: true,
        mandatory: true,
      },
    };
    const { queryAllByText } = render(<EmployeeQuestions {...updatedMockProps} />);
    const errorMessage = queryAllByText('This field is required.');
    expect(errorMessage[0]).toBeInTheDocument();
  });

  it('should show error message if character length is greater than 50', () => {
    const updatedMockProps = {
      ...mockProps,
      validateQuestions: true,
      userDefinedQuestions: [
        {
          answer:
            'Employee Question Unit Test Employee Question Unit Test Employee Question Unit Test',
          question: 'What is the capital of England?',
          questionId: 2,
          mandatory: true,
          hasError: true,
        },
        {
          answer:
            'Employee Question Unit Test Employee Question Unit Test Employee Question Unit Test',
          question: 'Who is the best football team?',
          questionId: 3,
          mandatory: true,
          hasError: true,
        },
      ],
    };
    const { queryAllByText } = render(<EmployeeQuestions {...updatedMockProps} />);
    const errorMessage = queryAllByText('Please enter a valid answer.');
    expect(errorMessage[0]).toBeInTheDocument();
  });

  it('should show Error mesaage if a invalid character is used on Purchase Order Number field', () => {
    const updatedMockProps = {
      ...mockProps,
      validateQuestions: true,
      purchaseOrder: {
        question: 'Purchase Order Number',
        answer: '@',
        hasError: true,
        mandatory: true,
      },
    };
    const { queryAllByText } = render(<EmployeeQuestions {...updatedMockProps} />);
    const errorMessage = queryAllByText('Please enter a valid purchase order');
    expect(errorMessage[0]).toBeInTheDocument();
  });

  it('should show Error mesaage if a invalid character is used on Customer Reference field', () => {
    const updatedMockProps = {
      ...mockProps,
      purchaseOrderManagement: {
        questionId: 0,
        label: 'Purchase order number',
        mandatory: true,
        managementHeader: 'Purchase order number',
        active: true,
        location: 'R',
        managementInformationAnswer: {
          answerType: 'F',
          answers: [],
        },
      },
      validateQuestions: true,
      customerRef: {
        question: 'Customer Reference',
        answer: '@',
        hasError: true,
        mandatory: true,
      },
    };
    const { queryAllByText } = render(<EmployeeQuestions {...updatedMockProps} />);
    const errorMessage = queryAllByText(
      'Customer Reference may not exceed 24 characters and may only contain letters, numbers, spaces, hyphens or dots.'
    );
    expect(errorMessage[0]).toBeInTheDocument();
  });

  it('should call setPurchaseOrder 1 time', () => {
    const { getByTestId } = render(<EmployeeQuestions {...mockProps} />);
    const input = getByTestId('input-EmployeeQuestions-8');
    userEvent.type(input, '1');
    expect(mockProps.setPurchaseOrder).toHaveBeenCalledTimes(1);
  });

  it('should call setCustomerRef 1 time', () => {
    const { getByTestId } = render(<EmployeeQuestions {...mockProps} />);
    const input = getByTestId('input-EmployeeQuestions-9');
    userEvent.type(input, '1');
    expect(mockProps.setCustomerRef).toHaveBeenCalledTimes(1);
  });

  it('should have purchaseOrder.hasError to equal false', () => {
    mockProps.companyManagementDetails.customerReferenceManagement.location = 'R';
    render(<EmployeeQuestions {...mockProps} />);
    expect(mockProps.purchaseOrder).toEqual({
      answer: 'ABCE',
      dirty: true,
      hasError: false,
      mandatory: true,
      question: 'Purchase order number',
      managementHeader: 'Purchase order number',
    });
  });

  it('should have customerRef.hasError to equal false', () => {
    mockProps.companyManagementDetails.purchaseOrderManagement.location = 'R';
    render(<EmployeeQuestions {...mockProps} />);
    expect(mockProps.customerRef).toEqual({
      answer: 'ABCE',
      dirty: true,
      hasError: false,
      mandatory: true,
      question: 'Customer reference',
      managementHeader: 'Customer reference',
    });
  });

  it('should NOT show * next to Purchase order number question', () => {
    mockProps.companyManagementDetails.purchaseOrderManagement.mandatory = false;
    mockProps.companyManagementDetails.purchaseOrderManagement.location = 'B';
    const { getByTestId } = render(<EmployeeQuestions {...mockProps} />);
    const input = getByTestId('input-EmployeeQuestions-8');
    expect(input).toHaveAttribute('placeholder', 'Purchase order number');
  });

  it('should NOT show * next to Customer reference question', () => {
    mockProps.companyManagementDetails.customerReferenceManagement.mandatory = false;
    mockProps.companyManagementDetails.customerReferenceManagement.location = 'B';
    const { getByTestId } = render(<EmployeeQuestions {...mockProps} />);
    const input = getByTestId('input-EmployeeQuestions-9');
    expect(input).toHaveAttribute('placeholder', 'Customer reference');
  });
});
