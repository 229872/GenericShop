import { Button, IconButton, ListItemIcon, ListItemText, Menu, MenuItem, Stack, Tooltip, Typography } from "@mui/material"
import { useEffect, useState } from "react"
import { AuthenticatedAccountRole, BasicAccount, Column, Role } from "../utils/types"
import axios from "axios"
import { environment } from "../utils/constants"
import { getJwtToken } from "../services/tokenService"
import handleAxiosException from "../services/apiService"
import TableWithPagination from "../components/reusable/TableWithPagination"
import { useTranslation } from "react-i18next"
import RefreshIcon from '@mui/icons-material/Refresh';
import { toast } from 'sonner'
import VisibilityIcon from '@mui/icons-material/Visibility';
import AccountViewDialog from "../components/singleuse/AccountViewDialog"
import CreateAccountDialog from "../components/singleuse/CreateAccountDialog"
import EditIcon from '@mui/icons-material/Edit';
import ForwardIcon from '@mui/icons-material/Forward';
import ClearIcon from '@mui/icons-material/Clear';
import AddIcon from '@mui/icons-material/Add';
import ChangeAccountRoleDialog from "../components/singleuse/ChangeRoleAccountDialog"
import AddAccountRoleDialog from "../components/singleuse/AddAccountRoleDialog"


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

type RoleData = {
  role: AuthenticatedAccountRole
}

export default function ManageAccountsPage({ setLoading, style } : ManageAccountsPageProps ) {
  const { t } = useTranslation()
  const [ accounts, setAccounts ] = useState<BasicAccount[]>([])
  const [ sortBy, setSortBy ] = useState<keyof BasicAccountWithActions>('id')
  const [ totalElements, setTotalElements ] = useState<number>(0)
  const [ currentPage, setCurrentPage ] = useState<number>(0)
  const [ pageSize, setPageSize ] = useState<number>(10)
  const [ direction, setDirection ] = useState<'asc' | 'desc'>('asc')
  const [ visibleAccountId, setVisibleAccountId ] = useState<number | undefined>(undefined)
  const [ visibleCreateAccountDialog, setVisibleCreateAccountDialog ] = useState<boolean>(false)
  const [ editAccountAnchorEl, setEditAccountAnchorEl ] = useState(null);
  const [ editAccountData, setEditAccountData ] = useState<BasicAccount | undefined>(undefined);
  const [ visibleChangeRoleDialog, setVisibleChangeRoleDialog ] = useState<AuthenticatedAccountRole | undefined>(undefined);
  const [ visibleAddRoleDialog, setVisibleAddRoleDialog ] = useState<AuthenticatedAccountRole[]>([]);
  const rowsPerPageOptions = [ 5, 10, 15, 20 ]
  const columns: Column<BasicAccountWithActions>[] = [
    { dataProp: 'id', name: t('manage_accounts.column.id'), label: true },
    { dataProp: 'archival', name: t('manage_accounts.column.archival'), label: true },
    { dataProp: 'login', name: t('manage_accounts.column.login'), label: true },
    { dataProp: 'email', name: t('manage_accounts.column.email'), label: true },
    { dataProp: 'firstName', name: t('manage_accounts.column.first_name'), label: true },
    { dataProp: 'lastName', name: t('manage_accounts.column.last_name'), label: true },
    { dataProp: 'accountState', name: t('manage_accounts.column.state'), label: true },
    { dataProp: 'accountRoles', name: t('manage_accounts.column.roles'), label: true },
    { dataProp: 'actions', name: t('manage_accounts.column.actions'), label: false }
  ]

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

  const addRole = async (accountId: number, newRole: RoleData) => {
    return axios.put(`${environment.apiBaseUrl}/accounts/id/${accountId}/role/add`, newRole, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const removeRole = async (accountId: number, roleForRemoval: RoleData) => {
    return axios.put(`${environment.apiBaseUrl}/accounts/id/${accountId}/role/remove`, roleForRemoval, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const changeRole = async (accountId: number, newRole: RoleData) => {
    return axios.put(`${environment.apiBaseUrl}/accounts/id/${accountId}/role/change`, newRole, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const sendChangeStateRequest = async (accountId: number, operation: 'block' | 'unblock' | 'archive') => {
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
      const { data } = await operationFunction(accountId)
      const updatedAccounts: BasicAccount[] = accounts.map(acccount => 
        acccount.id === accountId ? data : acccount
      )
      setAccounts(updatedAccounts)
      toast.success(t('manage_accounts.operation.success'))

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const sendEditRolesRequest = async (accountId: number, role: RoleData, operation: 'addRole' | 'removeRole' | 'changeRole') => {
    const operationsMap = {
      addRole: addRole,
      removeRole: removeRole,
      changeRole: changeRole
    };

    const operationFunction = operationsMap[operation];

    if (!operationFunction) {
      throw new Error('Invalid operation');
    }

    try {
      setLoading(true)
      const { data } = await operationFunction(accountId, role)
      const updatedAccounts: BasicAccount[] = accounts.map(acccount => 
        acccount.id === accountId ? data : acccount
      )
      setAccounts(updatedAccounts)
      toast.success(t('manage_accounts.operation.success'))

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const changeRoleOnValid = async (data: RoleData) => {
    editAccountData?.id && await sendEditRolesRequest(editAccountData.id, data, 'changeRole')
    setVisibleChangeRoleDialog(undefined)
    setEditAccountAnchorEl(null)
  }

  const addRoleOnValid = async (data: RoleData) => {
    editAccountData?.id && await sendEditRolesRequest(editAccountData.id, data, 'addRole')
    setVisibleAddRoleDialog([])
    setEditAccountAnchorEl(null)
  }

  const getButtonByAccountState = (accountState: string, isArchival: boolean, accountId: number) => {
    switch (accountState) {
      case 'ACTIVE':
        return !isArchival ? <Button variant='contained' color='secondary' onClick={() => sendChangeStateRequest(accountId, 'block')}>
          {t('manage_accounts.button.block')}
        </Button> : undefined
      case 'BLOCKED':
        return !isArchival ? <Button variant='contained' color='primary' onClick={() => sendChangeStateRequest(accountId, 'unblock')}>
          {t('manage_accounts.button.unblock')}
        </Button> : undefined
      default: 
        return
    }
  }

  const translateAccountProps = (account: BasicAccountWithActions): BasicAccountWithActions => {
    return {
      ...account,
      accountState: t(`manage_accounts.value.${account.accountState}`),
      accountRoles: account.accountRoles.map(role => t(`manage_accounts.value.${role}`))
    }
  }

  const createAccountWithButtons = (account: BasicAccount): BasicAccountWithActions => {
    return {
      ...account,
      actions: <Stack direction='row' spacing={2}>
        {
          <Tooltip title={t('manage_accounts.tooltip.show_more')} placement='top'>
            <IconButton onClick={() => setVisibleAccountId(account.id)}>
              <VisibilityIcon />
            </IconButton>
          </Tooltip>
        }
        {
          !account.archival && (
            <Tooltip title={t('manage_accounts.tooltip.edit')} placement='top'>
              <IconButton onClick={(e: any) => {
                setEditAccountData(account)
                setEditAccountAnchorEl(e.currentTarget)
              }}>
                <EditIcon />
              </IconButton>
            </Tooltip>
          )
        }
        {
          getButtonByAccountState(account.accountState, account.archival, account.id)
        }
        {
          !account.archival && 
          <Button variant='contained' color='primary' onClick={() => sendChangeStateRequest(account.id, 'archive')}>
            {t('manage_accounts.button.archive')}
          </Button>
        }
      </Stack>
    };
  };
  
  return (
    <Stack sx={{...style}}>
      <Typography textAlign='center' variant='h3'>{t('manage_accounts.title')}</Typography>
      <Stack direction='row' spacing={5} marginBottom='15px'>
        <Tooltip title={t('manage_accounts.button.refresh')} placement='top'>
            <Button startIcon={<RefreshIcon />} color='primary' onClick={() => loadAccounts(currentPage, pageSize, sortBy, direction)} />
        </Tooltip>
        <Button onClick={() => setVisibleCreateAccountDialog(true)}>{t('manage_accounts.button.create_account')}</Button>
      </Stack>

      <TableWithPagination 
        columns={columns}
        data={accounts.map(createAccountWithButtons).map(translateAccountProps)} 
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
        tableStyle={{ width: '100%', maxHeight: '60vh'}}
      />
      
      <AccountViewDialog
        accountId={visibleAccountId}
        setLoading={setLoading}
        open={Boolean(visibleAccountId)}
        onClose={() => setVisibleAccountId(undefined)}
      />

      <CreateAccountDialog
        setLoading={setLoading}
        open={visibleCreateAccountDialog}
        onClose={() => setVisibleCreateAccountDialog(false)}
      />

      <ChangeAccountRoleDialog
        currentRole={visibleChangeRoleDialog}
        open={Boolean(visibleChangeRoleDialog)}
        onClose={() => setVisibleChangeRoleDialog(undefined)}
        onValid={changeRoleOnValid}
      />

      <AddAccountRoleDialog
        currentRoles={visibleAddRoleDialog}
        open={visibleAddRoleDialog.length != 0}
        onClose={() => setVisibleAddRoleDialog([])}
        onValid={addRoleOnValid}
      />

      <Menu id='account-menu'
        anchorEl={editAccountAnchorEl}
        keepMounted
        open={Boolean(editAccountAnchorEl)}
        onClose={() => {
          setEditAccountAnchorEl(null)
          setEditAccountData(undefined)
        }}
      >
        {
          editAccountData?.accountRoles
            .filter((role) : role is Exclude<Role, Role.GUEST> => role !== Role.GUEST)
            .map((role, key) => {
              if (editAccountData.accountRoles.length > 1) {
                return (
                  <MenuItem key={key} onClick={() => {
                    sendEditRolesRequest(editAccountData.id, { role: role as unknown as AuthenticatedAccountRole }, 'removeRole')
                    setEditAccountAnchorEl(null)
                  }}>
                    <ListItemIcon>
                      <ClearIcon />
                    </ListItemIcon>
                    <ListItemText>{t('manage_accounts.remove_role')} {t(`manage_accounts.value.${role}`)}</ListItemText>
                  </MenuItem>
                )
              } else if (editAccountData.accountRoles.length === 1 && editAccountData.accountRoles[0] !== Role.GUEST) {
                return (
                  <MenuItem key={key} onClick={() => {
                    setVisibleChangeRoleDialog(role as unknown as AuthenticatedAccountRole)
                    setEditAccountAnchorEl(null)
                  }}>
                    <ListItemIcon onClick={() => setVisibleChangeRoleDialog(role as unknown as AuthenticatedAccountRole)}>
                      <ForwardIcon />
                    </ListItemIcon>
                    <ListItemText>{t('manage_accounts.change_role')} {t(`manage_accounts.value.${role}`)}</ListItemText>
                  </MenuItem>
                )
              }
            })
        }
        {
          editAccountData && editAccountData.accountRoles.length === 1 && editAccountData.accountRoles[0] !== Role.GUEST && editAccountData.accountRoles[0] !== Role.ADMIN && (
            <MenuItem onClick={() => setVisibleAddRoleDialog(editAccountData.accountRoles as unknown as AuthenticatedAccountRole[])}>
              <ListItemIcon>
                <AddIcon />
              </ListItemIcon>
              <ListItemText>{t('manage_accounts.add_role.title')}</ListItemText>
            </MenuItem>
          )
        }
      </Menu>

    </Stack>
  )
}