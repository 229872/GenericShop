import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import { SxProps, Theme } from '@mui/material';

type Action = {
  label: string
  onClick: () => void
  color?: "inherit" | "primary" | "secondary" | "success" | "error" | "info" | "warning"
}

type DialogProps = {
  open: boolean
  onClose: () => void
  title: string
  message: string
  actions: Action[]
  style?: SxProps<Theme> | undefined
}

function MuiDialog({ open, onClose, title, message, actions, style }: DialogProps) {
  return (
    <Dialog open={open} onClose={onClose} sx={style}>
      <DialogTitle><b>{title}</b></DialogTitle>
      <DialogContent>{message}</DialogContent>
      <DialogActions>
        {actions.map((action, index) => (
          <Button key={index} onClick={action.onClick} color={action.color}>
            {action.label}
          </Button>
        ))}
      </DialogActions>
    </Dialog>
  );
}

export default MuiDialog;