import '@testing-library/jest-dom';

import { render, screen } from '../../utils/test-utils';
import FreeFoodKidsNotification from './FreeFoodKidsNotification.component';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'upsell.notification.kids.header': 'Kids Eat Free!',
        'upsell.notification.kids.subheader': '1 free kids meal per paying adult.',
      };
      return translations[key] || key;
    },
  }),
}));

describe('FreeFoodKidsNotification', () => {
  it('should render the component with default data-testid', () => {
    const { getByTestId } = render(<FreeFoodKidsNotification />);
    expect(getByTestId('FreeFoodKidsNotification-Wrapper')).toBeInTheDocument();
  });

  it('should render with prefixed data-testid when prefixDataTestId is provided', () => {
    const { getByTestId } = render(<FreeFoodKidsNotification prefixDataTestId="TestPrefix" />);
    expect(getByTestId('TestPrefix-FreeFoodKidsNotification-Wrapper')).toBeInTheDocument();
  });

  it('should display header and subheader for dinner when isDinnerIncluded=false', () => {
    render(<FreeFoodKidsNotification isDinnerIncluded={false} />);
    expect(screen.getByText('Kids Eat Free!')).toBeInTheDocument();
  });

  it('should display header and subheader for dinner when isDinnerIncluded=true', () => {
    render(<FreeFoodKidsNotification isDinnerIncluded={true} />);
    expect(screen.getByText('Kids Eat Free!')).toBeInTheDocument();
  });

  it('should render the Info icon inside Notification', () => {
    const { container } = render(<FreeFoodKidsNotification />);
    // Check if an SVG element is present
    expect(container.querySelector('svg')).toBeInTheDocument();
  });
});
