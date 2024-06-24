export const camelCaseToWords = (str: string) => {
  return str
    .replace(/([a-z0-9])([A-Z])/g, '$1 $2')
    .replace(/^./, function (str) {
      return str.toUpperCase();
    });
}