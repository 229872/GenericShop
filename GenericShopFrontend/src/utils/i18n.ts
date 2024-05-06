import i18n from "i18next";
import { initReactI18next } from "react-i18next";

const resources = {
  en: {
    translation: {
      'nav.home': 'Home',
      'nav.login': 'Login',
      'nav.create_account': 'Create Account',
      'nav.language': 'Change language'
    }
  },
  pl: {
    translation: {
      'nav.home': 'Strona Główna',
      'nav.login': 'Zaloguj się',
      'nav.create_account': 'Stwórz konto',
      'nav.language': 'Zmień język'
    }
  }
};

i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: "pl",
    interpolation: {
      escapeValue: false
    }
  });

  export default i18n;