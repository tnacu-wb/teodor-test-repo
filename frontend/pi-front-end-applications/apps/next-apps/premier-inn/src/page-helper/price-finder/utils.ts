export const extractPriceFinderPath = (url: string): string => {
  // Match /price-finder/, /calendar/, or /kalender/ followed by a path
  const match = url.match(/\/(price-finder|calendar|kalender)\/(.+)/);
  if (match?.[2]) {
    let path = match[2].split('?')[0].split('#')[0];
    path = path.replace(/\/$/, '').replace(/\.html$/, '');
    return path;
  }
  return '';
};
