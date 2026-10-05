// framer-motion v6 types predate React 19 and the `mode` prop (added in v7).
// React 19 removed implicit `children` from React.FC, so AnimatePresenceProps
// needs explicit `children` and `mode` to satisfy the compiler.
import 'framer-motion';

declare module '*.svg' {
  const content: any;
  export default content;
}

declare module 'framer-motion' {
  interface AnimatePresenceProps {
    children?: React.ReactNode;
    mode?: 'sync' | 'wait' | 'popLayout';
  }
}
