import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Stack, TextField } from "@mui/material";
import { useForm } from "react-hook-form";
import z from "zod"
import { environment, regex } from "../../utils/constants";
import { CSSProperties, Dispatch, SetStateAction, useEffect } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslation } from "react-i18next";
import axios from "axios";
import { getJwtToken } from "../../services/tokenService";
import handleAxiosException from "../../services/apiService";
import { Account } from "../../utils/types";

const schema = z.object({
  firstName: z.string().regex(regex.CAPITALIZED, 'edit_self.not_valid.first_name'),
  lastName: z.string().regex(regex.CAPITALIZED, 'edit_self.not_valid.last_name'),
  address: z.object({
    postalCode: z.string().regex(regex.POSTAL_CODE, 'edit_self.not_valid.postal_code'),
    country: z.string().regex(regex.CAPITALIZED, 'edit_self.not_valid.country'),
    city: z.string().regex(regex.CAPITALIZED, 'edit_self.not_valid.city'),
    street: z.string().regex(regex.CAPITALIZED, 'edit_self.not_valid.street'),
    houseNumber: z.number({ message: 'edit_self.not_valid.house_number'}).positive('edit_self.not_valid.house_number')
  })
})

type EditContactData = z.infer<typeof schema>

type EditSelfAccountDialogProps = {
  open: boolean
  account: Account
  setAccount: Dispatch<SetStateAction<Account | null>>
  onClose: () => void
  style?: CSSProperties
  setLoading: (loading: boolean) => void
}

export default function EditSelfAccountDialog({ open, account, setAccount, onClose, style, setLoading } : EditSelfAccountDialogProps) {
  const { t } = useTranslation()
  const { register, handleSubmit, formState, reset } = useForm<EditContactData>({
    mode: 'onChange',
    resolver: zodResolver(schema),
    defaultValues: {
      firstName: account.firstName,
      lastName: account.lastName,
      address: {
        postalCode: account.address.postalCode,
        country: account.address.country,
        city: account.address.city,
        street: account.address.street,
        houseNumber: account.address.houseNumber
      }
    }
  })
  const { errors } = formState
  const fieldStyle = { height: '64px', width: '100%' }

  useEffect(() => {
    console.log('Use effect')
    reset(account)
  }, [open])

  const onValid = async (formData: EditContactData) => {
    try {
      setLoading(true)
      const version = account.version;
      const updateRequestData = { ...formData, version }
      const { data } = await sendEditContactRequest(updateRequestData)
      setAccount(data)
      reset(data)
      onClose();

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const sendEditContactRequest = async (data: EditContactData) => {
    return axios.put<Account>(`${environment.apiBaseUrl}/account/self/edit`, data, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  return (
    <Dialog open={open} onClose={onClose} sx={{...style}} fullWidth maxWidth='sm'>
      <DialogTitle variant='h3' textAlign='center'>{t('edit_self.title')}</DialogTitle>
      <form onSubmit={handleSubmit(onValid)} noValidate>
        <DialogContent>
          <Grid container columnSpacing={4} rowSpacing={6} justifyContent='center'>
            <Grid item xs={12} sm={6}>
              <TextField label={t('edit_self.label.first_name')} {...register('firstName')}
                placeholder={t('edit_self.enter.first_name')}
                error={Boolean(errors.firstName?.message)}
                helperText={errors.firstName?.message && t(errors.firstName.message)}
                sx={fieldStyle}
                autoComplete='true'
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField label={t('edit_self.label.last_name')} {...register('lastName')}
                placeholder={t('edit_self.enter.last_name')}
                error={Boolean(errors.lastName?.message)}
                helperText={errors.lastName?.message && t(errors.lastName.message)}
                sx={fieldStyle}
                autoComplete='true'
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField label={t('edit_self.label.postal_code')} {...register('address.postalCode')}
                placeholder={t('edit_self.enter.postal_code')}
                error={Boolean(errors.address?.postalCode?.message)}
                helperText={errors.address?.postalCode?.message && t(errors.address.postalCode.message)}
                sx={fieldStyle}
                autoComplete='true'
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField label={t('edit_self.label.country')} {...register('address.country')}
                placeholder={t('edit_self.enter.country')}
                error={Boolean(errors.address?.country?.message)}
                helperText={errors.address?.country?.message && t(errors.address.country.message)}
                sx={fieldStyle}
                autoComplete='true'
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField label={t('edit_self.label.city')} {...register('address.city')}
                placeholder={t('edit_self.enter.city')}
                error={Boolean(errors.address?.city?.message)}
                helperText={errors.address?.city?.message && t(errors.address.city.message)}
                sx={fieldStyle}
                autoComplete='true'
              />

            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField label={t('edit_self.label.street')} {...register('address.street')}
                placeholder={t('edit_self.enter.street')}
                error={Boolean(errors.address?.street?.message)}
                helperText={errors.address?.street?.message && t(errors.address.street.message)}
                sx={fieldStyle}
                autoComplete='true'
              />
            </Grid>

            <Grid item xs={12} sm={6} justifyContent='center'>
              <TextField label={t('edit_self.label.house_number')} {...register('address.houseNumber', { valueAsNumber: true })}
                type='number'
                placeholder={t('edit_self.enter.house_number')}
                error={Boolean(errors.address?.houseNumber?.message)}
                helperText={errors.address?.houseNumber?.message && t(errors.address.houseNumber.message)}
                sx={fieldStyle}
                autoComplete='true'
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions sx={{ justifyContent: 'center', marginTop: '12px', marginBottom: '20px' }}>
          <Stack direction='row' spacing={16}>
            <Stack direction='row' spacing={2}>
            <Button type='button' onClick={() => onClose()}>{t('edit_self.back')}</Button>
            <Button type='button' onClick={() => {
              reset(account)
            } }>{t('edit_self.reset')}</Button>
            </Stack>

            <Button type='submit' variant='contained'>{t('edit_self.submit')}</Button>
          </Stack>
        </DialogActions>
      </form>
    </Dialog>
  )
}