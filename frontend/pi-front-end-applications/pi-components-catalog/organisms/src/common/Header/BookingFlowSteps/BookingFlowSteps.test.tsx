import { useMediaQuery } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { FT_PI_BOOKING_STEPS_WITH_TITLE } from '@whitbread-eos/api';
import * as utils from '@whitbread-eos/utils';

import BookingFlowSteps from './BookingFlowSteps.component';

jest.mock('@chakra-ui/react', () => {
  const actual = jest.requireActual('@chakra-ui/react');
  return {
    ...actual,
    useMediaQuery: jest.fn(),
  };
});

const steps = [
  { id: 1, title: 'Step One' },
  { id: 2, title: 'Step Two' },
  { id: 3, title: 'Step Three' },
];
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(),
}));
describe('BookingFlowSteps', () => {
  beforeEach(() => {
    // Set the feature toggle ON for all tests by default
    (utils.useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_BOOKING_STEPS_WITH_TITLE]: true,
    });
    (useMediaQuery as jest.Mock).mockReturnValue([false]);
    jest.clearAllMocks();
  });

  it('should render a progress indicator', () => {
    const { getByTestId } = render(<BookingFlowSteps steps={steps} activeStep={2} />);
    getByTestId('progress-indicator-wrapper');
    expect(getByTestId('divider-2')).toHaveStyle('marginBottom: lg');
  });
  it('should render progress indicator and tick icon should be present for first step', () => {
    const { queryAllByTestId } = render(<BookingFlowSteps steps={steps} activeStep={2} />);
    queryAllByTestId('progress-indicator-icon');
  });
  it('should change the marginBottom style on medium resolution if the title is not defined', () => {
    const { getByTestId } = render(
      <BookingFlowSteps
        steps={[
          { id: 1, title: 'Step One' },
          { id: 2, title: 'Step Two' },
          { id: 3, title: 'Step Three' },
        ]}
        activeStep={1}
      />
    );
    expect(getByTestId('divider-2')).toHaveStyle('marginBottom: ');
  });

  it('shows step titles when FT_PI_BOOKING_STEPS_WITH_TITLE is ON', () => {
    render(<BookingFlowSteps steps={steps} activeStep={2} />);
    expect(screen.getAllByTestId('progress-indicator-title').length).toBeGreaterThan(0);
    expect(screen.getByText('Step One')).toBeInTheDocument();
    expect(screen.getByText('Step Two')).toBeInTheDocument();
  });

  it('renders active-step-summary when isMobileView=true and feature toggle is true', () => {
    (useMediaQuery as jest.Mock).mockReturnValue([true]);
    render(<BookingFlowSteps steps={steps} activeStep={2} />);
    expect(screen.getByTestId('active-step-summary')).toBeInTheDocument();
  });

  it('does NOT render active-step-summary when isMobileView=false', () => {
    render(<BookingFlowSteps steps={steps} activeStep={2} />);
    expect(screen.queryByTestId('active-step-summary')).not.toBeInTheDocument();
  });

  it('renders active-step-summary and summary for active step on mobile with feature toggle ON', () => {
    (useMediaQuery as jest.Mock).mockReturnValue([true]);
    (utils.useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_BOOKING_STEPS_WITH_TITLE]: true,
    });
    render(<BookingFlowSteps steps={steps} activeStep={2} />);
    expect(screen.getByTestId('active-step-summary')).toBeInTheDocument();
    expect(screen.getByText('bookingflow.step.now.label')).toBeInTheDocument();
  });

  it('does NOT render active-step-summary when feature toggle is false', () => {
    (utils.useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_BOOKING_STEPS_WITH_TITLE]: false,
    });
    render(<BookingFlowSteps steps={steps} activeStep={2} />);
    expect(screen.queryByTestId('active-step-summary')).not.toBeInTheDocument();
  });

  it('does not show step titles when FT_PI_BOOKING_STEPS_WITH_TITLE is OFF', () => {
    (utils.useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_BOOKING_STEPS_WITH_TITLE]: false,
    });
    render(<BookingFlowSteps steps={steps} activeStep={2} />);
    expect(screen.queryByTestId('progress-indicator-title')).not.toBeInTheDocument();
    expect(screen.queryByText('Step One')).not.toBeInTheDocument();
    expect(screen.queryByText('Step Two')).not.toBeInTheDocument();
  });
});
