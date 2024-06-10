import { CSSProperties, Dispatch, SetStateAction, useEffect, useState } from "react"
import { AccountState, Role } from "../../utils/types"
import { Autocomplete, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Stack, Step, StepLabel, Stepper, TextField, Typography } from "@mui/material"
import { useTranslation } from "react-i18next"
import axios from "axios"
import { environment, regex } from "../../utils/constants"
import { getJwtToken } from "../../services/tokenService"
import handleAxiosException from "../../services/apiService"
import z from "zod"
import { toast } from "sonner"
import { TFunction } from "i18next"
import { Control, Controller, FormState, UseFormRegister, UseFormWatch, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import VisibilityButton from "../reusable/VisibilityButton"

type CreateAccountDialogProps = {
  open: boolean
  onClose: () => void
  setLoading: (loading: boolean) => void
  style?: CSSProperties
}

export default function CreateAccountDialog({ open, onClose, setLoading, style } : CreateAccountDialogProps) {
  const steps: string[] = ['create_account.step.1.title', 'create_account.step.2.title'];
  const [ activeStep, setActiveStep ] = useState<1 | 2>(1);
  const [ isStep1Valid, setIsStep1Valid ] = useState<boolean>(false)
  const { t } = useTranslation();
  const { register, handleSubmit, formState, control, watch, reset } = useForm<CreateAccountData>({
    mode: 'onBlur', resolver: zodResolver(createAccountSchema)
  })
  const { isValid } = formState;

  useEffect(() => {
    reset()
  }, [open])

  const createAccount = async (account: CreateAccountData) => {
    return axios.post<CreateAccountData>(`${environment.apiBaseUrl}/accounts`, account, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const onValid = async (account: CreateAccountData)=> {
    try {
      setLoading(true)
      const { data } = await createAccount(account);
      toast.success(t('create_account.success'))
      onClose()

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const renderStep = (activeStep: 1 | 2) => {
    switch (activeStep) {
      case 1:
        return <Step1 t={t} register={register} formState={formState} control={control} setIsStep1Valid={setIsStep1Valid} watch={watch} />
      case 2:
        return <Step2 t={t} register={register} formState={formState} />
    }
  }

  const renderButtons = (activeStep: 1 | 2) => {
    switch (activeStep) {
      case 1:
        return <Stack direction='row' spacing={2} sx={{ marginTop: '40px' }}>
          <Button type='button' variant='contained' onClick={() => onClose()}>
            <Typography>{t('create_account.step.1.action.back')}</Typography>
          </Button>

          <Button type='button' variant='contained' disabled={!isStep1Valid} onClick={() => setActiveStep(2)}>
            <Typography>{t('create_account.step.1.action.next_step')}</Typography>
          </Button>
        </Stack>

      
      case 2:
        return <Stack direction='row' spacing={2} sx={{ marginTop: '40px' }}>
          <Button type='button' onClick={() => setActiveStep(1)}>
            <Typography>{t('create_account.step.2.action.back')}</Typography>
          </Button>

          <Button type='submit' disabled={!isValid} variant='contained'>
            <Typography>{t('create_account.step.2.action.submit')}</Typography>
          </Button>
        </Stack>
    }
  }

  return (
    <Dialog open={open} onClose={onClose} sx={{...style, marginTop: '4vh'}} maxWidth='md'>
      <form onSubmit={handleSubmit(onValid)} noValidate>
        <DialogContent>
          <Stepper activeStep={activeStep - 1}>
            {
              steps.map((label, key) => (
                <Step key={key}>
                  <StepLabel>{t(label)}</StepLabel>
                </Step>
              ))
            }
          </Stepper>

          { renderStep(activeStep) }
        </DialogContent>
        <DialogActions>
          { renderButtons(activeStep) }
        </DialogActions>
      </form>
    </Dialog>
  )
}



const step1Schema = z.object({
  login: z.string().regex(regex.LOGIN, 'create_account.step.1.error.login'),
  email: z.string().email('create_account.step.1.error.email'),
  password: z.string().regex(regex.PASSWORD, 'create_account.step.1.error.password'),
  locale: z.enum(environment.supportedLanguages as readonly [string, ...string[]], { message: 'create_account.step.1.error.language'}),
  accountState: z.nativeEnum(AccountState, { message: 'create_account.step.1.error.account_state'}),
  role: z.nativeEnum(Role, { message: 'create_account.step.1.error.account_role'})
});

type CreateAccountStep1 = z.infer<typeof step1Schema>;

type Step1Params = {
  t: TFunction<"translation", undefined>
  register: UseFormRegister<CreateAccountData>
  formState: FormState<CreateAccountData>
  control: Control<CreateAccountData>
  setIsStep1Valid: Dispatch<SetStateAction<boolean>>
  watch: UseFormWatch<CreateAccountData>
}

function Step1({ t, register, formState, control, setIsStep1Valid, watch }: Step1Params) {
  const fieldStyleForStep1 = {height: '64px', width: '100%'};
  const [ passwordVisible, setPasswordVisible ] = useState<boolean>(false);
  const { errors } = formState;

  const watchedFields = watch(['login', 'email', 'password', 'locale', 'accountState', 'role'])

  useEffect(() => {
    const [login, email, password, locale, accountState, role] = watchedFields;
    const formData: CreateAccountStep1 = { login, email, password, locale, accountState, role };
    try {
      step1Schema.parse(formData)
      setIsStep1Valid(true)
    } catch(e) {
      setIsStep1Valid(false)
    }
  }, [watchedFields]);

  return (
    <>
      <Stack spacing={5} sx={{ margin: '35px' }}>
        <Typography variant='h3' textAlign='center'><b>{t('create_account.title')}</b></Typography>
      </Stack>

      <Grid container spacing={5}>
        <Grid item xs={12} sm={6}>
          <TextField label={t('create_account.step.1.label.login')} {...register('login')}
            error={Boolean(errors.login?.message)}
            placeholder={t('create_account.step.1.enter.login')}
            helperText={errors.login?.message && t(errors.login.message)}
            sx={fieldStyleForStep1}
            autoComplete='true'
          />
        </Grid>

        <Grid item xs={12} sm={6}>
          <TextField label={t('create_account.step.1.label.password')} {...register('password')} type={passwordVisible ? 'text' : 'password'}
            error={Boolean(errors.password?.message)}
            placeholder={t('create_account.step.1.enter.password')}
            helperText={errors.password?.message && t(errors.password.message)}
            sx={fieldStyleForStep1}
            InputProps={{
              endAdornment: <VisibilityButton visible={passwordVisible} onClick={() => setPasswordVisible(!passwordVisible)} />
            }}
            autoComplete='true'
          />
        </Grid>

        <Grid item xs={12} sm={6}>
          <TextField label={t('create_account.step.1.label.email')} {...register('email')} type='email'
            error={Boolean(errors.email?.message)}
            placeholder={t('create_account.step.1.enter.email')}
            helperText={errors.email?.message && t(errors.email.message)}
            sx={fieldStyleForStep1}
            autoComplete='true'
          />
        </Grid>

        <Grid item xs={12} sm={6}>
          <Controller
            name='locale'
            control={control}
            render={({ field }) => (
              <Autocomplete
                options={environment.supportedLanguages}
                sx={fieldStyleForStep1}
                onChange={(e, value) => field.onChange(value)}
                isOptionEqualToValue={(option: any, value: any) => option.value === value.value}
                value={environment.supportedLanguages.find(option => option === field.value) || ''}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label={t('create_account.step.1.label.language')}
                    error={Boolean(errors.locale)}
                    helperText={errors.locale?.message && t(errors.locale.message)}
                    placeholder={t('create_account.step.1.enter.language')}
                  />
                )}
              />
            )}
          />
        </Grid>

        <Grid item xs={12} sm={6}>
          <Controller
            name='accountState'
            control={control}
            render={({ field }) => (
              <Autocomplete
                options={Object.values(AccountState)}
                sx={fieldStyleForStep1}
                onChange={(e, value) => field.onChange(value)}
                isOptionEqualToValue={(option: any, value: any) => option.value === value.value}
                value={Object.values(AccountState).find(option => option === field.value) || ''}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label={t('create_account.step.1.label.account_state')}
                    error={Boolean(errors.accountState)}
                    helperText={errors.accountState?.message && t(errors.accountState.message)}
                    placeholder={t('create_account.step.1.enter.account_state')}
                  />
                )}
              />
            )}
          />
        </Grid>

        <Grid item xs={12} sm={6}>
          <Controller
            name='role'
            control={control}
            render={({ field }) => (
              <Autocomplete
                options={Object.values(Role)}
                sx={fieldStyleForStep1}
                onChange={(e, value) => field.onChange(value)}
                isOptionEqualToValue={(option: any, value: any) => option.value === value.value}
                value={Object.values(Role).find(option => option === field.value) || ''}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label={t('create_account.step.1.label.account_role')}
                    error={Boolean(errors.role)}
                    helperText={errors.role?.message && t(errors.role.message)}
                    placeholder={t('create_account.step.1.enter.account_role')}
                  />
                )}
              />
            )}
          />
        </Grid>
      </Grid>
    </>
  )
}

const step2Schema = z.object({
  firstName: z.string().regex(regex.CAPITALIZED, 'create_account.step.2.error.firstname'),
  lastName: z.string().regex(regex.CAPITALIZED, 'create_account.step.2.error.lastname'),
  address: z.object({
    postalCode: z.string().regex(regex.POSTAL_CODE, 'create_account.step.2.error.postal_code'),
    country: z.string().regex(regex.CAPITALIZED, 'create_account.step.2.error.country'),
    city: z.string().regex(regex.CAPITALIZED, 'create_account.step.2.error.city'),
    street: z.string().regex(regex.CAPITALIZED, 'create_account.step.2.error.street'),
    houseNumber: z.number({message: 'create_account.step.2.error.house_number'}).int('create_account.step.2.error.house_number').positive('create_account.step.2.error.house_number')
  })
});

type CreateAccountStep2 = z.infer<typeof step2Schema>

type Step2Params = {
  t: TFunction<"translation", undefined>
  register: UseFormRegister<CreateAccountData>
  formState: FormState<CreateAccountData>
}

function Step2({ t, register, formState }: Step2Params) {
  const fieldStyleForStep2 = {height: '64px', width: '100%'};
  const { errors } = formState;

  return (
    <>
      <Stack spacing={5} sx={{ margin: '35px' }}>
        <Typography variant='h3' textAlign='center'><b>{t('register.title')}</b></Typography>
      </Stack>

      <Grid container spacing={5}>
        <Grid item xs={12} sm={6}>
          <TextField label={t('create_account.step.2.label.firstname')} {...register('firstName')}
            error={Boolean(errors.firstName?.message)}
            placeholder={t('create_account.step.2.enter.firstname')}
            helperText={errors.firstName?.message && t(errors.firstName.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('create_account.step.2.label.lastname')} {...register('lastName')}
            error={Boolean(errors.lastName?.message)}
            placeholder={t('create_account.step.2.enter.lastname')}
            helperText={errors.lastName?.message && t(errors.lastName.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('create_account.step.2.label.postal_code')} {...register('address.postalCode')}
            error={Boolean(errors.address?.postalCode?.message)}
            placeholder={t('create_account.step.2.enter.postal_code')}
            helperText={errors.address?.postalCode?.message && t(errors.address.postalCode.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('create_account.step.2.label.country')} {...register('address.country')}
            error={Boolean(errors.address?.country?.message)}
            placeholder={t('create_account.step.2.enter.country')}
            helperText={errors.address?.country?.message && t(errors.address.country.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('create_account.step.2.label.city')} {...register('address.city')}
            error={Boolean(errors.address?.city?.message)}
            placeholder={t('create_account.step.2.enter.city')}
            helperText={errors.address?.city?.message && t(errors.address.city.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('create_account.step.2.label.street')} {...register('address.street')}
            error={Boolean(errors.address?.street?.message)}
            placeholder={t('create_account.step.2.enter.street')}
            helperText={errors.address?.street?.message && t(errors.address.street.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('create_account.step.2.label.house_number')} {...register('address.houseNumber', { valueAsNumber: true })} type='number'
            error={Boolean(errors.address?.houseNumber?.message)}
            placeholder={t('create_account.step.2.enter.house_number')}
            helperText={errors.address?.houseNumber?.message && t(errors.address.houseNumber.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
      </Grid>
    </>
  )
}


const combinedSchema = step1Schema.merge(step2Schema);

const createAccountSchema = z.object({
  login: combinedSchema.shape.login,
  email: combinedSchema.shape.email,
  password: combinedSchema.shape.password,
  locale: combinedSchema.shape.locale,
  role: combinedSchema.shape.role,
  accountState: combinedSchema.shape.accountState,
  firstName: combinedSchema.shape.firstName,
  lastName: combinedSchema.shape.lastName,
  address: combinedSchema.shape.address
})

type CreateAccountData = z.infer<typeof createAccountSchema>