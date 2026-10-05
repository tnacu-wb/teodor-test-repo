import {
  Box,
  BoxProps,
  Flex,
  Tab,
  TabList,
  TabPanel,
  TabPanels,
  Tabs,
  Text,
  useMediaQuery,
  useToken,
} from '@chakra-ui/react';
import { Button, Error, Input, Notification } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import {
  Dispatch,
  MouseEvent,
  RefObject,
  SetStateAction,
  TouchEvent,
  useEffect,
  useState,
} from 'react';

import { isCanvasEmpty } from '../common';
import { handleTouchEvent } from './common';

interface Props {
  tabIndex: number;
  setTabIndex: (index: number) => void;
  canvasRef: RefObject<HTMLCanvasElement>;
  typedCanvasRef: RefObject<HTMLCanvasElement>;
  showSignatureEmptyError: boolean;
  setShowSignatureEmptyError: Dispatch<SetStateAction<boolean>>;
}

const SignatureSection = ({
  tabIndex,
  setTabIndex,
  canvasRef,
  typedCanvasRef,
  setShowSignatureEmptyError,
  showSignatureEmptyError,
}: Props) => {
  const { t } = useTranslation();

  const [isDrawing, setIsDrawing] = useState(false);
  const [typedSignature, setTypedSignature] = useState('');
  const [lightGrey2] = useToken('colors', ['lightGrey2']);
  const [canvasWidth, setCanvasWidth] = useState(0);
  const [typedCanvasWidth, setTypedCanvasWidth] = useState(0);
  const [isLargerThanSm] = useMediaQuery('(min-width: 576px)');

  const getOffsets = (
    event: MouseEvent<HTMLCanvasElement>
  ): undefined | { ctx: CanvasRenderingContext2D; offsetX: number; offsetY: number } => {
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    const { offsetX, offsetY } = event.nativeEvent;
    if (ctx)
      return {
        ctx,
        offsetX,
        offsetY,
      };
  };

  const startDrawing = (event: MouseEvent<HTMLCanvasElement>) => {
    const offsets = getOffsets(event);
    if (offsets) {
      const { ctx, offsetX, offsetY } = offsets;
      ctx.beginPath();
      ctx.moveTo(offsetX, offsetY);
      setIsDrawing(true);
    }
  };

  const draw = (event: MouseEvent<HTMLCanvasElement>) => {
    if (!isDrawing) return;
    const offsets = getOffsets(event);
    if (offsets) {
      const { ctx, offsetX, offsetY } = offsets;
      ctx.lineTo(offsetX, offsetY);
      ctx.stroke();
    }
  };

  const handleTouchEnd = (event: TouchEvent<HTMLCanvasElement>) => {
    event.preventDefault();
    setIsDrawing(false);
  };

  const endDrawing = () => {
    setIsDrawing(false);
  };

  const resizeCanvas = (canvas: HTMLCanvasElement) => {
    const { clientWidth } = canvas.parentNode as HTMLElement;
    canvas.width = clientWidth;

    return canvas.width;
  };

  useEffect(() => {
    const canvas = canvasRef.current as HTMLCanvasElement;
    const typedCanvas = typedCanvasRef.current as HTMLCanvasElement;
    const activeCanvas = tabIndex === 0 ? canvas : typedCanvas;
    const id = setTimeout(() => {
      if (!isCanvasEmpty(activeCanvas) && showSignatureEmptyError)
        setShowSignatureEmptyError(false);
    }, 20);

    if (!canvas.width) setCanvasWidth(resizeCanvas(canvas));
    if (!typedCanvas.width) setTypedCanvasWidth(resizeCanvas(canvas));

    return () => {
      clearTimeout(id);
    };
  }, [tabIndex]);

  return (
    <>
      <Box {...titleWrapper}>
        <Text {...titleStyle}>{t('precheckin.addsignature')}</Text>
        <Text mb={4} mt={2}>
          {t('precheckin.addsignature.description')}
        </Text>
      </Box>

      <Tabs onChange={setTabIndex}>
        <TabList>
          <Tab
            data-testid="precheckin.signature.draw"
            _selected={{
              color: 'var(--chakra-colors-primary)',
              borderColor: 'var(--chakra-colors-primary)',
              fontWeight: 'semibold',
            }}
            {...tabStyles}
          >
            {t('precheckin.signature.draw')}
          </Tab>
          <Tab
            _selected={{
              color: 'var(--chakra-colors-primary)',
              borderColor: 'var(--chakra-colors-primary)',
              fontWeight: 'semibold',
            }}
            {...tabStyles}
            data-testid="precheckin.signature.type"
          >
            {t('precheckin.signature.type')}
          </Tab>
        </TabList>

        <TabPanels>
          <TabPanel px={0}>
            <Flex justifyContent="space-between" alignItems="center" mb={4}>
              <Text {...signTextTitleStyle} mb={0}>
                {t('precheckin.yoursignature.title')}
              </Text>
              <Button
                variant="tertiary"
                size={'sm'}
                onClick={() => clearSignature(canvasRef)}
                colorScheme="#511E62"
                data-testid="signature-draw-reset-btn"
              >
                {t('precheckin.details.reset')}
              </Button>
            </Flex>
            {showSignatureEmptyError && (
              <Box mb="xl" mt="xl">
                <Notification
                  variant="error"
                  status="error"
                  svg={<Error />}
                  prefixDataTestId={'reg-card-draw-signature-error-notification'}
                  showCloseButton
                  id="reg-card-draw-signature-error-notification"
                  onClick={() => setShowSignatureEmptyError(false)}
                  description={t('precheckin.addsignature.errormsg')}
                />
              </Box>
            )}
            <Box width="100%" height="150px">
              <canvas
                ref={canvasRef}
                style={{
                  ...canvasStyle,
                  border: `1px solid ${lightGrey2}`,
                }}
                data-testid="signature-canvas"
                onMouseDown={startDrawing}
                onMouseMove={draw}
                onMouseUp={endDrawing}
                onMouseLeave={endDrawing}
                onTouchStart={(event) =>
                  handleTouchEvent({ event, canvas: canvasRef.current, isDrawing, setIsDrawing })
                }
                onTouchMove={(event) =>
                  handleTouchEvent({ event, canvas: canvasRef.current, isDrawing, setIsDrawing })
                }
                onTouchEnd={handleTouchEnd}
                width={canvasWidth}
                height="150px"
              />
            </Box>
          </TabPanel>
          <TabPanel px={0}>
            <Flex justifyContent="space-between" alignItems="center" mb={4}>
              <Box width="50%">
                <Input
                  value={typedSignature}
                  styles={{
                    inputElementStyles: {
                      height: isLargerThanSm ? '3.5rem' : '2.5rem',
                      px: isLargerThanSm ? 'md' : 'sm',
                    },
                  }}
                  onChange={(value) => {
                    setTypedSignature(value);
                    handleInputChange(typedCanvasRef, value);
                  }}
                  label={t('precheckin.yoursignature.title')}
                  placeholderText={t('precheckin.yoursignature.title')}
                  name={'typed-signature'}
                />
              </Box>
              <Button
                variant="tertiary"
                size={'sm'}
                onClick={() => {
                  clearSignature(typedCanvasRef);
                  setTypedSignature('');
                }}
                colorScheme="#511E62"
                data-testid="signature-typed-reset-btn"
              >
                {t('precheckin.details.reset')}
              </Button>
            </Flex>
            <Text {...signTextTitleStyle} mb={4}>
              {t('precheckin.yoursignature.title')}
            </Text>
            {showSignatureEmptyError && (
              <Box mb="xl">
                <Notification
                  variant="error"
                  status="error"
                  svg={<Error />}
                  prefixDataTestId={'reg-card-typed-signature-error-notification'}
                  showCloseButton
                  id="reg-card-typed-signature-error-notification"
                  onClick={() => setShowSignatureEmptyError(false)}
                  description={t('precheckin.addsignature.errormsg')}
                />
              </Box>
            )}
            <Box width="100%" height="150px">
              <canvas
                ref={typedCanvasRef}
                data-testid="signature-typed-canvas"
                style={{
                  ...canvasStyle,
                  border: `1px solid ${lightGrey2}`,
                }}
                width={typedCanvasWidth}
                height={150}
              />
            </Box>
          </TabPanel>
        </TabPanels>
      </Tabs>
    </>
  );
};

export default SignatureSection;

const tabStyles = {
  w: { xs: '50%', sm: '50%', md: '30%' },
};

const signTextTitleStyle = {
  fontSize: 'md',
  fontWeight: 'semibold',
  color: 'darkGrey1',
};

const titleStyle = {
  fontSize: 'xl',
  fontWeight: 'bold',
  color: 'darkGrey1',
};

const titleWrapper = {
  pos: 'relative',
  mt: 'xl',
} as BoxProps;

const canvasStyle = {
  borderRadius: '8px',
};

export const clearSignature = (ref: RefObject<HTMLCanvasElement>) => {
  const canvas = ref.current;
  if (canvas) {
    const ctx = canvas.getContext('2d');
    ctx?.clearRect(0, 0, canvas.width, canvas.height);
  } else return;
};

export const handleInputChange = (ref: RefObject<HTMLCanvasElement>, value: string) => {
  const canvas = ref.current;
  if (canvas) {
    const ctx = canvas.getContext('2d');

    ctx?.clearRect(0, 0, canvas.width, canvas.height);

    if (ctx) {
      ctx.font = 'italic 24px "Times New Roman", Times, serif';
      ctx.fillStyle = 'black';
      ctx.fillText(value, 20, 35);
    }
  }
};
