import { AppBar, Badge, Button, Divider, IconButton, ListItemIcon, ListItemText, Menu, MenuItem, Stack, Toolbar, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { isTokenExpired, logout } from "../../services/sessionService";
import { AUTH_PATH, CART_PATH, HOME_PATH, MANAGE_ACCOUNTS_PATH, MANAGE_PRODUCTS_PATH, REGISTER_PATH, SELF_ACCOUNT_PATH, SELF_ORDERS_PATH } from "./Routing";
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import LanguageIcon from '@mui/icons-material/Language';
import PersonIcon from '@mui/icons-material/Person';
import LogoutIcon from '@mui/icons-material/Logout';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ChangeLanguageMenu from "../reusable/ChangeLanguageMenu";
import { Role } from "../../utils/types";
import { getJwtToken, getRoles, saveLastActiveRole } from "../../services/tokenService";
import ChangeActiveRoleMenu from "../reusable/ChangeActiveRoleMenu";
import SupervisorAccountIcon from '@mui/icons-material/SupervisorAccount';

type NavigationBarProps = {
  setIsAuthenticated: (state: boolean) => void
  setActiveRole: (role: Role) => void
  activeRole: Role
  numberOfProductsInCart: number
}

export default function NavigationBar({ setIsAuthenticated, setActiveRole, activeRole, numberOfProductsInCart } : NavigationBarProps ) {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [changeLanguageAnchorEl, setLanguageAnchorEl] = useState(null);
  const [accountAnchorEl, setAccountAnchorEl] = useState(null);
  const [isActiveRoleMenuVisible, setActiveRoleMenuVisible] = useState(false);
  const [localeOptionsVisible, setLocaleOptionsVisible] = useState(false)
  const menuStyle = { width: '250px' }
  const [reload, setReload] = useState<boolean>(false)

  const logoutFromApp = () => {
    if (getRoles(getJwtToken()).length > 1) {
      saveLastActiveRole(activeRole)
    }
    logout();
    setIsAuthenticated(false)
    setActiveRole(Role.GUEST)
    setLanguageAnchorEl(null)
    setAccountAnchorEl(null)
    setReload(!reload)
    navigate(AUTH_PATH)
  }

  const navigateToSelfInformations = () => {
    setLanguageAnchorEl(null)
    setAccountAnchorEl(null)
    navigate(SELF_ACCOUNT_PATH)
  }

  return (
    <AppBar sx={{height: '64px'}}>
      <Toolbar sx={{justifyContent: 'space-between'}}>
        <Typography variant='h5'>{t('app.name')}</Typography>
        <Stack direction='row' spacing={2}>
          { activeRole !== Role.ADMIN && <Button color='inherit' onClick={() => navigate(HOME_PATH)}>{t('nav.home')}</Button> }
          {isTokenExpired() ? (
            <>
              <Button color='inherit' onClick={() => navigate(AUTH_PATH)}>{t('nav.login')}</Button>
              <Button color='inherit' onClick={() => navigate(REGISTER_PATH)}>{t('nav.create_account')}</Button>
              <Button color='inherit' onClick={(e: any) => setLanguageAnchorEl(e.currentTarget)}>{t('nav.language')}</Button>
            </>
          ) : (
            <>
              {activeRole === Role.ADMIN && <Button color='inherit' onClick={() => navigate(MANAGE_ACCOUNTS_PATH)}>{t('nav.manage.accounts')}</Button>}
              {activeRole === Role.EMPLOYEE && <Button color='inherit' onClick={() => navigate(MANAGE_PRODUCTS_PATH)}>{t('nav.manage.products')}</Button>}
              {activeRole === Role.CLIENT && <Button color='inherit' onClick={() => navigate(SELF_ORDERS_PATH)}>{t('nav.orders')}</Button>}
              {activeRole === Role.CLIENT && <IconButton color='inherit' size='large' onClick={(e: any) => navigate(CART_PATH)}>
                <Badge badgeContent={numberOfProductsInCart} color='secondary' showZero>
                  <ShoppingCartIcon fontSize='medium' />
                </Badge>
              </IconButton>}

              <IconButton color='inherit' size='large' onClick={(e: any) => setAccountAnchorEl(e.currentTarget)}>
                <AccountCircleIcon fontSize='large' />
              </IconButton>
            </>
          )}

        </Stack>        
      </Toolbar>

      <Menu id='language-menu' anchorEl={changeLanguageAnchorEl} open={Boolean(changeLanguageAnchorEl)} onClose={() => setLanguageAnchorEl(null)}>
        <ChangeLanguageMenu menuStyle={menuStyle} setLanguageAnchorEl={setLanguageAnchorEl} />
      </Menu>

      <Menu id='account-menu' anchorEl={accountAnchorEl} keepMounted open={Boolean(accountAnchorEl)} onClose={() => setAccountAnchorEl(null)}>
        <MenuItem sx={menuStyle} onClick={navigateToSelfInformations}>
          <ListItemIcon><PersonIcon /></ListItemIcon>
          <ListItemText>{t('nav.account')}</ListItemText>
        </MenuItem>

        <Divider />

        <MenuItem sx={menuStyle} onClick={() => setLocaleOptionsVisible(!localeOptionsVisible)}>
          <ListItemIcon><LanguageIcon /></ListItemIcon>
          <ListItemText>{t('nav.language')}</ListItemText>
          {localeOptionsVisible ? (
            <ListItemIcon><ExpandLessIcon /></ListItemIcon>
          ) : (
            <ListItemIcon><ExpandMoreIcon /></ListItemIcon>
          ) }
        </MenuItem>

        {localeOptionsVisible && (
          <ChangeLanguageMenu menuStyle={menuStyle} setLanguageAnchorEl={setLanguageAnchorEl} />
        )}
        {getRoles(getJwtToken()).length > 1 && <Divider />}
        {getRoles(getJwtToken()).length > 1 && (
          <MenuItem sx={menuStyle} onClick={() => setActiveRoleMenuVisible(!isActiveRoleMenuVisible)}>
            <ListItemIcon><SupervisorAccountIcon /></ListItemIcon>
            <ListItemText>{t('nav.change_role')}</ListItemText>
            {isActiveRoleMenuVisible ? (
              <ListItemIcon><ExpandLessIcon /></ListItemIcon>
            ) : (
              <ListItemIcon><ExpandMoreIcon /></ListItemIcon>
            )}
          </MenuItem>
        )}

        {getRoles(getJwtToken()).length > 1 && isActiveRoleMenuVisible && (
          <div>
            <Divider />
            <ChangeActiveRoleMenu menuStyle={menuStyle} activeRole={activeRole} roles={getRoles(getJwtToken())} setActiveRole={setActiveRole} />
          </div>
        )}

        <Divider />

        <MenuItem sx={menuStyle} onClick={logoutFromApp}>
          <ListItemIcon><LogoutIcon /></ListItemIcon>
          <ListItemText>{t('nav.logout')}</ListItemText>
        </MenuItem>
      </Menu>
    </AppBar>
  )
}