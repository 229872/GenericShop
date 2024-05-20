import { Autocomplete, Button, Card, CardActions, CardContent, Grid, Stack, Step, StepLabel, Stepper, TextField, Typography } from "@mui/material";
import { CSSProperties, Dispatch, SetStateAction, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { z } from "zod";
import { environment, regex } from "../utils/constants";
import { Control, Controller, FormState, UseFormRegister, UseFormWatch, useForm } from "react-hook-form";
import VisibilityButton from "../components/reusable/VisibilityButton";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Link } from "react-router-dom";
import { TFunction } from "i18next";
import handleAxiosException from "../services/apiService";
import { AUTH_PATH } from "../components/singleuse/Routing";


type RegisterPageParams = {
  setLoading: (state: boolean) => void
  style: CSSProperties
}

export default function RegisterPage({ setLoading, style } : RegisterPageParams) {
  const steps: string[] = ['register.step.1.title', 'register.step.2.title', 'register.step.3.title'];
  const [ activeStep, setActiveStep ] = useState<1 | 2 | 3>(1);
  const [ isStep1Valid, setIsStep1Valid ] = useState<boolean>(false);
  const { t } = useTranslation();
  const { register, handleSubmit, formState, control, watch } = useForm<RegisterAccountData>({
    mode: 'onBlur', resolver: zodResolver(registerSchema)
  })
  const { isValid } = formState;

  const onValid = async (data: RegisterAccountData) => {
    try {
      setLoading(true)
      await registerAccount(data);
      setActiveStep(3)

    } catch (e: any) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const registerAccount = async (data: RegisterAccountData) => {
    return await axios.post<RegisterAccountData>(`${environment.apiBaseUrl}/account/self/register`, data);
  }
  
  const renderStep = (activeStep: 1 | 2 | 3) => {
    switch (activeStep) {
      case 1:
        return <Step1 t={t} register={register} formState={formState} control={control} setIsStep1Valid={setIsStep1Valid} watch={watch} />
      case 2:
        return <Step2 t={t} register={register} formState={formState} control={control} />
      case 3:
        return <Step3 />
    }
  }

  const renderButtons = (activeStep: 1 | 2 | 3) => {
    switch (activeStep) {
      case 1:
        return <Button type='button' variant='contained' disabled={!isStep1Valid} onClick={() => setActiveStep(2)}>
          <Typography>{t('register.step.1.action.next_step')}</Typography>
        </Button>
      
      case 2:
        return <Stack direction='row' spacing={10} sx={{ marginTop: '40px' }}>
          <Button type='button' onClick={() => setActiveStep(1)}>
            <Typography>{t('register.step.2.action.back')}</Typography>
          </Button>

          <Button type='submit' disabled={!isValid} variant='contained'>
            <Typography>{t('register.step.2.action.submit')}</Typography>
          </Button>
        </Stack>
    }
  }

  return (
    <>
      <Card elevation={20} sx={style}>
        <form onSubmit={handleSubmit(onValid)} noValidate>
          <CardContent>
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
          </CardContent>

          <CardActions sx={{ justifyContent: 'center', marginBottom: '20px' }}>
            { renderButtons(activeStep) }
          </CardActions>
        </form>
      </Card>
    </>
  )
}



const step1Schema = z.object({
  login: z.string().regex(regex.LOGIN, 'register.step.1.error.login'),
  email: z.string().email('register.step.1.error.email'),
  password: z.string().regex(regex.PASSWORD, 'register.step.1.error.password'),
  locale: z.enum(environment.supportedLanguages as readonly [string, ...string[]], {message: 'register.step.1.error.language'})
});

type RegisterStep1 = z.infer<typeof step1Schema>;

type Step1Params = {
  t: TFunction<"translation", undefined>
  register: UseFormRegister<RegisterAccountData>
  formState: FormState<RegisterAccountData>
  control: Control<RegisterAccountData>
  setIsStep1Valid: Dispatch<SetStateAction<boolean>>
  watch: UseFormWatch<RegisterAccountData>
}

function Step1({ t, register, formState, control, setIsStep1Valid, watch }: Step1Params) {
  const fieldStyleForStep1 = {height: '64px', width: '60%'}
  const [ passwordVisible, setPasswordVisible ] = useState<boolean>(false);
  const { errors } = formState;

  const watchedFields = watch(['login', 'email', 'password', 'locale']);

  useEffect(() => {
    const [login, email, password, locale] = watchedFields;
    const formData: RegisterStep1 = { login, email, password, locale };
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
        <Typography variant='h3' textAlign='center'><b>{t('register.title')}</b></Typography>
      </Stack>

      <Stack direction='column' spacing={5} sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '30px' }}>
        <TextField label={t('register.step.1.label.login')} {...register('login')}
          error={Boolean(errors.login?.message)}
          placeholder={t('register.step.1.enter.login')}
          helperText={errors.login?.message && t(errors.login.message)}
          sx={fieldStyleForStep1}
          autoComplete='true'
        />

        <TextField label={t('register.step.1.label.password')} {...register('password')} type={passwordVisible ? 'text' : 'password'}
          error={Boolean(errors.password?.message)}
          placeholder={t('register.step.1.enter.password')}
          helperText={errors.password?.message && t(errors.password.message)}
          sx={fieldStyleForStep1}
          InputProps={{
            endAdornment: <VisibilityButton visible={passwordVisible} onClick={() => setPasswordVisible(!passwordVisible)} />
          }}
          autoComplete='true'
        />

        <TextField label={t('register.step.1.label.email')} {...register('email')} type='email'
          error={Boolean(errors.email?.message)}
          placeholder={t('register.step.1.enter.email')}
          helperText={errors.email?.message && t(errors.email.message)}
          sx={fieldStyleForStep1}
          autoComplete='true'
        />

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
                  label={t('register.step.1.label.language')}
                  error={Boolean(errors.locale)}
                  helperText={errors.locale?.message && t(errors.locale.message)}
                  placeholder={t('register.step.1.enter.language')}
                />
              )}
            />
          )}
        />
      </Stack>
    </>
  )
}



const step2Schema = z.object({
  firstName: z.string().regex(regex.CAPITALIZED, 'register.step.2.error.firstname'),
  lastName: z.string().regex(regex.CAPITALIZED, 'register.step.2.error.lastname'),
  address: z.object({
    postalCode: z.string().regex(regex.POSTAL_CODE, 'register.step.2.error.postal_code'),
    country: z.string().regex(regex.CAPITALIZED, 'register.step.2.error.country'),
    city: z.string().regex(regex.CAPITALIZED, 'register.step.2.error.city'),
    street: z.string().regex(regex.CAPITALIZED, 'register.step.2.error.street'),
    houseNumber: z.number({message: 'register.step.2.error.house_number'}).int('register.step.2.error.house_number').positive('register.step.2.error.house_number')
  })
});

type RegisterStep2 = z.infer<typeof step2Schema>

type Step2Params = {
  t: TFunction<"translation", undefined>
  register: UseFormRegister<RegisterAccountData>
  formState: FormState<RegisterAccountData>
  control: Control<RegisterAccountData>
}

function Step2({ t, register, formState, control }: Step2Params) {
  const fieldStyleForStep2 = {height: '64px', width: '100%'};
  const { errors } = formState;

  return (
    <>
      <Stack spacing={5} sx={{ margin: '35px' }}>
        <Typography variant='h3' textAlign='center'><b>{t('register.title')}</b></Typography>
      </Stack>

      <Grid container spacing={5}>
        <Grid item xs={12} sm={6}>
          <TextField label={t('register.step.2.label.firstname')} {...register('firstName')}
            error={Boolean(errors.firstName?.message)}
            placeholder={t('register.step.2.enter.firstname')}
            helperText={errors.firstName?.message && t(errors.firstName.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('register.step.2.label.lastname')} {...register('lastName')}
            error={Boolean(errors.lastName?.message)}
            placeholder={t('register.step.2.enter.lastname')}
            helperText={errors.lastName?.message && t(errors.lastName.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('register.step.2.label.postal_code')} {...register('address.postalCode')}
            error={Boolean(errors.address?.postalCode?.message)}
            placeholder={t('register.step.2.enter.postal_code')}
            helperText={errors.address?.postalCode?.message && t(errors.address.postalCode.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('register.step.2.label.country')} {...register('address.country')}
            error={Boolean(errors.address?.country?.message)}
            placeholder={t('register.step.2.enter.country')}
            helperText={errors.address?.country?.message && t(errors.address.country.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('register.step.2.label.city')} {...register('address.city')}
            error={Boolean(errors.address?.city?.message)}
            placeholder={t('register.step.2.enter.city')}
            helperText={errors.address?.city?.message && t(errors.address.city.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('register.step.2.label.street')} {...register('address.street')}
            error={Boolean(errors.address?.street?.message)}
            placeholder={t('register.step.2.enter.street')}
            helperText={errors.address?.street?.message && t(errors.address.street.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField label={t('register.step.2.label.house_number')} {...register('address.houseNumber', { valueAsNumber: true })} type='number'
            error={Boolean(errors.address?.houseNumber?.message)}
            placeholder={t('register.step.2.enter.house_number')}
            helperText={errors.address?.houseNumber?.message && t(errors.address.houseNumber.message)}
            sx={fieldStyleForStep2}
          />
        </Grid>
      </Grid>
    </>
  )
}



function Step3() {
  const { t } = useTranslation()

  return (
    <>
      <Stack spacing={5} sx={{ margin: '35px' }}>
        <Typography variant='h3' textAlign='center'><b>{t('register.step.3.label')}</b></Typography>
      </Stack>

      <Stack direction='column' spacing={5}>
        <Typography variant='body1'>
          {t('register.step.3.content.1')}
          <br />
          <br />
          {t('register.step.3.content.2')}
        </Typography>

        <Typography variant='body1'>
          {t('register.step.3.content.3')}
          <Link to={AUTH_PATH}>{t('register.step.3.link')}</Link>
        </Typography>
      </Stack>
    </>
  )
}



const combinedSchema = step1Schema.merge(step2Schema);

const registerSchema = z.object({
  login: combinedSchema.shape.login,
  email: combinedSchema.shape.email,
  password: combinedSchema.shape.password,
  locale: combinedSchema.shape.locale,
  firstName: combinedSchema.shape.firstName,
  lastName: combinedSchema.shape.lastName,
  address: combinedSchema.shape.address
});

type RegisterAccountData = z.infer<typeof registerSchema>;