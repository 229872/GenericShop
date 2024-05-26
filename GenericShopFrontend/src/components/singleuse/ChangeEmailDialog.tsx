import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField } from "@mui/material";
import { useForm } from "react-hook-form";
import z from "zod"
import { environment } from "../../utils/constants";
import { CSSProperties } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslation } from "react-i18next";
import axios from "axios";
import { getJwtToken } from "../../services/tokenService";
import handleAxiosException from "../../services/apiService";

const schema = z.object({
  newEmail: z.string().email('change_email.not_valid.wrong_format')
})

type ChangeEmailRequest = z.infer<typeof schema>


type ChangeEmailDialogProps = {
  open: boolean
  onClose: () => void
  style?: CSSProperties
  setLoading: (loading: boolean) => void
}

export default function ChangeEmailDialog({ open, onClose, style, setLoading } : ChangeEmailDialogProps) {
  const { t } = useTranslation()
  const { register, handleSubmit, formState, reset } = useForm<ChangeEmailRequest>({
    mode: 'onChange',
    resolver: zodResolver(schema)
  })
  const { errors, isValid } = formState
  const fieldStyle = { height: '64px', width: '75%' }

  const onValid = async (data: ChangeEmailRequest) => {
    try {
      setLoading(true)
      await sendChangeEmailRequest(data)
      reset()
      onClose();

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const sendChangeEmailRequest = async (data: ChangeEmailRequest) => {
    return axios.put<ChangeEmailRequest>(`${environment.apiBaseUrl}/account/self/change-email`, data, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  return (
    <Dialog open={open} onClose={onClose} sx={{...style}} fullWidth maxWidth='sm'>
      <DialogTitle variant='h3' textAlign='center'>{t('change_email.title')}</DialogTitle>
      <form onSubmit={handleSubmit(onValid)} noValidate>
        <DialogContent>
          <Stack direction='column' spacing={4} alignItems='center'>
            <TextField label={t('change_email.label.new_email')} {...register('newEmail')}
              type='email'
              placeholder={t('change_email.enter.email')}
              error={Boolean(errors.newEmail?.message)}
              helperText={errors.newEmail?.message && t(errors.newEmail.message)}
              sx={fieldStyle}
              autoComplete='true'
            />
          </Stack>
        </DialogContent>
        <DialogActions sx={{ justifyContent: 'center', marginTop: '12px', marginBottom: '20px' }}>
          <Stack direction='row' spacing={16}>
            <Stack direction='row' spacing={2}>
            <Button type='button' onClick={() => onClose()}>{t('change_email.back')}</Button>
            <Button type='button' onClick={() => reset()}>{t('change_email.reset')}</Button>
            </Stack>

            <Button type='submit' variant='contained' disabled={!isValid}>{t('change_email.submit')}</Button>
          </Stack>
        </DialogActions>
      </form>
    </Dialog>
  )
}