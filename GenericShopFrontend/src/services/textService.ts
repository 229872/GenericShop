export const camelCaseToWords = (str: string) => {
  return str
    .replace(/([a-z0-9])([A-Z])/g, '$1 $2') // Add spaces between camel case words
    .replace(/^./, function (str) { // Capitalize the first letter of the entire string
      return str.toUpperCase();
    })
    .replace(/\s+./g, function (str) { // Lowercase the first letter of each word after the first
      return str.toLowerCase();
    });
}

export const camelCaseToWordsStartingWillLowerCase = (str: string) => {
  return str
    .replace(/([a-z0-9])([A-Z])/g, '$1 $2') // Add spaces between camel case words
    .replace(/^./, function (str) { // Capitalize the first letter of the entire string
      return str.toLowerCase();
    })
    .replace(/\s+./g, function (str) { // Lowercase the first letter of each word after the first
      return str.toLowerCase();
    });
}