import { ListItemIcon, ListItemText, MenuItem } from "@mui/material";
import { CSSProperties, Dispatch, SetStateAction } from "react";
import { useTranslation } from "react-i18next";
import RadioButtonCheckedIcon from '@mui/icons-material/RadioButtonChecked';
import RadioButtonUncheckedIcon from '@mui/icons-material/RadioButtonUnchecked';
import axios from "axios";
import { environment } from "../../utils/constants";
import { getJwtToken } from "../../services/tokenService";
import { isUserSignIn } from "../../services/sessionService";

type ChangeLanguageRequest = {
  locale: 'pl' | 'en'
}

type ChangeLanguageMenuParams = {
  menuStyle: CSSProperties
  setLanguageAnchorEl: Dispatch<SetStateAction<null>>
}

export default function ChangeLanguageMenu({ menuStyle, setLanguageAnchorEl } : ChangeLanguageMenuParams) {
  const { t, i18n } = useTranslation()

  const changeLanguage = async (lng: 'pl' | 'en') => {
    i18n.changeLanguage(lng);
    setLanguageAnchorEl(null)
    if (isUserSignIn()) {
      try {
        await sendChangeLanguageRequest({ locale: lng })
      } catch (e) { }
    }
  };

  const sendChangeLanguageRequest = (data: ChangeLanguageRequest) => {
    return axios.put<ChangeLanguageRequest>(`${environment.apiBaseUrl}/account/self/change-locale`, data, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    });
  }

  return (
    <div>
      <MenuItem sx={menuStyle} onClick={() => changeLanguage('en')}>
        <ListItemIcon>{i18n.language === 'en' ? <RadioButtonCheckedIcon /> : <RadioButtonUncheckedIcon />}</ListItemIcon>
        <ListItemText>{t('nav.language.english')}</ListItemText>
      </MenuItem>
      <MenuItem sx={menuStyle} onClick={() => changeLanguage('pl')}>
        <ListItemIcon>{i18n.language === 'pl' ? <RadioButtonCheckedIcon /> : <RadioButtonUncheckedIcon />}</ListItemIcon>
        <ListItemText>{t('nav.language.polish')}</ListItemText>
      </MenuItem>
    </div>
  )
}