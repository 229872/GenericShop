import { Button, Dialog, DialogActions, DialogContent, Stack } from "@mui/material";
import axios from "axios";
import { CSSProperties } from "react";
import z from "zod";
import { environment } from "../../utils/constants";
import { useTranslation } from "react-i18next";


type ViewProductDetailsDialogProps = {
  productId: number
  open: boolean
  onClose: () => void
  setLoading: (loading: boolean) => void
  style?: CSSProperties
}

export default function ViewProductDetailsDialog({ open, onClose, setLoading, style } : ViewProductDetailsDialogProps) {
  const { t } = useTranslation();

  return (
    <Dialog open={open} onClose={onClose} sx={{ ...style, marginTop: '4vh' }} maxWidth='md'>
      <DialogContent>
        View product details dialog
      </DialogContent>
      <DialogActions>
        <Stack direction='row' spacing={3}>
          <Button onClick={() => onClose()}>{t('manage_products.button.back')}</Button>
        </Stack>
      </DialogActions>
    </Dialog>
  )
}