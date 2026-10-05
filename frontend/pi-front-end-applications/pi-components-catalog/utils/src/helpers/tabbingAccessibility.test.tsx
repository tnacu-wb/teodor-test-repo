import { tabbingAccessibility } from './tabbingAccessibility';

describe('tabbingAccessibility', () => {
  let elements: HTMLElement[];
  let focusableRefs: { current: HTMLElement[] };

  beforeEach(() => {
    elements = [
      document.createElement('button'),
      document.createElement('button'),
      document.createElement('button'),
    ];

    elements.forEach((el) => {
      document.body.appendChild(el);
      jest.spyOn(el, 'focus');
    });

    focusableRefs = { current: elements };
  });

  afterEach(() => {
    document.body.innerHTML = '';
    jest.restoreAllMocks();
  });

  const createEvent = (key: string, shiftKey = false) => {
    return {
      key,
      shiftKey,
      preventDefault: jest.fn(),
    } as unknown as React.KeyboardEvent;
  };

  it('returns early if key is not Tab', () => {
    const event = createEvent('Enter');

    tabbingAccessibility(event, focusableRefs);

    elements.forEach((el) => {
      expect(el.focus).not.toHaveBeenCalled();
    });
  });

  it('returns early if no focusable elements', () => {
    const event = createEvent('Tab');
    focusableRefs.current = [];

    tabbingAccessibility(event, focusableRefs);

    expect(event.preventDefault).not.toHaveBeenCalled();
  });

  it('moves focus forward', () => {
    const event = createEvent('Tab');

    elements[0].focus();
    tabbingAccessibility(event, focusableRefs);

    expect(elements[1].focus).toHaveBeenCalled();
  });

  it('moves focus backward with Shift+Tab', () => {
    const event = createEvent('Tab', true);

    elements[1].focus();
    tabbingAccessibility(event, focusableRefs);

    expect(elements[0].focus).toHaveBeenCalled();
  });

  it('wraps to first element when tabbing forward from last', () => {
    const event = createEvent('Tab');

    elements[2].focus();
    tabbingAccessibility(event, focusableRefs);

    expect(elements[0].focus).toHaveBeenCalled();
  });

  it('wraps to last element when shift-tabbing from first', () => {
    const event = createEvent('Tab', true);

    elements[0].focus();
    tabbingAccessibility(event, focusableRefs);

    expect(elements[2].focus).toHaveBeenCalled();
  });

  it('calls preventDefault when isOpen is true', () => {
    const event = createEvent('Tab');

    elements[0].focus();
    tabbingAccessibility(event, focusableRefs, true);

    expect(event.preventDefault).toHaveBeenCalled();
  });

  it('does not call preventDefault when isOpen is false', () => {
    const event = createEvent('Tab');

    elements[0].focus();
    tabbingAccessibility(event, focusableRefs, false);

    expect(event.preventDefault).not.toHaveBeenCalled();
  });

  it('handles activeElement not in focusable list', () => {
    const event = createEvent('Tab');
    const outside = document.createElement('div');
    document.body.appendChild(outside);

    outside.focus(); // not in array

    tabbingAccessibility(event, focusableRefs);

    expect(elements[0].focus).toHaveBeenCalled();
  });
});
