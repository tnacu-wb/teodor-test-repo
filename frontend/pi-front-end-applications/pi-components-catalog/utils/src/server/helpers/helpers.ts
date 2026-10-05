type AnyFunc<A extends any[], T> = (...args: A) => Promise<T>;

export function cachePromise<A extends any[], T>(fn: AnyFunc<A, T>): AnyFunc<A, T> {
  const cache = new Map<string, Promise<T>>();

  return (...args: A): Promise<T> => {
    const key = JSON.stringify(args);

    if (!cache.has(key)) {
      const promise = fn(...args);
      cache.set(key, promise);
    }

    return cache.get(key)!;
  };
}

export { getChannelByToken } from '../edge';
