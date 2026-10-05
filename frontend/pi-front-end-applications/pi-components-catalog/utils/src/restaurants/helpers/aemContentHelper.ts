export const dropdrownManipulatorFn = (properties: string) => {
  return properties.split(',').map((value, index) => {
    return {
      label: value.trim().replace(/'/g, ''),
      id: index + 1,
    };
  });
};

export const replaceInputPlaceholder = (inputString: string, replaceValue: string | undefined) => {
  return inputString.replace(/\${restaurant}/g, replaceValue as string);
};
