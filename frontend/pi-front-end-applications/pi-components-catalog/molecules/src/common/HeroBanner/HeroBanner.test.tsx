import '@testing-library/jest-dom';

import { render, screen } from '../../utils/test-utils';
import HeroBanner from './HeroBanner.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: (src: string) => (src.startsWith('/') ? src : `/${src}`),
}));

describe('HeroBanner', () => {
  const mockTitle = 'Hero Banner Image';
  const mockSubtitle = 'Test Banner Subtitle';
  const mockImage = '/test-image.jpg';

  it('should render the HeroBanner with title only', () => {
    render(<HeroBanner title={mockTitle} image={mockImage} />);
    expect(screen.getByTestId('HeroBanner-Image')).toBeInTheDocument();
    expect(screen.getByTestId('HeroBanner-Title')).toBeInTheDocument();
    expect(screen.queryByTestId('HeroBanner-Subtitle')).not.toBeInTheDocument();
  });

  it('should render the HeroBanner with title and subtitle', () => {
    render(<HeroBanner title={mockTitle} subtitle={mockSubtitle} image={mockImage} />);
    expect(screen.getByTestId('HeroBanner-Image')).toBeInTheDocument();
    expect(screen.getByTestId('HeroBanner-Title')).toBeInTheDocument();
    expect(screen.getByTestId('HeroBanner-Subtitle')).toBeInTheDocument();
  });

  it('should display the correct title', () => {
    render(<HeroBanner title={mockTitle} image={mockImage} />);
    expect(screen.getByTestId('HeroBanner-Title')).toHaveTextContent(mockTitle);
  });

  it('should display the correct subtitle when provided', () => {
    render(<HeroBanner title={mockTitle} subtitle={mockSubtitle} image={mockImage} />);
    expect(screen.getByTestId('HeroBanner-Subtitle')).toHaveTextContent(mockSubtitle);
  });

  it('should set the image src and alt attributes', () => {
    render(<HeroBanner title={mockTitle} image={mockImage} />);
    const image = screen.getByTestId('HeroBanner-Image');
    expect(image).toHaveAttribute('alt', mockTitle);
    expect(image).toHaveAttribute('src');
    expect(image.getAttribute('src')).toContain('test-image.jpg');
  });
});
