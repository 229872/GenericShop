import { Navigate, Route, Routes } from "react-router-dom";
import AuthenticationPage from "../../pages/AuthenticationPage";
import RegisterPage from "../../pages/RegisterPage";
import NotFoundPage from "../../pages/NotFoundPage";
import ConfirmAccountPage from "../../pages/ConfirmAccountPage";
import ForgotPasswordPage from "../../pages/ForgotPasswordPage";
import { CSSProperties } from "react";
import ResetPasswordPage from "../../pages/ResetPasswordPage";
import SelfAccountPage from "../../pages/SelfAccountPage";
import ProtectedElement from "./ProtectedElement";

export const ROOT_PATH = '/'
export const HOME_PATH = '/home'
export const AUTH_PATH = '/auth'
export const FORGOT_PASSWORD_PATH = '/auth/forgot-password'
export const REGISTER_PATH = '/register'
export const REGISTER_CONFIRM_PATH = '/register/confirm'
export const RESET_PASSWORD_PATH = '/auth/reset-password'
export const SELF_ACCOUNT_PATH = '/self'
export const NOT_FOUND_PATH = '/not-found'

type RoutingProps = {
  showTokenExpiredDialogAfterTimeout: () => void
  showExtendSessionDialogAfterTimeout: () => void
  setLoading: (value: boolean) => void
  style?: React.CSSProperties
  isAuthenticated: boolean
  setIsAuthenticated: (state: boolean) => void
}

export default function Routing({ showTokenExpiredDialogAfterTimeout, showExtendSessionDialogAfterTimeout, 
  setLoading, isAuthenticated, setIsAuthenticated } : RoutingProps) {

  const routesStyle: CSSProperties = { margin: '12vh 25vw' }

  return (
    <Routes>
      <Route path={ROOT_PATH} element={<Navigate replace to={HOME_PATH} />} />

      <Route path={NOT_FOUND_PATH} element={<NotFoundPage style={routesStyle} />} />

      <Route path={AUTH_PATH} element={
        <ProtectedElement
          element={
            <AuthenticationPage
              showTokenExpiredDialogAfterTimeout={showTokenExpiredDialogAfterTimeout}
              showExtendSessionDialogAfterTimeout={showExtendSessionDialogAfterTimeout}
              setLoading={setLoading}
              style={{ margin: '20vh 25vw' }}
              setIsAuthenticated={setIsAuthenticated}
            />
          }
          shouldRender={!isAuthenticated}
          redirect={NOT_FOUND_PATH}
        />
      } />

      <Route path={FORGOT_PASSWORD_PATH} element={
        <ProtectedElement
          element={
            <ForgotPasswordPage
              setLoading={setLoading}
              style={routesStyle}
            />
          }
          shouldRender={!isAuthenticated}
          redirect={NOT_FOUND_PATH}
        />
      } />

      <Route path={REGISTER_PATH} element={
        <ProtectedElement 
          element={
            <RegisterPage
              setLoading={setLoading}
              style={routesStyle}
            />
          } 
          shouldRender={!isAuthenticated}
          redirect={NOT_FOUND_PATH}
        />
      } />

      <Route path={REGISTER_CONFIRM_PATH} element={
        <ProtectedElement
          element={
            <ConfirmAccountPage />
          }
          shouldRender={!isAuthenticated}
          redirect={NOT_FOUND_PATH}
        />
      } />

      <Route path={RESET_PASSWORD_PATH} element={
        <ProtectedElement
          element={
            <ResetPasswordPage 
              setLoading={setLoading}
              style={routesStyle}
            />
          }
          shouldRender={!isAuthenticated}
          redirect={NOT_FOUND_PATH}
        />
      } />

      <Route path={SELF_ACCOUNT_PATH} element={
        <ProtectedElement
          element={
            <SelfAccountPage 
              setLoading={setLoading}
              style={{ margin: '12vh auto', maxWidth: '85%'}} 
            />
          } 
          shouldRender={isAuthenticated}
          redirect={AUTH_PATH}
        />
      } />
      
      <Route path='*' element={<NotFoundPage style={routesStyle} />} />
    </Routes>
  )
}