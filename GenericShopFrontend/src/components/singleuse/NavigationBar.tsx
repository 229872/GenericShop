import { AppBar, Button, Menu, MenuItem, Stack, Toolbar, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { isTokenExpired } from "../../services/sessionService";
import { AUTH_PATH, HOME_PATH, REGISTER_PATH } from "./Routing";

export default function NavigationBar() {
  const navigate = useNavigate();
  const { t, i18n } = useTranslation();
  const [anchorEl, setAnchorEl] = useState(null);


  const handleLanguageMenuOpen = (event: any) => {
    setAnchorEl(event.currentTarget);
  };

  const handleLanguageMenuClose = () => {
    setAnchorEl(null);
  };

  const changeLanguage = (lng: 'pl' | 'en') => {
    i18n.changeLanguage(lng);
    handleLanguageMenuClose();
  };

  return (
    <AppBar sx={{height: '64px'}}>
      <Toolbar sx={{justifyContent: 'space-between'}}>
        <Typography variant='h5'>{t('app.name')}</Typography>
        {isTokenExpired() && (
          <Stack direction='row' spacing={2}>
            <Button color='inherit' onClick={() => navigate(HOME_PATH)}>{t('nav.home')}</Button>
            <Button color='inherit' onClick={() => navigate(AUTH_PATH)}>{t('nav.login')}</Button>
            <Button color='inherit' onClick={() => navigate(REGISTER_PATH)}>{t('nav.create_account')}</Button>
            <Button color='inherit' onClick={handleLanguageMenuOpen}>{t('nav.language')}</Button>
            
            <Menu
              id='language-menu'
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleLanguageMenuClose}
            >
              <MenuItem onClick={() => changeLanguage('en')}>English</MenuItem>
              <MenuItem onClick={() => changeLanguage('pl')}>Polish</MenuItem>
            </Menu>
          </Stack>
        )}
      </Toolbar>
    </AppBar>
  )
}