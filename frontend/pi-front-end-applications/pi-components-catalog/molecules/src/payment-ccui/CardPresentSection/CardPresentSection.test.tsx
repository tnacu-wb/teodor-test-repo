import '@testing-library/jest-dom';
import { CardStatus, CcuiCardType } from '@whitbread-eos/api';

import { fireEvent, render } from '../../utils/test-utils';
import CardPresentSection from './CardPresentSection.component';

const mockSetValue = jest.fn().mockImplementation();

const data = {
  value: '',
  setValue: mockSetValue,
  disabledOption: '',
  cardType: '',
};

describe('Card Present Section ', () => {
  it('should render the component with default props', () => {
    const { getByTestId } = render(<CardPresentSection {...data} />);
    expect(getByTestId('cardStatusSection')).toBeInTheDocument();
  });
  it('by default radio buttons are not checked ', () => {
    const { getByRole, getAllByRole, getByText } = render(<CardPresentSection {...data} />);
    const radioButtonCP = getByRole('radio', { name: 'ccui.cardPresentStatus.cardPresent' });
    const radioButtonCNP = getByRole('radio', { name: 'ccui.cardPresentStatus.CNP' });

    expect(getByText('ccui.cardPresentStatus.cardPresent')).toBeInTheDocument();
    expect(getByText('ccui.cardPresentStatus.CNP')).toBeInTheDocument();
    expect(getAllByRole('radio')).toHaveLength(2);
    expect(radioButtonCP).not.toBeChecked();
    expect(radioButtonCNP).not.toBeChecked();
  });
  it('should check if the radio button is checked', () => {
    const { getAllByRole } = render(<CardPresentSection {...data} />);

    const radioButtonCP = getAllByRole('radio')[0];
    const radioButtonCNP = getAllByRole('radio')[1];

    fireEvent.click(radioButtonCP);
    expect(mockSetValue).toHaveBeenCalledWith(CardStatus.CARD_PRESENT);
    fireEvent.click(radioButtonCNP);
    expect(mockSetValue).toHaveBeenCalledWith(CardStatus.CARD_NOT_PRESENT);
  });

  it('should render the component with net piba card', () => {
    data.value = CardStatus.CARD_NOT_PRESENT;
    data.cardType = CcuiCardType.NEW_PIBA;
    const { getByTestId, getByText } = render(<CardPresentSection {...data} />);
    expect(getByTestId('cardStatusSection')).toBeInTheDocument();
    expect(getByText('ccui.passwordCheckOverlay.okButton')).toBeInTheDocument();
  });
});
