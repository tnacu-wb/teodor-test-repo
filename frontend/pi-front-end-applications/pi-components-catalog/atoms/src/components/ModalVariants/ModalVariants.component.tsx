import type { ModalHeaderProps, PortalProps, StyleProps } from '@chakra-ui/react';
import { ReactElement, ReactNode } from 'react';

import {
  GalleryModal,
  DefaultModal,
  InfoModal,
  CookieModal,
  LoginModal,
  DataModal,
  RoomUpgradeModal,
  GalleryModalProps,
  DefaultModalProps,
  InfoModalProps,
  CookieModalProps,
  LoginModalProps,
  DataModalProps,
  CookieModalVariantProps,
  DefaultModalVariantProps,
  GalleryModalVariantProps,
  InfoModalVariantProps,
  LoginModalVariantProps,
  DataModalvariantProps,
  RoomUpgradeModalProps,
  type RoomUpgradeModalVariantProps,
} from './variants';

type VariantNames = 'default' | 'gallery' | 'info' | 'cookie' | 'login' | 'data' | 'upgrade';
type VariantProps =
  | DefaultModalVariantProps
  | GalleryModalVariantProps
  | InfoModalVariantProps
  | CookieModalVariantProps
  | LoginModalVariantProps
  | DataModalvariantProps
  | RoomUpgradeModalVariantProps;

interface Props {
  isOpen: boolean;
  onClose: () => void;
  closeOnOverlayClick?: boolean;
  children: ReactNode;
  dataTestId?: string;
  variant: VariantNames;
  variantProps?: VariantProps;
  hasAutoFocus?: boolean;
  headerStyles?: ModalHeaderProps;
  headerContentStyles?: StyleProps;
  contentContainerStyles?: StyleProps;
  overlayStyles?: StyleProps;
  updatedWidth?: {
    mobile?: string;
    xs?: string;
    sm?: string;
    md?: string;
    lg?: string;
    xl?: string;
  };
  isLoading?: boolean;
  landscapeModal?: {
    primary?: boolean;
    secondary?: boolean;
  };
  portalProps?: Pick<PortalProps, 'appendToParentPortal' | 'containerRef'>;
}

interface Variants {
  default: (props: DefaultModalProps) => ReactElement;
  gallery: (props: GalleryModalProps) => ReactElement;
  info: (props: InfoModalProps) => ReactElement;
  cookie: (props: CookieModalProps) => ReactElement;
  login: (props: LoginModalProps) => ReactElement;
  data: (props: DataModalProps) => ReactElement;
  upgrade: (props: RoomUpgradeModalProps) => ReactElement;
}

const variants: Variants = {
  default: DefaultModal,
  gallery: GalleryModal,
  info: InfoModal,
  cookie: CookieModal,
  login: LoginModal,
  data: DataModal,
  upgrade: RoomUpgradeModal,
};

export default function ModalVariants(props: Readonly<Props>) {
  const SelectedComponent = variants[props.variant];

  return <SelectedComponent {...props} />;
}
