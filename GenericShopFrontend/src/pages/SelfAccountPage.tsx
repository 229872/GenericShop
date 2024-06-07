import { CSSProperties, Dispatch, SetStateAction, useEffect, useState } from "react"
import { Account, GridItemData } from "../utils/types"
import { useTranslation } from "react-i18next"
import axios from "axios"
import { environment } from "../utils/constants"
import { getJwtToken } from "../services/tokenService"
import handleAxiosException from "../services/apiService"
import { Avatar, Button, Card, CardContent, Grid, Stack, Tooltip, Typography } from "@mui/material"
import RefreshIcon from '@mui/icons-material/Refresh';
import { formatDate } from "../services/timeService"
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import GridCard from "../components/reusable/GridCard"
import { TFunction } from "i18next"
import ChangePasswordDialog from "../components/singleuse/ChangePasswordDialog"
import ChangeEmailDialog from "../components/singleuse/ChangeEmailDialog"
import EditSelfAccountDialog from "../components/singleuse/EditSelfAccountDialog"

type SelfAccountPageParams = {
  setLoading: (state: boolean) => void
  style: CSSProperties
}

export default function SelfAccountPage({ setLoading, style } : SelfAccountPageParams) {
  const { t } = useTranslation();
  const [account, setAccount] = useState<Account | null>(null)
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
    { label: 'self.roles', content: account?.accountRoles.join(', ') ?? '-' },
    { label: 'self.account_state', content: account?.accountState ?? '-' },
    { label: 'self.archival', content: t(account?.archival ? 'self.archival_true' : 'self.archival_false') }
  ];
  let once = true;

  useEffect(() => {
    if (once) {
      loadAccount()
      once = false;
    }
  }, [])
  
  const loadAccount = async () => {
    try {
      setLoading(true)
      const { data } = await getAccountData();
      setAccount(data)

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const getAccountData = async () => {
    return axios.get(`${environment.apiBaseUrl}/account/self`, { headers: {
      Authorization: `Bearer ${getJwtToken()}`
    }})
  }

  return (
    <Grid sx={{...style}} container spacing={3}>
      <Grid item xs={12} md={6}>
        <AccountOperationsCard t={t} account={account} setAccount={setAccount} loadAccount={loadAccount} setLoading={setLoading} />
        <GridCard data={accountAuthLogsData} labelSize={9} contentSize={3} />
      </Grid>

      <Grid item xs={12} md={6}>
        <GridCard title='self.account.information' data={accountData} />
        <GridCard title='self.address.information' data={addressData} />
        <GridCard title='self.account_state.information' data={accountStateData} />
      </Grid>
    </Grid>
  );
}

type AccountOperationsCardProps = {
  t: TFunction<"translation", undefined>
  account: Account | null
  setAccount: Dispatch<SetStateAction<Account | null>>
  loadAccount: () => Promise<void>
  setLoading: (state: boolean) => void
}

const AccountOperationsCard = ({ t, account, setAccount, loadAccount, setLoading } : AccountOperationsCardProps) => {
  const [ visibleChangePasswordDialog, setVisibleChangePasswordDialog ] = useState<boolean>(false)
  const [ visibleChangeEmailDialog, setVisibleChangeEmailDialog ] = useState<boolean>(false)
  const [ visibleEditAccountDialog, setVisibleEditAccountDialog ] = useState<boolean>(false)

  return (
    <Card sx={{ border: `1px solid black`, marginBottom: 3 }}>
      <CardContent sx={{ textAlign: 'center' }}>
        <Avatar
          children={<AccountCircleIcon sx={{ width: 200, height: 200, margin: '0 auto' }} />}
          sx={{ width: 150, height: 150, margin: '0 auto', border: '5px solid #E0E0E0' }}
        />
        <Typography variant="h4" sx={{ marginTop: 3 }}>{account?.firstName} {account?.lastName}</Typography>

        <Typography>{account?.email}</Typography>

        <Stack direction='row' spacing={3} sx={{ justifyContent: 'center', marginTop: 3 }}>
          <Tooltip title={t('self.tooltip.refresh')} placement='top'>
            <Button startIcon={<RefreshIcon />} color="primary" onClick={loadAccount} />
          </Tooltip>

          <Button color="primary" onClick={() => setVisibleEditAccountDialog(true)}>
            {t('self.button.edit')}
          </Button>

          {account && <EditSelfAccountDialog
            open={visibleEditAccountDialog}
            account={account}
            setAccount={setAccount}
            onClose={() => setVisibleEditAccountDialog(false)}
            setLoading={setLoading}
          />} 

          <Button color="primary" onClick={() => setVisibleChangeEmailDialog(true)}>
            {t('self.button.change_email')}
          </Button>

          <ChangeEmailDialog
            open={visibleChangeEmailDialog}
            onClose={() => setVisibleChangeEmailDialog(false)}
            setLoading={setLoading}
          />

          <Button color="primary" onClick={() => setVisibleChangePasswordDialog(true)}>
            {t('self.button.change_password')}
          </Button>

          <ChangePasswordDialog 
            open={visibleChangePasswordDialog} 
            onClose={() => setVisibleChangePasswordDialog(false)} 
            setLoading={setLoading} 
          />
        </Stack>
      </CardContent>
    </Card>
  )
}