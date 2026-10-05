export function isObjectNotEmpty(obj: any) {
  for (const property in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, property) && obj[property] !== '') {
      return true;
    }
  }
  return false;
}
