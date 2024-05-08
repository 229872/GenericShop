import './App.css'
import { Route, Routes, useNavigate } from "react-router-dom";
import NavigationBar from './pages/NavigationBar';
import './utils/i18n'; 
import AuthenticationPage from './pages/AuthenticationPage';
import { Toaster } from 'sonner'
import { useState } from 'react';
import MuiDialog from './components/MuiDialog';
import { useTranslation } from 'react-i18next';

function App() {
  const [showTokenExpiredDialog, setShowTokenExpiredDialog] = useState(false);
  const navigate = useNavigate();
  const {t} = useTranslation()

  const closeDialogAndNavigateToAuth = (): void => {
    setShowTokenExpiredDialog(false)
    navigate('/auth')
  }


  return <>
    <Toaster position='top-right' richColors style={{marginTop: '45px'}} />
    <NavigationBar />
    <Routes>
      <Route path='/auth' element={<AuthenticationPage setDialog={setShowTokenExpiredDialog} />} />
    </Routes>

    <MuiDialog
      open={showTokenExpiredDialog}
      onClose={() => setShowTokenExpiredDialog(false)}
      title={t('app.dialog.title')}
      message={t('app.dialog.message')}
      actions={[
        {label: t('app.dialog.action'), onClick: closeDialogAndNavigateToAuth}
      ]}
      style={{fontSize: '30px'}}
    />
  </> 
}

export default App;
