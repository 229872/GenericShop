import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';

type DialogProps = {
  open: boolean
  onClose: any
  title: string
  message: string
  actions: any
}

function MuiDialog({ open, onClose, title, message, actions }: DialogProps) {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>{title}</DialogTitle>
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