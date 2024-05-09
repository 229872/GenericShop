import { Route, Routes } from "react-router-dom";
import AuthenticationPage from "../../pages/AuthenticationPage";
import { SessionDialogsActions } from "../../utils/types";

export default function Routing({showTokenExpiredDialogAfterTimeout, showExtendSessionDialogAfterTimeout}: SessionDialogsActions) {
  return (
    <Routes>
      <Route path='/auth' element={
        <AuthenticationPage
          showTokenExpiredDialogAfterTimeout={showTokenExpiredDialogAfterTimeout}
          showExtendSessionDialogAfterTimeout={showExtendSessionDialogAfterTimeout}
        />
      } />
    </Routes>
  )
}