import { Autocomplete, Button, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField } from "@mui/material"
import { CSSProperties, useEffect } from "react"
import { AuthenticatedAccountRole } from "../../utils/types"
import { useTranslation } from "react-i18next"
import { Controller, useForm } from "react-hook-form"
import z from "zod"
import { zodResolver } from "@hookform/resolvers/zod"

const schema = z.object({
  role: z.nativeEnum(AuthenticatedAccountRole, { message: 'manage_accounts.invalid_role' })
})

type ChangeRoleData = z.infer<typeof schema>

type ChangeAccountRoleDialogProps = {
  currentRole: AuthenticatedAccountRole | undefined
  open: boolean
  onClose: () => void
  onValid: ( data: ChangeRoleData) => void
  style?: CSSProperties
}

export default function ChangeAccountRoleDialog({ currentRole, open, onClose, onValid, style } : ChangeAccountRoleDialogProps) {
  const { t } = useTranslation();
  const { formState, control, handleSubmit, reset } = useForm<ChangeRoleData>({
    mode: 'onChange',
    resolver: zodResolver(schema)   
  });
  const { errors, isValid } = formState;

  useEffect(() => {
    reset({role: currentRole})
  }, [currentRole])

  return (
    <Dialog open={open} onClose={onClose} sx={{ ...style, marginTop: '4vh' }} maxWidth='md'>
      <form onSubmit={handleSubmit(onValid)} noValidate>
        <DialogTitle variant='h3' textAlign='center'>{t('manage_accounts.change_role.title')}</DialogTitle>
        <DialogContent>
          <Stack direction='column' spacing={3} margin='40px'>
            <Controller
              name='role'
              control={control}
              render={({ field }) => (
                <Autocomplete
                  options={Object.values(AuthenticatedAccountRole).filter(role => role !== currentRole)}
                  onChange={(e, value) => field.onChange(value)}
                  isOptionEqualToValue={(option: any, value: any) => option.value === value.value}
                  value={Object.values(AuthenticatedAccountRole).find(option => option === field.value) || ''}
                  fullWidth
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      label={t('manage_accounts.label.role')}
                      error={Boolean(errors.role)}
                      helperText={errors.role?.message && t(errors.role.message)}
                      placeholder={t('manage_accounts.enter.role')}
                    />
                  )}
                />
              )}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Stack direction='row' spacing={4}>
            <Button onClick={onClose}>{t('manage_accouns.button.back')}</Button>
            <Button type='submit' variant='contained' disabled={!isValid}>{t('manage_accouns.button.submit')}</Button>
          </Stack>
        </DialogActions>
      </form>
    </Dialog>
  )
}