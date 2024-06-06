import { Box, Button, Card, Stack, Tooltip, Typography } from "@mui/material"
import { useEffect, useState } from "react"
import { BasicAccount, Column } from "../utils/types"
import axios from "axios"
import { environment } from "../utils/constants"
import { getJwtToken } from "../services/tokenService"
import handleAxiosException from "../services/apiService"
import TableWithPagination from "../components/reusable/TableWithPagination"
import { useTranslation } from "react-i18next"
import RefreshIcon from '@mui/icons-material/Refresh';

type ManageAccountsPageProps = {
  setLoading: (value: boolean) => void
  style?: React.CSSProperties
}

type AccountResponse = {
  content: BasicAccount[]
  totalElements: number
}

type AccountActions = {
  edit: JSX.Element
  actions: JSX.Element
}

type BasicAccountWithActions = BasicAccount & AccountActions;



export default function ManageAccountsPage({ setLoading, style } : ManageAccountsPageProps ) {
  const { t } = useTranslation()
  const [ accounts, setAccounts ] = useState<BasicAccount[]>([])
  const [ sortBy, setSortBy ] = useState<keyof BasicAccountWithActions>('id')
  const columns: Column<BasicAccountWithActions>[] = [
    { dataProp: 'id', name: t('manage_accounts.column.id') },
    { dataProp: 'archival', name: t('manage_accounts.column.archival') },
    { dataProp: 'login', name: t('manage_accounts.column.login') },
    { dataProp: 'email', name: t('manage_accounts.column.email') },
    { dataProp: 'firstName', name: t('manage_accounts.column.first_name') },
    { dataProp: 'lastName', name: t('manage_accounts.column.last_name') },
    { dataProp: 'state', name: t('manage_accounts.column.state') },
    { dataProp: 'roles', name: t('manage_accounts.column.roles') },
    { dataProp: 'edit', name: t('manage_accounts.column.edit') },
    { dataProp: 'actions', name: t('manage_accounts.column.actions') }
  ]
  const rowsPerPageOptions = [ 5, 10, 15, 20 ]
  const [totalElements, setTotalElements] = useState<number>(0)

  useEffect(() => {
    loadAccounts()
  }, [])

  const loadAccounts = async () => {
    try {
      setLoading(true)
      const { data } = await getAccounts(0, 10, 'id', 'asc');
      setAccounts(data.content)
      setTotalElements(data.totalElements)

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const getAccounts = async (pageNr: number, pageSize: number, sortBy: keyof BasicAccountWithActions, direction: 'asc' | 'desc') => {
    return axios.get<AccountResponse>(`${environment.apiBaseUrl}/accounts?page=${pageNr}&size=${pageSize}&sort=${sortBy},${direction}`, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const getButtonByAccountState = (accountState: string) => {
    switch (accountState) {
      case 'ACTIVE':
        return <Button variant='contained' color="secondary">{t('manage_accounts.button.block')}</Button>
      case 'BLOCKED':
        return <Button variant='contained' color="primary">{t('manage_accounts.button.unblock')}</Button>
      default: 
        return
    }
  }

  const createAccountWithButtons = (account: BasicAccount): BasicAccountWithActions => {
    return {
      ...account,
      edit: <Button variant='contained' color="primary">{t('manage_accounts.button.edit')}</Button>,
      actions: <Stack direction='row' spacing={2}>
        {
          getButtonByAccountState(account.state)
        }
        {
          !account.archival && <Button variant='contained' color="primary">{t('manage_accounts.button.archive')}</Button>
        }
      </Stack>
    };
  };

  return (
    <Stack sx={{...style}}>
      <Typography textAlign='center' variant='h3'>Manage accounts</Typography>
      <Stack direction='row' spacing={5} marginBottom='15px'>
        <Tooltip title={t('manage_accounts.button.refresh')} placement='top'>
            <Button startIcon={<RefreshIcon />} color="primary" />
        </Tooltip>
        <Button>Create Account</Button>
      </Stack>

      <TableWithPagination 
        columns={columns}
        data={
          accounts.map(createAccountWithButtons)
        } 
        totalElements={totalElements}
        getData={getAccounts}
        rowsPerPageOptions={rowsPerPageOptions}
        sortBy={sortBy}
        setSortBy={setSortBy}
        tableStyle={{ width: '100%' }}
      />

    </Stack>
  )
}