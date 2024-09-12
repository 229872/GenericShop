import { CSSProperties, Dispatch, SetStateAction } from "react"
import { useTranslation } from "react-i18next"
import { Role } from "../../utils/types"
import { ListItemIcon, ListItemText, MenuItem } from "@mui/material"
import RadioButtonCheckedIcon from '@mui/icons-material/RadioButtonChecked';
import RadioButtonUncheckedIcon from '@mui/icons-material/RadioButtonUnchecked';

type ChangeActiveRoleMenuParams = {
  menuStyle: CSSProperties
  roles: Role[]
  setActiveRole: (role: Role) => void
  activeRole: Role
}

export default function ChangeActiveRoleMenu({ menuStyle, roles, setActiveRole, activeRole } : ChangeActiveRoleMenuParams) {
  const { t, i18n } = useTranslation()

  const changeActiveRole =  (newRole: Role): void => {
    setActiveRole(newRole)
  };

  return (
    <div>
      {roles.map((role, key) => (
        <MenuItem key={key} sx={menuStyle} onClick={() => changeActiveRole(role)}>
          <ListItemIcon>{activeRole === role ? <RadioButtonCheckedIcon /> : <RadioButtonUncheckedIcon />}</ListItemIcon>
          <ListItemText>{t(`self.roles.value.${role}`)}</ListItemText>
        </MenuItem>
      ))}

    </div>
  )
}