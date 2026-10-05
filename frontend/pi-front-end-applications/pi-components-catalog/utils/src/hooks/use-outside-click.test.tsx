import { fireEvent } from '@testing-library/dom';
import { renderHook, act } from '@testing-library/react';

import useOutsideClick from './use-outside-click';

describe('useOutsideClick', () => {
  it('should initialize with isOpen as false', () => {
    const { result } = renderHook(() => useOutsideClick());
    expect(result.current.isOpen).toBe(false);
  });

  it('should set isOpen to true when setIsOpen is called', async () => {
    const { result } = renderHook(() => useOutsideClick());

    await act(async () => {
      result.current.setIsOpen(true);
    });

    expect(result.current.isOpen).toBe(true);
  });

  it('should close when clicking outside the element and icon', async () => {
    const { result } = renderHook(() => useOutsideClick());
    const element = document.createElement('div');
    const icon = document.createElement('img');
    const outsideElement = document.createElement('div');

    document.body.appendChild(element);
    document.body.appendChild(icon);
    document.body.appendChild(outsideElement);

    await act(async () => {
      result.current.elementRef.current = element;
      result.current.iconRef.current = icon;
      result.current.setIsOpen(true);
    });

    expect(result.current.isOpen).toBe(true);

    await act(async () => {
      fireEvent.mouseUp(outsideElement);
    });

    expect(result.current.isOpen).toBe(false);

    document.body.removeChild(element);
    document.body.removeChild(icon);
    document.body.removeChild(outsideElement);
  });

  it('should not close when clicking inside the element or icon', async () => {
    const { result } = renderHook(() => useOutsideClick());
    const element = document.createElement('div');
    const icon = document.createElement('img');

    document.body.appendChild(element);
    document.body.appendChild(icon);

    await act(async () => {
      result.current.elementRef.current = element;
      result.current.iconRef.current = icon;
      result.current.setIsOpen(true);
    });

    expect(result.current.isOpen).toBe(true);

    await act(async () => {
      fireEvent.mouseUp(element);
    });

    expect(result.current.isOpen).toBe(true);

    await act(async () => {
      fireEvent.mouseUp(icon);
    });

    expect(result.current.isOpen).toBe(true);

    document.body.removeChild(element);
    document.body.removeChild(icon);
  });
});
