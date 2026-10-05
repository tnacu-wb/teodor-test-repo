import '@testing-library/jest-dom';
import React, { createRef, RefObject } from 'react';

import { render, fireEvent, userEvent, act } from '../../utils/test-utils';
import SignatureSection, { clearSignature, handleInputChange } from './SignatureSection';

const mockUseMediaQuery = jest.fn();

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMediaQuery: (...args: string[]) => mockUseMediaQuery(...args),
}));

const mockProps = {
  tabIndex: 0,
  setTabIndex: jest.fn(),
  canvasRef: createRef<HTMLCanvasElement>(),
  typedCanvasRef: createRef<HTMLCanvasElement>(),
  showSignatureEmptyError: true,
  setShowSignatureEmptyError: jest.fn(),
};

describe('Should render the SignatureSection', () => {
  beforeEach(() => {
    mockUseMediaQuery.mockReturnValue([true]);

    HTMLCanvasElement.prototype.getContext = jest.fn(() => ({
      beginPath: jest.fn(),
      moveTo: jest.fn(),
      lineTo: jest.fn(),
      stroke: jest.fn(),
      clearRect: jest.fn(),
      getImageData: jest.fn((x, y, width, height) => ({
        data: new Uint8ClampedArray(width * height * 4),
      })),
      fillText: jest.fn(),
      measureText: jest.fn(() => ({ width: 100 })),
    })) as any;
  });

  it('renders SignatureSection with tabs', () => {
    const { getByText } = render(<SignatureSection {...mockProps} />);
    expect(getByText('precheckin.addsignature')).toBeInTheDocument();
    expect(getByText('precheckin.signature.draw')).toBeInTheDocument();
    expect(getByText('precheckin.signature.type')).toBeInTheDocument();
  });

  it('switches to the "Type" tab', () => {
    const { getByText, getByTestId } = render(<SignatureSection {...mockProps} />);
    fireEvent.click(getByText('precheckin.signature.type'));
    expect(getByTestId('signature-canvas')).toBeInTheDocument();
  });

  it('drawing on the canvas', () => {
    const { getByText, getByTestId } = render(<SignatureSection {...mockProps} />);
    userEvent.click(getByText('precheckin.signature.draw'));
    const canvas = getByTestId('signature-canvas');
    fireEvent.mouseDown(canvas, { clientX: 10, clientY: 10 });
    fireEvent.mouseMove(canvas, { clientX: 20, clientY: 20 });
    fireEvent.mouseUp(canvas);
    fireEvent.touchStart(canvas, { touches: [{ clientX: 10, clientY: 10 }] });
    fireEvent.touchMove(canvas, { touches: [{ clientX: 20, clientY: 20 }] });
    fireEvent.touchEnd(canvas, { touches: [{ clientX: 20, clientY: 20 }] });
  });

  it('typing in the signature section', async () => {
    jest.useFakeTimers();
    const { getByText, getByPlaceholderText, getByTestId } = render(
      <SignatureSection {...mockProps} />
    );
    fireEvent.click(getByText('precheckin.signature.type'));
    fireEvent.click(getByText('precheckin.signature.type'));
    await act(async () => {
      jest.runAllTimers();
    });
    const input = getByPlaceholderText('precheckin.yoursignature.title');
    const value = 'John Doe';
    fireEvent.change(input, { target: { value } });
    expect(input).toHaveValue(value);
    const canvasElement = getByTestId('signature-typed-canvas');
    expect(canvasElement).toBeInTheDocument();
    expect(canvasElement.tagName).toBe('CANVAS');
    jest.useRealTimers();
  });

  it('does nothing if ref is null', () => {
    const ref: RefObject<HTMLCanvasElement> = { current: null };

    clearSignature(ref);
  });
  it('does nothing if ref is null', () => {
    const ref: RefObject<HTMLCanvasElement> = { current: null };

    handleInputChange(ref, 'test');
  });
  it('does nothing if ref is null', () => {
    const ref: RefObject<HTMLCanvasElement> = { current: document.createElement('canvas') };

    handleInputChange(ref, 'test');
  });
  it('close error notification', () => {
    const { getAllByLabelText } = render(<SignatureSection {...mockProps} />);
    const closeButton = getAllByLabelText('Close')[0];
    expect(closeButton).toBeInTheDocument();
    fireEvent.click(closeButton);
  });
});

describe('Should render the SignatureSection', () => {
  beforeEach(() => {
    Object.defineProperty(HTMLCanvasElement.prototype, 'getContext', {
      value: jest.fn().mockReturnValue({
        beginPath: jest.fn(),
        moveTo: jest.fn(),
        lineTo: jest.fn(),
        stroke: jest.fn(),
      }),
    });
  });

  it('starts drawing when mouse is pressed on canvas', () => {
    const { getByTestId } = render(<SignatureSection {...mockProps} />);
    const canvas = getByTestId('signature-canvas') as HTMLCanvasElement;
    fireEvent.mouseDown(canvas, { clientX: 10, clientY: 20 });

    expect(canvas.getContext('2d')?.beginPath).toHaveBeenCalled();
    expect(canvas.getContext('2d')?.moveTo).toHaveBeenCalled();
  });

  it('draws on canvas when mouse is moved', () => {
    const { getByTestId } = render(<SignatureSection {...mockProps} />);
    const canvas = getByTestId('signature-canvas') as HTMLCanvasElement;

    fireEvent.mouseDown(canvas, { clientX: 10, clientY: 20 });
    fireEvent.mouseMove(canvas, { clientX: 30, clientY: 40 });

    expect(canvas.getContext('2d')?.lineTo).toHaveBeenCalled();
    expect(canvas.getContext('2d')?.stroke).toHaveBeenCalled();
  });

  it('clears the canvas when the context is available', () => {
    const handleClearCanvas = (canvas: HTMLCanvasElement) => {
      const ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
      }
    };

    const { getByTestId } = render(<SignatureSection {...mockProps} />);
    const canvas = getByTestId('signature-canvas') as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');

    if (ctx) {
      ctx.clearRect = jest.fn();
      handleClearCanvas(canvas);
      expect(ctx.clearRect).toHaveBeenCalledWith(0, 0, canvas.width, canvas.height);
    }

    const resetButtonDraw = getByTestId('signature-draw-reset-btn') as HTMLElement;
    userEvent.click(resetButtonDraw);
    const resetButtonTyped = getByTestId('signature-typed-reset-btn') as HTMLElement;
    userEvent.click(resetButtonTyped);
  });
});

describe('Should render the SignatureSection', () => {
  beforeEach(() => {
    Object.defineProperty(HTMLCanvasElement.prototype, 'getContext', {
      value: jest.fn().mockReturnValue({
        beginPath: jest.fn(),
        moveTo: jest.fn(),
        lineTo: jest.fn(),
        stroke: jest.fn(),
      }),
    });
  });

  it('starts drawing when mouse is pressed on canvas', () => {
    const { getByTestId } = render(<SignatureSection {...mockProps} />);
    const canvas = getByTestId('signature-canvas') as HTMLCanvasElement;
    fireEvent.mouseDown(canvas, { clientX: 10, clientY: 20 });

    expect(canvas.getContext('2d')?.beginPath).toHaveBeenCalled();
    expect(canvas.getContext('2d')?.moveTo).toHaveBeenCalled();
  });

  it('draws on canvas when mouse is moved', () => {
    const { getByTestId } = render(<SignatureSection {...mockProps} />);
    const canvas = getByTestId('signature-canvas') as HTMLCanvasElement;

    fireEvent.mouseDown(canvas, { clientX: 10, clientY: 20 });
    fireEvent.mouseMove(canvas, { clientX: 30, clientY: 40 });

    expect(canvas.getContext('2d')?.lineTo).toHaveBeenCalled();
    expect(canvas.getContext('2d')?.stroke).toHaveBeenCalled();
  });

  it('clears the canvas when the context is available', () => {
    const handleClearCanvas = (canvas: HTMLCanvasElement) => {
      const ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
      }
    };

    const { getByTestId } = render(<SignatureSection {...mockProps} />);
    const canvas = getByTestId('signature-canvas') as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');

    if (ctx) {
      ctx.clearRect = jest.fn();
      handleClearCanvas(canvas);
      expect(ctx.clearRect).toHaveBeenCalledWith(0, 0, canvas.width, canvas.height);
    }
  });
});
describe('clearSignature', () => {
  it('clears the canvas when the ref is not null', () => {
    const canvas = document.createElement('canvas');
    const ref: RefObject<HTMLCanvasElement> = { current: canvas };
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.clearRect = jest.fn();
      clearSignature(ref);
      expect(ctx.clearRect).toHaveBeenCalledWith(0, 0, canvas.width, canvas.height);
    }
  });

  it('does nothing when the ref is null', () => {
    const ref: RefObject<HTMLCanvasElement> = { current: null };
    clearSignature(ref);
  });
});

describe('handleInputChange', () => {
  it('updates the canvas when the ref is not null', () => {
    const canvas = document.createElement('canvas');
    const ref: RefObject<HTMLCanvasElement> = { current: canvas };
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.clearRect = jest.fn();
      ctx.fillText = jest.fn();
      handleInputChange(ref, 'test');
      expect(ctx.clearRect).toHaveBeenCalledWith(0, 0, canvas.width, canvas.height);
      expect(ctx.fillText).toHaveBeenCalledWith('test', 20, 35);
    }
  });
});

describe('SignatureSection', () => {
  beforeEach(() => {
    const contextMap = new WeakMap();
    HTMLCanvasElement.prototype.getContext = jest.fn(function (this: HTMLCanvasElement) {
      if (!contextMap.has(this)) {
        contextMap.set(this, {
          beginPath: jest.fn(),
          moveTo: jest.fn(),
          lineTo: jest.fn(),
          stroke: jest.fn(),
          clearRect: jest.fn(),
          getImageData: jest.fn((x, y, width, height) => ({
            data: new Uint8ClampedArray(width * height * 4),
          })),
          fillText: jest.fn(),
          measureText: jest.fn(() => ({ width: 100 })),
        });
      }
      return contextMap.get(this);
    }) as any;
  });

  it('renders SignatureSection with tabs', () => {
    const { getByText } = render(<SignatureSection {...mockProps} />);
    expect(getByText('precheckin.addsignature')).toBeInTheDocument();
    expect(getByText('precheckin.signature.draw')).toBeInTheDocument();
    expect(getByText('precheckin.signature.type')).toBeInTheDocument();
  });

  it('switches to the "Type" tab', () => {
    const { getByText, getByTestId } = render(<SignatureSection {...mockProps} />);
    userEvent.click(getByText('precheckin.signature.type'));
    expect(getByTestId('signature-canvas')).toBeInTheDocument();
  });

  it('drawing on the canvas', () => {
    const { getByText, getByTestId } = render(<SignatureSection {...mockProps} />);
    userEvent.click(getByText('precheckin.signature.draw'));
    const canvas = getByTestId('signature-canvas');
    fireEvent.mouseDown(canvas, { clientX: 10, clientY: 10 });
    fireEvent.mouseMove(canvas, { clientX: 20, clientY: 20 });
    fireEvent.mouseUp(canvas);
    fireEvent.touchStart(canvas, { touches: [{ clientX: 10, clientY: 10 }] });
    fireEvent.touchMove(canvas, { touches: [{ clientX: 20, clientY: 20 }] });
    fireEvent.touchEnd(canvas, { touches: [{ clientX: 20, clientY: 20 }] });
  });

  it('typing in the signature section', async () => {
    jest.useFakeTimers();
    const { getByText, getByPlaceholderText, getByTestId } = render(
      <SignatureSection {...mockProps} />
    );
    fireEvent.click(getByText('precheckin.signature.type'));
    fireEvent.click(getByText('precheckin.signature.type'));
    await act(async () => {
      jest.runAllTimers();
    });
    const input = getByPlaceholderText('precheckin.yoursignature.title');
    const value = 'John Doe';
    fireEvent.change(input, { target: { value } });
    expect(input).toHaveValue(value);
    const canvasElement = getByTestId('signature-typed-canvas');
    expect(canvasElement).toBeInTheDocument();
    expect(canvasElement.tagName).toBe('CANVAS');
    jest.useRealTimers();
  });

  it('clears the canvas when the "Draw" tab reset button is clicked', () => {
    const { getByTestId } = render(<SignatureSection {...mockProps} />);
    const resetButton = getByTestId('signature-draw-reset-btn');
    fireEvent.click(resetButton);
  });

  it('clears the canvas when the "Type" tab reset button is clicked', () => {
    const { getByTestId } = render(<SignatureSection {...mockProps} />);
    const resetButton = getByTestId('signature-typed-reset-btn');
    fireEvent.click(resetButton);
  });

  it('clears the canvas when the ref is not null', () => {
    const canvas = document.createElement('canvas');
    const ref = { current: canvas };
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.clearRect = jest.fn();
      clearSignature(ref);
      expect(ctx.clearRect).toHaveBeenCalledWith(0, 0, canvas.width, canvas.height);
    }
  });

  it('does nothing when the ref is null', () => {
    const ref = { current: null };
    clearSignature(ref);
  });

  it('updates the canvas when the ref is not null', () => {
    const canvas = document.createElement('canvas');
    const ref = { current: canvas };
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.clearRect = jest.fn();
      ctx.fillText = jest.fn();
      handleInputChange(ref, 'test');
      expect(ctx.clearRect).toHaveBeenCalledWith(0, 0, canvas.width, canvas.height);
      expect(ctx.fillText).toHaveBeenCalledWith('test', 20, 35);
    }
  });
});

describe('Error handler', () => {
  it('switches to the "Type" tab', () => {
    const { getByText, getByTestId } = render(<SignatureSection {...mockProps} />);
    userEvent.click(getByText('precheckin.signature.type'));
    expect(getByTestId('signature-canvas')).toBeInTheDocument();
  });

  it('should close the notification on click on it', () => {
    const { getByTestId } = render(<SignatureSection {...mockProps} />);
    act(() => {
      userEvent.click(getByTestId('reg-card-typed-signature-error-notification-AlertDescription'));
    });
    expect(getByTestId('signature-typed-canvas')).toBeInTheDocument();
  });

  it('should display component if tabindex is 1', () => {
    const mockProps = {
      tabIndex: 1,
      setTabIndex: jest.fn(),
      canvasRef: createRef<HTMLCanvasElement>(),
      typedCanvasRef: createRef<HTMLCanvasElement>(),
      showSignatureEmptyError: true,
      setShowSignatureEmptyError: jest.fn(),
    };
    const { getByTestId } = render(<SignatureSection {...mockProps} />);
    act(() => {
      userEvent.click(getByTestId('reg-card-typed-signature-error-notification-AlertDescription'));
    });
    expect(getByTestId('signature-typed-canvas')).toBeInTheDocument();
  });
});
