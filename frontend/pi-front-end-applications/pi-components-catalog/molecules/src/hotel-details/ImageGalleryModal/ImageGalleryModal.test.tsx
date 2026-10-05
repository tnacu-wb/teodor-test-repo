import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import ImageGalleryModal from './ImageGalleryModal.component';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => {
    return {
      t: (key: string) => {
        switch (key) {
          case 'hdp.imageGallery.modal.back':
            return 'Back';
          default:
            return key;
        }
      },
    };
  },
}));

describe('ImageGalleryModal', () => {
  const onModalClose = jest.fn();

  it('renders modal when open', () => {
    const { getByTestId, getByText } = render(
      <ImageGalleryModal isModalOpen onModalClose={onModalClose} />
    );

    expect(getByTestId('ImageGalleryModal-Content')).toBeInTheDocument();
    expect(getByTestId('ImageGalleryModal-Header')).toBeInTheDocument();
    expect(getByTestId('ImageGalleryModal-Body')).toBeInTheDocument();
    expect(getByText('Back')).toBeInTheDocument();
  });

  it('does not render modal when closed', () => {
    const { queryByTestId } = render(
      <ImageGalleryModal isModalOpen={false} onModalClose={onModalClose} />
    );
    expect(queryByTestId('ImageGalleryModal-Content')).not.toBeInTheDocument();
  });

  it('calls onModalClose when Back is clicked', () => {
    const { getByText } = render(
      <ImageGalleryModal isModalOpen={true} onModalClose={onModalClose} />
    );
    const backButton = getByText('Back');
    fireEvent.click(backButton);
    expect(onModalClose).toHaveBeenCalled();
  });
});
