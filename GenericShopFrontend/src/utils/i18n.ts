import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from 'i18next-browser-languagedetector';
import { environment } from "./constants";

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

      'change_email.not_valid.wrong_format': 'Email is in wrong format',
      'change_email.title': 'Change your email',
      'change_email.label.new_email': 'New email',
      'change_email.enter.email': 'Enter new email',
      'change_email.back': 'Back',
      'change_email.reset': 'Reset',
      'change_email.submit': 'Submit',

      'change_password.current_password.not_valid': 'Current password is incorrect',
      'change_password.new_password.not_valid': 'New password is in wrong format',
      'change_password.repeat_password.not_valid': 'The repeated password is different',
      'change_password.new_password_same_as_current': 'Your new password can\'t be same as current one',
      'change_password.title': 'Change your password',
      'change_password.label.current_password': 'Current password',
      'change_password.enter.current_password': 'Enter current password',
      'change_password.label.new_password': 'New password',
      'change_password.enter.new_password': 'Enter new password',
      'change_password.label.repeat_new_password': 'Repeat new password',
      'change_password.enter.repeat_new_password': 'Enter new password again',
      'change_password.back': 'Back',
      'change_password.submit': 'Submit',
      'change_password.reset': 'Reset',

      'edit_self.not_valid.first_name': 'First name must start with capital letter and contains only letters',
      'edit_self.not_valid.last_name': 'Last name must start with capital letter and contains only letters',
      'edit_self.not_valid.postal_code': 'Postal code must be in dd-ddd format',
      'edit_self.not_valid.country': 'Country must start with capital letter and contains only letters',
      'edit_self.not_valid.city': 'City must start with capital letter and contains only letters',
      'edit_self.not_valid.street': 'Street must start with capital letter and contains only letters',
      'edit_self.not_valid.house_number': 'House number must be positive integer number',
      'edit_self.title': 'Edit contact information',
      'edit_self.label.first_name': 'First name',
      'edit_self.label.last_name': 'Last name',
      'edit_self.label.postal_code': 'Postal code',
      'edit_self.label.country': 'Country',
      'edit_self.label.city': 'City',
      'edit_self.label.street': 'Street',
      'edit_self.label.house_number': 'House number',
      'edit_self.enter.first_name': 'Enter first name',
      'edit_self.enter.last_name': 'Enter last name',
      'edit_self.enter.postal_code': 'Enter postal code',
      'edit_self.enter.country': 'Enter country',
      'edit_self.enter.city': 'Enter city',
      'edit_self.enter.street': 'Enter street',
      'edit_self.enter.house_number': 'Enter house number',
      'edit_self.back': 'Back',
      'edit_self.reset': 'Reset',
      'edit_self.submit': 'Submit',

      'manage_accounts.column.id': 'ID',
      'manage_accounts.column.archival': 'Is archival?',
      'manage_accounts.column.login': 'Login',
      'manage_accounts.column.email': 'Email',
      'manage_accounts.column.first_name': 'First name',
      'manage_accounts.column.last_name': 'Last name',
      'manage_accounts.column.state': 'State',
      'manage_accounts.column.roles': 'Roles',
      'manage_accounts.column.edit': 'Edit',      
      'manage_accounts.column.actions': 'Actions',      
      'manage_accounts.button.edit': 'Edit',      
      'manage_accounts.button.block': 'Block',      
      'manage_accounts.button.unblock': 'Unblock',      
      'manage_accounts.button.archive': 'Archive',
      'manage_accounts.button.refresh': 'Refresh data',

      'nav.home': 'Home',
      'nav.login': 'Login',
      'nav.create_account': 'Create Account',
      'nav.language': 'Change language',
      'nav.language.polish': 'Polish',
      'nav.language.english': 'English',
      'nav.account': 'My account',
      'nav.logout': 'Logout',

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

      'confirm_account.success': 'Congratulations, your account is now active. Sign in to continue :)',

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
                                    To activate your account, check your email address and push the activation button.`,
      'register.step.3.content.2': 'We hope you will find what you are looking for and enjoy shopping with us :)',
      'register.step.3.content.3': 'If your account has already been verified, ',
      'register.step.3.link': 'you can log in now.',

      'forgot_password.email.not_valid' : 'Email is not valid',
      'forgot_password.title': 'Send reset password request',
      'forgot_password.enter.email' : 'Enter your email address associated with your account',
      'forgot_password.label.email' : 'Email',
      'forgot_password.submit' : 'Submit',
      'forgot_password.reset' : 'Clear form',
      'forgot_password.success': 'A password reset request has been sent. If the email address you provided is part of an account, an email will be sent to it with further instructions',

      'reset_password.password.not_valid': 'Password is in wrong format',
      'reset_password.repeatPassword.not_valid': 'Passwords are not identical',
      'reset_password_success': 'Your password is successfully changed',
      'reset_password.title': 'Reset your password',
      'reset_password.label.password': 'New password',
      'reset_password.enter.password': 'Enter new password',
      'reset_password.label.repeatPassword': 'Repeat new password',
      'reset_password.enter.repeatPassword': 'Enter new password again',
      'reset_password.submit': 'Submit',
      'reset_password.reset': 'Reset form',

      'self.account.information': 'Account information',
      'self.address.information': 'Address information',
      'self.account_state.information': 'Account state information',
      'self.first_name': 'First name',
      'self.last_name': 'Last name',
      'self.login': 'Login',
      'self.email': 'Email',
      'self.language': 'Language',
      'self.country': 'Country',
      'self.city': 'City',
      'self.street': 'Street',
      'self.house_number': 'House number',
      'self.postal_code': 'Postal code',
      'self.roles': 'Roles',
      'self.account_state': 'Account state',
      'self.archival': 'Archival',
      'self.archival_true': 'Yes',
      'self.archival_false': 'No',
      'self.last_successful_auth_time': 'Last successful authentication time',
      'self.last_unsuccessful_auth_time': 'Last unsuccessful authentication time',
      'self.unsuccessful_auth_attempts': 'Unsuccessful authentication attempts',
      'self.last_successful_auth_ip': 'Last successful authentication ip address',
      'self.last_unsuccessful_auth_ip': 'Last unsuccessful authentication ip address',
      'self.blockade_end_time': 'Account blockade end time',
      'self.button.edit': 'Edit',
      'self.button.change_email': 'Change email',
      'self.button.change_password': 'Change password',
      'self.tooltip.refresh': 'Refresh data',

      'table.no_content': 'No records found',
      'table.yes': 'Yes',
      'table.no': 'No',

      'notfound.title': 'Page not found',
      'notfound.description': 'Page you are trying to access does not exist...',
      'notfound.button': 'Home',
      'notfound.oops': 'Oops!',

      // Backend
      'exception.auth.credentials': 'Credentials are incorrect',
      // todo, change this message cause it can be caused by different use cases
      'exception.auth.token.expired': `Your account could not be confirmed because the time for this operation has most likely expired.
                                       Don't worry, try creating an account again`,
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

      'change_email.not_valid.wrong_format': 'Adres email jest w złym formacie',
      'change_email.title': 'Zmień swój adres email',
      'change_email.label.new_email': 'Nowy adres email',
      'change_email.enter.email': 'Wprowadź nowy adres email',
      'change_email.back': 'Wróć',
      'change_email.reset': 'Wyczyść',
      'change_email.submit': 'Zatwierdź',

      'change_password.current_password.not_valid': 'Obecne hasło jest nieprawidłowe',
      'change_password.new_password.not_valid': 'Nowe hasło jest w złym formacie',
      'change_password.repeat_password.not_valid': 'Powtórzone nowe hasło się różni',
      'change_password.new_password_same_as_current': 'Twoje nowe hasło nie może być takie samo jak obecne',
      'change_password.title': 'Zmień swoje hasło',
      'change_password.label.current_password': 'Obecne hasło',
      'change_password.enter.current_password': 'Wprowadź obecne hasło',
      'change_password.label.new_password': 'Nowe hasło',
      'change_password.enter.new_password': 'Wprowadź nowe hasło',
      'change_password.label.repeat_new_password': 'Powtórz nowe hasło',
      'change_password.enter.repeat_new_password': 'Wprowadź nowe hasło jeszcze raz',
      'change_password.back': 'Wróć',
      'change_password.reset': 'Wyczyść',
      'change_password.submit': 'Zatwierdź',

      'edit_self.not_valid.first_name': 'Imię musi zaczynać się literą i zawierać tylko litery',
      'edit_self.not_valid.last_name': 'Nazwisko musi zaczynać się literą i zawierać tylko litery',
      'edit_self.not_valid.postal_code': 'Kod pocztowy musi być w postaci: dd-ddd',
      'edit_self.not_valid.country': 'Kraj musi zaczynać się literą i zawierać tylko litery',
      'edit_self.not_valid.city': 'Miasto musi zaczynać się literą i zawierać tylko litery',
      'edit_self.not_valid.street': 'Ulica musi zaczynać się literą i zawierać tylko litery',
      'edit_self.not_valid.house_number': 'Numer domu musi być pozytywną liczbą',
      'edit_self.title': 'Edytuj dane kontaktowe',
      'edit_self.label.first_name': 'Imię',
      'edit_self.label.last_name': 'Nazwisko',
      'edit_self.label.postal_code': 'Kod pocztowy',
      'edit_self.label.country': 'Kraj',
      'edit_self.label.city': 'Miasto',
      'edit_self.label.street': 'Ulica',
      'edit_self.label.house_number': 'Number domu',
      'edit_self.enter.first_name': 'Wprowadź imię',
      'edit_self.enter.last_name': 'Wprowadź nazwisko',
      'edit_self.enter.postal_code': 'Wprowadź kod pocztowy',
      'edit_self.enter.country': 'Wprowadź kraj',
      'edit_self.enter.city': 'Wprowadź miasto',
      'edit_self.enter.street': 'Wprowadź ulicę',
      'edit_self.enter.house_number': 'Wprowadź numer domu',
      'edit_self.back': 'Wróć',
      'edit_self.reset': 'Przywróć domyślne',
      'edit_self.submit': 'Zatwierdź',

      'manage_accounts.column.id': 'ID',
      'manage_accounts.column.archival': 'Czy jest archiwalne?',
      'manage_accounts.column.login': 'Login',
      'manage_accounts.column.email': 'Email',
      'manage_accounts.column.first_name': 'Imię',
      'manage_accounts.column.last_name': 'Nazwisko',
      'manage_accounts.column.state': 'Stan',
      'manage_accounts.column.roles': 'Role',
      'manage_accounts.column.edit': 'Edytuj',      
      'manage_accounts.column.actions': 'Akcje',      
      'manage_accounts.button.edit': 'Edytuj',      
      'manage_accounts.button.block': 'Zablokuj',      
      'manage_accounts.button.unblock': 'Odblokuj',      
      'manage_accounts.button.archive': 'Archiwizuj',
      'manage_accounts.button.refresh': 'Odśwież dane',
      
      'nav.home': 'Strona Główna',
      'nav.login': 'Zaloguj się',
      'nav.create_account': 'Stwórz konto',
      'nav.language': 'Zmień język',
      'nav.language.polish': 'Polski',
      'nav.language.english': 'Angielski',
      'nav.account': 'Moje konto',
      'nav.logout': 'Wyloguj się',

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

      'confirm_account.success': 'Gratulacje, Twoje konto jest teraz aktywne. Zaloguj się, aby kontynuować :)',

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
      'register.step.2.error.house_number': 'Numer domu musi być pozytywną liczbą',
      'register.step.3.label': 'Gratulacje!',
      'register.step.3.content.1': `Twoje konto zostało pomyślnie utworzone ale jeszcze nie jest aktywne.
                                  Aby aktywować konto sprawdź podaną przez ciebie skrzynkę mailową i wciśnij przycisk aktywacji.`,
      'register.step.3.content.2': 'Mamy nadzieję że znajdziesz to, czego szukasz i będziesz cieszył/a się zakupami u nas :)',
      'register.step.3.content.3': 'Jeśli twoje konto zostało już zweryfikowane, ',
      'register.step.3.link': 'możesz się teraz zalogować.',

      'forgot_password.email.not_valid' : 'Email jest w niepoprawnej formie',
      'forgot_password.title': 'Wyślij prośbę o reset hasła',
      'forgot_password.enter.email' : 'Wprowadź swój email który jest powiązany z twoim kontem',
      'forgot_password.label.email' : 'Email',
      'forgot_password.submit' : 'Wyślij',
      'forgot_password.reset' : 'Wyczyść formularz',
      'forgot_password.success': 'Wysłano prośbę o reset hasła. Jeżeli podany przez ciebie adres email należy do konta, zostanie na niego wysłana wiadomość z dalszymi instrukcjami',

      'reset_password.password.not_valid': 'Hasło jest w złym formacie',
      'reset_password.repeatPassword.not_valid': 'Hasła się różnią',
      'reset_password_success': 'Twoje hasło zostało pomyślnie zmienione',
      'reset_password.title': 'Zresetuj swoje hasło',
      'reset_password.label.password': 'Nowe hasło',
      'reset_password.enter.password': 'Wprowadź nowe hasło',
      'reset_password.label.repeatPassword': 'Powtórz nowe hasło',
      'reset_password.enter.repeatPassword': 'Wprowadź nowe hasło jeszcze raz',
      'reset_password.submit': 'Zatwierdź',
      'reset_password.reset': 'Wyczyść formularz',

      'self.account.information': 'Informacje o koncie',
      'self.address.information': 'Informacje o adresie',
      'self.account_state.information': 'Informacje o stanie konta',
      'self.first_name': 'Imię',
      'self.last_name': 'Nazwisko',
      'self.login': 'Login',
      'self.email': 'Email',
      'self.language': 'Język',
      'self.country': 'Kraj',
      'self.city': 'Miasto',
      'self.street': 'Ulica',
      'self.house_number': 'Numer domu',
      'self.postal_code': 'Kod pocztowy',
      'self.roles': 'Role',
      'self.account_state': 'Status konta',
      'self.archival': 'Konto archiwalne',
      'self.archival_true': 'Tak',
      'self.archival_false': 'Nie',
      'self.last_successful_auth_time': 'Czas ostatniego prawidłowego uwierzytelnienia',
      'self.last_unsuccessful_auth_time': 'Czas ostatniego nieprawidłowego uwierzytelnienia',
      'self.unsuccessful_auth_attempts': 'Liczba nieudanych prób uwierzytelnienia',
      'self.last_successful_auth_ip': 'Adres ip ostatniego prawidłowego uwierzytelnienia',
      'self.last_unsuccessful_auth_ip': 'Adres ip ostatniego nieprawidłowego uwierzytelnienia',
      'self.blockade_end_time': 'Czas końca blokady konta',
      'self.button.edit': 'Edytuj',
      'self.button.change_email': 'Zmień adres email',
      'self.button.change_password': 'Zmień hasło',
      'self.tooltip.refresh': 'Odśwież dane',

      'table.no_content': 'Nie ma szukanych rekordów',
      'table.yes': 'Tak',
      'table.no': 'Nie',

      'notfound.title': 'Nie znaleziono strony',
      'notfound.description': 'Strona którą próbujesz odwiedzić nie istnieje...',
      'notfound.button': 'Wróć',
      'notfound.oops': 'Ups!',
      

      // Backend
      'exception.auth.credentials': 'Nieprawidłowe dane logowania',
      'exception.auth.token.expired': `Nie udało się potwierdzić twojego konta, ponieważ najprawdopodobniej czas na tę operację dobiegł końca.
                                       Nie martw się, spróbój założyć konto jeszcze raz`,
    }
  }
};

i18n
  .use(initReactI18next)
  .use(LanguageDetector)
  .init({
    resources,
    supportedLngs: environment.supportedLanguages,
    fallbackLng: environment.defaultLanguage,
    detection: { 
      order: ['localStorage'],
      lookupLocalStorage: environment.localeKey,
    },

    interpolation: {
      escapeValue: false
    }
  });



  export default i18n;