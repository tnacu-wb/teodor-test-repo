import { handleTouchEvent } from './common';

describe('handleTouchEvent', () => {
  let canvas: HTMLCanvasElement | null;
  let isDrawing: boolean;
  let setIsDrawing: jest.Mock;

  beforeEach(() => {
    canvas = document.createElement('canvas');
    isDrawing = false;
    setIsDrawing = jest.fn();
  });

  afterEach(() => {
    canvas = null;
    isDrawing = false;
    setIsDrawing.mockReset();
  });

  test('should handle touch start event', () => {
    const event = {
      preventDefault: jest.fn(),
      type: 'touchstart',
      touches: [{ clientX: 10, clientY: 20 }],
    };

    const ctx = {
      beginPath: jest.fn(),
      moveTo: jest.fn(),
    };
    if (!canvas) return;

    canvas.getContext = jest.fn().mockReturnValue(ctx);

    handleTouchEvent({ event, canvas, isDrawing, setIsDrawing });

    expect(event.preventDefault).toHaveBeenCalled();
    expect(canvas.getContext).toHaveBeenCalledWith('2d');
    expect(ctx.beginPath).toHaveBeenCalled();
    expect(ctx.moveTo).toHaveBeenCalledWith(10, 20);
    expect(setIsDrawing).toHaveBeenCalledWith(true);
  });

  test('should handle touch move event when drawing', () => {
    const event = {
      preventDefault: jest.fn(),
      type: 'touchmove',
      touches: [{ clientX: 30, clientY: 40 }],
    };

    const ctx = {
      lineTo: jest.fn(),
      stroke: jest.fn(),
    };
    if (!canvas) return;

    canvas.getContext = jest.fn().mockReturnValue(ctx);

    handleTouchEvent({ event, canvas, isDrawing: true, setIsDrawing });

    expect(event.preventDefault).toHaveBeenCalled();
    expect(canvas.getContext).toHaveBeenCalledWith('2d');
    expect(ctx.lineTo).toHaveBeenCalledWith(30, 40);
    expect(ctx.stroke).toHaveBeenCalled();
    expect(setIsDrawing).not.toHaveBeenCalled();
  });

  test('should not do anything if canvas is null', () => {
    const event = {
      preventDefault: jest.fn(),
      type: 'touchstart',
      touches: [{ clientX: 10, clientY: 20 }],
    };

    handleTouchEvent({ event, canvas: null, isDrawing, setIsDrawing });

    expect(event.preventDefault).toHaveBeenCalled();
  });

  test('should not do anything if context is null', () => {
    const event = {
      preventDefault: jest.fn(),
      type: 'touchstart',
      touches: [{ clientX: 10, clientY: 20 }],
    };
    if (!canvas) return;

    canvas.getContext = jest.fn().mockReturnValue(null);

    handleTouchEvent({ event, canvas, isDrawing, setIsDrawing });

    expect(event.preventDefault).toHaveBeenCalled();
    expect(canvas.getContext).toHaveBeenCalledWith('2d');
  });

  test('should handle touch move event', () => {
    const event = {
      preventDefault: jest.fn(),
      type: 'touchmove',
      touches: [{ clientX: 10, clientY: 20 }],
    };

    const ctx = {
      beginPath: jest.fn(),
      moveTo: jest.fn(),
    };

    if (!canvas) return;
    canvas.getContext = jest.fn().mockReturnValue(ctx);
    handleTouchEvent({ event, canvas, isDrawing, setIsDrawing });
    expect(event.preventDefault).toHaveBeenCalled();
  });
});
