import { Route, Routes } from "react-router-dom";
import AuthenticationPage from "../../pages/AuthenticationPage";
import { SessionDialogsActions } from "../../utils/types";
import RegisterPage from "../../pages/RegisterPage";
import NotFoundPage from "../../pages/NotFoundPage";

export default function Routing({showTokenExpiredDialogAfterTimeout, showExtendSessionDialogAfterTimeout, setLoading}: SessionDialogsActions) {
  return (
    <Routes>
      
      <Route path='/auth' element={
        <AuthenticationPage
          showTokenExpiredDialogAfterTimeout={showTokenExpiredDialogAfterTimeout}
          showExtendSessionDialogAfterTimeout={showExtendSessionDialogAfterTimeout}
          setLoading={setLoading}
        />
      } />

      <Route path='/register' element={<RegisterPage setLoading={setLoading}/>} />

      <Route path='*' element={<NotFoundPage />} />
    </Routes>
  )
}