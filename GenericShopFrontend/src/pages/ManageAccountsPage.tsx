import { Button, Card, Typography } from "@mui/material"
import { useEffect, useState } from "react"
import { BasicAccount, Column } from "../utils/types"
import axios from "axios"
import { environment } from "../utils/constants"
import { getJwtToken } from "../services/tokenService"
import handleAxiosException from "../services/apiService"
import TableWithPagination from "../components/reusable/TableWithPagination"
import { useTranslation } from "react-i18next"

type ManageAccountsPageProps = {
  setLoading: (value: boolean) => void
  style?: React.CSSProperties
}

export default function ManageAccountsPage({ setLoading, style } : ManageAccountsPageProps ) {
  const { t } = useTranslation()
  const [ accounts, setAccounts ] = useState<BasicAccount[]>([])
  const columns: Column<BasicAccount>[] = [
    { dataProp: 'id', name: t('manage_accounts.column.id') },
    { dataProp: 'archival', name: t('manage_accounts.column.archival') },
    { dataProp: 'login', name: t('manage_accounts.column.login') },
    { dataProp: 'email', name: t('manage_accounts.column.email') },
    { dataProp: 'firstName', name: t('manage_accounts.column.first_name') },
    { dataProp: 'lastName', name: t('manage_accounts.column.last_name') },
    { dataProp: 'state', name: t('manage_accounts.column.state') },
    { dataProp: 'roles', name: t('manage_accounts.column.roles') },
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
      setAccounts(data)

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const getAccounts = async (pageNr: number, pageSize: number, sortBy: keyof BasicAccount, direction: 'asc' | 'desc') => {
    return axios.get<BasicAccount[]>(`${environment.apiBaseUrl}/accounts?page=${pageNr}&size=${pageSize}&sort=${sortBy},${direction}`, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  return (
    <Card elevation={20} sx={{...style}}>
      <Typography textAlign='center' variant='h3'>Manage accounts</Typography>
      <Button>Create Account</Button>

      <TableWithPagination 
        columns={columns}
        data={accounts} 
        totalElements={totalElements}
        getData={getAccounts}
        rowsPerPageOptions={rowsPerPageOptions}
        tableStyle={{ width: '100%' }}
      />

    </Card>
  )
}