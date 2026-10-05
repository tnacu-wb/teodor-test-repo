import { Dispatch, SetStateAction } from 'react';

export const handleTouchEvent = ({
  event,
  canvas,
  isDrawing,
  setIsDrawing,
}: {
  event: any;
  canvas: HTMLCanvasElement | null;
  isDrawing: boolean;
  setIsDrawing: Dispatch<SetStateAction<boolean>>;
}) => {
  event.preventDefault();
  if (!canvas) return;

  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  const { clientX, clientY } = event.touches[0];
  const rect = canvas.getBoundingClientRect();
  const offsetX = clientX - rect.left;
  const offsetY = clientY - rect.top;

  if (event.type === 'touchstart') {
    ctx.beginPath();
    ctx.moveTo(offsetX, offsetY);
    setIsDrawing(true);
  } else if (event.type === 'touchmove' && isDrawing) {
    ctx.lineTo(offsetX, offsetY);
    ctx.stroke();
  }
};
