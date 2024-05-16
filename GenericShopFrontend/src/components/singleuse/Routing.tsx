import { Navigate, Route, Routes } from "react-router-dom";
import AuthenticationPage from "../../pages/AuthenticationPage";
import { SessionDialogsActions } from "../../utils/types";
import RegisterPage from "../../pages/RegisterPage";
import NotFoundPage from "../../pages/NotFoundPage";
import ConfirmAccountPage from "../../pages/ConfirmAccountPage";

export default function Routing({showTokenExpiredDialogAfterTimeout, showExtendSessionDialogAfterTimeout, setLoading}: SessionDialogsActions) {
  return (
    <Routes>
      <Route path='/' element={<Navigate replace to='/home' />} />
      <Route path='/auth' element={
        <AuthenticationPage
          showTokenExpiredDialogAfterTimeout={showTokenExpiredDialogAfterTimeout}
          showExtendSessionDialogAfterTimeout={showExtendSessionDialogAfterTimeout}
          setLoading={setLoading}
        />
      } />
      <Route path='/register' element={<RegisterPage setLoading={setLoading}/>} />
      <Route path='/register/confirm' element={<ConfirmAccountPage />} />
      <Route path='*' element={<NotFoundPage />} />
    </Routes>
  )
}