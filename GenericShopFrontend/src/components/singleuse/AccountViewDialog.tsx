import { CSSProperties, useEffect, useState } from "react"
import { Account, GridItemData } from "../../utils/types"
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid } from "@mui/material"
import GridCard from "../reusable/GridCard"
import { useTranslation } from "react-i18next"
import { formatDate } from "../../services/timeService"
import axios from "axios"
import { environment } from "../../utils/constants"
import { getJwtToken } from "../../services/tokenService"
import handleAxiosException from "../../services/apiService"

type AccountViewDialogProps = {
  accountId: number | undefined
  open: boolean
  onClose: () => void
  setLoading: (loading: boolean) => void
  style?: CSSProperties
}

export default function AccountViewDialog({ accountId, open, onClose, setLoading, style } : AccountViewDialogProps) {
  const { t } = useTranslation();
  const [ account, setAccount ] = useState<Account | undefined>()
  const accountAuthLogsData: GridItemData[] = [
    { label: 'self.last_successful_auth_time', content: formatDate(account?.authLogs?.lastSuccessfulAuthTime) },
    { label: 'self.last_unsuccessful_auth_time', content: formatDate(account?.authLogs?.lastUnsuccessfulAuthTime) },
    { label: 'self.unsuccessful_auth_attempts', content: account?.authLogs?.unsuccessfulAuthCounter ?? '-' },
    { label: 'self.last_successful_auth_ip', content: account?.authLogs?.lastSuccessfulAuthIpAddr ?? '-' },
    { label: 'self.last_unsuccessful_auth_ip', content: account?.authLogs?.lastUnsuccessfulAuthIpAddr ?? '-' },
    { label: 'self.blockade_end_time', content: formatDate(account?.authLogs?.blockadeEndTime) ?? '-' }
  ]
  const accountData: GridItemData[] = [
    { label: 'self.first_name', content: account?.firstName ?? '-' },
    { label: 'self.last_name', content: account?.lastName ?? '-' },
    { label: 'self.login', content: account?.login ?? '-' },
    { label: 'self.email', content: account?.email ?? '-' },
    { label: 'self.language', content: account?.locale ?? '-'}
  ];
  const addressData: GridItemData[] = [
    { label: 'self.country', content: account?.address.country ?? '-' },
    { label: 'self.city', content: account?.address.city ?? '-' },
    { label: 'self.street', content: account?.address.street ?? '-' },
    { label: 'self.house_number', content: account?.address.houseNumber ?? '-' },
    { label: 'self.postal_code', content: account?.address.postalCode ?? '-'}
  ];
  const accountStateData: GridItemData[] = [
    { label: 'self.roles', content: account?.accountRoles.map(role => t(`self.roles.value.${role}`)).join(', ') ?? '-' },
    { label: 'self.account_state', content: t(`self.account_state.value.${account?.accountState}`) ?? '-' },
    { label: 'self.archival', content: t(account?.archival ? 'self.archival_true' : 'self.archival_false') }
  ];

  useEffect(() => {
    accountId ? sendAccountRequest(accountId) : onClose()
  }, [accountId])

  const getAccount = async (id: number) => {
    return axios.get<Account>(`${environment.apiBaseUrl}/accounts/id/${id}`, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const sendAccountRequest = async (id: number) => {
    try {
      setLoading(true)
      const { data } = await getAccount(id);
      setAccount(data)

    } catch (e) {
      onClose()
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  return (
    <Dialog open={open} onClose={onClose} sx={{...style, marginTop: '4vh'}} maxWidth='md'>
      <DialogTitle textAlign='center'><b>{account?.login} {t('informations')}</b></DialogTitle>
      <DialogContent>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <GridCard title='self.account.information' data={accountData} />
            <GridCard title='self.address.information' data={addressData} labelSize={12} />
          </Grid>
          <Grid item xs={12} md={6}>
            <GridCard title='self.account_state.information' data={accountStateData} />
            <GridCard data={accountAuthLogsData} labelSize={12} />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} variant='contained'>{t('Back')}</Button>
      </DialogActions>
    </Dialog>
  )
}