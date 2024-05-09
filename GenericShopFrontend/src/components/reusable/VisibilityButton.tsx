import { Visibility, VisibilityOff } from "@mui/icons-material"
import { IconButton, InputAdornment } from "@mui/material"

type ComponentParams = {
  visible: boolean
  onClick: () => void
}

export default function VisibilityButton({visible, onClick}: ComponentParams) {
  return (
    <InputAdornment position='end'>
      <IconButton onClick={onClick}>
        {visible ? <Visibility /> : <VisibilityOff />}
      </IconButton>
    </InputAdornment>
  )
}