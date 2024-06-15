import { Button, Dialog, DialogActions, DialogContent, Stack } from "@mui/material";
import axios from "axios";
import { CSSProperties } from "react";
import { useForm } from "react-hook-form";
import z from "zod";
import { environment } from "../../utils/constants";
import { useTranslation } from "react-i18next";

const schema = z.object({
  name: z.string({ message: '' })
})

type CreateProductDialogProps = {
  open: boolean
  onClose: () => void
  setLoading: (loading: boolean) => void
  style?: CSSProperties
}

export default function CreateProductDialog({ open, onClose, setLoading, style } : CreateProductDialogProps) {
  const { t } = useTranslation();
  const { register, reset, formState, handleSubmit } = useForm()
  const { errors, isValid } = formState;

  const onValid = () => {

  }


  return (
    <Dialog open={open} onClose={onClose} sx={{ ...style, marginTop: '4vh' }} maxWidth='md'>
      <form onSubmit={handleSubmit(onValid)} noValidate>
        <DialogContent>
          Create product dialog
        </DialogContent>
        <DialogActions>
          <Stack direction='row' spacing={3}>
            <Button onClick={() => onClose()}>{t('manage_products.button.back')}</Button>
            <Button type='submit'>Find category properties</Button>
          </Stack>
        </DialogActions>
      </form>
    </Dialog>
  )
}