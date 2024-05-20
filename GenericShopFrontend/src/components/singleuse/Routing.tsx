import { Navigate, Route, Routes } from "react-router-dom";
import AuthenticationPage from "../../pages/AuthenticationPage";
import { SessionDialogsActions } from "../../utils/types";
import RegisterPage from "../../pages/RegisterPage";
import NotFoundPage from "../../pages/NotFoundPage";
import ConfirmAccountPage from "../../pages/ConfirmAccountPage";
import ResetPasswordPage from "../../pages/ResetPasswordPage";
import { CSSProperties } from "react";

export default function Routing({showTokenExpiredDialogAfterTimeout, showExtendSessionDialogAfterTimeout, setLoading}: SessionDialogsActions) {
  const routesStyle: CSSProperties = { margin: '12vh 25vw' }

  return (
    <Routes>
      <Route path='/' element={<Navigate replace to='/home' />} />
      <Route path='/auth' element={
        <AuthenticationPage
          showTokenExpiredDialogAfterTimeout={showTokenExpiredDialogAfterTimeout}
          showExtendSessionDialogAfterTimeout={showExtendSessionDialogAfterTimeout}
          setLoading={setLoading}
          style={{ margin: '20vh 25vw' }}
        />
      } />
      <Route path='/auth/reset-password' element={<ResetPasswordPage setLoading={setLoading} style={routesStyle} />} />
      <Route path='/register' element={<RegisterPage setLoading={setLoading} style={routesStyle} />} />
      <Route path='/register/confirm' element={<ConfirmAccountPage />} />
      <Route path='*' element={<NotFoundPage style={routesStyle} />} />
    </Routes>
  )
}