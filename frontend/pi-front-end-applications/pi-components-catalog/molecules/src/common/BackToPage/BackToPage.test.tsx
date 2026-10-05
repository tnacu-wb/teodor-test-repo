import BackToPage from '.';
import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';

const mockedBackToPage = {
  linkText: 'Link Text',
  goBack: jest.fn(),
  hasNegativeMargin: false,
};

jest.mock('next/router', () => ({
  useRouter() {
    return {
      router: { locale: 'en' },
    };
  },
}));

describe('BackToPage ', () => {
  it('should render the Back To Page component', function () {
    const { getByTestId } = render(<BackToPage {...mockedBackToPage} />);
    expect(getByTestId('backToPage')).toBeVisible();
  });

  it('should display correct message when receive it', function () {
    const { getByText } = render(<BackToPage {...mockedBackToPage} />);
    expect(getByText(mockedBackToPage.linkText)).toBeVisible();
  });

  it('should be able to click the back to details text ', function () {
    const { getByTestId } = render(<BackToPage {...mockedBackToPage} />);
    const backText = getByTestId('backToPageContainer');
    fireEvent.click(backText);
  });

  it('should render the Back To Page component with negative margin and link empty string', function () {
    mockedBackToPage.hasNegativeMargin = true;
    mockedBackToPage.linkText = undefined;
    const { getByTestId } = render(<BackToPage {...mockedBackToPage} />);
    expect(getByTestId('backToPage')).toBeVisible();
  });

  it('should render the Back To Page component with negative margin undefined', function () {
    mockedBackToPage.hasNegativeMargin = undefined;
    const { getByTestId } = render(<BackToPage {...mockedBackToPage} />);
    expect(getByTestId('backToPage')).toBeVisible();
  });
});
