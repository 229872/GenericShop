import { Navigate, Route, Routes } from "react-router-dom";
import AuthenticationPage from "../../pages/AuthenticationPage";
import { SessionDialogsActions } from "../../utils/types";
import RegisterPage from "../../pages/RegisterPage";
import NotFoundPage from "../../pages/NotFoundPage";
import ConfirmAccountPage from "../../pages/ConfirmAccountPage";
import ForgotPasswordPage from "../../pages/ForgotPasswordPage";
import { CSSProperties } from "react";
import ResetPasswordPage from "../../pages/ResetPasswordPage";
import SelfAccountPage from "../../pages/SelfAccountPage";

export const ROOT_PATH = '/'
export const HOME_PATH = '/home'
export const AUTH_PATH = '/auth'
export const FORGOT_PASSWORD_PATH = '/auth/forgot-password'
export const REGISTER_PATH = '/register'
export const REGISTER_CONFIRM_PATH = '/register/confirm'
export const RESET_PASSWORD_PATH = '/auth/reset-password'
export const SELF_ACCOUNT_PATH = '/self'

export default function Routing({showTokenExpiredDialogAfterTimeout, showExtendSessionDialogAfterTimeout, setLoading}: SessionDialogsActions) {
  const routesStyle: CSSProperties = { margin: '12vh 25vw' }


  return (
    <Routes>
      <Route path={ROOT_PATH} element={<Navigate replace to={HOME_PATH} />} />

      <Route path={AUTH_PATH} element={
        <AuthenticationPage
          showTokenExpiredDialogAfterTimeout={showTokenExpiredDialogAfterTimeout}
          showExtendSessionDialogAfterTimeout={showExtendSessionDialogAfterTimeout}
          setLoading={setLoading}
          style={{ margin: '20vh 25vw' }}
        />
      } />

      <Route path={FORGOT_PASSWORD_PATH} element={<ForgotPasswordPage setLoading={setLoading} style={routesStyle} />} />

      <Route path={REGISTER_PATH} element={<RegisterPage setLoading={setLoading} style={routesStyle} />} />

      <Route path={REGISTER_CONFIRM_PATH} element={<ConfirmAccountPage />} />

      <Route path={RESET_PASSWORD_PATH} element={<ResetPasswordPage setLoading={setLoading} style={routesStyle} />} />

      <Route path={SELF_ACCOUNT_PATH} element={<SelfAccountPage setLoading={setLoading} style={{ margin: '12vh auto', maxWidth: '85%'}} />} />
      
      <Route path='*' element={<NotFoundPage style={routesStyle} />} />
    </Routes>
  )
}