import { Button, Dialog, DialogActions, DialogContent, Stack } from "@mui/material";
import axios from "axios";
import { CSSProperties } from "react";
import { useForm } from "react-hook-form";
import z from "zod";
import { environment } from "../../utils/constants";
import { useTranslation } from "react-i18next";


type EditProductDialogProps = {
  productId: number
  open: boolean
  onClose: () => void
  setLoading: (loading: boolean) => void
  style?: CSSProperties
}

export default function EditProductDialog({ productId, open, onClose, setLoading, style } : EditProductDialogProps) {
  const { t } = useTranslation();
  const { register, reset, formState, handleSubmit } = useForm()
  const { errors, isValid } = formState;

  const onValid = () => {

  }


  return (
    <Dialog open={open} onClose={onClose} sx={{ ...style, marginTop: '4vh' }} maxWidth='md'>
      <form onSubmit={handleSubmit(onValid)} noValidate>
        <DialogContent>
          Edit product dialog
        </DialogContent>
        <DialogActions>
          <Stack direction='row' spacing={3}>
            <Button onClick={() => onClose()}>{t('manage_products.button.back')}</Button>
            <Button type='submit'>{t('manage_products.button.submit')}</Button>
          </Stack>
        </DialogActions>
      </form>
    </Dialog>
  )
}