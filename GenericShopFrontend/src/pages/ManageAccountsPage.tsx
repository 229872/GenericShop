import { Button, Stack, Tooltip, Typography } from "@mui/material"
import { useEffect, useState } from "react"
import { BasicAccount, Column } from "../utils/types"
import axios from "axios"
import { environment } from "../utils/constants"
import { getJwtToken } from "../services/tokenService"
import handleAxiosException from "../services/apiService"
import TableWithPagination from "../components/reusable/TableWithPagination"
import { useTranslation } from "react-i18next"
import RefreshIcon from '@mui/icons-material/Refresh';
import { toast } from 'sonner'

type ManageAccountsPageProps = {
  setLoading: (value: boolean) => void
  style?: React.CSSProperties
}

type AccountResponse = {
  content: BasicAccount[]
  totalElements: number
}

type AccountActions = {
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
    { dataProp: 'actions', name: t('manage_accounts.column.actions') }
  ]
  const rowsPerPageOptions = [ 5, 10, 15, 20 ]
  const [totalElements, setTotalElements] = useState<number>(0)
  const [currentPage, setCurrentPage] = useState<number>(0)
  const [pageSize, setPageSize] = useState<number>(10)
  const [direction, setDirection] = useState<'asc' | 'desc'>('asc')

  useEffect(() => {
    loadAccounts(currentPage, pageSize, sortBy, direction)
  }, [])

  const loadAccounts = async (pageNr: number, pageSize: number, sortBy: keyof BasicAccountWithActions, direction: 'asc' | 'desc') => {
    try {
      setLoading(true)
      const { data } = await getAccounts(pageNr, pageSize, sortBy, direction);
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

  const blockAccount = async (accountId: number) => {
    return axios.put<BasicAccount>(`${environment.apiBaseUrl}/accounts/id/${accountId}/block`, null, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const unblockAccount = async (accountId: number) => {
    return axios.put<BasicAccount>(`${environment.apiBaseUrl}/accounts/id/${accountId}/unblock`, null, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const archiveAccount = async (accountId: number) => {
    return axios.put<BasicAccount>(`${environment.apiBaseUrl}/accounts/id/${accountId}/archive`, null, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const sendRequest = async (accountId: number, operation: 'block' | 'unblock' | 'archive') => {
    const operationsMap = {
      block: blockAccount,
      unblock: unblockAccount,
      archive: archiveAccount
    };

    const operationFunction = operationsMap[operation];

    if (!operationFunction) {
      throw new Error('Invalid operation');
    }

    try {
      setLoading(true)
      await operationFunction(accountId)
      toast.success(t('manage_accounts.operation.success'))

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const getButtonByAccountState = (accountState: string, accountId: number) => {
    switch (accountState) {
      case 'ACTIVE':
        return <Button variant='contained' color='secondary' onClick={() => sendRequest(accountId, 'block')}>
          {t('manage_accounts.button.block')}
        </Button>
      case 'BLOCKED':
        return <Button variant='contained' color='primary' onClick={() => sendRequest(accountId, 'unblock')}>
          {t('manage_accounts.button.unblock')}
        </Button>
      default: 
        return
    }
  }

  const createAccountWithButtons = (account: BasicAccount): BasicAccountWithActions => {
    return {
      ...account,
      actions: <Stack direction='row' spacing={2}>
        {
          getButtonByAccountState(account.state, account.id)
        }
        {
          !account.archival && 
          <Button variant='contained' color='primary' onClick={() => sendRequest(account.id, 'archive')}>
            {t('manage_accounts.button.archive')}
          </Button>
        }
      </Stack>
    };
  };

  return (
    <Stack sx={{...style}}>
      <Typography textAlign='center' variant='h3'>Manage accounts</Typography>
      <Stack direction='row' spacing={5} marginBottom='15px'>
        <Tooltip title={t('manage_accounts.button.refresh')} placement='top'>
            <Button startIcon={<RefreshIcon />} color='primary' onClick={() => loadAccounts(currentPage, pageSize, sortBy, direction)} />
        </Tooltip>
        <Button>Create Account</Button>
      </Stack>

      <TableWithPagination 
        columns={columns}
        data={
          accounts.map(createAccountWithButtons)
        } 
        getData={loadAccounts}
        totalElements={totalElements}
        sortBy={sortBy}
        setSortBy={setSortBy}
        rowsPerPageOptions={rowsPerPageOptions}
        currentPage={currentPage}
        setCurrentPage={setCurrentPage}
        pageSize={pageSize}
        setPageSize={setPageSize}
        direction={direction}
        setDirection={setDirection}
        tableStyle={{ width: '100%' }}
      />

    </Stack>
  )
}