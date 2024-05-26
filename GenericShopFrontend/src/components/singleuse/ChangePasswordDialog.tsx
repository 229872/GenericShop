import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField } from "@mui/material";
import { useForm } from "react-hook-form";
import z from "zod"
import { environment, regex } from "../../utils/constants";
import { CSSProperties, useState } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslation } from "react-i18next";
import VisibilityButton from "../reusable/VisibilityButton";
import axios from "axios";
import { getJwtToken } from "../../services/tokenService";
import handleAxiosException from "../../services/apiService";

const schema = z.object({
  currentPassword: z.string().regex(regex.PASSWORD, 'change_password.current_password.not_valid'),
  newPassword: z.string().regex(regex.PASSWORD, 'change_password.new_password.not_valid'),
  repeatNewPassword: z.string()
}).refine(data => data.currentPassword !== data.newPassword, {
  message: 'change_password.new_password_same_as_current',
  path: ['newPassword']
}).refine(data => data.newPassword === data.repeatNewPassword, {
  message: 'change_password.repeat_password.not_valid',
  path: ['repeatNewPassword']
});

type ChangePasswordData = z.infer<typeof schema>

type ChangePasswordRequest = {
  currentPassword: string
  newPassword: string
}

type ChangePasswordDialogProps = {
  open: boolean
  onClose: () => void
  style?: CSSProperties
  setLoading: (loading: boolean) => void
}

export default function ChangePasswordDialog({ open, onClose, style, setLoading } : ChangePasswordDialogProps) {
  const { t } = useTranslation()
  const { register, handleSubmit, formState, reset } = useForm<ChangePasswordData>({
    mode: 'onChange',
    resolver: zodResolver(schema)
  })
  const { errors, isValid } = formState
  const [ currentPasswordVisible, setCurrentPasswordVisible ] = useState<boolean>(false)
  const [ newPasswordVisible, setNewPasswordVisible ] = useState<boolean>(false)
  const fieldStyle = { height: '64px', width: '75%' }

  const onValid = async (data: ChangePasswordData) => {
    try {
      setLoading(true)
      const { repeatNewPassword, ...requestData } = data
      await sendChangePasswordRequest(requestData)
      reset()
      onClose();

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const sendChangePasswordRequest = async (data: ChangePasswordRequest) => {
    return axios.put<ChangePasswordRequest>(`${environment.apiBaseUrl}/account/self/change-password`, data, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  return (
    <Dialog open={open} onClose={onClose} sx={{...style}} fullWidth maxWidth='sm'>
      <DialogTitle variant='h3' textAlign='center'>{t('change_password.title')}</DialogTitle>
      <form onSubmit={handleSubmit(onValid)} noValidate>
        <DialogContent>
          <Stack direction='column' spacing={4} alignItems='center'>
            <TextField label={t('change_password.label.current_password')} {...register('currentPassword')}
              type={currentPasswordVisible ? 'text' : 'password'}
              placeholder={t('change_password.enter.current_password')}
              error={Boolean(errors.currentPassword?.message)}
              helperText={errors.currentPassword?.message && t(errors.currentPassword.message)}
              sx={fieldStyle}
              InputProps={{
                endAdornment: (
                  <VisibilityButton visible={currentPasswordVisible} onClick={() => setCurrentPasswordVisible(!currentPasswordVisible)} />
                )
              }}
              autoComplete='true'
            />

            <TextField label={t('change_password.label.new_password')} {...register('newPassword')}
              type={newPasswordVisible ? 'text' : 'password'}
              placeholder={t('change_password.enter.new_password')}
              error={Boolean(errors.newPassword?.message)}
              helperText={errors.newPassword?.message && t(errors.newPassword.message)}
              sx={fieldStyle}
              InputProps={{
                endAdornment: (
                  <VisibilityButton visible={newPasswordVisible} onClick={() => setNewPasswordVisible(!newPasswordVisible)} />
                )
              }}
              autoComplete='true'
            />

            <TextField label={t('change_password.label.repeat_new_password')} {...register('repeatNewPassword')}
              type={newPasswordVisible ? 'text' : 'password'}
              placeholder={t('change_password.enter.repeat_new_password')}
              error={Boolean(errors.repeatNewPassword?.message)}
              helperText={errors.repeatNewPassword?.message && t(errors.repeatNewPassword.message)}
              sx={fieldStyle}
              InputProps={{
                endAdornment: (
                  <VisibilityButton visible={newPasswordVisible} onClick={() => setNewPasswordVisible(!newPasswordVisible)} />
                )
              }}
              autoComplete='true'
            />
          </Stack>
        </DialogContent>
        <DialogActions sx={{ justifyContent: 'center', marginTop: '12px', marginBottom: '20px' }}>
          <Stack direction='row' spacing={16}>
            <Stack direction='row' spacing={2}>
            <Button type='button' onClick={() => onClose()}>{t('change_password.back')}</Button>
            <Button type='button' onClick={() => reset()}>{t('change_password.reset')}</Button>
            </Stack>

            <Button type='submit' variant='contained' disabled={!isValid}>{t('change_password.submit')}</Button>
          </Stack>
        </DialogActions>
      </form>
    </Dialog>
  )
}