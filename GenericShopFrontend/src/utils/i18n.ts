import i18n from "i18next";
import { initReactI18next } from "react-i18next";

const resources = {
  en: {
    translation: {
      'app.name': 'Internet shop',
      'error': 'Unknown error occurred',

      'app.dialog.session_expired.title': 'Session expired',
      'app.dialog.session_expired.message': 'Session expired, please sign in again.',
      'app.dialog.session_expired.action': 'OK',
      'app.dialog.session_extend.title': 'Extend session',
      'app.dialog.session_extend.message': 'Do you want to extend session ?',
      'app.dialog.session_extend.action.yes': 'Yes',
      'app.dialog.session_extend.action.no': 'No',
      'app.dialog.session_extend.dialog.success': 'Session extended successfully',
      'app.dialog.session_extend.dialog.error': 'Couldn\'t extend session',

      'nav.home': 'Home',
      'nav.login': 'Login',
      'nav.create_account': 'Create Account',
      'nav.language': 'Change language',

      'authentication.please.login': 'Log In to continue',
      'authentication.login.not_valid': 'Login must starts with letter and then contains only letters and digits',
      'authentication.password.not_valid': 'Password not valid',
      'authentication.label.login': 'Login',
      'authentication.label.password': 'Password',
      'authentication.enter.login': 'Enter login',
      'authentication.enter.password': 'Enter password',
      'authentication.button.login': 'Log in',
      'authentication.toast.success': 'You have successfully logged in !',
      'authentication.forgot.password': 'Forgot password ?',

      'register.title': 'Create account',
      'register.step.1.title': 'Complete your account details',
      'register.step.2.title': 'Complete your personal details',
      'register.step.3.title': 'Confirm email address',
      'register.step.1.label.login': 'Login',
      'register.step.1.label.password': 'Password',
      'register.step.1.label.email': 'Email',
      'register.step.1.label.language': 'Language',
      'register.step.1.enter.login': 'Enter login',
      'register.step.1.enter.password': 'Enter password',
      'register.step.1.enter.email': 'Enter email',
      'register.step.1.enter.language': 'Choose language',
      'register.step.1.action.next_step': 'Next',
      'register.step.1.error.login': 'Login must starts with letter and then contains at least one lette or digit',
      'register.step.1.error.email': 'Email is not valid',
      'register.step.1.error.password': 'Password is not valid',
      'register.step.1.error.language': 'Language must be pl or en',
      'register.step.2.label.firstname': 'First name',
      'register.step.2.label.lastname': 'Last name',
      'register.step.2.label.postal_code': 'Postal code',
      'register.step.2.label.country': 'Country',
      'register.step.2.label.city': 'City',
      'register.step.2.label.street': 'Street',
      'register.step.2.label.house_number': 'House number',
      'register.step.2.enter.firstname': 'Enter first name',
      'register.step.2.enter.lastname': 'Enter last name',
      'register.step.2.enter.postal_code': 'Enter postal code',
      'register.step.2.enter.country': 'Enter country',
      'register.step.2.enter.city': 'Enter city',
      'register.step.2.enter.street': 'Enter street',
      'register.step.2.enter.house_number': 'Enter house number',
      'register.step.2.action.back': 'Back',
      'register.step.2.action.submit': 'Register',
      'register.step.2.error.firstname': 'First name must start with capital letter and contains only letters',
      'register.step.2.error.lastname': 'Last name must start with capital letter and contains only letters',
      'register.step.2.error.postal_code': 'Postal code must be in dd-ddd format',
      'register.step.2.error.country': 'Country must start with capital letter and contains only letters',
      'register.step.2.error.city': 'City must start with capital letter and contains only letters',
      'register.step.2.error.street': 'Street must start with capital letter and contains only letters',
      'register.step.2.error.house_number': 'House number must be positive integer number',
      'register.step.3.label': 'Congratulations',
      'register.step.3.content.1': `Your account has been successfully created but is not yet active.
                                    To activate your account, check your email address and go to the link we sent you.`,
      'register.step.3.content.2': 'We hope you will find what you are looking for and enjoy shopping with us :)',
      'register.step.3.content.3': 'If your account has already been verified, ',
      'register.step.3.link': 'you can log in now.',

      // Backend
      'exception.auth.credentials': 'Credentials are incorrect'
    }
  },
  pl: {
    translation: {
      'app.name': 'Sklep internetowy',
      'error': 'Wystąpił nieznany błąd',

      'app.dialog.session_expired.title': 'Sesja wygasła',
      'app.dialog.session_expired.message': 'Sesja wygasła. Zaloguj się ponownie.',
      'app.dialog.session_expired.action': 'OK',
      'app.dialog.session_extend.title': 'Przedłuż sesję',
      'app.dialog.session_extend.message': 'Czy chcesz przedłużyć sesję ?',
      'app.dialog.session_extend.action.yes': 'Tak',
      'app.dialog.session_extend.action.no': 'Nie',
      'app.dialog.session_extend.dialog.success': 'Przedłużono sesję z powodzeniem',
      'app.dialog.session_extend.dialog.error': 'Nie udało się przedłużyć sesji',

      'nav.home': 'Strona Główna',
      'nav.login': 'Zaloguj się',
      'nav.create_account': 'Stwórz konto',
      'nav.language': 'Zmień język',

      'authentication.please.login': 'Zaloguj się aby kontynuować',
      'authentication.login.not_valid': 'Login musi zaczynać się literą i dalej zawierać tylko litery i cyfry',
      'authentication.password.not_valid': 'Hasło jest nieodpowiednie',
      'authentication.label.login': 'Login',
      'authentication.label.password': 'Hasło',
      'authentication.enter.login': 'Wprowadź login',
      'authentication.enter.password': 'Wprowadź hasło',
      'authentication.button.login': 'Zaloguj się',
      'authentication.toast.success': 'Zalogowałeś się pomyślnie',
      'authentication.forgot.password': 'Zapomniałeś hasła ?',

      'register.title': 'Stwórz konto',
      'register.step.1.title': 'Uzupełnij dane konta',
      'register.step.2.title': 'Uzupełnij dane personalne',
      'register.step.3.title': 'Potwierdź adres email',
      'register.step.1.label.login': 'Login',
      'register.step.1.label.password': 'Hasło',
      'register.step.1.label.email': 'Email',
      'register.step.1.label.language': 'Język',
      'register.step.1.enter.login': 'Wprowadź login',
      'register.step.1.enter.password': 'Wprowadź password',
      'register.step.1.enter.email': 'Wprowadź email',
      'register.step.1.enter.language': 'Wybierz język',
      'register.step.1.action.next_step': 'Dalej',
      'register.step.1.error.login': 'Login musi zaczynać się literą i potem zawierać przynajmniej jedną literę lub cyfrę',
      'register.step.1.error.email': 'Email jest nieodpowiedni',
      'register.step.1.error.password': 'Hasło jest nieodpowiednie',
      'register.step.1.error.language': 'Język może przyjmować wartość pl albo en',
      'register.step.2.label.firstname': 'Imię',
      'register.step.2.label.lastname': 'Nazwisko',
      'register.step.2.label.postal_code': 'Kod pocztowy',
      'register.step.2.label.country': 'Kraj',
      'register.step.2.label.city': 'Miasto',
      'register.step.2.label.street': 'Ulica',
      'register.step.2.label.house_number': 'Numer domu',
      'register.step.2.enter.firstname': 'Wprowadź imię',
      'register.step.2.enter.lastname': 'Wprowadź nazwisko',
      'register.step.2.enter.postal_code': 'Wprowadź kod pocztowy',
      'register.step.2.enter.country': 'Wprowadź kraj',
      'register.step.2.enter.city': 'Wprowadź miasto',
      'register.step.2.enter.street': 'Wprowadź ulicę',
      'register.step.2.enter.house_number': 'Wprowadź numer domu',
      'register.step.2.action.back': 'Wróć',
      'register.step.2.action.submit': 'Zarejestruj się',
      'register.step.2.error.firstname': 'Imię musi zaczynać się literą i zawierać tylko litery',
      'register.step.2.error.lastname': 'Nazwisko musi zaczynać się literą i zawierać tylko litery',
      'register.step.2.error.postal_code': 'Kod pocztowy musi być w postaci: dd-ddd',
      'register.step.2.error.country': 'Kraj musi zaczynać się literą i zawierać tylko litery',
      'register.step.2.error.city': 'Miasto musi zaczynać się literą i zawierać tylko litery',
      'register.step.2.error.street': 'Ulica musi zaczynać się literą i zawierać tylko litery',
      'register.step.2.error.house_number': 'Number domu musi być pozytywną liczbą',
      'register.step.3.label': 'Gratulacje!',
      'register.step.3.content.1': `Twoje konto zostało pomyślnie utworzone ale jeszcze nie jest aktywne.
                                  Aby aktywować konto sprawdź podaną przez ciebie skrzynkę mailową i przejdź na podane tobie hiperłącze.`,
      'register.step.3.content.2': 'Mamy nadzieję że znajdziesz to, czego szukasz i będziesz cieszył/a się zakupami u nas :)',
      'register.step.3.content.3': 'Jeśli twoje konto zostało już zweryfikowane, ',
      'register.step.3.link': 'możesz się teraz zalogować.',
      

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