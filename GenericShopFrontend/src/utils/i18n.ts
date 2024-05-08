import i18n from "i18next";
import { initReactI18next } from "react-i18next";

const resources = {
  en: {
    translation: {
      'error': 'Unknown error occurred',

      'app.dialog.title': 'Session expired',
      'app.dialog.message': 'Session expired, please sign in again.',
      'app.dialog.action': 'OK',

      'nav.home': 'Home',
      'nav.login': 'Login',
      'nav.create_account': 'Create Account',
      'nav.language': 'Change language',

      'authentication.app.name': 'Internet shop',
      'authentication.app.please.login': 'Log In to continue',
      'authentication.login.not_valid': 'Login must starts with letter and then contains only letters and digits',
      'authentication.password.not_valid': 'Password not valid',
      'authentication.label.login': 'Login',
      'authentication.label.password': 'Password',
      'authentication.enter.login': 'Enter login',
      'authentication.enter.password': 'Enter password',
      'authentication.button.login': 'Log in',
      'authentication.toast.success': 'You have successfully logged in !',
      'authentication.forgot.password': 'Forgot password ?',

      // Backend
      'exception.auth.credentials': 'Credentials are incorrect'
    }
  },
  pl: {
    translation: {
      'error': 'Wystąpił nieznany błąd',

      'app.dialog.title': 'Sesja wygasła',
      'app.dialog.message': 'Sesja wygasła. Zaloguj się ponownie.',
      'app.dialog.action': 'OK',

      'nav.home': 'Strona Główna',
      'nav.login': 'Zaloguj się',
      'nav.create_account': 'Stwórz konto',
      'nav.language': 'Zmień język',

      'authentication.app.name': 'Sklep internetowy',
      'authentication.app.please.login': 'Zaloguj się aby kontynuować',
      'authentication.login.not_valid': 'Login musi zaczynać się literą i dalej zawierać tylko litery i cyfry',
      'authentication.password.not_valid': 'Hasło jest nieodpowiednie',
      'authentication.label.login': 'Login',
      'authentication.label.password': 'Hasło',
      'authentication.enter.login': 'Wprowadź login',
      'authentication.enter.password': 'Wprowadź hasło',
      'authentication.button.login': 'Zaloguj się',
      'authentication.toast.success': 'Zalogowałeś się pomyślnie',
      'authentication.forgot.password': 'Zapomniałeś hasła ?',

      // Backend
      'exception.auth.credentials': 'Nieprawidłowe dane logowania'
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