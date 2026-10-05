import { Box } from '@chakra-ui/react';
import { clientLogger, getClientDefaultSessionTracing } from '@whitbread-eos/utils';
import { withTranslation } from 'next-i18next';
import React, { ErrorInfo } from 'react';

import { Alert } from '../../assets/icons';
import Notification from '../Notification';
import StaticFooter from './StaticFooter';
import StaticHeader from './StaticHeader';

interface Props {
  onError?: (err: Error) => void;
  t: (x: string, y?: { [key: string]: string }) => string;
  isFooterBoundary?: boolean;
  isHeaderBoundary?: boolean;
  noContentBoundary?: boolean;
  errorMessage?: string;
  children?: React.ReactNode;
}

interface State {
  error?: Error;
}

class ErrorBoundary extends React.Component<any, any> {
  static readonly defaultProps = {
    isFooterBoundary: false,
    isHeaderBoundary: false,
    noContentBoundary: false,
  };

  state: State = { error: undefined };
  // eslint-disable-next-line no-console
  onError = console.error;
  t: (x: string, y?: { [key: string]: string }) => string;
  isFooterBoundary?: boolean;
  isHeaderBoundary?: boolean;
  noContentBoundary?: boolean;
  errorMessage: string;

  constructor(props: Props) {
    super(props);
    this.onError = this.onError ?? props.onError;
    this.t = props.t;
    this.isFooterBoundary = props.isFooterBoundary ?? false;
    this.isHeaderBoundary = props.isHeaderBoundary ?? false;
    this.noContentBoundary = props.noContentBoundary ?? false;
    this.errorMessage = props.errorMessage ?? this.t('errors.sorry');
  }

  static getDerivedStateFromError(error: Error) {
    return { error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    const sessionTracing = getClientDefaultSessionTracing();

    const errorDetails = {
      label: 'ERROR_BOUNDARY',
      err: error.message,
      errorInfo: errorInfo,
      sessionId: sessionTracing['WB-SESSION-ID'] as string,
    };
    if (typeof window !== 'undefined' && typeof CustomEvent !== 'undefined') {
      window.dispatchEvent(new CustomEvent('error_boundary_triggered', { detail: errorDetails }));
    }
    clientLogger.error(errorDetails);
  }

  componentDidUpdate(prevProps: Props, prevState: State) {
    if (this.state.error && !prevState.error) {
      this.onError(this.state.error);
    }
  }

  render() {
    if (this.state.error) {
      if (this.isFooterBoundary) {
        return <StaticFooter />;
      }
      if (this.isHeaderBoundary) {
        return <StaticHeader />;
      }
      if (this.noContentBoundary) {
        return null;
      }
      return (
        <Box mt="lg">
          <Notification
            status="warning"
            description={this.errorMessage}
            variant="alert"
            maxW="full"
            svg={<Alert />}
          />
        </Box>
      );
    }

    return this.props.children;
  }
}

export default withTranslation(['common'])(ErrorBoundary);
