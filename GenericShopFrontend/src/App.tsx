import './App.css'
import { Route, Routes, useNavigate } from "react-router-dom";
import NavigationBar from './pages/NavigationBar';
import './utils/i18n'; 
import AuthenticationPage from './pages/AuthenticationPage';
import { Toaster } from 'sonner'
import { useState } from 'react';
import MuiDialog from './components/MuiDialog';
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import { environment } from './utils/constants';
import { getExpirationTime, getJwtToken, getRefreshToken, isTokenExpired, saveJwtToken, saveRefreshToken } from './utils/tokenService';
import { Tokens } from './utils/types';
import { toast } from 'sonner'


function App() {
  const [showTokenExpiredDialog, setShowTokenExpiredDialog] = useState(false);
  const [showExtendSessionDialog, setShowExtendSessionDialog] = useState(false);
  const navigate = useNavigate();
  const {t} = useTranslation()

  const closeDialogAndNavigateToAuth = (): void => {
    setShowTokenExpiredDialog(false)
    navigate('/auth')
  }

  const extendSession = async (): Promise<void> => {
    const refreshToken = getRefreshToken();
    const token = getJwtToken();

    if (refreshToken && token) {
      try {
        const data: Tokens = await sendExtendSessionRequest(refreshToken, token)
        saveJwtToken(data.token)
        saveRefreshToken(data.refreshToken)
        showExtendSessionDialogAfterTimeout()
        toast.success(t('app.dialog.session_extend.dialog.success'))

      } catch (e) {
        toast.error(t('app.dialog.session_extend.dialog.error'))
      }

    }
    setShowExtendSessionDialog(false)
  }

  const showTokenExpiredDialogAfterTimeout = () => {
    setTimeout(() => {
      if (isTokenExpired()) {
        setShowExtendSessionDialog(false)
        setShowTokenExpiredDialog(true)
      } else {
        showTokenExpiredDialogAfterTimeout()
      }
    }, calculateSessionExpiredTimeout())
  }

  const showExtendSessionDialogAfterTimeout = () => {
    const timeout = calculateExtendSessionDialogTimeout()
    setTimeout(() => {
      if (!isTokenExpired()) {
        setShowExtendSessionDialog(true)
      }
    }, timeout)
  }

  const calculateSessionExpiredTimeout = () => {
    const now = Date.now() / 1000;
    return (Number(getExpirationTime(getJwtToken())) - now) * 1000;
  };
  
  const calculateExtendSessionDialogTimeout = (): number | undefined => {
    const expirationTime = getExpirationTime(getJwtToken());
    if (expirationTime) {
      const sessionTmeInMillis = expirationTime * 1000 - Date.now();
  
      if (sessionTmeInMillis <= 1.5 * 180 * 1000) {
        return sessionTmeInMillis - (0.3 * 180 * 1000)
      }
  
      return sessionTmeInMillis - (180 * 1000)
    }
    return undefined;
  }
  
  const sendExtendSessionRequest = async (givenRefreshToken: string, givenToken: string): Promise<Tokens> => {
    const { data: {token, refreshToken} } = await axios.get(`${environment.apiBaseUrl}/auth/extend/${givenRefreshToken}`, {
      headers: {
        Authorization: `Bearer ${givenToken}`
      }
    })

    return {token, refreshToken}
  }


  return <>
    <Toaster position='top-right' richColors style={{marginTop: '45px'}} />
    <NavigationBar />
    <Routes>
      <Route path='/auth' element={
        <AuthenticationPage
          showTokenExpiredDialogAfterTimeout={showTokenExpiredDialogAfterTimeout}
          showExtendSessionDialogAfterTimeout={showExtendSessionDialogAfterTimeout}
        />
      } />
    </Routes>

    <MuiDialog
      open={showTokenExpiredDialog}
      onClose={() => setShowTokenExpiredDialog(false)}
      title={t('app.dialog.session_expired.title')}
      message={t('app.dialog.session_expired.message')}
      actions={[
        {label: t('app.dialog.session_expired.action'), onClick: closeDialogAndNavigateToAuth}
      ]}
      style={{fontSize: '30px'}}
    />

    <MuiDialog
      open={showExtendSessionDialog}
      onClose={() => setShowTokenExpiredDialog(false)}
      title={t('app.dialog.session_extend.title')}
      message={t('app.dialog.session_extend.message')}
      actions={[
        {label: t('app.dialog.session_extend.action.yes'), onClick: extendSession},
        {label: t('app.dialog.session_extend.action.no'), onClick: () => setShowExtendSessionDialog(false), color: 'error'}
      ]}
      style={{fontSize: '30px'}}
    />
  </> 
}

export default App;
