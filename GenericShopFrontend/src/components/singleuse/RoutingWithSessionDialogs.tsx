import { useState } from 'react';
import MuiDialog from '../reusable/MuiDialog';
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import { environment } from '../../utils/constants';
import { getJwtToken, getRefreshToken, saveJwtToken, saveRefreshToken } from '../../services/tokenService';
import { Tokens } from '../../utils/types';
import { toast } from 'sonner'
import { useNavigate } from 'react-router-dom';
import { calculateExtendSessionDialogTimeout, calculateSessionExpiredTimeout, isTokenExpired, isUserSignIn } from '../../services/sessionService';
import Routing, { AUTH_PATH } from './Routing';

type RoutingWithSessionDialogsParams = {
  setLoading: (state: boolean) => void
  isAuthenticated: boolean
  setIsAuthenticated: (state: boolean) => void
}

export default function RoutingWithSessionDialogs({ setLoading, isAuthenticated, setIsAuthenticated }: RoutingWithSessionDialogsParams) {
  const navigate = useNavigate();
  const {t} = useTranslation()
  const [showTokenExpiredDialog, setShowTokenExpiredDialog] = useState(false);
  const [showExtendSessionDialog, setShowExtendSessionDialog] = useState(false);
  

  const closeDialogAndNavigateToAuth = (): void => {
    setShowTokenExpiredDialog(false)
    navigate(AUTH_PATH)
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
        setIsAuthenticated(false)
        navigate(AUTH_PATH)
        getJwtToken() && setShowTokenExpiredDialog(true)
      } else {
        showTokenExpiredDialogAfterTimeout()
      }
    }, calculateSessionExpiredTimeout())
  }

  const showExtendSessionDialogAfterTimeout = () => {
    const timeout = calculateExtendSessionDialogTimeout()
    setTimeout(() => {
      if (isUserSignIn()) {
        setShowExtendSessionDialog(true)
      }
    }, timeout)
  }
  
  const sendExtendSessionRequest = async (givenRefreshToken: string, givenToken: string): Promise<Tokens> => {
    const { data: {token, refreshToken} } = await axios.get(`${environment.apiBaseUrl}/auth/extend/${givenRefreshToken}`, {
      headers: {
        Authorization: `Bearer ${givenToken}`
      }
    })

    return {token, refreshToken}
  }

  return (
    <>
      <Routing
        showTokenExpiredDialogAfterTimeout={showTokenExpiredDialogAfterTimeout}
        showExtendSessionDialogAfterTimeout={showExtendSessionDialogAfterTimeout}
        setLoading={setLoading}
        isAuthenticated={isAuthenticated}
        setIsAuthenticated={setIsAuthenticated}
      />

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
  )
}