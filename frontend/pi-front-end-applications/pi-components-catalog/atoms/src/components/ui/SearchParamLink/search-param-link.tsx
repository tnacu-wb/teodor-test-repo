import Link from 'next/link';

type Props = {
  name: string;
  value?: string;
  children?: React.ReactNode;
  clear?: boolean | string[];
  toggle?: boolean;
  searchParams: URLSearchParams;
} & React.AnchorHTMLAttributes<HTMLAnchorElement>;

export const SEARCH_PARAM_LINK_ACTIVE = '1';

export function SearchParamLink({
  name,
  value = '',
  children,
  clear = false,
  toggle = false,
  searchParams,
  ...props
}: Props) {
  const getInitialSearchParams = () => {
    if (clear === true) {
      return new URLSearchParams();
    }

    if (Array.isArray(clear)) {
      const params = new URLSearchParams(searchParams);
      clear.forEach((searchParam) => params.delete(searchParam));
      return params;
    }

    return new URLSearchParams(searchParams);
  };

  const modifySearchParams = (params: URLSearchParams) => {
    if (toggle) {
      if (params.get(name)) {
        params.delete(name);
      } else {
        params.set(name, SEARCH_PARAM_LINK_ACTIVE);
      }

      return;
    }

    params.set(name, value);
  };

  const params = getInitialSearchParams();
  modifySearchParams(params);

  return (
    <Link href={`?${params}`} prefetch={false} scroll={false} replace={true} {...props}>
      {children}
    </Link>
  );
}
