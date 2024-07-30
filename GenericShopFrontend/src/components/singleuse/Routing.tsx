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
import { Role } from "../../utils/types";
import ManageAccountsPage from "../../pages/ManageAccountsPage";
import ManageProductsPage from "../../pages/ManageProductsPage";
import ProductsPage from "../../pages/ProductsPage";

export const ROOT_PATH = '/'
export const HOME_PATH = '/home'
export const AUTH_PATH = '/auth'
export const FORGOT_PASSWORD_PATH = '/auth/forgot-password'
export const REGISTER_PATH = '/register'
export const REGISTER_CONFIRM_PATH = '/register/confirm'
export const RESET_PASSWORD_PATH = '/auth/reset-password'
export const SELF_ACCOUNT_PATH = '/self'
export const NOT_FOUND_PATH = '/not-found'
export const MANAGE_ACCOUNTS_PATH = '/manage/accounts'
export const MANAGE_PRODUCTS_PATH = '/manage/products'

type RoutingProps = {
  showTokenExpiredDialogAfterTimeout: () => void
  showExtendSessionDialogAfterTimeout: () => void
  setLoading: (value: boolean) => void
  style?: React.CSSProperties
  isAuthenticated: boolean
  setIsAuthenticated: (state: boolean) => void
  activeRole: Role
  setActiveRole: (role: Role) => void
}

export default function Routing({ showTokenExpiredDialogAfterTimeout, showExtendSessionDialogAfterTimeout, 
  setLoading, isAuthenticated, setIsAuthenticated, setActiveRole, activeRole } : RoutingProps) {

  const routesStyle: CSSProperties = { margin: '12vh 25vw' }

  return (
    <Routes>
      <Route path={ROOT_PATH} element={<Navigate replace to={HOME_PATH} />} />

      <Route path={NOT_FOUND_PATH} element={<NotFoundPage style={routesStyle} />} />

      <Route path={HOME_PATH} element={
        <ProductsPage
          setLoading={setLoading}
          style={{ margin: '8vh 8vw' }}
        />
      } />

      <Route path={AUTH_PATH} element={
        <ProtectedElement
          element={
            <AuthenticationPage
              showTokenExpiredDialogAfterTimeout={showTokenExpiredDialogAfterTimeout}
              showExtendSessionDialogAfterTimeout={showExtendSessionDialogAfterTimeout}
              setLoading={setLoading}
              style={{ margin: '20vh 25vw' }}
              setIsAuthenticated={setIsAuthenticated}
              setActiveRole={setActiveRole}
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

      <Route path={MANAGE_ACCOUNTS_PATH} element={
        <ProtectedElement
          element={
            <ManageAccountsPage
              setLoading={setLoading}
              style={{ margin: '10vh 10vw' }}
            />
          }
          shouldRender={isAuthenticated && (activeRole === Role.ADMIN)}
          redirect={NOT_FOUND_PATH}
        />
      } />

      <Route path={MANAGE_PRODUCTS_PATH} element={
        <ProtectedElement
          element={
            <ManageProductsPage
              setLoading={setLoading}
              style={{ margin: '10vh 10vw' }}
            />
          }
          shouldRender={isAuthenticated && (activeRole === Role.EMPLOYEE)}
          redirect={NOT_FOUND_PATH}
        />
      } />
      
      <Route path='*' element={<NotFoundPage style={{ margin: '12vh 15vw' }} />} />
    </Routes>
  )
}