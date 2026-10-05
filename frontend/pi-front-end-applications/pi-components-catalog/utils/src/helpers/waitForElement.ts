export function waitForElement(selector: string, maxTimeOut = 5000) {
  const TIME_OUT = 50;
  let elapsedTime = 0;

  return new Promise((resolve, reject) => {
    const timer = setInterval(() => {
      elapsedTime += TIME_OUT;

      if (elapsedTime > maxTimeOut) {
        reject(new Error('Something went wrong when waiting for element'));
        clearInterval(timer);
      }

      const el = document.querySelector(selector);
      if (el) {
        clearInterval(timer);
        resolve(el);
      }
    }, TIME_OUT);
  });
}
