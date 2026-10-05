import Link from 'next/link';
import { usePathname } from 'next/navigation';
import React from 'react';

interface Props extends React.AnchorHTMLAttributes<HTMLAnchorElement> {
  href: string;
  children: React.ReactNode;
  prefetch?: boolean;
}

const InnBLink = React.forwardRef<HTMLAnchorElement, Props>(({ href, children, ...rest }, ref) => {
  const pathname = usePathname();

  const isNewPath = (path: string) => {
    return path.includes('en-gb') || path.includes('de-de');
  };

  const isAnchor = () => {
    const pagePath = isNewPath(pathname ?? '');
    const linkPath = isNewPath(href ?? '');

    return pagePath !== linkPath;
  };

  if (isAnchor()) {
    return (
      <a data-type="anchor" {...rest} href={href ?? '/'} ref={ref}>
        {children}
      </a>
    );
  }

  return (
    <Link {...rest} href={href ?? '/'} ref={ref}>
      {children}
    </Link>
  );
});

InnBLink.displayName = 'InnBLink';

export default InnBLink;
